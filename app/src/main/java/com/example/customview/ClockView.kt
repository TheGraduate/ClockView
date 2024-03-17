package com.example.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import java.util.Calendar
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import androidx.core.content.ContextCompat.getColor

private const val UPDATE_PERIOD_FOR_CLOCK = 60

private var isRunning = true
private var isRomanNumeralDisplay = false

class ClockCustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var clockRadius = 0.0f
    
    private var coorCenterByX = 0.0f
    private var coorCenterByY = 0.0f
    
    private val blackColorSample = getColor(context, R.color.black)

    private var colorOfClockFace = getColor(context, R.color.grey)
    private var colorOfNumbers = blackColorSample
    private var colorOfClockFrame = blackColorSample
    private var colorOfSpots = blackColorSample
    private var colorOfHourHand = blackColorSample
    private var colorOfMinuteHand = blackColorSample
    private var colorOfSecondHand = blackColorSample

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textScaleX = 0.8f
        letterSpacing = -0.001f
    }

    private val paintHourHand = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
        strokeCap = Paint.Cap.ROUND
    }

    private val paintMinuteHand = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val paintSecondHand = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val paintClockFrame = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        clockRadius = min(width, height) / 2f - (min(width, height) / 25)
        coorCenterByX = width / 2f
        coorCenterByY = height / 2f
    }

    private fun calculationOfcoorXYpoints(pos: Int, radius: Float, centerX: Float, centerY: Float): PointF {
        val angle = (pos * (Math.PI / 30)).toFloat()
        val x = radius * cos(angle) + centerX
        val y = radius * sin(angle) + centerY
        return PointF(x, y)
    }

    private fun calculationOfcoorXYlabels(hour: Int, radius: Float, centerX: Float, centerY: Float, paint: Paint): PointF {
        val angle = (-Math.PI / 2 + hour * (Math.PI / 6)).toFloat()
        val textBaselineToCenter = (paint.descent() + paint.ascent()) / 2
        val x = radius * cos(angle) + centerX
        val y = radius * sin(angle) + centerY - textBaselineToCenter
        return PointF(x, y)
    }

    private fun drawClockFace(canvas: Canvas) {
        paint.color = colorOfClockFace
        paint.style = Paint.Style.FILL
        canvas.drawCircle(coorCenterByX, coorCenterByY, clockRadius, paint)
    }

    private fun drawClockFrame(canvas: Canvas) {
        paintClockFrame.color = colorOfClockFrame
        paintClockFrame.style = Paint.Style.STROKE
        paintClockFrame.strokeWidth = clockRadius / 12
        val boundaryRadius = clockRadius - paintClockFrame.strokeWidth / 2
        val minOfHeightWidth = min(width, height)
        paintClockFrame.setShadowLayer(minOfHeightWidth / 2f / 20, 0.0f, 0.0f, Color.BLACK)
        canvas.drawCircle(coorCenterByX, coorCenterByY, boundaryRadius, paintClockFrame)
        paintClockFrame.strokeWidth = 0f
    }

    private fun drawSpotsOfClock(canvas: Canvas) {
        paint.color = colorOfSpots
        paint.style = Paint.Style.FILL
        val drowSpotsLineRadius = clockRadius * 5 / 6
        for (i in 0 until 60) {
            val position = calculationOfcoorXYpoints(i, drowSpotsLineRadius, coorCenterByX, coorCenterByY)
            val dotRadius = if (i % 5 == 0) clockRadius / 96 else clockRadius / 128
            canvas.drawCircle(position.x, position.y, dotRadius, paint)
        }
    }

    private fun drawNumberOnClockFace(canvas: Canvas) {
        paint.textSize = clockRadius * 2 / 7
        paint.strokeWidth = 0f
        paint.color = colorOfNumbers

        val labelsDrawLineRadius = clockRadius * 11 / 16
        val numerals = if (isRomanNumeralDisplay) getRomanNumerals() else getArabicNumerals()

        for (i in 1..12) {
            val position = calculationOfcoorXYlabels(i, labelsDrawLineRadius, coorCenterByX, coorCenterByY, paint)
            val label = numerals[i - 1] // Получаем цифру из массива
            canvas.drawText(label, position.x, position.y, paint)
        }
    }

    private fun getRomanNumerals(): Array<String> {
        return arrayOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII")
    }

    private fun getArabicNumerals(): Array<String> {
        return arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    }

    private fun drawClockHands(canvas: Canvas) {
        val currentTimeMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeMillis
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        drawHourHand(canvas, hour + minute / 60f)
        drawMinuteHand(canvas, minute + second / 60f)
        drawSecondHand(canvas, second)
    }

    private fun drawClockFrameWithShadow(canvas: Canvas) {
        paintClockFrame.color = colorOfClockFrame
        paintClockFrame.style = Paint.Style.STROKE
        paintClockFrame.strokeWidth = clockRadius / 12

        paintClockFrame.setShadowLayer(10f, 0f, 0f, Color.GRAY)
        val boundaryRadius = clockRadius - paintClockFrame.strokeWidth / 2
        val minOfHeightWidth = min(width, height)
        canvas.drawCircle(coorCenterByX, coorCenterByY, boundaryRadius, paintClockFrame)
        paintClockFrame.strokeWidth = 0f

        paintClockFrame.clearShadowLayer()
    }

    private fun drawHourHand(canvas: Canvas, hourWithMinutes: Float) {
        paintHourHand.color = colorOfHourHand
        paintHourHand.strokeWidth = clockRadius / 15
        val minOfHeightWidth = min(width, height)
        paintHourHand.setShadowLayer(
            minOfHeightWidth / 2f / 20,
            min(width, height) / 150.0f,
            min(width, height) / 150.0f,
            Color.BLACK
        )
        val angle = (Math.PI * hourWithMinutes / 6 + (-Math.PI / 2)).toFloat()
        canvas.drawLine(
            coorCenterByX - cos(angle) * clockRadius * 3 / 14,
            coorCenterByY - sin(angle) * clockRadius * 3 / 14,
            coorCenterByX + cos(angle) * clockRadius * 7 / 14,
            coorCenterByY + sin(angle) * clockRadius * 7 / 14,
            paintHourHand
        )
    }

    private fun drawMinuteHand(canvas: Canvas, minute: Float) {
        paintMinuteHand.color = colorOfMinuteHand
        paintMinuteHand.strokeWidth = clockRadius / 40
        val minOfHeightWidth = min(width, height)
        paintMinuteHand.setShadowLayer(
            minOfHeightWidth / 2f / 20,
            min(width, height) / 80.0f,
            min(width, height) / 60.0f,
            Color.BLACK
        )
        val angle = (Math.PI * minute / 30 + (-Math.PI / 2)).toFloat()
        canvas.drawLine(
            coorCenterByX - cos(angle) * clockRadius * 2 / 7,
            coorCenterByY - sin(angle) * clockRadius * 2 / 7,
            coorCenterByX + cos(angle) * clockRadius * 5 / 7,
            coorCenterByY + sin(angle) * clockRadius * 5 / 7,
            paintMinuteHand
        )
    }

    private fun drawSecondHand(canvas: Canvas, second: Int) {
        paintSecondHand.color = colorOfSecondHand
        val angle = (Math.PI * second / 30 + (-Math.PI / 2)).toFloat()
        val minOfHeightWidth = min(width, height)
        paintSecondHand.setShadowLayer(
            minOfHeightWidth / 2f / 25,
            min(width, height) / 25.0f,
            min(width, height) / 25.0f,
            Color.BLACK
        )
        paintSecondHand.strokeWidth = clockRadius / 80
        canvas.drawLine(
            coorCenterByX - cos(angle) * clockRadius * 1 / 14,
            coorCenterByY - sin(angle) * clockRadius * 1 / 14,
            coorCenterByX + cos(angle) * clockRadius * 5 / 7,
            coorCenterByY + sin(angle) * clockRadius * 5 / 7,
            paintSecondHand
        )
        paintSecondHand.strokeWidth = clockRadius / 50
        canvas.drawLine(
            coorCenterByX - cos(angle) * clockRadius * 2 / 7,
            coorCenterByY - sin(angle) * clockRadius * 2 / 7,
            coorCenterByX - cos(angle) * clockRadius * 1 / 14,
            coorCenterByY - sin(angle) * clockRadius * 1 / 14,
            paintSecondHand
        )
    }

    private fun drawCircle(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        paint.color = paintSecondHand.color
        canvas.drawCircle(coorCenterByX, coorCenterByY, clockRadius / 35, paint)
    }

    fun setHourHandColor(color: Int) {
        colorOfHourHand = color
        invalidate()
    }

    fun setMinuteHandColor(color: Int) {
        colorOfMinuteHand = color
        invalidate()
    }

    fun setSecondHandColor(color: Int) {
        colorOfSecondHand = color
        invalidate()
    }

    fun setClockFaceColor(color: Int) {
        colorOfClockFace = color
        invalidate()
    }

    fun stopStartClock() {
        if (!isRunning) {
            postInvalidateDelayed(UPDATE_PERIOD_FOR_CLOCK.toLong())
        }
        isRunning = !isRunning
    }

    fun toggleDisplayMode() {
        isRomanNumeralDisplay = !isRomanNumeralDisplay
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawClockFace(canvas)
        drawNumberOnClockFace(canvas)

        drawClockFrame(canvas)
        drawClockFrameWithShadow(canvas)

        drawSpotsOfClock(canvas)

        drawClockHands(canvas)
        drawCircle(canvas)

        if (isRunning) {
            postInvalidateDelayed(UPDATE_PERIOD_FOR_CLOCK.toLong())
        }

    }
}