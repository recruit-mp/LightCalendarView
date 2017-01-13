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

import org.junit.Test
import java.util.*

/**
 * Created by masayuki-recruit on 8/30/16.
 */
class LightCalendarViewTest : BaseTestCase() {

    @Test
    fun testGetDateForPosition() {
        val view = LightCalendarView(context)

        assertTrue(view.getDateForPosition(0).month == 3)
        assertTrue(view.getDateForPosition(8).month == 11)
        assertTrue(view.getDateForPosition(9).month == 0)
        assertTrue(view.getDateForPosition(11).month == 2)
    }

    @Test
    fun testGetPositionForDate() {
        val view = LightCalendarView(context).apply {
            monthFrom = Date(2016 - 1900, 4, 1)
            monthTo = Date(2017 - 1900, 3, 30)
        }

        assertTrue(view.getPositionForDate(Date(2016 - 1900, 3, 1)) == 3)
        assertTrue(view.getPositionForDate(Date(2016 - 1900, 11, 1)) == 8)
        assertTrue(view.getPositionForDate(Date(2016 - 1900, 0, 1)) == 9)
        assertTrue(view.getPositionForDate(Date(2016 - 1900, 2, 1)) == 2)
    }

}
