/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guet.flexbox.litho.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.ViewDebug;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * A specialized Drawable that fills the Canvas with a specified color.
 * Note that a ColorDrawable ignores the ColorFilter.
 */
public class ColorDrawable extends Drawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ColorState mColorState;

    private boolean mMutated;

    private boolean mPathIsDirty = true;

    private final Path mPath = new Path();
    private final RectF mRect = new RectF();

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mPathIsDirty = true;
    }

    /**
     * Creates a new black ColorDrawable.
     */
    public ColorDrawable() {
        mColorState = new ColorState();
    }

    /**
     * Creates a new ColorDrawable with the specified color.
     *
     * @param color The color to draw.
     */
    public ColorDrawable(@ColorInt int color) {
        mColorState = new ColorState();
        setColor(color);
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | mColorState.getChangingConfigurations();
    }

    /**
     * A mutable BitmapDrawable still shares its Bitmap with any other Drawable
     * that comes from the same resource.
     *
     * @return This drawable.
     */
    @NonNull
    @Override
    public Drawable mutate() {
        if (!mMutated && super.mutate() == this) {
            mColorState = new ColorState(mColorState);
            mMutated = true;
        }
        return this;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final ColorFilter colorFilter = mPaint.getColorFilter();
        if (mColorState.mUseColor >>> 24 != 0 || colorFilter != null) {
            if (mColorState.mRadius <= 0 && mColorState.mRadiusArray == null) {

                mPaint.setColor(mColorState.mUseColor);
                canvas.drawRect(getBounds(), mPaint);
                // Restore original color filter.
                mPaint.setColorFilter(colorFilter);


            } else if (mColorState.mRadiusArray != null) {

                mPaint.setColor(mColorState.mUseColor);
                buildPathIfDirty();
                canvas.drawPath(mPath, mPaint);
                mPaint.setColorFilter(colorFilter);


            } else if (mColorState.mRadius > 0) {

                mPaint.setColor(mColorState.mUseColor);
                mRect.set(getBounds());
                float rad = Math.min(mColorState.mRadius,
                        Math.min(mRect.width(), mRect.height()) * 0.5f);
                canvas.drawRoundRect(mRect, rad, rad, mPaint);
                mPaint.setColorFilter(colorFilter);


            }
        }
    }

    /**
     * Gets the drawable's color value.
     *
     * @return int The color to draw.
     */
    @ColorInt
    public int getColor() {
        return mColorState.mUseColor;
    }

    /**
     * Sets the drawable's color value. This action will clobber the results of
     * prior calls to {@link #setAlpha(int)} on this object, which side-affected
     * the underlying color.
     *
     * @param color The color to draw.
     */
    public void setColor(@ColorInt int color) {
        if (mColorState.mBaseColor != color || mColorState.mUseColor != color) {
            mColorState.mBaseColor = mColorState.mUseColor = color;
            invalidateSelf();
        }
    }

    /**
     * Returns the alpha value of this drawable's color.
     *
     * @return A value between 0 and 255.
     */
    @Override
    public int getAlpha() {
        return mColorState.mUseColor >>> 24;
    }

    /**
     * Sets the color's alpha value.
     *
     * @param alpha The alpha value to set, between 0 and 255.
     */
    @Override
    public void setAlpha(int alpha) {
        alpha += alpha >> 7;   // make it 0..256
        final int baseAlpha = mColorState.mBaseColor >>> 24;
        final int useAlpha = baseAlpha * alpha >> 8;
        final int useColor = (mColorState.mBaseColor << 8 >>> 8) | (useAlpha << 24);
        if (mColorState.mUseColor != useColor) {
            mColorState.mUseColor = useColor;
            invalidateSelf();
        }
    }

    /**
     * Sets the color filter applied to this color.
     * <p>
     * Only supported on version {@link android.os.Build.VERSION_CODES#LOLLIPOP} and
     * above. Calling this method has no effect on earlier versions.
     *
     * @see android.graphics.drawable.Drawable#setColorFilter(ColorFilter)
     */
    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    /**
     * @param mode new transfer mode
     */
    public void setXfermode(@Nullable Xfermode mode) {
        mPaint.setXfermode(mode);
        invalidateSelf();
    }

    /**
     * @return current transfer mode
     */
    public Xfermode getXfermode() {
        return mPaint.getXfermode();
    }

    @Override
    public int getOpacity() {
        if (mPaint.getColorFilter() != null) {
            return PixelFormat.TRANSLUCENT;
        }

        switch (mColorState.mUseColor >>> 24) {
            case 255:
                return PixelFormat.OPAQUE;
            case 0:
                return PixelFormat.TRANSPARENT;
        }
        return PixelFormat.TRANSLUCENT;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(@NonNull Outline outline) {
        outline.setRect(getBounds());
        outline.setAlpha(getAlpha() / 255.0f);
    }

    @Override
    public ConstantState getConstantState() {
        return mColorState;
    }

    final static class ColorState extends ConstantState {
        int mBaseColor; // base color, independent of setAlpha()
        @ViewDebug.ExportedProperty
        int mUseColor;  // basecolor modulated by setAlpha()
        int mChangingConfigurations;
        float[] mRadiusArray = null;
        float mRadius;

        ColorState() {
            // Empty constructor.
        }

        ColorState(ColorState state) {
            mBaseColor = state.mBaseColor;
            mUseColor = state.mUseColor;
            mChangingConfigurations = state.mChangingConfigurations;
            mRadius = state.mRadius;
            mRadiusArray = state.mRadiusArray != null ? state.mRadiusArray.clone() : null;
        }

        @Override
        public Drawable newDrawable() {
            return new ColorDrawable(this);
        }

        @Override
        public Drawable newDrawable(Resources res) {
            return new ColorDrawable(this);
        }

        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }
    }

    private void buildPathIfDirty() {
        final ColorState st = mColorState;
        if (mPathIsDirty) {
            mPath.reset();
            mPath.addRoundRect(mRect, st.mRadiusArray, Path.Direction.CW);
            mPathIsDirty = false;
        }
    }

    public float getCornerRadius() {
        return mColorState.mRadius;
    }

    public void setCornerRadius(float value) {
        mPathIsDirty = true;
        mColorState.mRadius = value;
        mColorState.mRadiusArray = null;
    }

    public void setCornerRadii(float[] radii) {
        mPathIsDirty = true;
        mColorState.mRadiusArray = radii;
        if (radii == null) {
            mColorState.mRadius = 0;
        } else {
            int count = 0;
            for (float value : radii) {
                count += value;
            }
            if (count == 0) {
                mColorState.mRadiusArray = null;
                mColorState.mRadius = 0;
            }
        }
    }

    public float[] getCornerRadii() {
        return mColorState.mRadiusArray != null
                ? mColorState.mRadiusArray.clone() : null;
    }

    private ColorDrawable(ColorState state) {
        mColorState = state;
    }
}
