package com.example.customview

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import java.util.Calendar

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val hourHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 16f
        strokeCap = Paint.Cap.ROUND
    }

    private val minuteHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 12f
        strokeCap = Paint.Cap.ROUND
    }

    private val secondHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 6f
        strokeCap = Paint.Cap.ROUND
    }

    private var calendar: Calendar = Calendar.getInstance()
    private var secondHandAngle = 0f
    private var minuteHandAngle = 0f
    private var hourHandAngle = 0f

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClockView)
        paint.color = typedArray.getColor(R.styleable.ClockView_clockColor, Color.BLACK)
        typedArray.recycle()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        radius = (min(width, height) / 2 * 0.8).toFloat()
        centerX = (width / 2).toFloat()
        centerY = (height / 2).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawClockFace(canvas)
        drawHourHand(canvas)
        drawMinuteHand(canvas)
        drawSecondHand(canvas)
        invalidate()
    }

    private fun drawClockFace(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, radius, paint)
        for (i in 1..12) {
            val angle = Math.PI / 6 * (i - 3)
            val x = centerX + cos(angle) * radius
            val y = centerY + sin(angle) * radius
            canvas.drawText(i.toString(), x.toFloat(), y.toFloat(), paint)
        }
    }

    private fun drawHand(canvas: Canvas, loc: Float, angle: Double, length: Float, paint: Paint) {
        val handRadius = radius * length
        canvas.drawLine(
            centerX,
            centerY,
            centerX + (cos(angle) * handRadius).toFloat(),
            centerY + (sin(angle) * handRadius).toFloat(),
            paint
        )
    }

    private fun drawHourHand(canvas: Canvas) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY) % 12
        val minute = calendar.get(Calendar.MINUTE)
        val hourAngle = Math.PI * ((hour + minute / 60.0) - 3) / 6 + Math.toRadians(hourHandAngle.toDouble())
        drawHand(canvas, hour.toFloat(), hourAngle, 0.5f, hourHandPaint)
    }

    private fun drawMinuteHand(canvas: Canvas) {
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val minuteAngle = Math.PI * ((minute + second / 60.0) - 15) / 30 + Math.toRadians(minuteHandAngle.toDouble())
        drawHand(canvas, minute.toFloat(), minuteAngle, 0.8f, minuteHandPaint)
    }

    private fun drawSecondHand(canvas: Canvas) {
        val second = calendar.get(Calendar.SECOND)
        val secondAngle = Math.PI * (second - 15) / 30 + Math.toRadians(secondHandAngle.toDouble())
        drawHand(canvas, second.toFloat(), secondAngle, 0.9f, secondHandPaint)
    }

    private fun setHourHandAngle(angle: Float) {
        hourHandAngle = angle
        invalidate()
    }

    private fun setMinuteHandAngle(angle: Float) {
        minuteHandAngle = angle
        invalidate()
    }

    private fun setSecondHandAngle(angle: Float) {
        secondHandAngle = angle
        invalidate()
    }

    fun setSecondHandColor(color: Int) {
        secondHandPaint.color = color
        invalidate()
    }

   /* fun startHourHandAnimation() {
        val rotateAnimation = ObjectAnimator.ofFloat(this, "hourHandAngle", 0f, 360f)
        rotateAnimation.duration = 43200000 // 12 hours
        rotateAnimation.repeatCount = ObjectAnimator.INFINITE
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.start()
    }

    fun startMinuteHandAnimation() {
        val rotateAnimation = ObjectAnimator.ofFloat(this, "minuteHandAngle", 0f, 360f)
        rotateAnimation.duration = 3600000 // 60 minutes
        rotateAnimation.repeatCount = ObjectAnimator.INFINITE
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.start()
    }

    fun startSecondHandAnimation() {
        val rotateAnimation = ObjectAnimator.ofFloat(this, "secondHandAngle", 0f, 360f)
        rotateAnimation.duration = 5000 // 60 seconds
        rotateAnimation.repeatCount = ObjectAnimator.INFINITE
        rotateAnimation.interpolator = LinearInterpolator()

        rotateAnimation.addUpdateListener { animation ->
            val currentSecondAngle = animation.animatedValue as Float
            val minuteAngle = calculateMinuteAngle(currentSecondAngle)
            val hourAngle = calculateHourAngle(currentSecondAngle, minuteAngle)
            setMinuteHandAngle(minuteAngle)
            setHourHandAngle(hourAngle)
        }

        rotateAnimation.start()
    }*/

   /* private fun calculateMinuteAngle(secondAngle: Float): Float {
        return (secondAngle / 60.0).toFloat()
    }

    private fun calculateHourAngle(secondAngle: Float, minuteAngle: Float): Float {
        val totalMinutes = secondAngle / 60.0 + minuteAngle
        return (totalMinutes / 60.0 * 30.0).toFloat()
    }*/

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putLong("time", calendar.timeInMillis)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var superState = state
        if (superState is Bundle) {
            val time = superState.getLong("time")
            calendar.timeInMillis = time
            superState = superState.getParcelable("superState")!!
        }
        super.onRestoreInstanceState(superState)
    }

    private val handler = Handler(Looper.getMainLooper())

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateClock()
            handler.postDelayed(this, 1000)
        }
    }

    private fun updateClock() {
        val currentTime = Calendar.getInstance()
        val second = currentTime.get(Calendar.SECOND)
        val secondAngle = (second - 15) * 6f
        setSecondHandAngle(secondAngle)

        val minute = currentTime.get(Calendar.MINUTE)
        val minuteAngle = (minute + second / 60.0 - 36) * 6f
        setMinuteHandAngle(minuteAngle.toFloat())

        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val hourAngle = (hour % 12) * 30f + minute / 1.5f
        setHourHandAngle(hourAngle)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateClock()
        handler.postDelayed(updateTimeRunnable, 1000)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(updateTimeRunnable)
    }


}