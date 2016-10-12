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

package jp.co.recruit_mp.android.lightcalendarview.accent

import android.graphics.Canvas
import android.graphics.Paint

/**
 * Accent that can be used to decorate a {@link DayView}
 *
 * See an example implementation in {@link DotAccent}.
 * Created by masayuki-recruit on 9/7/16.
 */
abstract class Accent(val key: Any = Any()) {
    abstract val width: Int
    abstract val height: Int
    abstract val marginLeftRight: Int
    abstract val marginTopBottom: Int

    /**
     * Performs drawing the accent on {@link Canvas}
     *
     * @param x: horizontal center of the accent to be drawn
     * @param y: vertical center of the accent to be drawn
     * @param paint: {@link Paint} that can be used in the drawing process
     */
    abstract fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Accent) return false

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}
