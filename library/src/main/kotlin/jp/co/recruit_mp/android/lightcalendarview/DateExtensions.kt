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

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by masayuki-recruit on 8/24/16.
 */

/** 日付が一致するかどうかを返す */
internal fun Date.isSameDay(date: Date): Boolean {
    val thisCal = Calendar.getInstance().apply { time = this@isSameDay }
    val thatCal = Calendar.getInstance().apply { time = date }
    return (thisCal[Calendar.YEAR] == thatCal[Calendar.YEAR] && thisCal[Calendar.DAY_OF_YEAR] == thatCal[Calendar.DAY_OF_YEAR])
}

/** 自身が date の何日後かを返す */
internal fun Date.daysAfter(date: Date): Long = ((this.time - date.time) / TimeUnit.DAYS.toMillis(1))

/** 自身が date の何ヶ月後かを返す */
internal fun Date.monthsAfter(date: Date): Long {
    val thisCal = Calendar.getInstance().apply { time = this@monthsAfter }
    val thatCal = Calendar.getInstance().apply { time = date }
    return ((thisCal[Calendar.YEAR] - thatCal[Calendar.YEAR]) * Month.values().size + (thisCal[Calendar.MONTH] - thatCal[Calendar.MONTH])).toLong()
}

/** 日付が一致する {@link Date} がリストに含まれているかどうかを返す */
internal fun List<Date>.containsSameDay(date: Date): Boolean = any { it.isSameDay(date) }

/** 日付の計算 */
internal fun Date.add(field: Int, value: Int) = Calendar.getInstance().apply {
    time = this@add
    add(field, value)
}.time

/** 年度の取得 */
internal val Date.fiscalYear: Int
    get() = Calendar.getInstance().apply { time = this@fiscalYear }.let {
        if (it[Calendar.MONTH] < 3) {
            it[Calendar.YEAR] - 1
        } else {
            it[Calendar.YEAR]
        }
    }

/** {@link Calendar} への変換 */
internal fun Date.toCalendar(): Calendar = Calendar.getInstance().apply { time = this@toCalendar }
