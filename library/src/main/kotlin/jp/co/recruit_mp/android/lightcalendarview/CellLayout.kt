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
import android.view.View
import android.view.ViewGroup

/**
 * 子ビューをグリッド状に配置する {@link ViewGroup}
 * Created by masayuki-recruit on 8/19/16.
 */
abstract class CellLayout(context: Context, protected val settings: CalendarSettings) : ViewGroup(context) {

    abstract val rowNum: Int
    abstract val colNum: Int

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val (specWidthSize, specWidthMode) = Measure.createFromSpec(widthMeasureSpec)
        val (specHeightSize, specHeightMode) = Measure.createFromSpec(heightMeasureSpec)

        // 自分の大きさを確定
        val minSide = when {
            specWidthMode == MeasureSpec.UNSPECIFIED && specHeightMode == MeasureSpec.UNSPECIFIED ->
                throw IllegalStateException("CellLayout can never be left to determine its size")
            specWidthMode == MeasureSpec.UNSPECIFIED -> specHeightSize / rowNum
            specHeightMode == MeasureSpec.UNSPECIFIED -> specWidthSize / colNum
            else -> Math.min(specWidthSize / colNum, specHeightSize / rowNum)
        }
        val minWidth = minSide * colNum
        val minHeight = minSide * rowNum
        val selfMeasuredWidth = when (specWidthMode) {
            MeasureSpec.EXACTLY -> specWidthSize
            MeasureSpec.AT_MOST -> Math.min(minWidth, specWidthSize)
            MeasureSpec.UNSPECIFIED -> minWidth
            else -> specWidthSize
        }
        val selfMeasuredHeight = when (specHeightMode) {
            MeasureSpec.EXACTLY -> specHeightSize
            MeasureSpec.AT_MOST -> Math.min(minHeight, specHeightSize)
            MeasureSpec.UNSPECIFIED -> minHeight
            else -> specHeightSize
        }
        setMeasuredDimension(selfMeasuredWidth, selfMeasuredHeight)

        // 子ビューを measure
        val childMeasuredWidth = selfMeasuredWidth / colNum
        val childMeasuredHeight = selfMeasuredHeight / rowNum
        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childMeasuredWidth, MeasureSpec.AT_MOST)
        val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childMeasuredHeight, MeasureSpec.AT_MOST)
        childList.forEach {
            it.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // セルを中央に配置する為に、上下左右のマージンを計算
        val offsetTop: Int = (measuredHeight % rowNum) / 2
        val offsetStart: Int = (measuredWidth % colNum) / 2

        val parentTop: Int = paddingTop
        val parentStart: Int = ViewCompat.getPaddingStart(this)

        // RTL かどうか判定
        val isRtl: Boolean = (layoutDirection == View.LAYOUT_DIRECTION_RTL)

        // 各セルを配置
        childList.forEachIndexed { i, child ->
            val x: Int = i % colNum
            val y: Int = i / colNum

            val childWidth: Int = child.measuredWidth
            val childHeight: Int = child.measuredHeight

            val childTop: Int = parentTop + offsetTop + (y * childHeight)
            val childStart: Int = parentStart + offsetStart + (x * childWidth)
            val childLeft: Int = if (isRtl) {
                r - childStart - childWidth
            } else {
                childStart
            }

            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        }
    }

}
