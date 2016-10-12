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

/**
 * 月カレンダー内の曜日ラベルを表示する {@link ViewGroup}
 * Created by masayuki-recruit on 8/19/16.
 */
class WeekDayLayout(context: Context, settings: CalendarSettings) : CellLayout(context, settings) {
    companion object {
        val DEFAULT_DAYS_IN_WEEK = 7
    }

    override val rowNum: Int
        get() = 1
    override val colNum: Int
        get() = DEFAULT_DAYS_IN_WEEK

    init {
        // 7 x 1 マスの WeekDayView を追加する
        WeekDay.values().forEach { weekDay ->
            addView(WeekDayView(context, settings, weekDay))
        }
    }

}
