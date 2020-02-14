package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.math.MathUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.guet.flexbox.enums.Orientation
import com.guet.flexbox.litho.LayoutThreadHandler
import com.guet.flexbox.litho.drawable.DelegateTarget
import com.guet.flexbox.litho.drawable.DrawableWrapper
import com.guet.flexbox.litho.drawable.NoOpDrawable
import com.guet.flexbox.litho.toPx

@MountSpec(isPureRender = true, hasChildLithoViews = true)
object BannerSpec {

    @PropDefault
    val timeSpan = 3000L

    @get:JvmName(name = "getIsCircular")
    @PropDefault
    val isCircular: Boolean = true

    @PropDefault
    val indicatorHeightPx: Int = 5.toPx()

    @PropDefault
    val orientation = Orientation.HORIZONTAL

    @PropDefault
    val indicatorEnable: Boolean = true

    @OnCreateMountContent
    fun onCreateMountContent(c: Context): BannerLithoView {
        return BannerLithoView(c)
    }

    @OnCreateInitialState
    fun onCreateInitialState(
            c: ComponentContext,
            position: StateValue<PagePosition>,
            onPageChangeCallback: StateValue<ViewPager2.OnPageChangeCallback>,
            componentTrees: StateValue<ArrayList<ComponentTree>>
    ) {
        val pagePosition = PagePosition(0)
        onPageChangeCallback.set(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                pagePosition.value = position
            }
        })
        componentTrees.set(ArrayList())
        position.set(pagePosition)
    }

    @OnMount
    fun onMount(
            c: ComponentContext,
            view: BannerLithoView,
            @Prop(optional = true) orientation: Orientation,
            @Prop(optional = true) isCircular: Boolean,
            @Prop(optional = true) indicatorHeightPx: Int,
            @Prop(optional = true) indicatorSelected: Any?,
            @Prop(optional = true) indicatorUnselected: Any?,
            @Prop(optional = true) indicatorEnable: Boolean,
            @FromBoundsDefined componentWidth: Int,
            @FromBoundsDefined componentHeight: Int,
            @State onPageChangeCallback: ViewPager2.OnPageChangeCallback,
            @State componentTrees: ArrayList<ComponentTree>,
            @State position: PagePosition
    ) {
        view.mount(
                orientation,
                isCircular,
                componentWidth,
                componentHeight,
                indicatorHeightPx,
                indicatorSelected,
                indicatorUnselected,
                indicatorEnable,
                onPageChangeCallback,
                componentTrees,
                position
        )
    }

    private fun adjustComponentTrees(
            c: ComponentContext,
            @State componentTrees: ArrayList<ComponentTree>,
            size: Int
    ) {
        if (size > 0) {
            synchronized(componentTrees) {
                if (componentTrees.size != size) {
                    if (componentTrees.size > size) {
                        ((componentTrees.size - 1)..size).forEach {
                            componentTrees.removeAt(it).release()
                        }
                    }
                    if (componentTrees.size <= size) {
                        (1..size - componentTrees.size).forEach { _ ->
                            componentTrees.add(ComponentTree.create(c)
                                    .layoutThreadHandler(LayoutThreadHandler)
                                    .isReconciliationEnabled(false)
                                    .build()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun measureAll(
            c: ComponentContext,
            @State componentTrees: ArrayList<ComponentTree>,
            @Prop(optional = true, varArg = "child") children: List<Component>?,
            width: Int,
            height: Int
    ) {
        val size = Size()
        if (!children.isNullOrEmpty()) {
            synchronized(componentTrees) {
                (children.indices).forEach {
                    val com = children[it]
                    componentTrees[it].setRootAndSizeSpec(
                            com,
                            SizeSpec.makeSizeSpec(width, SizeSpec.EXACTLY),
                            SizeSpec.makeSizeSpec(height, SizeSpec.EXACTLY),
                            size
                    )
                    com.measure(
                            c,
                            SizeSpec.makeSizeSpec(width, SizeSpec.EXACTLY),
                            SizeSpec.makeSizeSpec(height, SizeSpec.EXACTLY),
                            size
                    )
                }
            }
        }
    }

    @OnMeasure
    fun onMeasure(
            c: ComponentContext,
            layout: ComponentLayout,
            widthSpec: Int,
            heightSpec: Int,
            size: Size,
            @State componentTrees: ArrayList<ComponentTree>,
            @Prop(optional = true, varArg = "child") children: List<Component>?,
            width: Output<Int?>,
            height: Output<Int?>
    ) {
        val w = SizeSpec.getSize(widthSpec)
        val h = SizeSpec.getSize(heightSpec)
        adjustComponentTrees(c, componentTrees, children?.size ?: 0)
        measureAll(c, componentTrees, children, w, h)
        size.width = w
        size.height = h
        width.set(w)
        height.set(h)
    }

    @OnBoundsDefined
    fun onBoundsDefined(
            c: ComponentContext,
            layout: ComponentLayout,
            @Prop(optional = true, varArg = "child") children: List<Component>?,
            @State componentTrees: ArrayList<ComponentTree>,
            @FromMeasure width: Int?,
            @FromMeasure height: Int?,
            componentWidth: Output<Int>,
            componentHeight: Output<Int>
    ) {
        // If onMeasure() has been called, this means the content component already
        // has a defined size, no need to calculate it again.
        if (width != null && height != null) {
            componentWidth.set(width)
            componentHeight.set(height)
        } else {
            val w = layout.width
            val h = layout.height
            adjustComponentTrees(c, componentTrees, children?.size ?: 0)
            measureAll(c, componentTrees, children, w, h)
            componentWidth.set(w)
            componentHeight.set(h)
        }
    }

    @OnUnmount
    fun onUnmount(
            c: ComponentContext,
            view: BannerLithoView,
            @State onPageChangeCallback: ViewPager2.OnPageChangeCallback
    ) {
        view.unmount(onPageChangeCallback)
    }

    @OnBind
    fun onBind(
            c: ComponentContext,
            view: BannerLithoView,
            @Prop(optional = true) timeSpan: Long
    ) {

    }

    @OnUnbind
    fun onUnbind(
            c: ComponentContext,
            view: BannerLithoView
    ) {
    }

    class PagePosition(var value: Int)

    class BannerLithoView @JvmOverloads constructor(
            context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : ViewGroup(context, attrs, defStyleAttr), HasLithoViewChildren {
        private var indicatorEnable: Boolean = false
        private var componentWidth = 0
        private var componentHeight = 0
        private var indicatorHeightPx = 0
        private var isCircular: Boolean = false
        private var componentTrees: ArrayList<ComponentTree>? = null


        private val viewPager2 = ViewPager2(context)
        private val adapter = ComponentTreeAdapter()
        private val selectedDrawable = IndicatorDrawable()
        private val unselectedDrawable = IndicatorDrawable()

        init {
            addView(viewPager2, LayoutParams(-1, -1))
            viewPager2.adapter = adapter
        }

        fun mount(
                @Prop(optional = true) orientation: Orientation,
                @Prop(optional = true) isCircular: Boolean,
                @FromMeasure width: Int,
                @FromMeasure height: Int,
                @Prop(optional = true) indicatorHeightPx: Int,
                @Prop(optional = true) indicatorSelected: Any?,
                @Prop(optional = true) indicatorUnselected: Any?,
                @Prop(optional = true) indicatorEnable: Boolean,
                @State onPageChangeCallback: ViewPager2.OnPageChangeCallback,
                @State componentTrees: ArrayList<ComponentTree>,
                @State position: PagePosition
        ) {
            this.indicatorHeightPx = indicatorHeightPx
            this.isCircular = isCircular
            this.indicatorEnable = indicatorEnable
            this.componentWidth = width
            this.componentHeight = height
            if (indicatorEnable) {
                if (indicatorSelected != null) {
                    Glide.with(this)
                            .load(indicatorSelected)
                            .override(0, 0)
                            .into(selectedDrawable)
                }
                if (indicatorUnselected != null) {
                    Glide.with(this)
                            .load(indicatorUnselected)
                            .override(0, 0)
                            .into(unselectedDrawable)
                }
            }
            if (componentTrees.isNullOrEmpty()) {
                this.componentTrees = null
            } else {
                this.componentTrees = componentTrees
            }
            adapter.notifyDataSetChanged()
            viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
            viewPager2.orientation = orientation.value
            if (componentTrees.isNullOrEmpty()) {
                return
            }
            if (!isCircular) {
                position.value = MathUtils.clamp(0,
                        componentTrees.size - 1,
                        position.value
                )
            } else {
                if (position.value < 100) {
                    position.value = (position.value % componentTrees.size) * 100
                }
                if (position.value == 0) {
                    position.value = componentTrees.size * 100
                }
            }
            viewPager2.setCurrentItem(position.value, false)
        }

        fun unmount(onPageChangeCallback: ViewPager2.OnPageChangeCallback) {
            viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback)
            Glide.with(this).clear(selectedDrawable)
            Glide.with(this).clear(unselectedDrawable)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            viewPager2.measure(
                    MeasureSpec.makeMeasureSpec(componentWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(componentHeight, MeasureSpec.EXACTLY)
            )
            setMeasuredDimension(componentWidth, componentHeight)
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            viewPager2.layout(l, t, r, b)
        }

        override fun obtainLithoViewChildren(lithoViews: MutableList<LithoView>) {
            val vpRv = viewPager2.getChildAt(0) as RecyclerView
            (0 until vpRv.childCount).forEach {
                val v = vpRv.getChildAt(it)
                if (v is LithoView) {
                    lithoViews.add(v)
                }
            }
        }

        override fun dispatchDraw(canvas: Canvas) {
            super.dispatchDraw(canvas)

        }

        private inner class ComponentTreeAdapter : RecyclerView.Adapter<LithoViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LithoViewHolder {
                return LithoViewHolder(parent.context)
            }

            override fun getItemCount(): Int {
                val trees = componentTrees ?: return 0
                return if (isCircular) {
                    Int.MAX_VALUE
                } else {
                    trees.size
                }
            }

            override fun onViewAttachedToWindow(holder: LithoViewHolder) {
                holder.lithoView.performIncrementalMount()
            }

            override fun onBindViewHolder(holder: LithoViewHolder, position: Int) {
                val trees = componentTrees
                if (trees != null) {
                    val pos = if (isCircular) {
                        position % trees.size
                    } else {
                        position
                    }
                    val tree = trees[pos]
                    holder.lithoView.componentTree = tree
                }
            }

            override fun onViewRecycled(holder: LithoViewHolder) {
                holder.lithoView.unmountAllItems()
                holder.lithoView.componentTree = null
            }
        }
    }


    private class LithoViewHolder(
            c: Context
    ) : RecyclerView.ViewHolder(LithoView(c).apply {
        layoutParams = RecyclerView.LayoutParams(-1, -1)
    }) {
        val lithoView: LithoView
            get() = itemView as LithoView
    }

    class IndicatorDrawable : DrawableWrapper<Drawable>(NoOpDrawable()),
            Target<Drawable> by DelegateTarget() {
        private val cacheNoOpDrawable = wrappedDrawable

        override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
        ) {
            wrappedDrawable = resource
            invalidateSelf()
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            wrappedDrawable = cacheNoOpDrawable
        }
    }
}

