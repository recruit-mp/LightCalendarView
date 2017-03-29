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

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.support.v4.view.ViewCompat
import android.text.format.DateUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import jp.co.recruit_mp.android.lightcalendarview.accent.Accent
import java.util.*

/**
 * 日を表示するための {@link CellView}
 * Created by masayuki-recruit on 8/18/16.
 */
class DayView(context: Context, settings: CalendarSettings, cal: Calendar) : CellView(context, settings) {

    val date: Date = cal.time
    val weekDay: WeekDay = WeekDay.fromOrdinal(cal[Calendar.DAY_OF_WEEK] - 1)
    val text: String = cal.get(Calendar.DAY_OF_MONTH).toString()

    private val accents: MutableList<AccentWrapper> = mutableListOf()
    private val accentsWidth: Int
        get() = accents.fold(0, { w, a -> w + a.width + a.marginLeftRight * 2 })

    private var circlePaint: Paint = settings.dayView.defaultCirclePaint

    private var textPaint: Paint = settings.dayView.defaultTextPaint(weekDay)

    private var accentPaint: Paint = settings.dayView.defaultAccentPaint

    private var baseX: Float = 0f
    private var baseY: Float = 0f
    private var accentsCenterX: Float = 0f
    private var accentsCenterY: Float = 0f

    private var drawCircle: Boolean = false
    private var isHoliday: Boolean = false

