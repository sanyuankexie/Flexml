package com.guet.flexbox

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.TextureView
import android.view.ViewGroup
import com.facebook.litho.*
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicInteger

class AsyncLithoView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val worker : DefaultLithoHandler
    private val textureView:TextureView
    private val innerLithoView : InnerLithoView
    private val componentContext: ComponentContext
        get() = innerLithoView.componentContext

    init {
        val futureTask = FutureTask<SurfaceTexture> {
            SurfaceTexture(0, true)
        }
        worker = DefaultLithoHandler()
        worker.post(futureTask)
        innerLithoView = InnerLithoView(context)
        innerLithoView.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )
        val componentTree = ComponentTree.create(innerLithoView.componentContext)
                .layoutThreadHandler(worker)
                .build()
        innerLithoView.componentTree = componentTree
        textureView = TextureView(context)
        textureView.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )
        textureView.surfaceTexture = futureTask.get()
        addView(textureView)
        addView(innerLithoView)
    }

    fun setComponentAsync(c: Component) {
        innerLithoView.setComponentAsync(c)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        innerLithoView.measure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(innerLithoView.measuredWidth, innerLithoView.measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        innerLithoView.layout(l, t, r, b)
        textureView.layout(l, t, r, b)
    }

    internal inner class InnerLithoView(context: Context) : LithoView(context) {

        private val drawDirty = Rect()

        private fun superDispatchDraw(canvas: Canvas) {
            super.dispatchDraw(canvas)
        }

        override fun dispatchDraw(unused: Canvas) {
            synchronized(drawDirty) {
                worker.post {
                    synchronized(drawDirty) {
                        val canvas = textureView.lockCanvas()
                        if (canvas != null) {
                            superDispatchDraw(canvas)
                            textureView.unlockCanvasAndPost(canvas)
                        }
                    }
                }
            }
        }

        override fun invalidateDrawable(drawable: Drawable) {
            synchronized(drawDirty) {
                val dirty = drawable.dirtyBounds
                drawDirty.set(
                        dirty.left + scrollX,
                        dirty.top + scrollY,
                        dirty.right + scrollX,
                        dirty.bottom + scrollY
                )
                worker.post {
                    synchronized(drawDirty) {
                        val canvas = textureView.lockCanvas(drawDirty)
                        if (canvas != null) {
                            superDispatchDraw(canvas)
                            textureView.unlockCanvasAndPost(canvas)
                        }
                    }
                }
            }
        }

    }

    internal class DefaultLithoHandler : Handler({
        HandlerThread(AsyncLithoView::class.java.simpleName + "_" + count.getAndIncrement())
                .apply { start() }
                .looper
    }()), LithoHandler {

        override fun isTracing(): Boolean {
            return false
        }

        override fun post(runnable: Runnable, tag: String) {
            post(runnable)
        }

        override fun remove(runnable: Runnable) {
            removeCallbacks(runnable)
        }

        internal companion object {
            private val count = AtomicInteger()
        }
    }

}