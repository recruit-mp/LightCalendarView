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
import android.view.ViewGroup
import android.widget.LinearLayout
import jp.co.recruit_mp.android.lightcalendarview.accent.Accent
import java.util.*

/**
 * 月カレンダーを表示する {@link LinearLayout}
 * Created by masayuki-recruit on 8/19/16.
 */
class MonthView(context: Context, private val settings: CalendarSettings, var month: Date) : LinearLayout(context), DayLayout.Callback {

    internal var callback: Callback? = null

    private val weekDayLayout: WeekDayLayout
    private val dayLayout: DayLayout

    init {
        orientation = LinearLayout.VERTICAL

        weekDayLayout = WeekDayLayout(context, settings)
        addView(weekDayLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        dayLayout = DayLayout(context, settings, month).apply { callback = this@MonthView }
        addView(dayLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    override fun onDateSelected(date: Date) {
        callback?.onDateSelected(date)
    }

    fun setSelectedDate(date: Date) {
        dayLayout.setSelectedDay(date)
    }

    fun setAccents(date: Date, accents: Collection<Accent>) = dayLayout.let {
        it.getDayView(date)?.setAccents(accents)
        it.invalidateDayViews()
    }

    fun setAccents(map: Map<Date, Collection<Accent>>) {
        map.forEach { it ->
            val (date, accents) = it
            dayLayout.getDayView(date)?.setAccents(accents)
        }
        dayLayout.invalidateDayViews()
    }

    interface Callback {
        fun onDateSelected(date: Date)
    }

    override fun toString(): String = "MonthView(${month})"
}
