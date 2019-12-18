package com.guet.flexbox.playground.widget

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.Px
import androidx.core.math.MathUtils
import com.guet.flexbox.playground.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class PullToRefreshLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var inRefresh: Boolean = false

    private var inLoadMore: Boolean = false

    private var lastY: Float = 0f

    //记录第一次点击的位置
    private var touchY: Float = 0f

    private val headerRes: Int

    private val footerRes: Int

    private val touchSlope: Int

    private val header: View
        get() {
            return if (super.getChildCount() == 3) {
                super.getChildAt(1)
            } else {
                super.getChildAt(0)
            }
        }

    private val footer: View
        get() {
            return if (super.getChildCount() == 3) {
                super.getChildAt(2)
            } else {
                super.getChildAt(0)
            }
        }

    val isRefreshOrLoad: Boolean
        get() = inRefresh || inLoadMore

    var onRefreshListener: OnRefreshListener? = null

    var canLoadMore: Boolean = true

    var canRefresh: Boolean = true

    @Px
    var footerLength: Int = 0
        set(value) {
            field = min(value, defaultLength)
        }

    @Px
    var headerLength: Int = 0
        set(value) {
            field = min(value, defaultLength)
        }

    var headerAdapter: PullViewAdapter? = DefaultPullView.HeaderAdapter

    var footerAdapter: PullViewAdapter? = DefaultPullView.FooterAdapter

    var content: View?
        get() {
            return if (super.getChildCount() == 3) {
                super.getChildAt(0)
            } else {
                null
            }
        }
        set(value) {
            if (super.getChildCount() == 3) {
                super.removeViewAt(0)
            }
            super.addView(value, 0)
        }

    init {
        val value = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout)
        touchSlope = ViewConfiguration.get(context).scaledTouchSlop
        headerRes = value.getResourceId(R.styleable.PullToRefreshLayout_header, 0)
        footerRes = value.getResourceId(R.styleable.PullToRefreshLayout_footer, 0)
        headerLength = value.getDimensionPixelSize(
                R.styleable.PullToRefreshLayout_header_length,
                defaultLength
        )
        footerLength = value.getDimensionPixelSize(
                R.styleable.PullToRefreshLayout_footer_length,
                defaultLength
        )
        value.recycle()
    }

    fun finish() {
        if (inRefresh) {
            createTranslationY(
                    header,
                    headerAdapter,
                    headerLength,
                    headerLength,
                    0
            ) {
                inRefresh = false
                headerAdapter?.onStateChanged(header, PullState.HIDE)
            }
        }
        if (inLoadMore) {
            createTranslationY(
                    footer,
                    footerAdapter,
                    footerLength,
                    -footerLength,
                    0
            ) {
                inLoadMore = false
                footerAdapter?.onStateChanged(footer, PullState.HIDE)
            }
        }
    }

    fun refresh() {
        createTranslationY(
                header,
                headerAdapter,
                headerLength,
                0,
                headerLength
        ) {
            inRefresh = true
            onRefreshListener?.onRefresh(this)
            headerAdapter?.onStateChanged(header, PullState.LOAD)
        }
    }

    fun loadMore() {
        createTranslationY(
                footer,
                footerAdapter,
                footerLength,
                0,
                -footerLength
        ) {
            inLoadMore = true
            onRefreshListener?.onLoadMore(this)
            footerAdapter?.onStateChanged(footer, PullState.LOAD)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val inflater = LayoutInflater.from(context)
        addView(if (headerRes != 0) {
            inflater.inflate(headerRes, null)
        } else {
            DefaultPullView(context)
        }.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0)
        })
        addView(if (footerRes != 0) {
            inflater.inflate(footerRes, null)
        } else {
            DefaultPullView(context)
        }.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0)
                    .apply {
                        gravity = Gravity.BOTTOM
                    }
        })
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!canLoadMore && !canRefresh) {
            return super.onInterceptTouchEvent(ev)
        } else {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchY = ev.y
                    lastY = ev.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val currentY = ev.y
                    val dy = currentY - touchY
                    if (canRefresh && dy > touchSlope && !canChildScrollUp) {
                        headerAdapter?.onStateChanged(header, PullState.SHOW)
                        return true
                    }
                    if (canLoadMore && dy < -touchSlope && !canChildScrollDown) {
                        footerAdapter?.onStateChanged(footer, PullState.SHOW)
                        return true
                    }
                    lastY = currentY
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private val canChildScrollUp: Boolean
        get() {
            return content?.canScrollVertically(-1) ?: false
        }

    private val canChildScrollDown: Boolean
        get() {
            return content?.canScrollVertically(1) ?: false
        }

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (inLoadMore || inRefresh) {
            return true
        }
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val currentY = event.y
                //速度减慢三倍
                var height = (currentY - touchY) / 3.0f
                if (height > 0 && canRefresh) {
                    height = max(min(headerLength * 2.0f, height), 0f)
                    header.layoutParams.height = height.toInt()
                    content?.translationY = height
                    requestLayout()
                    headerAdapter?.onProgress(
                            header,
                            height / headerLength,
                            lastY - currentY > 0
                    )
                }
                if (height < 0 && canLoadMore) {
                    height = abs(height)
                    height = MathUtils.clamp(height, 0f, footerLength * 2.0f)
                    footer.layoutParams.height = height.toInt()
                    content?.translationY = -height
                    requestLayout()
                    footerAdapter?.onProgress(
                            footer,
                            height / footerLength,
                            lastY - currentY > 0
                    )
                }
                lastY = currentY
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val currentY = event.y
                val dy = (currentY - touchY) / 3.0f
                if (dy > 0 && canRefresh) {
                    if (dy > headerLength) {
                        createTranslationY(
                                header,
                                headerAdapter,
                                headerLength,
                                min(headerLength * 2, dy.toInt()),
                                headerLength
                        ) {
                            inRefresh = true
                            onRefreshListener?.onRefresh(this)
                            headerAdapter?.onStateChanged(header, PullState.LOAD)
                        }
                    } else {
                        createTranslationY(
                                header,
                                headerAdapter,
                                headerLength,
                                dy.toInt(),
                                0
                        ) {
                            inRefresh = false
                            headerAdapter?.onStateChanged(header, PullState.HIDE)
                        }
                    }
                }
                if (dy < 0 && canLoadMore) {
                    if (abs(dy) >= footerLength) {
                        createTranslationY(
                                footer,
                                footerAdapter,
                                footerLength,
                                max(dy.toInt(), -footerLength * 2),
                                -footerLength
                        ) {
                            inLoadMore = true
                            onRefreshListener?.onLoadMore(this)
                            footerAdapter?.onStateChanged(footer, PullState.LOAD)
                        }
                    } else {
                        createTranslationY(
                                footer,
                                footerAdapter,
                                footerLength,
                                dy.toInt(),
                                0
                        ) {
                            inLoadMore = false
                            footerAdapter?.onStateChanged(footer, PullState.HIDE)
                        }
                    }
                }
                lastY = currentY
            }
        }
        return super.onTouchEvent(event)
    }

    private inline fun createTranslationY(
            v: View,
            adapter: PullViewAdapter?,
            length: Int,
            start: Int,
            end: Int,
            crossinline callback: () -> Unit
    ) {
        val isUp = start > end
        ValueAnimator.ofInt(start, end).apply {
            duration = animateDuration
            interpolator = DecelerateInterpolator()
            addUpdateListener { va ->
                content?.let { c ->
                    val value = va.animatedValue as Int
                    v.layoutParams.height = abs(value)
                    c.translationY = value.toFloat()
                    adapter?.onProgress(v, abs(value) / length * 2.0f, isUp)
                    this@PullToRefreshLayout.requestLayout()
                    if (value == end) {
                        callback()
                    }
                }
            }
        }.start()
    }

    enum class PullState {
        /**
         * 不显示
         **/
        HIDE,
        /**
         * 开始
         **/
        SHOW,
        /**
         * 开始刷新
         **/
        LOAD,
    }

    interface PullViewAdapter {

        fun onProgress(v: View, progress: Float, directionIsUp: Boolean)

        fun onStateChanged(v: View, pullState: PullState)
    }

    abstract class OnRefreshListener {

        open fun onRefresh(v: PullToRefreshLayout) {}

        open fun onLoadMore(v: PullToRefreshLayout) {}
    }

    internal companion object {

        internal const val animateDuration: Long = 250L

        private val defaultLength: Int

        init {
            val default = 60
            val scale = Resources.getSystem().displayMetrics.density
            defaultLength = (default * scale + 0.5f).toInt()
        }

    }

}