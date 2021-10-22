package gleb.apps.pyhie.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GraphView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val dataSet = mutableListOf<DataPoint>()

    private fun dataLinePaint(strokeSize: Float) = Paint().apply {
        color = Color.BLUE
        strokeWidth = strokeSize
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        scaleY = 0f
        animate().scaleY(1f)
        animate().duration = 1000
        animate().start()

    }

    private val axisLinePaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 4f
    }

    private val whiteBackground = Paint().apply {
        color = Color.WHITE
    }

    private fun textPaint(tSize: Float) = Paint().apply {
        textSize = tSize
        color = Color.GRAY
        isFakeBoldText = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        dataSet.forEachIndexed { index, currentDataPoint ->
            val realX = currentDataPoint.xVal.toRealX()
            val realY = currentDataPoint.yVal.toRealY()
            // data bars ->
            canvas.drawLine(
                realX,
                height * 0.85f,
                realX,
                realY.toFloat(),
                dataLinePaint(width * 0.05f)
            )
        }
        // 20% Line ->
        canvas.drawLine(
            width * 0.1f,
            height * 0.9f,
            width.toFloat(),
            height * 0.9f,
            axisLinePaint
        )
        // 40% Line ->
        canvas.drawLine(
            width * 0.1f,
            height * 0.74f,
            width.toFloat(),
            height * 0.74f,
            axisLinePaint
        )
        // 60% Line ->
        canvas.drawLine(
            width * 0.1f,
            height * 0.55f,
            width.toFloat(),
            height * 0.55f,
            axisLinePaint
        )
        // 80% Line ->
        canvas.drawLine(
            width * 0.1f,
            height * 0.40f,
            width.toFloat(),
            height * 0.40f,
            axisLinePaint
        )
        // 80% Line ->
        canvas.drawLine(
            width * 0.1f,
            height * 0.23f,
            width.toFloat(),
            height * 0.23f,
            axisLinePaint
        )
        // 100% Line ->
        canvas.drawLine(
            width * 0.1f,
            height * 0.05f,
            width.toFloat(),
            height * 0.05f,
            axisLinePaint
        )

        val textSize = width * 0.035f
        val xAxisTextMargin = 0.97f * height

        canvas.drawText("0%", width * 0.03f, 0.92f * height, textPaint(textSize))
        canvas.drawText("20%", width * 0.02f, 0.76f * height, textPaint(textSize))
        canvas.drawText("40%", width * 0.02f, 0.59f * height, textPaint(textSize))
        canvas.drawText("60%", width * 0.02f, 0.42f * height, textPaint(textSize))
        canvas.drawText("80%", width * 0.02f, 0.25f * height, textPaint(textSize))
        canvas.drawText("100%", width * 0.0f, 0.08f * height, textPaint(textSize))

        canvas.drawText("MON", width * 0.1f, xAxisTextMargin, textPaint(textSize))
        canvas.drawText("TUE", width * 0.23f, xAxisTextMargin, textPaint(textSize))
        canvas.drawText("WED", width * 0.37f, xAxisTextMargin, textPaint(textSize))
        canvas.drawText("THU", width * 0.50f, xAxisTextMargin, textPaint(textSize))
        canvas.drawText("FRI", width * 0.64f, xAxisTextMargin, textPaint(textSize))
        canvas.drawText("SAT", width * 0.78f, xAxisTextMargin, textPaint(textSize))
        canvas.drawText("SUN", width * 0.91f, xAxisTextMargin, textPaint(textSize))

    }

    fun setData(newDataSet: List<DataPoint>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
        invalidate()
    }

    private fun Int.toRealX() = (toFloat() / 7) * (width * 0.94f)
    private fun Double.toRealY() =
        if (toFloat().compareTo(1f) == 0) toFloat() * (height * 0.85) else (toFloat() * (height * 0.85)) + (height * 0.1)

}

data class DataPoint(
    val xVal: Int,
    val yVal: Double
)