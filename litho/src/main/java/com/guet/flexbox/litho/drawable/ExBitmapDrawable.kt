package com.guet.flexbox.litho.drawable

import android.graphics.*
import android.graphics.Matrix.ScaleToFit
import android.graphics.Path.Direction
import android.graphics.Shader.TileMode
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import kotlin.math.min
import kotlin.math.round

class ExBitmapDrawable : Drawable {

    private companion object {

        private fun createMatrix(
                result: Matrix,
                intrinsicWidth: Int,
                intrinsicHeight: Int,
                scaleType: ScaleType,
                width: Int,
                height: Int
        ): Pair<Matrix?, Boolean> {
            if (intrinsicWidth <= 0 || intrinsicHeight <= 0
                    || ScaleType.FIT_XY == scaleType) {
                return null to false
            }
            if (width == intrinsicWidth && height == intrinsicHeight) {
                // The bitmap fits exactly, no transform needed.
                return null to false
            }
            result.reset()
            var shouldClipRect = false
            when (scaleType) {
                ScaleType.CENTER -> {
                    // Center bitmap in view, no scaling.
                    result.setTranslate(
                            round((width - intrinsicWidth) * 0.5f),
                            round((height - intrinsicHeight) * 0.5f)
                    )
                    shouldClipRect = intrinsicWidth > width || intrinsicHeight > height
                }
                ScaleType.CENTER_CROP -> {
                    val scale: Float
                    var dx = 0f
                    var dy = 0f
                    if (intrinsicWidth * height > width * intrinsicHeight) {
                        scale = height.toFloat() / intrinsicHeight.toFloat()
                        dx = (width - intrinsicWidth * scale) * 0.5f
                    } else {
                        scale = width.toFloat() / intrinsicWidth.toFloat()
                        dy = (height - intrinsicHeight * scale) * 0.5f
                    }
                    result.setScale(scale, scale)
                    result.postTranslate(round(dx), round(dy))
                    shouldClipRect = true
                }
                ScaleType.CENTER_INSIDE -> {
                    val scale: Float = if (intrinsicWidth <= width && intrinsicHeight <= height) {
                        1.0f
                    } else {
                        min(width.toFloat() / intrinsicWidth.toFloat(), height.toFloat() / intrinsicHeight.toFloat())
                    }
                    val dx = round((width - intrinsicWidth * scale) * 0.5f)
                    val dy = round((height - intrinsicHeight * scale) * 0.5f)
                    result.setScale(scale, scale)
                    result.postTranslate(dx, dy)
                }
                else -> {
                    val src = RectF()
                    val dest = RectF()
                    // Generate the required transform.
                    src.set(0f, 0f, intrinsicWidth.toFloat(), intrinsicHeight.toFloat())
                    dest.set(0f, 0f, width.toFloat(), height.toFloat())
                    result.setRectToRect(src, dest, scaleTypeToScaleToFit(scaleType))
                }
            }
            return result to shouldClipRect
        }

        private fun scaleTypeToScaleToFit(
                st: ScaleType
        ): ScaleToFit {
            // ScaleToFit enum to their corresponding Matrix.ScaleToFit values
            return when (st) {
                ScaleType.FIT_XY -> ScaleToFit.FILL
                ScaleType.FIT_START -> ScaleToFit.START
                ScaleType.FIT_CENTER -> ScaleToFit.CENTER
                ScaleType.FIT_END -> ScaleToFit.END
                else -> throw IllegalArgumentException("Only FIT_... values allowed")
            }
        }

        private const val DEFAULT_PAINT_FLAGS = Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG
    }

    private val state: EnhancedBitmapState
    private var shouldClipRect: Boolean = false
    private var pathIsDirty: Boolean = true
    private var scaleTypeIsDirty: Boolean = true
    private lateinit var path: Path
    private lateinit var dstRect: RectF
    private lateinit var srcRect: RectF
    private lateinit var matrix: Matrix
    private lateinit var shader: BitmapShader

    constructor(bitmap: Bitmap) : this(EnhancedBitmapState(bitmap))

    private constructor(state: EnhancedBitmapState) : super() {
        this.state = state
    }

    private class EnhancedBitmapState : ConstantState {
        val paint: Paint
        var radiiArray: FloatArray? = null
        var scaleType: ScaleType = ScaleType.FIT_XY
        var bitmap: Bitmap? = null

        constructor(bitmap: Bitmap) {
            this.bitmap = bitmap
            this.paint = Paint(DEFAULT_PAINT_FLAGS)
        }

        constructor(state: EnhancedBitmapState) {
            paint = Paint(state.paint)
            radiiArray = state.radiiArray
            scaleType = state.scaleType
            bitmap = state.bitmap
        }

        override fun newDrawable(): Drawable {
            return ExBitmapDrawable(this)
        }

        override fun getChangingConfigurations(): Int {
            return 0
        }
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        scaleTypeIsDirty = true
    }

    var hasMipMap: Boolean
        get() {
            val bm = state.bitmap
            return bm != null && bm.hasMipMap()
        }
        set(mipMap) {
            state.bitmap?.let {
                it.setHasMipMap(mipMap)
                invalidateSelf()
            }
        }

