package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView.ScaleType
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.litho.widget.EmptyComponent
import com.facebook.litho.widget.Image
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import com.guet.flexbox.el.InternalFunctions
import com.guet.flexbox.enums.Orientation
import com.guet.flexbox.litho.drawable.ColorDrawable
import com.guet.flexbox.litho.drawable.GradientDrawable
import com.guet.flexbox.litho.drawable.LazyImageDrawable
import com.guet.flexbox.litho.resolve.UrlType
import com.guet.flexbox.litho.toPx
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min
import android.graphics.drawable.GradientDrawable.Orientation as GradientOrientation

@MountSpec(isPureRender = true, hasChildLithoViews = true)
object BannerSpec {

    @PropDefault
    val timeSpan = 3000L

    @get:JvmName(name = "getIsCircular")
    @PropDefault
    val isCircular: Boolean = true

    @PropDefault
    val orientation = Orientation.HORIZONTAL

    @PropDefault
    val indicatorEnable: Boolean = true

    @PropDefault
    val indicatorHeight = 5.toPx()

    @PropDefault
    val indicatorMargin = 2.5f.toPx()

    @PropDefault
    val indicatorSize = 5.toPx()

    @PropDefault
    val indicatorSelected = InternalFunctions.drawable("indicator_light")

    @PropDefault
    val indicatorUnselected = InternalFunctions.drawable("indicator_black")

    @OnCreateMountContent
    fun onCreateMountContent(c: Context): BannerLithoView {
        return BannerLithoView(c)
    }

    @OnCreateInitialState
    fun onCreateInitialState(
            c: ComponentContext,
            position: StateValue<PagePosition>,
            componentTrees: StateValue<ArrayList<ComponentTree>>
    ) {
        componentTrees.set(ArrayList())
        position.set(PagePosition())
    }

    private fun loadDrawable(c: Context, url: String?): Drawable? {
        if (url.isNullOrEmpty()) {
            return null
        } else {
            val (type, args) = UrlType.parseUrl(c, url)
            when (type) {
                UrlType.COLOR -> {
                    return ColorDrawable(args[0] as Int)
                }
                UrlType.GRADIENT -> {
                    return GradientDrawable(
                            args[0] as GradientOrientation,
                            args[1] as IntArray
                    )
                }
                UrlType.URL, UrlType.RESOURCE -> {
                    return LazyImageDrawable(c, args[0])
                }
                else -> {
                    return null
                }
            }
        }
    }

    private fun adjustTreesCount(
            c: ComponentContext,
            @State componentTrees: ArrayList<ComponentTree>,
            size: Int
    ) {
        if (size > 0) {
            synchronized(componentTrees) {
                if (componentTrees.size != size) {
                    if (componentTrees.size > size) {
                        ((componentTrees.size - 1)..size).forEach {
                            ComponentTreePool.releaseTree(componentTrees.removeAt(it))
                        }
                    }
                    if (componentTrees.size <= size) {
                        (1..size - componentTrees.size).forEach { _ ->
                            componentTrees.add(ComponentTreePool.obtainTree())
                        }
                    }
                }
            }
        }
    }


    //ViewPager2如果无限循环，小于3的话会显示异常
    private fun getCompatCountChildren(
            @Prop(optional = true) isCircular: Boolean,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ): List<Component>? {
        return when {
            children.isNullOrEmpty() -> {
                null
            }
            isCircular -> {
                val output = LinkedList<Component>(children)
                do {
                    output.addAll(output)
                } while (output.size <= 4)
                output
            }
            else -> {
                children
            }
        }
    }

    @OnPrepare
    fun onPrepare(
            c: ComponentContext,
            @Prop(optional = true) indicatorSelected: String?,
            @Prop(optional = true) indicatorUnselected: String?,
            @Prop(optional = true) isCircular: Boolean,
            @Prop(optional = true, varArg = "child") children: List<Component>?,
            @State componentTrees: ArrayList<ComponentTree>,
            selectedDrawable: Output<Drawable?>,
            unselectedDrawable: Output<Drawable?>,
            toDisplayChildren: Output<List<@JvmWildcard Component>?>,
            realChildrenCount: Output<Int>
    ) {
        selectedDrawable.set(loadDrawable(c.androidContext, indicatorSelected))
        unselectedDrawable.set(loadDrawable(c.androidContext, indicatorUnselected))
        val myChildren = getCompatCountChildren(isCircular, children)
        adjustTreesCount(c, componentTrees, myChildren?.size ?: 0)
        toDisplayChildren.set(myChildren)
        realChildrenCount.set(children?.size ?: 0)
    }

