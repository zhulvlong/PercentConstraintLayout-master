package com.github.layout.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import androidx.annotation.ColorInt;

import android.util.AttributeSet;
import android.widget.ImageView;

import com.github.layout.R;

/**
 * ImageView-Helper
 * 已经弃用
 *
 * @author ZhongDaFeng
 */
@Deprecated
public class SubImageViewHelper {

    //Icon
    private Drawable mIconNormal;
    private Drawable mIconPressed;
    private Drawable mIconUnable;
    private Drawable mIconSelected;

    //圆角
    private float mCorner = -1;
    private float mCornerTopLeft = 0;
    private float mCornerTopRight = 0;
    private float mCornerBottomLeft = 0;
    private float mCornerBottomRight = 0;
    private float mCornerBorderRadii[] = new float[8];
    private float mCornerBitmapRadii[] = new float[8];

    //边框
    private int mBorderWidth = 0;
    private int mBorderColor = Color.BLACK;

    //是否为普通ImageView(不是圆形或者圆角)
    private boolean mIsNormal = true;

    //是否圆形
    private boolean mIsCircle = false;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();
    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    private Path mBitmapPath;

    protected int[][] states = new int[4][];
    private StateListDrawable mStateDrawable;

    private ImageView mView;
    private BitmapShader mBitmapShader;

    public SubImageViewHelper(Context context, ImageView view, AttributeSet attrs) {
        mView = view;
        initAttributeSet(context, attrs);
    }

