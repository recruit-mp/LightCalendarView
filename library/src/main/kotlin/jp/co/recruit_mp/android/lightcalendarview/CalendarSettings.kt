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

    // settings for WeekDayView
    val weekDayView = WeekDayView(observer)

    // settings for DayView
    val dayView = DayView(observer)
    var timeZone: TimeZone = TimeZone.getDefault()
    var locale: Locale = Locale.getDefault()
    var displayOutside: Boolean = false

    // settings for DayLayout and WeekDayLayout: first day of the week
    var firstDayOfWeek: WeekDay = WeekDay.SUNDAY
    val dayOfWeekOffset: Int
        get() = WeekDay.values().indexOf(firstDayOfWeek)

    /**
     * Settings for {@link jp.co.recruit_mp.android.lightcalendarview.WeekDayView}
     */
    inner class WeekDayView(observer: Observer) : ObservableSettings() {
        init {
            addObserver(observer)
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

        private val textFilterColorMap: MutableMap<WeekDay, Int?> = WeekDay.values().map {
            it to when (it) {
                WeekDay.SUNDAY -> context.getColorCompat(R.color.light_calendar_view__week_day_sunday_text_color)
                WeekDay.SATURDAY -> context.getColorCompat(R.color.light_calendar_view__week_day_saturday_text_color)
                else -> 0x00000000
            }
        }.toMutableMap()

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
            it to basePaint.copy().colorFilter(textFilterColorMap[it]?.let { color -> PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP) } ?: throw IllegalStateException("WeekDay color map for $it not found."))
        }.toMap()
        // ---------------------------------------------------------------------------------------------------------

        internal fun setTextColorStateList(colorStateList: ColorStateList) {
            textColor = colorStateList.getColorForState(State.DEFAULT.value, textColor)
        }

        internal fun setTextFilterColor(weekDay: WeekDay, color: Int?) {
            textFilterColorMap[weekDay] = color
            defaultTextPaints[weekDay]?.colorFilter(textFilterColorMap[weekDay]?.let { color -> PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP) } ?: throw IllegalStateException("WeekDay color map for $weekDay not found."))
        }
    }

    /**
     * Settings for {@link jp.co.recruit_mp.android.lightcalendarview.DayView}
     */
    inner class DayView(observer: Observer) : ObservableSettings() {
        init {
            addObserver(observer)
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

        private val textFilterColorMap: MutableMap<WeekDay, Int?> = WeekDay.values().map {
            it to when (it) {
                WeekDay.SUNDAY -> context.getColorCompat(R.color.light_calendar_view__day_sunday_text_color)
                WeekDay.SATURDAY -> context.getColorCompat(R.color.light_calendar_view__day_saturday_text_color)
                else -> 0x00000000
            }
        }.toMutableMap()

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

        internal var outsideTextPaint: Paint = initializeOutsideTextPaint()
        internal var defaultTextPaints: Map<WeekDay, Paint> = initializedDefaultTextPaints()
        internal var todayTextPaint: Paint = initializedTodayTextPaint()
        internal var selectedTextPaint: Paint = initializeSelectedTextPaint()
        internal var selectedTodayTextPaint: Paint = initializedSelectedTodayTextPaint()

        private fun initializedDefaultTextPaints() = WeekDay.values().map {
            it to baseTextPaint.copy().colorFilter(textFilterColorMap[it]?.let { color -> PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP) } ?: throw IllegalStateException("Day color map for $it not found."))
        }.toMap()

        private fun initializeOutsideTextPaint() = baseTextPaint.copy().color(context.getStyledColor(android.R.attr.textColorPrimary, context.getColorCompat(R.color.light_calendar_view__day_today_text_color)))
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
        internal fun setOutsideTextColorStateList(color:Int) {
            outsideTextPaint.color = color
        }

        internal fun setTextFilterColor(weekDay: WeekDay, color: Int?) {
            textFilterColorMap[weekDay] = color
            defaultTextPaints[weekDay]?.colorFilter(textFilterColorMap[weekDay]?.let { color -> PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP) } ?: throw IllegalStateException("Day color map for $weekDay not found."))
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

    /** Converts a map to a mutable map */
    private fun <K, V> Iterable<Pair<K, V>>.toMutableMap(): MutableMap<K, V> where K : WeekDay, V : Int? = mutableMapOf<K, V>().apply { putAll(this@toMutableMap) }
}
