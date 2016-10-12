/*
 * Copyright (C) 2016 RECRUIT MARKETING PARTNERS CO., LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.recruit_mp.android.lightcalendarview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import java.util.*

/**
 * Created by recruit-mahayash on 9/14/16.
 */
class CalendarSettings(private val context: Context) : ObservableSettings() {

    private val observer = Observer { observable, any -> notifyObservers(any) }

    val weekDayView = WeekDayView(observer)
    val dayView = DayView(observer)

    /**
     * Settings for {@link jp.co.recruit_mp.android.lightcalendarview.WeekDayView}
     */
    inner class WeekDayView(observer: Observer? = null) : ObservableSettings() {
        init {
            observer?.let { addObserver(it) }
        }

        var textColor: Int = context.getStyledColor(android.R.attr.textColorPrimary, context.getColorCompat(R.color.light_calendar_view__week_day_weekday_text_color))
            set(value) {
                field = value
                defaultTextPaints.values.forEach { it.color = value }
            }

        var textSize: Float = context.getStyledDimension(android.R.attr.textSize, context.getDimension(R.dimen.light_calendar_view__week_day_text_size))
            set(value) {
                field = value
                defaultTextPaints.values.forEach { it.textSize = value }
            }

        // ------------- Base --------------------------------------------------------------------------------------
        var basePaint: Paint = Paint().textSize(this@WeekDayView.textSize).color(this@WeekDayView.textColor).typeface(Typeface.DEFAULT).isAntiAlias(true)
            set(value) {
                field = value
                defaultTextPaints = initializedDefaultTextPaints()
            }
        // ---------------------------------------------------------------------------------------------------------

        // ------------ Text ---------------------------------------------------------------------------------------
        internal fun defaultTextPaint(weekDay: WeekDay): Paint = defaultTextPaints[weekDay] ?: throw IllegalStateException("cannot find default Paint with weekDay - $weekDay")

        internal var defaultTextPaints: Map<WeekDay, Paint> = initializedDefaultTextPaints()

        private fun initializedDefaultTextPaints() = WeekDay.values().map {
            it to basePaint.copy().colorFilter(when (it) {
                WeekDay.SUNDAY -> PorterDuffColorFilter(context.getColorCompat(R.color.light_calendar_view__week_day_sunday_text_color), PorterDuff.Mode.SRC_ATOP)
                WeekDay.SATURDAY -> PorterDuffColorFilter(context.getColorCompat(R.color.light_calendar_view__week_day_saturday_text_color), PorterDuff.Mode.SRC_ATOP)
                else -> null
            })
        }.toMap()
        // ---------------------------------------------------------------------------------------------------------

        internal fun setTextColorStateList(colorStateList: ColorStateList) {
            textColor = colorStateList.getColorForState(State.DEFAULT.value, textColor)
        }
    }

