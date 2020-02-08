package com.guet.flexbox.playground.widget

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.guet.flexbox.AppExecutors
import com.guet.flexbox.playground.R
import kotlin.math.max
import kotlin.math.min

class BlurLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var background: Bitmap? = null
    private var shader: BitmapShader? = null
    private var snapshotCanvas: Canvas? = null
    //记录了View的状态信息
    private var flags: Int = 0
    @Volatile
    var cornerRadius: Float = dp2px(10f).toFloat()
        set(value) {
            field = max(0f, value)
        }
    @Volatile
    var sampling: Float = 4f
        set(value) {
            field = max(1f, value)
        }
    @Volatile
    var blurRadius: Float = 10f
        set(value) {
            field = max(25f, min(0f, value))
        }
    private val paint = Paint()
    private val rectF = RectF()
    private val rect = Rect()
    private val toBitmap = Canvas()
    private val snapshot = Picture()
    private val application = context
            .applicationContext as Application
    private val onPreDrawListener = OnPreDrawListener {
        scheduleWork()
        return@OnPreDrawListener true
    }

    init {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.BlurLayout, defStyleAttr, 0
        )
        cornerRadius = a.getDimension(R.styleable.BlurLayout_cornerRadius, dp2px(10f).toFloat())
        sampling = a.getFloat(R.styleable.BlurLayout_sampling, 4f)
        blurRadius = a.getDimension(R.styleable.BlurLayout_blurRadius, 10f)
        a.recycle()
        setWillNotDraw(false)
        background = null
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            attach()
        } else {
            detach()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attach()
    }

    override fun onDetachedFromWindow() {
        detach()
        super.onDetachedFromWindow()
    }

    private fun attach() {
        detach()
        viewTreeObserver.addOnPreDrawListener(onPreDrawListener)
    }

    private fun detach() {
        viewTreeObserver.removeOnPreDrawListener(onPreDrawListener)
    }

    override fun dispatchDraw(canvas: Canvas) {
        // PreDraw时，不画自己
        if (canvas !== snapshotCanvas) {
            super.dispatchDraw(canvas)
        }
    }

    override fun onDraw(canvas: Canvas) {
        paint.reset()
        val myBackground = background ?: return
        val cornerRadius = this.cornerRadius
        val mySimpling = this.sampling
        // PreDraw时，不画自己
        if (canvas !== snapshotCanvas) {
            if (cornerRadius > 0) {
                val myShader = getShader(myBackground)
                canvas.save()
                // 经过渲染的Bitmap由于缩放的关系
                // 可能会比View小，所以要做特殊处理，把它放大回去
                canvas.scale(
                        mySimpling,
                        mySimpling
                )
                canvas.drawRoundRect(
                        rectF.apply {
                            set(
                                    0f,
                                    0f,
                                    width.toFloat() / mySimpling,
                                    height.toFloat() / mySimpling
                            )
                        },
                        cornerRadius / mySimpling,
                        cornerRadius / mySimpling,
                        paint.apply {
                            isAntiAlias = true
                            shader = myShader
                        }
                )
                canvas.restore()
            } else {
                canvas.drawBitmap(
                        myBackground,
                        null,
                        rect.apply {
                            set(
                                    0,
                                    0,
                                    width,
                                    height
                            )
                        },
                        null
                )
            }
        }
    }

    // 如果shader没有创建，那就创建并缓存
    private fun getShader(bitmap: Bitmap): BitmapShader {
        val myShader = shader ?: BitmapShader(
                bitmap,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
        )
        shader = myShader
        return myShader
    }

    // 调度任务
    private fun scheduleWork() {
        // 如果是子View要求的绘制，我们忽略它
        if ((flags and skipChildInvalidate) != 0) {
            //set to false
            flags = flags and (skipChildInvalidate.inv())
            return
        }
        // 如果正在工作
        if ((flags and inWorking) != 0) {
            // 并且是在等待绘制
            // 那么一个任务序列彻底完成
            if ((flags and inWaitDraw) != 0) {
                //set to false
                flags = flags and (inWorking.inv())
                //set to false
                flags = flags and (inWaitDraw.inv())
                // 但是如果还有堆积的任务
                // 那就要开始新的任务
                if ((flags and hasPendingWork) != 0) {
                    //set to false
                    flags = flags and (hasPendingWork.inv())
                    startWork()
                }
            } else {
                // 如果不是在等待绘制，那么记录有堆积的任务
                // set to true
                flags = flags or hasPendingWork
            }
        } else {
            // 空闲状态直接开始新任务
            startWork()

        }
    }

    // 开始一个新的渲染任务
    private fun startWork() {
        // 获取View显示区的绝对位置
        getGlobalVisibleRect(rect)
        val width = rect.width()
        val height = rect.height()
        if (width * height == 0) {
            return
        } else {
            //set to true
            flags = flags or inWorking
        }
        // 使用Picture来记录绘制内容
        // 因为它只记录绘制的操作，所以这比直接用Canvas要更快
        // 不需要绘制整个屏幕，只需要绘制View底下那一层就可以了
        val canvas = snapshot.beginRecording(width, height)
        // 转换canvas来到View的绝对位置
        canvas.translate(
                -rect.left.toFloat(),
                -rect.top.toFloat()
        )
        // 设置snapshotCanvas用来识别，防止画到自己
        snapshotCanvas = canvas
        val root = parent as? ViewGroup ?: rootView
        root.draw(canvas)
        snapshotCanvas = null
        // 结束录制
        snapshot.endRecording()
        val mySampling = this.sampling
        val myBlurRadius = this.blurRadius
        AppExecutors.threadPool.execute {
            // 获取新的Bitmap，但是不用这么大，越大性能越差
            // 所以默认的sampling=4，也就是只有原图1/4的像素量
            val scaledWidth = (width / mySampling).toInt()
            val scaledHeight = (height / mySampling).toInt()
            val bitmap = Glide.get(context).bitmapPool[
                    scaledWidth,
                    scaledHeight,
                    Bitmap.Config.ARGB_8888
            ]
            // 在后台慢慢用软件画图来画，防止主线程卡住
            // 因为软件绘制实在是太慢了
            toBitmap.setBitmap(bitmap)
            toBitmap.save()
            // 放大画布来绘制
            toBitmap.scale(1f / mySampling, 1f / mySampling)
            toBitmap.drawPicture(snapshot)
            toBitmap.restore()
            toBitmap.setBitmap(null)
            // 使用renderscript处理Bitmap
            // 在Android平台上这是最快的做法
            processBitmap(myBlurRadius, bitmap)
            // 完成
            finishWork(bitmap)
        }
    }

    // 使用renderscript处理Bitmap
    // 在Android平台上这是最快的做法
    private fun processBitmap(blurRadius: Float, bitmap: Bitmap) {
        val radius = max(25f, min(0f, blurRadius))
        var rs: RenderScript? = null
        var input: Allocation? = null
        var output: Allocation? = null
        var blur: ScriptIntrinsicBlur? = null
        try {
            rs = RenderScript.create(application)
            rs.messageHandler = RenderScript.RSMessageHandler()
            input = Allocation.createFromBitmap(
                    rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT
            )
            output = Allocation.createTyped(rs, input.type)
            blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

            blur.setInput(input)
            blur.setRadius(radius)
            blur.forEach(output)
            output.copyTo(bitmap)
        } finally {
            rs?.destroy()
            input?.destroy()
            output?.destroy()
            blur?.destroy()
        }
    }

    // 使任务完成，并发送渲染的bitmap回到主线程
    private fun finishWork(bitmap: Bitmap) {
        post {
            val oldBackground = background
            shader = null
            background = bitmap
            //回收之前的Bitmap
            if (oldBackground != null) {
                Glide.get(context).bitmapPool.put(oldBackground)
            }
            // 将等待绘制true
            //set to true
            flags = flags or inWaitDraw
            // 通知ViewRootImpl重绘此View
            invalidate()
        }
    }

    // 忽略所有子View的失效
    override fun onDescendantInvalidated(child: View, target: View) {
        super.onDescendantInvalidated(child, target)
        // 将忽略所有子View的失效的flag设为true
        flags = flags or skipChildInvalidate
    }

    companion object {
        // 是否在后台进行工作
        private const val inWorking = 1 shl 0
        // 工作完成了，但是渲染出来的仍在等待首次绘制
        private const val inWaitDraw = 1 shl 1
        // 在后台工作的时候，BlurLayout不接受新任务
        // 但是如果父View发生改变，在任务完成后会开始新的任务
        private const val hasPendingWork = 1 shl 2
        // 忽略所有子View的失效
        private const val skipChildInvalidate = 1 shl 3

        fun dp2px(dpValue: Float): Int {
            val scale = Resources.getSystem().displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }
}