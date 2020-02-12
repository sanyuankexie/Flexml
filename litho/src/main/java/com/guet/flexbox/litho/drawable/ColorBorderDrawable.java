/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guet.flexbox.litho.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import com.facebook.litho.drawable.ComparableDrawable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Drawable that draws border lines with given color, widths and path effect.
 */
public class ColorBorderDrawable extends Drawable implements ComparableDrawable {
    private static final int QUICK_REJECT_COLOR = Color.TRANSPARENT;
    private final State mState;

    private RectF mDrawBounds;
    private RectF mInnerDrawBounds;
    private Paint mPaint;
    private Path mPath;
    private Path mClipPath;
    private boolean mDrawBorderWithPath;

    private ColorBorderDrawable(State state) {
        mState = state;
    }

    private static void drawBorder(
            Canvas canvas, RectF bounds, Path path, float[] radii, Paint paint) {
        float padding = paint.getStrokeWidth() / 2f;
        float maxRadii = Math.min(bounds.width(), bounds.height()) / 2f;
        if (path == null) {
            // All radii are the same
            float radius = Math.min(maxRadii, radii[0]) + padding;
            canvas.drawRoundRect(bounds, radius, radius, paint);
        } else {
            if (path.isEmpty()) {
                path.addRoundRect(bounds, radii, Path.Direction.CW);
            }
            canvas.drawPath(path, paint);
        }
    }