    /**
     * 初始化控件属性
     *
     * @param context
     * @param attrs
     */
    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (context == null || attrs == null) {
            setup();
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SubImageView_Layout);
        //icon
        //Vector兼容处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*mIconNormal = a.getDrawable(R.styleable.RImageView_icon_src_normal);
            mIconPressed = a.getDrawable(R.styleable.RImageView_icon_src_pressed);
            mIconUnable = a.getDrawable(R.styleable.RImageView_icon_src_unable);
            mIconSelected = a.getDrawable(R.styleable.RImageView_icon_src_selected);*/
        } else {
            /*int normalId = a.getResourceId(R.styleable.RImageView_icon_src_normal, -1);
            int pressedId = a.getResourceId(R.styleable.RImageView_icon_src_pressed, -1);
            int unableId = a.getResourceId(R.styleable.RImageView_icon_src_unable, -1);
            int selectedId = a.getResourceId(R.styleable.RImageView_icon_src_selected, -1);

            if (normalId != -1)
                mIconNormal = AppCompatResources.getDrawable(context, normalId);
            if (pressedId != -1)
                mIconPressed = AppCompatResources.getDrawable(context, pressedId);
            if (unableId != -1)
                mIconUnable = AppCompatResources.getDrawable(context, unableId);
            if (selectedId != -1)
                mIconSelected = AppCompatResources.getDrawable(context, selectedId);*/
        }
        //基础属性
        mIsCircle = a.getBoolean(R.styleable.SubImageView_Layout_sub_is_circle, false);
        mCorner = a.getDimensionPixelSize(R.styleable.SubImageView_Layout_sub_corner_radius, -1);
        mCornerTopLeft = a.getDimensionPixelSize(R.styleable.SubImageView_Layout_sub_corner_radius_top_left, 0);
        mCornerTopRight = a.getDimensionPixelSize(R.styleable.SubImageView_Layout_sub_corner_radius_top_right, 0);
        mCornerBottomLeft = a.getDimensionPixelSize(R.styleable.SubImageView_Layout_sub_corner_radius_bottom_left, 0);
        mCornerBottomRight = a.getDimensionPixelSize(R.styleable.SubImageView_Layout_sub_corner_radius_bottom_right, 0);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.SubImageView_Layout_sub_border_width, 0);
        mBorderColor = a.getColor(R.styleable.SubImageView_Layout_sub_border_color, Color.BLACK);

        a.recycle();

        //未使用自定义属性容错处理
        if (mIconNormal == null) mIconNormal = mView.getDrawable();

        //init
        init();

        //setup
        setup();

    }

    /**
     * 设置
     */
    private void setup() {
        mStateDrawable = new StateListDrawable();

        /**
         * 设置背景默认值
         */
        if (mIconPressed == null) {
            mIconPressed = mIconNormal;
        }
        if (mIconUnable == null) {
            mIconUnable = mIconNormal;
        }
        if (mIconSelected == null) {
            mIconSelected = mIconNormal;
        }

        //unable,focused,pressed,selected,normal
        states[0] = new int[]{-android.R.attr.state_enabled};//unable
        states[1] = new int[]{android.R.attr.state_pressed};//pressed
        states[2] = new int[]{android.R.attr.state_selected};//selected
        states[3] = new int[]{android.R.attr.state_enabled};//normal

        mStateDrawable.addState(states[0], mIconUnable);
        mStateDrawable.addState(states[1], mIconPressed);
        mStateDrawable.addState(states[2], mIconSelected);
        mStateDrawable.addState(states[3], mIconNormal);

        setIcon();
    }

    /**
     * 初始化设置
     */
    private void init() {

        //统一设置圆角弧度优先
        updateCornerBorderRadii();
        updateCornerBitmapRadii();

        //设置圆形，或者某个角度圆角则认为不是普通imageView
        if (mIsCircle || mCorner > 0 || mCornerTopLeft != 0 || mCornerTopRight != 0 || mCornerBottomRight != 0 || mCornerBottomLeft != 0) {
            mIsNormal = false;
        }

        //border
        if (mBorderPaint == null) {
            mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        mBorderPaint.setStyle(Paint.Style.STROKE);
        //bitmap
        if (mBitmapPaint == null) {
            mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        //BitmapPath
        if (mBitmapPath == null) {
            mBitmapPath = new Path();
        }
    }

    /**
     * 绘制
     *
     * @param canvas
     */
    public void onDraw(Canvas canvas) {
        /**
         * 绘制图片
         */
        drawBitmap(canvas);

        /**
         * 绘制边框
         */
        drawBorder(canvas);
    }

    /**
     * 绘制bitmap
     *
     * @param canvas
     */
    private void drawBitmap(Canvas canvas) {

        //drawable
        Drawable drawable = mView.getDrawable();
        if (drawable == null) return;

        //drawable' width & height
        int bmpW = drawable.getIntrinsicWidth();
        int bmpH = drawable.getIntrinsicHeight();

        //ScaleType
        ImageView.ScaleType scaleType = mView.getScaleType();

        //获取bitmap,处理ScaleType
        Bitmap bitmap = getBitmapFromDrawable(drawable, scaleType, bmpW, bmpH, getWidth(), getHeight());

        //根据 bitmap 创建 BitmapShader
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        // 设置变换矩阵
        //mBitmapShader.setLocalMatrix(matrix);

        // Paint 设置 Shader
        mBitmapPaint.setShader(mBitmapShader);

        //更新各个圆角,根据圆角路径绘制形状
        updateCornerBitmapRadii();
        updateDrawableAndBorderRect();

        if (mIsCircle) {//圆形
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, Math.min(mDrawableRect.width() / 2, mDrawableRect.width() / 2), mBitmapPaint);
        } else {//圆角
            mBitmapPath.reset();//must重置
            mBitmapPath.addRoundRect(mDrawableRect, mCornerBitmapRadii, Path.Direction.CCW);
            canvas.drawPath(mBitmapPath, mBitmapPaint);
        }
    }

    /**
     * 获取bitmap,处理ScaleType
     *
     * @param drawable
     * @param scaleType
     * @param bmpW      图片宽
     * @param bmpH      图片高
     * @param w         控件宽
     * @param h         控件高
     * @return
     */
    private Bitmap getBitmapFromDrawable(Drawable drawable, ImageView.ScaleType scaleType, int bmpW, int bmpH, int w, int h) {
        Matrix matrix = mView.getMatrix();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            matrix = new Matrix();
            matrix.set(mView.getMatrix());
        }

        /**
         * 支持padding 考虑边框宽度
         */
        int paddingLeft = mView.getPaddingLeft() + mBorderWidth;
        int paddingTop = mView.getPaddingTop() + mBorderWidth;
        int paddingRight = mView.getPaddingRight() + mBorderWidth;
        int paddingBottom = mView.getPaddingBottom() + mBorderWidth;

        /**
         * 实际宽高
         */
        float actualW = w - paddingLeft - paddingRight;
        float actualH = h - paddingTop - paddingBottom;

        /**
         * 宽高缩放比例
         */
        float scaleW = actualW / w;
        float scaleH = actualH / h;

        Bitmap viewBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);//根据view大小创建bitmap
        Canvas viewCanvas = new Canvas(viewBitmap);//根据 viewBitmap 大小创建 canvas 画布
        viewCanvas.translate(paddingLeft, paddingTop);//移动画布,必须先于缩放，避免误差
        viewCanvas.scale(scaleW, scaleH);//缩放画布

        /**
         * 根据 scaleType 处理图片 参考 ImageView 源码 configureBounds()
         */
        float scale;
        float dx = 0, dy = 0;
        switch (scaleType) {
            default:
            case CENTER:

                matrix.setTranslate(Math.round((w - bmpW) * 0.5f), Math.round((h - bmpH) * 0.5f));
                break;
            case FIT_START:
            case FIT_END:
            case FIT_CENTER:

                RectF mTempSrc = new RectF(0, 0, bmpW, bmpH);
                RectF mTempDst = new RectF(0, 0, w, h);
                matrix.setRectToRect(mTempSrc, mTempDst, scaleTypeToScaleToFit(scaleType));
                break;
            case FIT_XY:

                drawable.setBounds(0, 0, w, h);
                matrix = null;
                break;
            case CENTER_CROP:

                if (bmpW * h > w * bmpH) {
                    scale = (float) h / (float) bmpH;
                    dx = (w - bmpW * scale) * 0.5f;
                } else {
                    scale = (float) w / (float) bmpW;
                    dy = (h - bmpH * scale) * 0.5f;
                }

                matrix.setScale(scale, scale);
                matrix.postTranslate(Math.round(dx), Math.round(dy));
                break;
            case CENTER_INSIDE:

                if (bmpW <= w && bmpH <= h) {
                    scale = 1.0f;
                } else {
                    scale = Math.min((float) w / (float) bmpW, (float) h / (float) bmpH);
                }

                dx = Math.round((w - bmpW * scale) * 0.5f);
                dy = Math.round((h - bmpH * scale) * 0.5f);

                matrix.setScale(scale, scale);
                matrix.postTranslate(dx, dy);
                break;
            case MATRIX:

                if (matrix.isIdentity()) {
                    matrix = null;
                }
                break;
        }

        if (matrix != null) viewCanvas.concat(matrix);//设置变化矩阵
        drawable.draw(viewCanvas);//绘制drawable
        return viewBitmap;
    }

    private static Matrix.ScaleToFit scaleTypeToScaleToFit(ImageView.ScaleType st) {
        /**
         * 根据源码改造  sS2FArray[st.nativeInt - 1]
         */
        switch (st) {
            case FIT_XY:
                return Matrix.ScaleToFit.FILL;
            case FIT_START:
                return Matrix.ScaleToFit.START;
            case FIT_END:
                return Matrix.ScaleToFit.END;
            case FIT_CENTER:
                return Matrix.ScaleToFit.CENTER;
            default:
                return Matrix.ScaleToFit.CENTER;
        }
    }

    /**
     * 绘制边框
     *
     * @param canvas
     */
    private void drawBorder(Canvas canvas) {
        if (mBorderWidth > 0) {
            //重新设置 color & width
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setStrokeWidth(mBorderWidth);
            if (mIsCircle) {
                float borderRadiusX = (mBorderRect.width() - mBorderWidth) / 2;
                float borderRadiusY = (mBorderRect.height() - mBorderWidth) / 2;
                canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, Math.min(borderRadiusX, borderRadiusY), mBorderPaint);
            } else {
                updateCornerBorderRadii();
                Path path = new Path();
                path.addRoundRect(mBorderRect, mCornerBorderRadii, Path.Direction.CW);
                canvas.drawPath(path, mBorderPaint);
            }
        }
    }

    /**
     * 更新drawable和border Rect
     */
    private void updateDrawableAndBorderRect() {

        float half = mBorderWidth / 2f;
        if (mIsCircle) {//圆形
            float minRect = Math.min(getWidth(), getHeight());
            mBorderRect.set(half, half, getWidth() - half, getHeight() - half);//边框Rect圆角
            mDrawableRect.set(mBorderRect.left + half, mBorderRect.top + half, minRect - half, minRect - half);//drawableRect
        } else {//圆角
            mBorderRect.set(half, half, getWidth() - half, getHeight() - half);//边框Rect圆角
            mDrawableRect.set(mBorderRect.left + half, mBorderRect.top + half, mBorderRect.right - half, mBorderRect.bottom - half);//drawableRect
        }
    }

    /**
     * 更新bitmap圆角弧度
     */
    private void updateCornerBitmapRadii() {
        if (mCorner >= 0) {
            for (int i = 0; i < mCornerBitmapRadii.length; i++) {
                mCornerBitmapRadii[i] = mCorner;
            }
            return;
        }

        if (mCorner < 0) {
            mCornerBitmapRadii[0] = mCornerTopLeft;
            mCornerBitmapRadii[1] = mCornerTopLeft;
            mCornerBitmapRadii[2] = mCornerTopRight;
            mCornerBitmapRadii[3] = mCornerTopRight;
            mCornerBitmapRadii[4] = mCornerBottomRight;
            mCornerBitmapRadii[5] = mCornerBottomRight;
            mCornerBitmapRadii[6] = mCornerBottomLeft;
            mCornerBitmapRadii[7] = mCornerBottomLeft;
            return;
        }
    }

    /**
     * 更新border圆角弧度
     */
    private void updateCornerBorderRadii() {

        if (mCorner >= 0) {
            for (int i = 0; i < mCornerBorderRadii.length; i++) {
                mCornerBorderRadii[i] = mCorner == 0 ? mCorner : mCorner + mBorderWidth;
            }
            return;
        }

        if (mCorner < 0) {
            mCornerBorderRadii[0] = mCornerTopLeft == 0 ? mCornerTopLeft : mCornerTopLeft + mBorderWidth;
            mCornerBorderRadii[1] = mCornerTopLeft == 0 ? mCornerTopLeft : mCornerTopLeft + mBorderWidth;
            mCornerBorderRadii[2] = mCornerTopRight == 0 ? mCornerTopRight : mCornerTopRight + mBorderWidth;
            mCornerBorderRadii[3] = mCornerTopRight == 0 ? mCornerTopRight : mCornerTopRight + mBorderWidth;
            mCornerBorderRadii[4] = mCornerBottomRight == 0 ? mCornerBottomRight : mCornerBottomRight + mBorderWidth;
            mCornerBorderRadii[5] = mCornerBottomRight == 0 ? mCornerBottomRight : mCornerBottomRight + mBorderWidth;
            mCornerBorderRadii[6] = mCornerBottomLeft == 0 ? mCornerBottomLeft : mCornerBottomLeft + mBorderWidth;
            mCornerBorderRadii[7] = mCornerBottomLeft == 0 ? mCornerBottomLeft : mCornerBottomLeft + mBorderWidth;
            return;
        }

    }

    private int getWidth() {
        return mView.getWidth();
    }

    private int getHeight() {
        return mView.getHeight();
    }

    private void invalidate() {
        mView.invalidate();
    }


    /************************
     * Icon
     ************************/

    public SubImageViewHelper setIconNormal(Drawable icon) {
        this.mIconNormal = icon;
        if (mIconPressed == null) {
            mIconPressed = mIconNormal;
        }
        if (mIconUnable == null) {
            mIconUnable = mIconNormal;
        }
        if (mIconSelected == null) {
            mIconSelected = mIconNormal;
        }
        setIcon();
        return this;
    }

    public Drawable getIconNormal() {
        return mIconNormal;
    }

    public SubImageViewHelper setIconPressed(Drawable icon) {
        this.mIconPressed = icon;
        setIcon();
        return this;
    }

    public Drawable getIconPressed() {
        return mIconPressed;
    }

    public SubImageViewHelper setIconUnable(Drawable icon) {
        this.mIconUnable = icon;
        setIcon();
        return this;
    }

    public Drawable getIconUnable() {
        return mIconUnable;
    }

    public SubImageViewHelper setIconSelected(Drawable icon) {
        this.mIconSelected = icon;
        setIcon();
        return this;
    }

    public Drawable getIconSelected() {
        return mIconSelected;
    }

    private void setIcon() {
        mView.setImageDrawable(mStateDrawable);
        mView.invalidate();
    }

    /************************
     * Border
     ************************/

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public SubImageViewHelper setBorderWidth(int borderWidth) {
        this.mBorderWidth = borderWidth;
        invalidate();
        return this;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public SubImageViewHelper setBorderColor(@ColorInt int borderColor) {
        this.mBorderColor = borderColor;
        invalidate();
        return this;
    }

    /************************
     * Corner
     ************************/
    public float getCorner() {
        return mCorner;
    }

    public SubImageViewHelper setCorner(float corner) {
        this.mCorner = corner;
        init();
        invalidate();
        return this;
    }

    public float getCornerTopLeft() {
        return mCornerTopLeft;
    }

    public SubImageViewHelper setCornerTopLeft(float cornerTopLeft) {
        this.mCorner = -1;
        this.mCornerTopLeft = cornerTopLeft;
        init();
        invalidate();
        return this;
    }

    public float getCornerTopRight() {
        return mCornerTopRight;
    }

    public SubImageViewHelper setCornerTopRight(float cornerTopRight) {
        this.mCorner = -1;
        this.mCornerTopRight = cornerTopRight;
        init();
        invalidate();
        return this;
    }

    public float getCornerBottomLeft() {
        return mCornerBottomLeft;
    }

    public SubImageViewHelper setCornerBottomLeft(float cornerBottomLeft) {
        this.mCorner = -1;
        this.mCornerBottomLeft = cornerBottomLeft;
        init();
        invalidate();
        return this;
    }

    public float getCornerBottomRight() {
        return mCornerBottomRight;
    }

    public SubImageViewHelper setCornerBottomRight(float cornerBottomRight) {
        this.mCorner = -1;
        this.mCornerBottomRight = cornerBottomRight;
        init();
        invalidate();
        return this;
    }

    public SubImageViewHelper setCorner(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        this.mCorner = -1;
        this.mCornerTopLeft = topLeft;
        this.mCornerTopRight = topRight;
        this.mCornerBottomRight = bottomRight;
        this.mCornerBottomLeft = bottomLeft;
        init();
        invalidate();
        return this;
    }

    /**
     * 是否默认ImageView
     *
     * @return
     */
    public boolean isNormal() {
        return mIsNormal;
    }
}
