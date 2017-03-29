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
import android.support.v4.view.ViewCompat
import android.util.Log
import java.util.*

/**
 * 月カレンダー内の日を表示する {@link ViewGroup}
 * Created by masayuki-recruit on 8/18/16.
 */
class DayLayout(context: Context, settings: CalendarSettings, var month: Date) : CellLayout(context, settings) {

    companion object {
        val DEFAULT_WEEKS = 6
        val DEFAULT_DAYS_IN_WEEK = WeekDay.values().size
    }

    override val rowNum: Int
        get() = DEFAULT_WEEKS
    override val colNum: Int
        get() = DEFAULT_DAYS_IN_WEEK

    internal var selectedDayView: DayView? = null

    internal var onDateSelected: ((date: Date) -> Unit)? = null
    private var firstDate: Calendar = CalendarKt.getInstance(settings)
    private var dayOfWeekOffset: Int = -1
    private val thisYear: Int
    private val thisMonth: Int

    init {
        val cal: Calendar = CalendarKt.getInstance(settings).apply {
            time = month
            set(Calendar.DAY_OF_MONTH, 1)
        }
        thisYear = cal[Calendar.YEAR]
        thisMonth = cal[Calendar.MONTH]

        // update the layout
        updateLayout()

        // 今日を選択
        setSelectedDay(Date())
    }

    private val observer = Observer { observable, any ->
        updateLayout()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        settings.addObserver(observer)
    }

    override fun onDetachedFromWindow() {
        settings.deleteObserver(observer)
        super.onDetachedFromWindow()
    }

    private fun updateLayout() {
        if (dayOfWeekOffset != settings.dayOfWeekOffset) {
            dayOfWeekOffset = settings.dayOfWeekOffset

            // calculate the date of top-left cell
            val cal: Calendar = CalendarKt.getInstance(settings).apply {
                time = month
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.DAY_OF_YEAR, (-this[Calendar.DAY_OF_WEEK] + dayOfWeekOffset + 1).let { offset ->
                    if (offset > 0) (offset - WeekDay.values().size) else offset
                })
            }
            firstDate = cal

            // remove all children
            removeAllViews()

            // populate children
            populateViews()
        }
    }

    private fun populateViews() {
        val cal = firstDate.clone() as Calendar

        // 7 x 6 マスの DayView を追加する
        (0..rowNum - 1).forEach {
            (0..colNum - 1).forEach {
                when (cal[Calendar.MONTH]) {
                    thisMonth -> {
                        addView(instantiateDayView(cal.clone() as Calendar))
                    }
                    else -> {
                        if (settings.displayOutside) {
                            addView(instantiateDayView(cal.clone() as Calendar).setOutside())
                        } else {
                            addView(EmptyView(context, settings))
                        }
                    }
                }
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
        }
    }

    private fun instantiateDayView(cal: Calendar): DayView = DayView(context, settings, cal).apply {
        setOnClickListener { setSelectedDay(this) }
    }

    internal fun invalidateDayViews() {
        childList.map { it as? DayView }.filterNotNull().forEach {
            it.updateState()
            ViewCompat.postInvalidateOnAnimation(it)
        }
    }

    /**
     * 日付を選択する
     * @param date 選択する日
     */
    fun setSelectedDay(date: Date) {
        setSelectedDay(getDayView(date))
    }

    private fun setSelectedDay(view: DayView?) {
        selectedDayView?.apply {
            isSelected = false
            updateState()
        }
        selectedDayView = view?.apply {
            isSelected = true
            updateState()
            onDateSelected?.invoke(date)
        }
    }

    /**
     * 日付に対応する {@link DayView} を返す
     */
    fun getDayView(date: Date): DayView? = childList.getOrNull(date.daysAfter(firstDate.time).toInt()) as? DayView

}
