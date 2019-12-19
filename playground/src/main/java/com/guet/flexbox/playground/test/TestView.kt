package com.guet.flexbox.playground.test

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import com.guet.flexbox.playground.R

class TestView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bitmap = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.ic_launcher))
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
        }
    }

    override fun onDraw(canvas: Canvas) {



        val sc = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        bitmap.bounds = canvas.clipBounds
        bitmap.draw(canvas)

        paint.reset()
        path.reset()
        rectF.set(canvas.clipBounds)
        path.apply {
            reset()
            addRect(rectF, Path.Direction.CW)
            addRoundRect(rectF, 30f, 30f, Path.Direction.CCW)
            close()
        }
        paint.xfermode = xfermode
        canvas.drawPath(path, paint)


        canvas.restoreToCount(sc)



    }
}