    /**
     * Settings for {@link jp.co.recruit_mp.android.lightcalendarview.DayView}
     */
    inner class DayView(observer: Observer? = null) : ObservableSettings() {
        init {
            observer?.let { addObserver(it) }
        }

        var textColor: Int = context.getStyledColor(android.R.attr.textColorPrimary, context.getColorCompat(R.color.light_calendar_view__day_weekday_text_color))
            set(value) {
                field = value
                defaultTextPaints.values.forEach { it.color(value) }
            }

        var textSize: Float = context.getStyledDimension(android.R.attr.textSize, context.getDimension(R.dimen.light_calendar_view__day_text_size))
            set(value) {
                field = value
                defaultTextPaints.values.forEach { it.textSize(value) }
                todayTextPaint.textSize(value)
                selectedTextPaint.textSize(value)
                selectedTodayTextPaint.textSize(value)
            }

        // ------------- Base --------------------------------------------------------------------------------------
        var baseCirclePaint: Paint = Paint().isAntiAlias(true).style(Paint.Style.FILL)
            set(value) {
                field = value

                defaultCirclePaint = initializedDefaultCirclePaint()
                todayCirclePaint = initializedTodayCirclePaint()
            }

        var baseTextPaint: Paint = Paint().textSize(this@DayView.textSize).color(this@DayView.textColor).typeface(Typeface.DEFAULT).isAntiAlias(true)
            set(value) {
                field = value

                defaultTextPaints = initializedDefaultTextPaints()
                todayTextPaint = initializedTodayTextPaint()
                selectedTextPaint = initializeSelectedTextPaint()
                selectedTodayTextPaint = initializedSelectedTodayTextPaint()
            }

        var baseAccentPaint: Paint = Paint().isAntiAlias(true).style(Paint.Style.FILL)
            set(value) {
                field = value

                defaultAccentPaint = initializedDefaultAccentPaint()
                todayAccentPaint = initializedTodayAccentPaint()
                selectedAccentPaint = initializedSelectedAccentPaint()
                selectedTodayAccentPaint = initializedTodaySelectedAccentPaint()
            }
        // ---------------------------------------------------------------------------------------------------------

        // ------------ Circle -------------------------------------------------------------------------------------
        internal var defaultCirclePaint: Paint = initializedDefaultCirclePaint()
        internal var todayCirclePaint: Paint = initializedTodayCirclePaint()

        private fun initializedDefaultCirclePaint() = baseCirclePaint.copy().color(context.getColorCompat(R.color.light_calendar_view__day_circle_color))
        private fun initializedTodayCirclePaint() = baseCirclePaint.copy().color(context.getStyledColor(android.R.attr.colorPrimary, context.getColorCompat(R.color.light_calendar_view__day_today_circle_color)))
        // ---------------------------------------------------------------------------------------------------------

        // ------------ Text ---------------------------------------------------------------------------------------
        internal fun defaultTextPaint(weekDay: WeekDay): Paint = defaultTextPaints[weekDay] ?: throw IllegalStateException("cannot find default Paint with weekDay - $weekDay")

        internal var defaultTextPaints: Map<WeekDay, Paint> = initializedDefaultTextPaints()
        internal var todayTextPaint: Paint = initializedTodayTextPaint()
        internal var selectedTextPaint: Paint = initializeSelectedTextPaint()
        internal var selectedTodayTextPaint: Paint = initializedSelectedTodayTextPaint()

        private fun initializedDefaultTextPaints() = WeekDay.values().map {
            it to baseTextPaint.copy().colorFilter(when (it) {
                WeekDay.SUNDAY -> PorterDuffColorFilter(context.getColorCompat(R.color.light_calendar_view__day_sunday_text_color), PorterDuff.Mode.SRC_ATOP)
                WeekDay.SATURDAY -> PorterDuffColorFilter(context.getColorCompat(R.color.light_calendar_view__day_saturday_text_color), PorterDuff.Mode.SRC_ATOP)
                else -> null
            })
        }.toMap()

        private fun initializedTodayTextPaint() = baseTextPaint.copy().color(context.getStyledColor(android.R.attr.textColorPrimary, context.getColorCompat(R.color.light_calendar_view__day_today_text_color)))
        private fun initializeSelectedTextPaint() = baseTextPaint.copy().typeface(Typeface.DEFAULT_BOLD).color(context.getStyledColor(android.R.attr.textColorPrimaryInverse, context.getColorCompat(R.color.light_calendar_view__day_selected_text_color)))
        private fun initializedSelectedTodayTextPaint() = baseTextPaint.copy().typeface(Typeface.DEFAULT_BOLD).color(context.getStyledColor(android.R.attr.textColorPrimaryInverse, context.getColorCompat(R.color.light_calendar_view__day_selected_today_text_color)))
        // ---------------------------------------------------------------------------------------------------------

        // ------------ Accent -------------------------------------------------------------------------------------
        internal var defaultAccentPaint: Paint = initializedDefaultAccentPaint()
        internal var todayAccentPaint: Paint = initializedTodayAccentPaint()
        internal var selectedAccentPaint: Paint = initializedSelectedAccentPaint()
        internal var selectedTodayAccentPaint: Paint = initializedTodaySelectedAccentPaint()

        private fun initializedDefaultAccentPaint() = baseAccentPaint.copy().color(context.getStyledColor(android.R.attr.colorAccent, context.getColorCompat(R.color.light_calendar_view__day_dot_color)))
        private fun initializedTodayAccentPaint() = baseAccentPaint.copy().color(context.getStyledColor(android.R.attr.colorAccent, context.getColorCompat(R.color.light_calendar_view__day_today_dot_color)))
        private fun initializedSelectedAccentPaint() = baseAccentPaint.copy().color(context.getStyledColor(android.R.attr.colorAccent, context.getColorCompat(R.color.light_calendar_view__day_selected_dot_color)))
        private fun initializedTodaySelectedAccentPaint() = baseAccentPaint.copy().color(context.getStyledColor(android.R.attr.colorAccent, context.getColorCompat(R.color.light_calendar_view__day_selected_today_dot_color)))
        // ---------------------------------------------------------------------------------------------------------

        internal fun setCircleColorStateList(colorStateList: ColorStateList) {
            defaultCirclePaint.color(colorStateList, State.SELECTED)
            todayCirclePaint.color(colorStateList, State.SELECTED_TODAY)
        }

        internal fun setTextColorStateList(colorStateList: ColorStateList) {
            textColor = colorStateList.getColorForState(State.DEFAULT.value, textColor)
            todayTextPaint.color(colorStateList, State.TODAY)
            selectedTextPaint.color(colorStateList, State.SELECTED)
            selectedTodayTextPaint.color(colorStateList, State.SELECTED_TODAY)
        }

        internal fun setAccentColorStateList(colorStateList: ColorStateList) {
            defaultAccentPaint.color(colorStateList, State.DEFAULT)
            todayAccentPaint.color(colorStateList, State.TODAY)
            selectedAccentPaint.color(colorStateList, State.SELECTED)
            selectedTodayAccentPaint.color(colorStateList, State.SELECTED_TODAY)
        }
    }

