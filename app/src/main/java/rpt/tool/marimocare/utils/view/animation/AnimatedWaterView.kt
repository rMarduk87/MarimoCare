package rpt.tool.marimocare.utils.view.animation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.sin

class AnimatedWaterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val path = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GREEN
    }

    private var waveShift = 0f
    private val amplitude = 12f
    private var waterPercentage = 0.5f

    private var animator: ValueAnimator? = null

    private val bubbles = Array(5) { Bubble() }
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        alpha = 90
    }

    fun setWaterColor(color: Int) {
        paint.color = color
        invalidate()
    }

    fun setWaterPercentage(percentage: Float) {
        waterPercentage = percentage.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animator = ValueAnimator.ofFloat(0f, (2 * Math.PI).toFloat()).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                waveShift = it.animatedValue as Float
                updateBubbles()
                invalidate()
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        animator = null
        super.onDetachedFromWindow()
    }

    private fun updateBubbles() {
        val waterTop = height - (height * waterPercentage)
        bubbles.forEach {
            if (!it.initialized && width > 0 && height > 0) {
                it.x = (Math.random() * width).toFloat()
                it.y = (Math.random() * height).toFloat()
                it.initialized = true
            }
            it.y -= it.speed

            if (it.y < waterTop - amplitude) {
                it.y = height.toFloat() + it.radius
                it.x = (Math.random() * width).toFloat()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0 || waterPercentage == 0f) return

        val waterHeight = height * waterPercentage
        val baseLine = height - waterHeight

        path.reset()
        path.moveTo(0f, height.toFloat())
        path.lineTo(0f, baseLine)

        val waveFrequency = 1.5 * Math.PI / width
        for (x in 0..width step 5) {
            val y = baseLine + sin(x * waveFrequency + waveShift) * amplitude
            path.lineTo(x.toFloat(), y.toFloat())
        }

        path.lineTo(width.toFloat(), height.toFloat())
        path.close()

        canvas.drawPath(path, paint)

        bubbles.forEach {
            if (it.initialized && it.y > baseLine) {
                canvas.drawCircle(it.x, it.y, it.radius, bubblePaint)
            }
        }
    }

    private inner class Bubble {
        var x = 0f
        var y = 0f
        var radius = (Math.random() * 5 + 3).toFloat()
        var speed = (Math.random() * 2 + 1.5).toFloat()
        var initialized = false
    }
}