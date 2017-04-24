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
import android.content.res.Configuration
import android.util.DisplayMetrics
import java.util.*
import android.R.id.message
import android.annotation.SuppressLint


/**
 * 曜日を表す enum
 * Created by masayuki-recruit on 8/19/16.
 */
enum class WeekDay {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;

    companion object {
        val ordinals = values()
        fun fromOrdinal(ordinal: Int) = ordinals[ordinal]

        /**
         * Returns the rearranged WeekDay values by sliding elements by n times
         *
         * @param n number of times to slide elements
         */
        fun getPermutation(n: Int): List<WeekDay> = values().let {
            val indices = it.size.let { size -> (0..size - 1).map { i -> (i + n) % size } }
            it.slice(indices)
        }
    }

    fun getShortLabel(context: Context): String = context.getStringArray(R.array.week_days_short)[ordinal]
    fun getShortLabel(context: Context, locale: Locale): String = getString(context, locale, R.array.week_days_short)[ordinal]

    fun getLabel(context: Context): String = context.getStringArray(R.array.week_days_full)[ordinal]

    @SuppressLint("NewApi")
    protected fun getString(context: Context, locale: Locale, id: Int): Array<String> {
        val configuration = getConfiguration(context, locale)

        return context.createConfigurationContext(configuration).getResources().getStringArray(id)
    }

    @SuppressLint("NewApi")
    private fun getConfiguration(context: Context, locale: Locale): Configuration {
        val configuration = Configuration(context.getResources().getConfiguration())
        configuration.setLocale(locale)
        return configuration
    }
}