    /**
     * Enum State
     */
    enum class State(val value: IntArray) {
        DEFAULT(intArrayOf()),
        SELECTED(intArrayOf(android.R.attr.state_selected)),
        TODAY(intArrayOf(android.R.attr.state_active)),
        SELECTED_TODAY(intArrayOf(android.R.attr.state_selected, android.R.attr.state_active))
    }


    // ------------ Extensions ---------------------------------------------------------------------------------

    /** Sets {@link Paint#textSize} */
    private fun Paint.textSize(s: Float): Paint = apply { textSize = s }

    /** Sets {@link Paint#color} */
    private fun Paint.color(c: Int): Paint = apply { color = c }

    /** Sets {@link Paint#color} */
    private fun Paint.color(c: ColorStateList, s: State): Paint = apply { color = c.getColorForState(s.value, color) }

    /** Sets {@link Paint#typeface} */
    private fun Paint.typeface(t: Typeface): Paint = apply { typeface = t }

    /** Sets {@link Paint#isAntiAlias} */
    private fun Paint.isAntiAlias(a: Boolean): Paint = apply { isAntiAlias = a }

    /** Sets {@link Paint#colorFilter} */
    private fun Paint.colorFilter(c: ColorFilter?): Paint = apply { colorFilter = c }

    /** Sets {@link Paint#style} */
    private fun Paint.style(s: Paint.Style): Paint = apply { style = s }

    /**
     * Returns a copied {@link Paint} object
     *
     * Typeface is explicitly set as there is a known bug in the SDK implementation.
     * {@see http://stackoverflow.com/questions/12155553/android-typeface-not-copied-into-new-paint}
     */
    private fun Paint.copy(): Paint = Paint(this).apply { typeface = this@copy.typeface }
}
