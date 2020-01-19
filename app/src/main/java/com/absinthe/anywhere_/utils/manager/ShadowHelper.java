package com.absinthe.anywhere_.utils.manager;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

/**
 * 类名称：ShadowHelper
 * 创建者：Create by liujc
 * 创建时间：Create on 2018/5/28
 * 描述：控件阴影效果
 */
public class ShadowHelper extends Drawable {

    private static ShadowHelper shadowHelper;
    private Paint mPaint;
    /**
     * 阴影模糊半径，越大越模糊
     */
    private int mShadowRadius;
    /**
     * 阴影颜色
     */
    private int mShadowColor;
    /**
     * 背景形状
     */
    private int mShape;
    /**
     * 背景圆角半径
     */
    private int mShapeRadius;
    /**
     * 阴影x偏移(右偏移)
     */
    private int mOffsetX;
    /**
     * 阴影y偏移(下偏移)
     */
    private int mOffsetY;
    /**
     * 背景颜色
     */
    private int mBgColor[];
    private RectF mRect;

    public final static int SHAPE_ROUND = 1;
    public final static int SHAPE_CIRCLE = 2;
    public static final int ALL = 0x1111;
    public static final int LEFT = 0x0001;
    public static final int TOP = 0x0010;
    public static final int RIGHT = 0x0100;
    public static final int BOTTOM = 0x1000;
    /**
     * 阴影边 例：0x1100 表示RIGHT和BOTTOM
     */
    private int shadowSide = ALL;

    public ShadowHelper() {
        mShape = ShadowHelper.SHAPE_ROUND;
        mShapeRadius = 0;
        mShadowColor = Color.parseColor("#4d000000");
        mShadowRadius = 18;
        mOffsetX = 0;
        mOffsetY = 0;
        mBgColor = new int[1];
        mBgColor[0] = Color.TRANSPARENT;

        mPaint = new Paint();
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setAntiAlias(true);
        mPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

    }

    public static ShadowHelper getInstance() {
        shadowHelper = new ShadowHelper();
        return shadowHelper;
    }

    public ShadowHelper setShape(int mShape) {
        this.mShape = mShape;
        return this;
    }

    public ShadowHelper setShadowSide(int shadowSide) {
        this.shadowSide = shadowSide;
        return this;
    }

    public ShadowHelper setShapeRadius(int ShapeRadius) {
        this.mShapeRadius = ShapeRadius;
        return this;
    }

    /**
     * 设置阴影颜色
     *
     * @param shadowColor 例：R.color.colorPrimary
     * @return
     */
    public ShadowHelper setShadowColor(int shadowColor) {
        this.mShadowColor = shadowColor;
        mPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        return this;
    }

    /**
     * 设置阴影颜色
     *
     * @param shadowColor 例：#ffffff
     * @return
     */
    public ShadowHelper setShadowColor(String shadowColor) {
        this.mShadowColor = Color.parseColor(shadowColor);
        mPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        return this;
    }

    public ShadowHelper setShadowRadius(int shadowRadius) {
        this.mShadowRadius = shadowRadius;
        mPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        return this;
    }

    public ShadowHelper setOffsetX(int OffsetX) {
        this.mOffsetX = OffsetX;
        mPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        return this;
    }

    public ShadowHelper setOffsetY(int OffsetY) {
        this.mOffsetY = OffsetY;
        mPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        return this;
    }

    /**
     * 设置背景颜色
     *
     * @param BgColor 例：R.color.colorPrimary
     * @return
     */
    public ShadowHelper setBgColor(int BgColor) {
        this.mBgColor[0] = BgColor;
        return this;
    }

    /**
     * 设置背景颜色
     *
     * @param bgColor 例：#ffffff
     * @return
     */
    public ShadowHelper setBgColor(String bgColor) {
        this.mBgColor[0] = Color.parseColor(bgColor);
        return this;
    }

    public ShadowHelper setBgColor(int[] BgColor) {
        this.mBgColor = BgColor;
        return this;
    }

    public ShadowHelper setBgColor(String[] bgColor) {
        int length = bgColor.length;
        int[] color = new int[length];
        for (int i = 0; i < length; i++) {
            color[i] = Color.parseColor(bgColor[i]);
        }
        this.mBgColor = color;
        return this;
    }

    public ShadowHelper setShadowAlpha(int i) {
        setAlpha(i);
        return this;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int leftShadow = (shadowSide & LEFT) == LEFT ? mShadowRadius - mOffsetX : -mShapeRadius;
        int topShadow = (shadowSide & TOP) == TOP ? mShadowRadius - mOffsetY : -mShapeRadius;
        int rightShadow = (shadowSide & RIGHT) == RIGHT ? mShadowRadius + mOffsetX : -mShapeRadius;
        int bottomShadow = (shadowSide & BOTTOM) == BOTTOM ? mShadowRadius + mOffsetY : -mShapeRadius;
        mRect = new RectF(bounds.left + leftShadow, bounds.top + topShadow, bounds.right - rightShadow,
                bounds.bottom - bottomShadow);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Paint newPaint = new Paint();
        if (mBgColor != null) {
            if (mBgColor.length == 1) {
                newPaint.setColor(mBgColor[0]);
            } else {
                newPaint.setShader(new LinearGradient(mRect.left, mRect.height() / 2, mRect.right, mRect.height() / 2, mBgColor,
                        null, Shader.TileMode.CLAMP));
            }
        }
        newPaint.setAntiAlias(true);
        if (mShape == SHAPE_ROUND) {
            canvas.drawRoundRect(mRect, mShapeRadius, mShapeRadius, mPaint);
            canvas.drawRoundRect(mRect, mShapeRadius, mShapeRadius, newPaint);
        } else {
            canvas.drawCircle(mRect.centerX(), mRect.centerY(), Math.min(mRect.width(), mRect.height()) / 2, mPaint);
            canvas.drawCircle(mRect.centerX(), mRect.centerY(), Math.min(mRect.width(), mRect.height()) / 2, newPaint);
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public static void setShadowHelper(View view, Drawable drawable) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public void into(View view) {
        if (view == null) {
            return;
        }
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, this);
    }

}
