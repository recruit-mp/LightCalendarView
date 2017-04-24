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
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.view.ViewCompat
import java.util.*

/**
 * 曜日のラベルを表示する {@link CellView}
 * Created by masayuki-recruit on 8/19/16.
 */
class WeekDayView(context: Context, settings: CalendarSettings, var weekDay: WeekDay) : CellView(context, settings) {

    val text: String = weekDay.getShortLabel(context, settings.locale)

    private val textPaint: Paint = settings.weekDayView.defaultTextPaint(weekDay)
    private var baseX: Float = 0f
    private var baseY: Float = 0f

    private val observer = Observer { observable, any ->
        updateMetrics()
        ViewCompat.postInvalidateOnAnimation(this@WeekDayView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        settings.weekDayView.addObserver(observer)
    }

    override fun onDetachedFromWindow() {
        settings.weekDayView.deleteObserver(observer)
        super.onDetachedFromWindow()
    }

    private fun updateMetrics() {
        val fm = textPaint.fontMetrics
        val textWidth = textPaint.measureText(text)
        val textHeight = fm.descent - fm.ascent
        baseX = centerX - textWidth / 2
        baseY = centerY + textHeight / 2 - fm.descent
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateMetrics()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawText(text, baseX, baseY, textPaint)
    }

    override fun toString(): String = "WeekDayView($weekDay)"

}