    public void init() {
        mDrawBounds = new RectF();
        mInnerDrawBounds = new RectF();
        mPaint = new Paint();
        mPath = new Path();
        mClipPath = new Path();
        boolean hasRadius = false;
        float lastRadius = 0f;
        for (int i = 0; i < mState.mBorderRadius.length; ++i) {
            final float radius = mState.mBorderRadius[i];
            if (radius > 0f) {
                hasRadius = true;
            }
            if (i == 0) {
                lastRadius = radius;
            } else if (lastRadius != radius) {
                mDrawBorderWithPath = true;
                break;
            }
        }

        if (mDrawBorderWithPath && mState.mBorderRadius.length != 8) {
            // Need to duplicate values because Android expects X / Y radii specified separately
            float[] radii = new float[8];
            float padding = mState.mBorderWidth / 2f;
            for (int i = 0; i < 4; ++i) {
                radii[i * 2] = mState.mBorderRadius[i] + padding;
                radii[i * 2 + 1] = mState.mBorderRadius[i] + padding;
            }
            mState.mBorderRadius = radii;
        }

        mPaint.setPathEffect(mState.mPathEffect);
        mPaint.setAntiAlias(mState.mPathEffect != null || hasRadius);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mState.mBorderWidth == 0) {
            // No border widths, nothing to draw
            return;
        }
        if (mPaint == null || mPath == null) {
            init();
        }
        final boolean equalBorderColors =
                mState.mBorderLeftColor == mState.mBorderTopColor
                        && mState.mBorderTopColor == mState.mBorderRightColor
                        && mState.mBorderRightColor == mState.mBorderBottomColor;
        if (equalBorderColors) {
            drawAllBorders(canvas, mState.mBorderWidth, mState.mBorderLeftColor);
        } else {
            drawMultiColoredBorders(canvas);
        }
    }

    /**
     * Best case possible, all colors are the same and all widths are the same
     */
    private void drawAllBorders(Canvas canvas, float strokeWidth, @ColorInt int color) {
        float inset = -strokeWidth / 2f;
        mDrawBounds.set(getBounds());
        mDrawBounds.inset(inset, inset);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(color);
        drawBorder(canvas, mDrawBounds, path(), mState.mBorderRadius, mPaint);
    }

    /**
     * Special, special case, support for multi color with same widths for API 28
     */
    private void drawMultiColoredBorders(Canvas canvas) {
        mPaint.setStrokeWidth(mState.mBorderWidth);
        float inset = -mState.mBorderWidth / 2f;
        mDrawBounds.set(getBounds());
        final int translateSaveCount = canvas.save();
        canvas.translate(mDrawBounds.left, mDrawBounds.top);
        mDrawBounds.offsetTo(0.0f, 0.0f);
        mDrawBounds.inset(inset, inset);
        mInnerDrawBounds.set(mDrawBounds);
        float third = Math.min(mDrawBounds.width(), mDrawBounds.height()) / 3f;
        mInnerDrawBounds.inset(third, third);
        int saveCount;

        // Left
        int color = mState.mBorderLeftColor;
        if (color != QUICK_REJECT_COLOR) {
            saveCount = canvas.save();
            mPaint.setColor(color);
            mClipPath.reset();
            mClipPath.moveTo(mDrawBounds.left - inset, mDrawBounds.top - inset);
            mClipPath.lineTo(mInnerDrawBounds.left, mInnerDrawBounds.top);
            mClipPath.lineTo(mInnerDrawBounds.left, mInnerDrawBounds.bottom);
            mClipPath.lineTo(mDrawBounds.left - inset, mDrawBounds.bottom + inset);
            mClipPath.close();
            canvas.clipPath(mClipPath);
            drawBorder(canvas, mDrawBounds, path(), mState.mBorderRadius, mPaint);
            canvas.restoreToCount(saveCount);
        }

        // Top
        color = mState.mBorderTopColor;
        if (color != QUICK_REJECT_COLOR) {
            saveCount = canvas.save();
            mPaint.setColor(color);
            mClipPath.reset();
            mClipPath.moveTo(mDrawBounds.left - inset, mDrawBounds.top - inset);
            mClipPath.lineTo(mInnerDrawBounds.left, mInnerDrawBounds.top);
            mClipPath.lineTo(mInnerDrawBounds.right, mInnerDrawBounds.top);
            mClipPath.lineTo(mDrawBounds.right + inset, mDrawBounds.top - inset);
            mClipPath.close();
            canvas.clipPath(mClipPath);
            drawBorder(canvas, mDrawBounds, path(), mState.mBorderRadius, mPaint);
            canvas.restoreToCount(saveCount);
        }

        // Right
        color = mState.mBorderRightColor;
        if (color != QUICK_REJECT_COLOR) {
            saveCount = canvas.save();
            mPaint.setColor(color);
            mClipPath.reset();
            mClipPath.moveTo(mDrawBounds.right + inset, mDrawBounds.top - inset);
            mClipPath.lineTo(mInnerDrawBounds.right, mInnerDrawBounds.top);
            mClipPath.lineTo(mInnerDrawBounds.right, mInnerDrawBounds.bottom);
            mClipPath.lineTo(mDrawBounds.right + inset, mDrawBounds.bottom + inset);
            mClipPath.close();
            canvas.clipPath(mClipPath);
            drawBorder(canvas, mDrawBounds, path(), mState.mBorderRadius, mPaint);
            canvas.restoreToCount(saveCount);
        }

        // Bottom
        color = mState.mBorderBottomColor;
        if (color != QUICK_REJECT_COLOR) {
            saveCount = canvas.save();
            mPaint.setColor(color);
            mClipPath.reset();
            mClipPath.moveTo(mDrawBounds.left - inset, mDrawBounds.bottom + inset);
            mClipPath.lineTo(mInnerDrawBounds.left, mInnerDrawBounds.bottom);
            mClipPath.lineTo(mInnerDrawBounds.right, mInnerDrawBounds.bottom);
            mClipPath.lineTo(mDrawBounds.right + inset, mDrawBounds.bottom + inset);
            mClipPath.close();
            canvas.clipPath(mClipPath);
            drawBorder(canvas, mDrawBounds, path(), mState.mBorderRadius, mPaint);
            canvas.restoreToCount(saveCount);
        }

        canvas.restoreToCount(translateSaveCount);
    }

    @Nullable
    private Path path() {
        return mDrawBorderWithPath ? mPath : null;
    }

    @Override
    public void setAlpha(int alpha) {
        if (mPaint != null) {
            mPaint.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (mPaint != null) {
            mPaint.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public boolean isEquivalentTo(ComparableDrawable other) {
        return equals(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ColorBorderDrawable)) {
            return false;
        }

        ColorBorderDrawable that = (ColorBorderDrawable) o;

        return Objects.equals(mState, that.mState);
    }

    @Override
    public int hashCode() {
        return mState.hashCode();
    }

    static class State {
        float mBorderWidth;

        @ColorInt
        int mBorderLeftColor;
        @ColorInt
        int mBorderTopColor;
        @ColorInt
        int mBorderRightColor;
        @ColorInt
        int mBorderBottomColor;

        @Nullable
        PathEffect mPathEffect;
        float[] mBorderRadius;

        @Override
        public int hashCode() {
            int result = 0;
            result = 31 * result + (int) mBorderWidth;
            result = 31 * result + mBorderLeftColor;
            result = 31 * result + mBorderTopColor;
            result = 31 * result + mBorderRightColor;
            result = 31 * result + mBorderBottomColor;
            result = 31 * result + (mPathEffect != null ? mPathEffect.hashCode() : 0);
            result = 31 * result + Arrays.hashCode(mBorderRadius);

            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            State state = (State) o;
            return state.mBorderWidth == mBorderWidth
                    && mBorderLeftColor == state.mBorderLeftColor
                    && mBorderTopColor == state.mBorderTopColor
                    && mBorderRightColor == state.mBorderRightColor
                    && mBorderBottomColor == state.mBorderBottomColor
                    && Objects.equals(mPathEffect, state.mPathEffect)
                    && Arrays.equals(mBorderRadius, state.mBorderRadius);
        }
    }

    public static class Builder {

        private State mState;

        public Builder() {
            mState = new State();
        }

        public Builder pathEffect(@Nullable PathEffect pathEffect) {
            mState.mPathEffect = pathEffect;
            return this;
        }

        public Builder borderColor(@ColorInt int color) {
            mState.mBorderLeftColor = color;
            mState.mBorderTopColor = color;
            mState.mBorderRightColor = color;
            mState.mBorderBottomColor = color;
            return this;
        }

        public Builder borderLeftColor(@ColorInt int color) {
            mState.mBorderLeftColor = color;
            return this;
        }

        public Builder borderTopColor(@ColorInt int color) {
            mState.mBorderTopColor = color;
            return this;
        }

        public Builder borderRightColor(@ColorInt int color) {
            mState.mBorderRightColor = color;
            return this;
        }

        public Builder borderBottomColor(@ColorInt int color) {
            mState.mBorderBottomColor = color;
            return this;
        }

        public Builder borderWidth(@Px int border) {
            mState.mBorderWidth = border;
            return this;
        }

        public Builder borderRadius(float... radius) {
            mState.mBorderRadius = Arrays.copyOf(radius, radius.length);
            return this;
        }

        public ColorBorderDrawable build() {
            return new ColorBorderDrawable(mState);
        }
    }
}
