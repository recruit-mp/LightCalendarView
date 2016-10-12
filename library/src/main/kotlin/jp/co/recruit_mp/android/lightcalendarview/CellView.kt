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
import android.view.View

/**
 * {@link CellLayout} 直下に配置されるセルの {@link ViewGroup}
 * Created by masayuki-recruit on 8/18/16.
 */
open class CellView(context: Context, protected val settings: CalendarSettings) : View(context) {

    var centerX: Float = 0f
    var centerY: Float = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val (specWidthSize, specWidthMode) = Measure.createFromSpec(widthMeasureSpec)
        val (specHeightSize, specHeightMode) = Measure.createFromSpec(heightMeasureSpec)

        if (specWidthMode == MeasureSpec.UNSPECIFIED || specHeightMode == MeasureSpec.UNSPECIFIED) {
            throw IllegalStateException("CellView can never be left to determine its size")
        }

        val measuredCellWidth = specWidthSize
        val measuredCellHeight = specHeightSize
        setMeasuredDimension(measuredCellWidth, measuredCellHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 中央に描画するための計算
        centerX = paddingLeft + (w - paddingLeft - paddingRight) / 2f
        centerY = paddingTop + (h - paddingTop - paddingBottom) / 2f
    }

}
