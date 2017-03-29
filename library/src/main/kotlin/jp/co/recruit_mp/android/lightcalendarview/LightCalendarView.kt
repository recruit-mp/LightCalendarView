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
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import java.util.*

/**
 * 月カレンダーのカルーセルを表示する {@link FrameLayout}
 * Created by masayuki-recruit on 8/18/16.
 */
class LightCalendarView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : ViewPager(context, attrs) {

    private var settings: CalendarSettings = CalendarSettings(context)

    private var selectedPage: Int = 0
    var onMonthSelected: ((date: Date, view: MonthView) -> Unit)? = null
    var onDateSelected: ((date: Date) -> Unit)? = null
    private val onPageChangeListener: OnPageChangeListener = object : SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            selectedPage = position

            getMonthViewForPosition(position)?.let { view ->
                onMonthSelected?.invoke(getDateForPosition(position), view)
            }
        }
    }
    var monthCurrent: Date
        get() = getDateForPosition(currentItem)
        set(value) {
            currentItem = getPositionForDate(value)
        }

    var monthFrom: Date = CalendarKt.getInstance(settings).apply { set(Date().getFiscalYear(settings), Calendar.APRIL, 1) }.time
        set(value) {
            field = value
            adapter.notifyDataSetChanged()
        }
    var monthTo: Date = CalendarKt.getInstance(settings).apply { set(monthFrom.getFiscalYear(settings) + 1, Calendar.MARCH, 1) }.time
        set(value) {
            field = value
            adapter.notifyDataSetChanged()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LightCalendarView, defStyleAttr, defStyleRes)
        (0..a.indexCount - 1).forEach { i ->
            val attr = a.getIndex(i)
            when (attr) {
                R.styleable.LightCalendarView_lcv_weekDayTextSize -> setWeekDayRawTextSize(a.getDimension(attr, 0f))
                R.styleable.LightCalendarView_lcv_dayTextSize -> setDayRawTextSize(a.getDimension(attr, 0f))
                R.styleable.LightCalendarView_lcv_textColor -> setTextColor(a.getColorStateList(attr))
                R.styleable.LightCalendarView_lcv_selectionColor -> setSelectionColor(a.getColorStateList(attr))
                R.styleable.LightCalendarView_lcv_accentColor -> setAccentColor(a.getColorStateList(attr))
                R.styleable.LightCalendarView_lcv_firstDayOfWeek -> setFirstDayOfWeek(a.getInt(attr, 0))
                R.styleable.LightCalendarView_lcv_holidayTextColor -> setHolidayTextColor(a.getColor(attr, 0))
            }
        }
        a.recycle()

        adapter = Adapter()
        addOnPageChangeListener(onPageChangeListener)

        currentItem = getPositionForDate(Date())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec

        // 高さの WRAP_CONTENT を有効にする
        val (specHeightSize, specHeightMode) = Measure.createFromSpec(heightMeasureSpec)
        if (specHeightMode == MeasureSpec.AT_MOST || specHeightMode == MeasureSpec.UNSPECIFIED) {
            val height = childList.map {
                it.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(specHeightSize, specHeightMode))
                it.measuredHeight
            }.fold(0, { h1, h2 -> Math.max(h1, h2) })
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * {@link ViewPager} のページに対応する月を返す
     */
    fun getDateForPosition(position: Int): Date = monthFrom.add(settings, Calendar.MONTH, position)

    /**
     * 月に対応する {@link ViewPager} のページを返す
     */
    fun getPositionForDate(date: Date): Int = date.monthsAfter(settings, monthFrom).toInt()

    /**
     * {@link ViewPager} の特定のページにある {@link MonthView} を返す
     */
    fun getMonthViewForPosition(position: Int): MonthView? = findViewWithTag(context.getString(R.string.month_view_tag_name, position)) as? MonthView

    /**
     * 特定の日付を選択する
     */
    fun setSelectedDate(date: Date) {
        getMonthViewForPosition(getPositionForDate(date))?.setSelectedDate(date)
    }

    /**
     * 曜日ビューの文字サイズを設定する
     */
    fun setWeekDayTextSize(unit: Int, size: Float) {
        setWeekDayRawTextSize(TypedValue.applyDimension(unit, size, context.resources.displayMetrics))
    }

    private fun setWeekDayRawTextSize(size: Float) {
        settings.weekDayView.apply {
            textSize = size
        }.notifySettingsChanged()
    }

    /**
     * 日付ビューの文字サイズを指定する
     */
    fun setDayTextSize(unit: Int, size: Float) {
        setDayRawTextSize(TypedValue.applyDimension(unit, size, context.resources.displayMetrics))
    }

    private fun setDayRawTextSize(size: Float) {
        settings.dayView.apply {
            textSize = size
        }.notifySettingsChanged()
    }

    /**
     * 文字色を設定する
     */
    fun setTextColor(color: Int) {
        settings.weekDayView.apply {
            textColor = color
        }.notifySettingsChanged()
        settings.dayView.apply {
            textColor = color
        }.notifySettingsChanged()
    }

    /**
     * 文字色を設定する
     */
    fun setTextColor(colorStateList: ColorStateList) {
        settings.weekDayView.apply {
            setTextColorStateList(colorStateList)
        }.notifySettingsChanged()
        settings.dayView.apply {
            setTextColorStateList(colorStateList)
        }.notifySettingsChanged()
    }

    /**
     * 祝日の文字色を設定する
     */
    fun setHolidayTextColor(color: Int) {
        settings.dayView.apply {
            setHolidayTextColorStateList(color)
        }.notifySettingsChanged()
    }

    /**
     * 日付ビューの選択時の背景色を設定する
     */
    fun setSelectionColor(colorStateList: ColorStateList) {
        settings.dayView.apply {
            setCircleColorStateList(colorStateList)
        }.notifySettingsChanged()
    }

    /**
     * 日付ビューのアクセントの色を設定する
     */
    fun setAccentColor(colorStateList: ColorStateList) {
        settings.dayView.apply {
            setAccentColorStateList(colorStateList)
        }.notifySettingsChanged()
    }

    /**
     * Sets color of a weekday of the week
     */
    fun setWeekDayFilterColor(weekDay: WeekDay, color: Int?) {
        settings.weekDayView.apply {
            setTextFilterColor(weekDay, color)
        }.notifySettingsChanged()
    }

    /**
     * Sets color of a day of the week
     */
    fun setDayFilterColor(weekDay: WeekDay, color: Int?) {
        settings.dayView.apply {
            setTextFilterColor(weekDay, color)
        }.notifySettingsChanged()
    }

    /**
     * First day of the week (e.g. Sunday, Monday, ...)
     */
    var firstDayOfWeek: WeekDay
        get() = settings.firstDayOfWeek
        set(value) {
            settings.firstDayOfWeek = value
            settings.notifySettingsChanged()
        }

    private fun setFirstDayOfWeek(n: Int) {
        firstDayOfWeek = WeekDay.fromOrdinal(n)
    }

    /**
     * Sets the timezone to use in LightCalendarView.
     * Set null to use TimeZone.getDefault()
     */
    var timeZone: TimeZone
        get() = settings.timeZone
        set(value) {
            settings.apply {
                timeZone = value
            }.notifySettingsChanged()
        }

    /**
     * Sets the locale to use in LightCalendarView.
     * Set null to use Locale.getDefault()
     */
    var locale: Locale
        get() = settings.locale
        set(value) {
            settings.apply {
                locale = value
            }.notifySettingsChanged()
        }

    private inner class Adapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup?, position: Int): View {
            val view = MonthView(context, settings, getDateForPosition(position)).apply {
                tag = context.getString(R.string.month_view_tag_name, position)
                onDateSelected = { date -> this@LightCalendarView.onDateSelected?.invoke(date) }
            }
            container?.addView(view, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

            if (position == selectedPage) {
                onMonthSelected?.invoke(getDateForPosition(position), view)
            }

            return view
        }

        override fun destroyItem(container: ViewGroup?, position: Int, view: Any?) {
            (view as? View)?.let { container?.removeView(it) }
        }

        override fun isViewFromObject(view: View?, obj: Any?): Boolean = view === obj

        override fun getCount(): Int = Math.max(0, monthTo.monthsAfter(settings, monthFrom).toInt() + 1)
    }
}
