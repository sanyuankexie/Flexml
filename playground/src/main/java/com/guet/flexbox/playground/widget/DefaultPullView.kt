package com.guet.flexbox.playground.widget

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.guet.flexbox.playground.R

class DefaultPullView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    val text: TextView

    val progress: ProgressBar

    private val arrow: ImageView

    private var animator: ValueAnimator? = null

    var arrowDirectionIsUp: Boolean = false
        set(value) {
            if (field != value) {
                animator?.cancel()
                if (value) {
                    animator = ValueAnimator.ofFloat(0f, 180f).apply {
                        duration = PullToRefreshLayout.animateDuration
                        interpolator = DecelerateInterpolator()
                        addUpdateListener {
                            arrow.rotation = it.animatedValue as Float
                        }
                        start()
                    }
                } else {
                    animator = ValueAnimator.ofFloat(180f, 0f).apply {
                        duration = PullToRefreshLayout.animateDuration
                        interpolator = DecelerateInterpolator()
                        addUpdateListener {
                            arrow.rotation = it.animatedValue as Float
                        }
                        start()
                    }
                }
                field = value
            }
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_default, this)
        text = findViewById(R.id.header_tv)
        progress = findViewById(R.id.header_progress)
        arrow = findViewById(R.id.header_arrow)
    }

    object HeaderAdapter : PullToRefreshLayout.PullViewAdapter {

        override fun onProgress(v: View, progress: Float, directionIsUp: Boolean) {
            if (v is DefaultPullView) {
                //👇转👆
                if (progress >= 0.9f && !directionIsUp) {
                    v.arrowDirectionIsUp = true
                }
                //👆转👇
                if (progress <= 0.9f && directionIsUp) {
                    v.arrowDirectionIsUp = false
                }
                if (progress >= 0.9f) {
                    v.text.text = "松开刷新"
                } else {
                    v.text.text = "下拉加载"
                }
            }
        }

        override fun onStateChanged(v: View, pullState: PullToRefreshLayout.PullState) {
            if (v is DefaultPullView) {
                if (pullState == PullToRefreshLayout.PullState.HIDE) {
                    v.text.text = "下拉加载"
                    v.progress.visibility = View.GONE
                    v.arrow.visibility = View.VISIBLE
                } else if (pullState == PullToRefreshLayout.PullState.LOAD) {
                    v.text.text = "刷新中..."
                    v.arrow.visibility = View.GONE
                    v.progress.visibility = View.VISIBLE
                }
            }
        }

    }

    object FooterAdapter : PullToRefreshLayout.PullViewAdapter {

        override fun onProgress(v: View, progress: Float, directionIsUp: Boolean) {
            if (v is DefaultPullView) {
                //👆转👇
                if (progress >= 0.9f && directionIsUp) {
                    v.arrowDirectionIsUp = false
                }
                //👇转👆
                if (progress <= 0.9f && !directionIsUp) {
                    v.arrowDirectionIsUp = true
                }
                if (progress >= 0.9f) {
                    v.text.text = "松开加载更多"
                } else {
                    v.text.text = "上拉加载更多"
                }
            }
        }

        override fun onStateChanged(v: View, pullState: PullToRefreshLayout.PullState) {
            if (v is DefaultPullView) {
                if (pullState == PullToRefreshLayout.PullState.HIDE) {
                    v.text.text = "上拉加载更多"
                    v.progress.visibility = View.GONE
                    v.arrow.visibility = View.VISIBLE
                } else if (pullState == PullToRefreshLayout.PullState.LOAD) {
                    v.text.text = "加载中..."
                    v.arrow.visibility = View.GONE
                    v.progress.visibility = View.VISIBLE
                }
            }
        }

    }

}