    private var radius: Float = 0f
    private var currentRadius: Float = 0f
    private val animInterpolator: Interpolator = DecelerateInterpolator()
    private val animDuration: Long = 100
    private val fadeInAnim: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        interpolator = animInterpolator
        duration = animDuration
        addUpdateListener {
            currentRadius = (animatedValue as Float) * radius
            ViewCompat.postInvalidateOnAnimation(this@DayView)
        }
    }
    private val fadeOutAnim: ValueAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
        interpolator = animInterpolator
        duration = animDuration
        addUpdateListener {
            currentRadius = (animatedValue as Float) * radius
            ViewCompat.postInvalidateOnAnimation(this@DayView)
        }
    }

    private val observer = Observer { observable, any ->
        updateMetrics()
        ViewCompat.postInvalidateOnAnimation(this@DayView)
    }

    init {
        setBackgroundResource(R.drawable.light_calendar_view__day_background)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        settings.dayView.addObserver(observer)
    }

    override fun onDetachedFromWindow() {
        settings.dayView.deleteObserver(observer)
        super.onDetachedFromWindow()
    }

    /** アクセントを設定する */
    fun setAccents(accents: Collection<Accent>) {
        // map Accents to AccentWrappers
        val wrapped = accents.map { accent ->
            this.accents.find { it.accent == accent } ?: AccentWrapper(accent)
        }
        this.accents.apply {
            clear()
            addAll(wrapped)
        }

        layoutAccents()
        animateAccents()
    }
    // 祝日に設定
    fun setHoliday() {
        this.isHoliday = true
        updatePaint();
    }
    // 各アクセントの位置を設定する
    private fun layoutAccents() {
        // アクセント左端の位置を計算
        val startX: Float = accentsCenterX - accentsWidth / 2.0f
        val startY: Float = accentsCenterY
        // 各アクセントの位置を設定
        var centerX = startX
        val centerY = startY
        accents.forEach { accent ->
            centerX += accent.boundingWidth / 2.0f
            accent.offsetX = accent.centerX - centerX
            accent.offsetY = accent.centerY - centerY
            accent.centerX = centerX
            accent.centerY = centerY
            centerX += accent.boundingWidth / 2.0f
        }
    }

    // 各アクセントのアニメーションを行う
    private fun animateAccents() {
        accents.forEach { accent ->
            accent.translate()
            if (!accent.isDisplayed) {
                accent.fadeIn()
            }
        }
    }

    /** テキストとアクセントを描画する際の位置を決定する */
    private fun updateMetrics() {
        val fm = textPaint.fontMetrics
        // FontMetrics で得られる ascend, descend の位置
        //  ascend  < 0  -> ___________________________
        //                    |＼　／|
        //                  __|＿Ｖ＿|＼＿／_____________ <- textHeight / 2  (*)
        //                    |　　　|　Y
        //                  ^^^^^^^^^／^^^^^^^^^^^^^^^^ <- baseline = 0
        //  descend > 0  -> ^^^^^^^^^^^^^^^^^^^^^^^^^^^
        //
        // --> (*) をコンテナの中央に配置するための ascend の位置
        //         = (コンテナの高さ - textHeight / 2) - (descend - baseline)
        //         = (コンテナの高さ - textHeight / 2) - descend

        val textWidth = textPaint.measureText(text)
        val textHeight = fm.descent - fm.ascent
        baseX = centerX - textWidth / 2
        baseY = centerY + textHeight / 2 - fm.descent

        //                  ___________________________
        //                          ／|   ／^＼
        //                          　|  ｜   ｜
        //                          　|   ＼_／
        //                  ^^^^^^^^^^^^^^^^^^^^^^^^^^^ <- baseY
        //                  ^^^^^^^^^^^^^^^^^^^^^^^^^^^
        //                             ／￣＼
        //                             ＼＿／
        //                  ___________________________ <- bottom = height
        // --> 丸印を表示する位置
        //       = baseY とコンテナ一番下の間の中央
        //       = baseY + (height - baseY) / 2
        accentsCenterX = centerX
        accentsCenterY = baseY + (height - baseY) / 2f

        radius = Math.min((width - paddingLeft - paddingRight) / 2f, (height - paddingTop - paddingBottom) / 2f)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateMetrics()
    }

    /** 状態を更新する. {@link DayLayout} から呼ばれることを想定している. */
    internal fun updateState() {
        drawCircle = isSelected || fadeInAnim.isRunning || fadeOutAnim.isRunning

        updatePaint()
    }

    /** 曜日と日付に応じて描画に使用する {@link Paint} を設定する */
    private fun updatePaint() {
        circlePaint = if (DateUtils.isToday(date.time)) {
            settings.dayView.todayCirclePaint
        } else {
            settings.dayView.defaultCirclePaint
        }

        when {
            isSelected && DateUtils.isToday(date.time) -> {
                textPaint = settings.dayView.selectedTodayTextPaint
                accentPaint = settings.dayView.selectedTodayAccentPaint
            }
            isSelected -> {
                textPaint = settings.dayView.selectedTextPaint
                accentPaint = settings.dayView.selectedAccentPaint
            }
            DateUtils.isToday(date.time) -> {
                textPaint = settings.dayView.todayTextPaint
                accentPaint = settings.dayView.todayAccentPaint
            }
            // 祝日の設定
            isHoliday -> {
                textPaint = settings.dayView.holidayTextPaint
                accentPaint = settings.dayView.defaultAccentPaint
            }
            else -> {
                textPaint = settings.dayView.defaultTextPaint(weekDay)
                accentPaint = settings.dayView.defaultAccentPaint
            }
        }
    }

    override fun setSelected(selected: Boolean) {
        if (isSelected != selected) {
            if (selected) {
                fadeInAnim.start()
            } else {
                fadeOutAnim.start()
            }
        }
        super.setSelected(selected)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            // 背景の描画
            if (drawCircle) {
                it.drawCircle(centerX, centerY, currentRadius, circlePaint)
            }

            // 日付描画
            it.drawText(text, baseX, baseY, textPaint)

            // accents 描画
            drawAccents(it)
        }
    }

    /** アクセントの描画を行う */
    private fun drawAccents(canvas: Canvas) {
        accents.forEach { accent -> accent.draw(canvas, accentPaint) }
    }

    /** アニメーションを付加するためのアクセントのラッパー */
    private inner class AccentWrapper(val accent: Accent) {
        val width: Int
            get() = accent.width
        val height: Int
            get() = accent.height
        val marginLeftRight: Int
            get() = accent.marginLeftRight
        val marginTopBottom: Int
            get() = accent.marginTopBottom
        val boundingWidth: Int
            get() = width + marginLeftRight * 2
        val boundingHeight: Int
            get() = height + marginTopBottom * 2

        private var radius: Float = Math.max(width / 2, height / 2).toFloat()
        private var currentRadius: Float = radius

        var centerX: Float = accentsCenterX
        var centerY: Float = accentsCenterY

        var offsetX: Float = 0f
        private var currentOffsetX: Float = offsetX

        var offsetY: Float = 0f
        private var currentOffsetY: Float = offsetY

        private val translateAnim: ValueAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 200
            addUpdateListener {
                currentOffsetX = (animatedValue as Float) * offsetX
                currentOffsetY = (animatedValue as Float) * offsetY
                ViewCompat.postInvalidateOnAnimation(this@DayView)
            }
        }

        fun translate() = translateAnim.start()

        private val fadeInAnim: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 100
            addUpdateListener {
                currentRadius = (animatedValue as Float) * radius
                ViewCompat.postInvalidateOnAnimation(this@DayView)
            }
        }

        private val fadeOutAnim: ValueAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 100
            addUpdateListener {
                currentRadius = (animatedValue as Float) * radius
                ViewCompat.postInvalidateOnAnimation(this@DayView)
            }
        }

        val isDisplayed: Boolean
            get() = (currentRadius > 0)

        fun fadeIn() = fadeInAnim.start()

        fun fadeOut() = fadeOutAnim.start()

        fun draw(canvas: Canvas, paint: Paint) {
            canvas.save()
            canvas.clipPath(Path().apply {
                addCircle(centerX + currentOffsetX, centerY + currentOffsetY, currentRadius, Path.Direction.CCW)
            })
            accent.draw(canvas, centerX + currentOffsetX, centerY + currentOffsetY, paint)
            canvas.restore()
        }
    }

    override fun toString(): String = "DayView($date)"

}
