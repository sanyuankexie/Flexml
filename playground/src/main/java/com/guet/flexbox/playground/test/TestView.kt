package com.guet.flexbox.playground.test

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.guet.flexbox.playground.R

@TargetApi(Build.VERSION_CODES.P)
class TestView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val bitmap = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.ic_photo2))
    private val paint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
    }
    private var index = 0
    private val rectF = RectF()
    private var xfermode = PorterDuffXfermode(enumValues<PorterDuff.Mode>()[0])
    private val path = Path()

    init {
        setOnClickListener {
            if (++index >= enumValues<PorterDuff.Mode>().size) {
                index = 0
            }
            println(enumValues<PorterDuff.Mode>()[index].name)
            xfermode = PorterDuffXfermode(enumValues<PorterDuff.Mode>()[index])
            invalidate()
            requestLayout()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.RED)
        canvas.save()
        canvas.save()
        bitmap.bounds = Rect(0, 0, width, height)
        bitmap.draw(canvas)
        canvas.restore()
        paint.reset()
        path.reset()
        rectF.set(Rect(0, 0, width, height))
        path.apply {
            reset()
            addRect(rectF, Path.Direction.CW)
            addRoundRect(rectF, 100f, 100f, Path.Direction.CCW)
            close()
        }
        paint.xfermode = xfermode
        canvas.drawPath(path, paint)
        canvas.restore()

    }
}