    @OnMount
    fun onMount(
            c: ComponentContext,
            view: BannerLithoView,
            @Prop(optional = true) orientation: Orientation,
            @Prop(optional = true) isCircular: Boolean,
            @Prop(optional = true, resType = ResType.DIMEN_SIZE) indicatorHeight: Int,
            @Prop(optional = true, resType = ResType.DIMEN_SIZE) indicatorMargin: Int,
            @Prop(optional = true, resType = ResType.DIMEN_SIZE) indicatorSize: Int,
            @Prop(optional = true) indicatorEnable: Boolean,
            @FromPrepare selectedDrawable: Drawable?,
            @FromPrepare unselectedDrawable: Drawable?,
            @FromPrepare realChildrenCount: Int,
            @FromBoundsDefined componentWidth: Int,
            @FromBoundsDefined componentHeight: Int,
            @State componentTrees: ArrayList<ComponentTree>,
            @State position: PagePosition
    ) {
        view.mount(
                orientation,
                isCircular,
                componentWidth,
                componentHeight,
                realChildrenCount,
                indicatorHeight,
                indicatorMargin,
                indicatorSize,
                selectedDrawable,
                unselectedDrawable,
                indicatorEnable,
                componentTrees,
                position
        )
    }

    private fun computeLayout(
            c: ComponentContext,
            @State componentTrees: ArrayList<ComponentTree>,
            @Prop(optional = true, varArg = "child") children: List<Component>?,
            width: Int,
            height: Int
    ) {
        if (!children.isNullOrEmpty()) {
            val size = Size()
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

    @OnMeasure
    fun onMeasure(
            c: ComponentContext,
            layout: ComponentLayout,
            widthSpec: Int,
            heightSpec: Int,
            size: Size,
            @FromPrepare toDisplayChildren: List<Component>?,
            @State componentTrees: ArrayList<ComponentTree>,
            width: Output<Int?>,
            height: Output<Int?>
    ) {
        val w = layout.width
        val h = layout.height
        computeLayout(c, componentTrees, toDisplayChildren, w, h)
        size.width = w
        size.height = h
        width.set(w)
        height.set(h)
    }

    @OnBoundsDefined
    fun onBoundsDefined(
            c: ComponentContext,
            layout: ComponentLayout,
            @FromPrepare toDisplayChildren: List<Component>?,
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
            computeLayout(c, componentTrees, toDisplayChildren, w, h)
            componentWidth.set(w)
            componentHeight.set(h)
        }
    }

    @OnUnmount
    fun onUnmount(
            c: ComponentContext,
            view: BannerLithoView,
            @FromPrepare selectedDrawable: Drawable?,
            @FromPrepare unselectedDrawable: Drawable?

    ) {
        view.unmount()
        if (selectedDrawable is Target<*>) {
            Glide.with(c.androidContext).clear(selectedDrawable)
        }
        if (unselectedDrawable is Target<*>) {
            Glide.with(c.androidContext).clear(unselectedDrawable)
        }
    }

    @OnBind
    fun onBind(
            c: ComponentContext,
            view: BannerLithoView,
            @Prop(optional = true) timeSpan: Long
    ) {
        view.bind(timeSpan)
    }

    @OnUnbind
    fun onUnbind(
            c: ComponentContext,
            view: BannerLithoView
    ) {
        view.unbind()
    }

    @ShouldUpdate
    fun shouldUpdate(
            @Prop(optional = true) orientation: Diff<Orientation>,
            @Prop(optional = true) isCircular: Diff<Boolean>,
            @Prop(optional = true) indicatorSelected: Diff<String?>,
            @Prop(optional = true) indicatorUnselected: Diff<String?>,
            @Prop(optional = true, resType = ResType.DIMEN_SIZE) indicatorHeight: Diff<Int>,
            @Prop(optional = true, resType = ResType.DIMEN_SIZE) indicatorMargin: Diff<Int>,
            @Prop(optional = true, resType = ResType.DIMEN_SIZE) indicatorSize: Diff<Int>,
            @Prop(optional = true) indicatorEnable: Diff<Boolean>,
            @Prop(optional = true, varArg = "child") children: Diff<List<@JvmWildcard Component>?>
    ): Boolean {
        return fastDiff(
                orientation,
                isCircular,
                indicatorSelected,
                indicatorUnselected,
                indicatorHeight,
                indicatorMargin,
                indicatorSize,
                indicatorEnable,
                children
        )
    }

    private fun fastDiff(vararg diff: Diff<out Any?>): Boolean {
        return diff.all {
            Objects.deepEquals(it.next, it.previous)
        }
    }

    class PagePosition(var value: Int = 0) : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            value = position
        }
    }

    class BannerLithoView @JvmOverloads constructor(
            context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : ViewGroup(context, attrs, defStyleAttr), HasLithoViewChildren {

        private var componentWidth = 0
        private var componentHeight = 0

        private var indicatorEnable: Boolean = false
        private var indicatorHeight: Int = 0
        private var indicatorMargin: Int = 0
        private var indicatorSize: Int = 0
        private var selectedDrawable: Drawable? = null
        private var unselectedDrawable: Drawable? = null

        private var isCircular: Boolean = false
        private var timeSpan: Long = 0

        private var realChildrenCount: Int = 0
        private var componentTrees: ArrayList<ComponentTree>? = null
        private var position: PagePosition? = null


        private val indicators = LithoView(context)
        private val viewPager2 = ViewPager2(context)
        private val adapter = InternalAdapter()

        private val autoNextPosition = object : Runnable {
            override fun run() {
                val trees = componentTrees
                if (timeSpan <= 0 || trees.isNullOrEmpty()
                        || realChildrenCount == 0) {
                    return
                }
                val current = viewPager2.currentItem
                val next = if (isCircular) {
                    current + 1
                } else {
                    (current + 1) % realChildrenCount
                }
                viewPager2.currentItem = next
                removeCallbacks(this)
                postDelayed(this, timeSpan)
            }
        }
        private val onPageChanged = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val c = indicators.componentContext
                val trees = componentTrees
                if (!indicatorEnable || trees.isNullOrEmpty()
                        || realChildrenCount <= 0) {
                    indicators.setComponent(
                            EmptyComponent.create(c).build()
                    )
                    return
                }
                val realPosition = if (isCircular) {
                    position % realChildrenCount
                } else {
                    position
                }
                val rowBuilder = Row.create(c)
                        .justifyContent(YogaJustify.CENTER)
                        .alignItems(YogaAlign.CENTER)
                        .marginPx(YogaEdge.BOTTOM, indicatorHeight)
                        .justifyContent(YogaJustify.CENTER)
                (0 until realChildrenCount).forEach {
                    if (it == realPosition) {
                        rowBuilder.child(Image.create(c)
                                .marginPx(YogaEdge.HORIZONTAL, indicatorMargin)
                                .widthPx(indicatorSize)
                                .heightPx(indicatorSize)
                                .scaleType(ScaleType.FIT_XY)
                                .drawable(selectedDrawable)
                        )
                    } else {
                        rowBuilder.child(Image.create(c)
                                .marginPx(YogaEdge.HORIZONTAL, indicatorMargin)
                                .widthPx(indicatorSize)
                                .heightPx(indicatorSize)
                                .scaleType(ScaleType.FIT_XY)
                                .drawable(unselectedDrawable)
                        )
                    }
                }
                indicators.setComponent(Row.create(c)
                        .flexGrow(1f)
                        .justifyContent(YogaJustify.CENTER)
                        .alignItems(YogaAlign.FLEX_END)
                        .child(Row.create(c)
                                .marginPx(YogaEdge.BOTTOM, 0)
                                .justifyContent(YogaJustify.CENTER)
                                .child(rowBuilder)
                                .build())
                        .build())
                indicators.performIncrementalMount()
            }
        }

        init {
            addView(viewPager2, LayoutParams(-1, -1))
            val rv = viewPager2.getChildAt(0) as RecyclerView
            rv.apply {
                isFocusableInTouchMode = false
                isFocusable = false
            }
            val manager = rv.layoutManager as? LinearLayoutManager
            manager?.apply {
                initialPrefetchItemCount = 3
            }
            addView(indicators, LayoutParams(-1, -1))
            viewPager2.registerOnPageChangeCallback(onPageChanged)
        }

        fun mount(
                @Prop(optional = true) orientation: Orientation,
                @Prop(optional = true) isCircular: Boolean,
                @FromBoundsDefined width: Int,
                @FromBoundsDefined height: Int,
                @FromBoundsDefined realChildrenCount: Int,
                @Prop(optional = true, resType = ResType.DIMEN_SIZE) indicatorHeight: Int,
                @Prop(optional = true, resType = ResType.DIMEN_SIZE) indicatorMargin: Int,
                @Prop(optional = true, resType = ResType.DIMEN_SIZE) indicatorSize: Int,
                @FromPrepare selectedDrawable: Drawable?,
                @FromPrepare unselectedDrawable: Drawable?,
                @Prop(optional = true) indicatorEnable: Boolean,
                @State componentTrees: ArrayList<ComponentTree>,
                @State position: PagePosition
        ) {

            this.realChildrenCount = realChildrenCount
            this.isCircular = isCircular

            this.componentWidth = width
            this.componentHeight = height

            this.indicatorEnable = indicatorEnable
            this.indicatorHeight = indicatorHeight
            this.indicatorMargin = indicatorMargin
            this.indicatorSize = indicatorSize
            if (indicatorEnable) {
                this.selectedDrawable = selectedDrawable
                this.unselectedDrawable = unselectedDrawable
            } else {
                this.selectedDrawable = null
                this.unselectedDrawable = null
            }

            if (componentTrees.isNullOrEmpty()) {
                this.componentTrees = null
            } else {
                this.componentTrees = ArrayList(componentTrees)
            }
            viewPager2.adapter = adapter
            viewPager2.orientation = orientation.value
            if (componentTrees.isNullOrEmpty()) {
                return
            }
            if (!isCircular) {
                position.value = min(
                        componentTrees.size - 1,
                        max(0, position.value)
                )
            } else {
                if (position.value < 100) {
                    position.value = (position.value % componentTrees.size) * 100
                }
                if (position.value == 0) {
                    position.value = (componentTrees.size) * 100
                }
            }
            this.position = position
            viewPager2.setCurrentItem(position.value, false)
            viewPager2.registerOnPageChangeCallback(position)
        }

        fun unmount() {
            this.selectedDrawable = null
            this.unselectedDrawable = null
            this.componentTrees = null
            viewPager2.adapter = null
            val pos = position
            if (pos != null) {
                viewPager2.unregisterOnPageChangeCallback(pos)
                position = null
            }
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            viewPager2.measure(
                    MeasureSpec.makeMeasureSpec(componentWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(componentHeight, MeasureSpec.EXACTLY)
            )
            setMeasuredDimension(componentWidth, componentHeight)
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            viewPager2.layout(0, 0, measuredWidth, measuredHeight)
            indicators.layout(0, 0, measuredWidth, measuredHeight)
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

        fun bind(@Prop(optional = true) timeSpan: Long) {
            indicators.rebind()
            this.timeSpan = timeSpan
            if (timeSpan <= 0 || componentTrees.isNullOrEmpty()
                    || realChildrenCount == 0) {
                return
            }
            removeCallbacks(autoNextPosition)
            postDelayed(autoNextPosition, timeSpan)
        }

        fun unbind() {
            indicators.unbind()
            removeCallbacks(autoNextPosition)
        }

        private inner class InternalAdapter : LithoViewsAdapter() {

            override fun getItemCount(): Int {
                val trees = componentTrees ?: return 0
                return if (isCircular) {
                    Int.MAX_VALUE
                } else {
                    trees.size
                }
            }

            override fun onBindViewHolder(
                    holder: LithoViewHolder,
                    position: Int
            ) {
                val trees = componentTrees
                if (!trees.isNullOrEmpty()) {
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
}