    var isAntiAlias: Boolean
        get() {
            return state.paint.isAntiAlias
        }
        set(value) {
            state.paint.isAntiAlias = value
            invalidateSelf()
        }

    var scaleType: ScaleType
        get() {
            return state.scaleType
        }
        set(value) {
            if (value == ScaleType.MATRIX) {
                throw UnsupportedOperationException()
            }
            scaleTypeIsDirty = true
            state.scaleType = value
            invalidateSelf()
        }

    var cornerRadius: Float
        get() {
            val radii = state.radiiArray
            if (radii != null && radii.size == 1) {
                return radii[0]
            } else {
                throw UnsupportedOperationException()
            }
        }
        set(value) {
            if (value == 0f) {
                state.radiiArray = null
            } else {
                state.radiiArray = floatArrayOf(value)
            }
            invalidateSelf()
        }

    var cornerRadii: FloatArray?
        get() {
            return state.radiiArray
        }
        set(value) {
            if (value != null && value.size != 8) {
                throw UnsupportedOperationException()
            }
            state.radiiArray = value?.copyOf()
            pathIsDirty = true
            invalidateSelf()
        }

    val bitmap: Bitmap?
        get() = state.bitmap

    override fun setFilterBitmap(filter: Boolean) {
        state.paint.isFilterBitmap = filter
        invalidateSelf()
    }

    override fun isFilterBitmap(): Boolean {
        return state.paint.isFilterBitmap
    }

    override fun setDither(dither: Boolean) {
        state.paint.isDither = dither
        invalidateSelf()
    }

    override fun getAlpha(): Int {
        return state.paint.alpha
    }

    override fun setAlpha(alpha: Int) {
        val oldAlpha: Int = state.paint.alpha
        if (alpha != oldAlpha) {
            state.paint.alpha = alpha
            invalidateSelf()
        }
    }

    private fun init() {
        val bm = state.bitmap ?: return
        if (state.paint.shader == null) {
            val shader = BitmapShader(
                    bm,
                    TileMode.CLAMP,
                    TileMode.CLAMP
            )
            this.shader = shader
            state.paint.shader = shader
            matrix = Matrix()
        }

    }

    private fun applyScaleTypeIfDirty(bm: Bitmap) {
        if (scaleTypeIsDirty) {
            val (
                    outMatrix,
                    shouldClipRect
            ) = createMatrix(
                    matrix,
                    bm.width,
                    bm.height,
                    state.scaleType,
                    bounds.width(),
                    bounds.height()
            )
            this.shouldClipRect = shouldClipRect
            if (outMatrix == null) {
                state.paint.shader = null
            } else {
                this.shader.setLocalMatrix(outMatrix)
                state.paint.shader = this.shader
            }
            scaleTypeIsDirty = false
        }
    }

    private fun buildPathIfDirty() {
        val radii = state.radiiArray ?: return
        if (!this::path.isInitialized) {
            path = Path()
        }
        if (pathIsDirty) {
            path.reset()
            path.addRoundRect(srcRect, radii, Direction.CW)
            pathIsDirty = false
        }
    }

    override fun draw(canvas: Canvas) {
        val bm = state.bitmap ?: return
        init()
        applyScaleTypeIfDirty(bm)
        if (this::srcRect.isInitialized) {
            srcRect.set(bounds)
        } else {
            srcRect = RectF(bounds)
        }
        val sc = canvas.save()
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())
        if (shouldClipRect) {
            canvas.clipRect(0, 0, bounds.width(), bounds.height())
        }
        if (state.paint.shader != null) {
            if (!this::dstRect.isInitialized) {
                dstRect = RectF(bounds)
            }
            dstRect.set(0f, 0f, bm.width.toFloat(), bm.height.toFloat())
            matrix.mapRect(dstRect)
            canvas.clipRect(dstRect)
        }
        val radii = state.radiiArray
        when {
            state.paint.shader == null -> {
                canvas.drawBitmap(
                        bm,
                        null,
                        srcRect,
                        state.paint
                )
            }
            radii == null || radii.isEmpty() -> {
                canvas.drawPaint(state.paint)
            }
            radii.size == 1 -> {
                canvas.drawRoundRect(
                        srcRect,
                        radii[0],
                        radii[0],
                        state.paint
                )
            }
            else -> {
                buildPathIfDirty()
                canvas.drawPath(
                        path,
                        state.paint
                )
            }
        }
        canvas.restoreToCount(sc)
    }

    override fun getOpacity(): Int {
        if (state.scaleType != ScaleType.FIT_XY) {
            return PixelFormat.TRANSLUCENT
        }
        val bitmap: Bitmap? = state.bitmap
        return if (bitmap == null
                || bitmap.hasAlpha()
                || state.paint.alpha < 255
        ) {
            PixelFormat.TRANSLUCENT
        } else {
            PixelFormat.OPAQUE
        }
    }

    override fun getConstantState(): ConstantState {
        return state
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        state.paint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getColorFilter(): ColorFilter? {
        return state.paint.colorFilter
    }
}