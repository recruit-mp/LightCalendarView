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
 * Created by masayuki-recruit on 9/7/16.
 */
class DotAccent(var radius: Float, var color: Int? = null, key: Any = Any()) : Accent(key) {
    override val width: Int
        get() = (radius * 2).toInt()
    override val height: Int
        get() = (radius * 2).toInt()

    override val marginLeftRight: Int
        get() = (0.1 * radius).toInt()
    override val marginTopBottom: Int
        get() = (0.1 * radius).toInt()

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        val oldColor = paint.color
        canvas.let {
            paint.color = this.color ?: paint.color
            it.drawCircle(x, y, this.radius, paint)
            paint.color = oldColor
        }
    }
}
