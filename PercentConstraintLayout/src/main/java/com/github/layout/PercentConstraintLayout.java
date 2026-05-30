package com.github.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.layout.helper.SubViewBaseHelper;
import com.github.layout.iface.RHelper;

public class PercentConstraintLayout extends ConstraintLayout implements RHelper<SubViewBaseHelper> {
    private Paint shadowPaint;
    private Paint clipPaint;
    private SubViewBaseHelper mHelper;

    public PercentConstraintLayout(Context context) {
        this(context, null);
    }

    public PercentConstraintLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHelper = new SubViewBaseHelper(context, this, attrs);
        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setDither(true);
        shadowPaint.setFilterBitmap(true);
        shadowPaint.setStyle(Paint.Style.FILL);

        clipPaint = new Paint();
        clipPaint.setAntiAlias(true);
        clipPaint.setDither(true);
        clipPaint.setFilterBitmap(true);
        clipPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public SubViewBaseHelper getHelper() {
        return mHelper;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mHelper.dispatchDraw(canvas);
    }

    @Override
    public ConstraintLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PercentLayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams lp) {
        return lp instanceof PercentLayoutParams;
    }

    /**
     * 自定义控件在onMeasure前篡改MeasureSpec mode
     * 我们强制重写了onMeasure方法，使得控件在父控件约束模式下（MeasureSpec.AT_MOST）下充满屏幕（MeasureSpec.EXACTLY）。
     * 通过MeasureSpec.makeMeasureSpec方法创建了一个新的MeasureSpec，其模式被设置为MeasureSpec.EXACTLY，
     * 表示子控件应该完全按照父控件指定的大小进行布局。
     * 这样的修改可以满足一些特定的布局需求，但是应当谨慎使用，因为它可能会影响布局的灵活性。
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获取原始的MeasureSpec模式和大小
        int parentWidthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int parentHeightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (parentWidthSpecMode == MeasureSpec.AT_MOST) {
            // 根据需求修改模式，例如：强制使控件充满父控件
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY);
        }
        if (parentHeightSpecMode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeight, MeasureSpec.EXACTLY);
        }
        int childCount = getChildCount();
        int paddingLeft = getPaddingLeft() != 0 ? getPaddingLeft() : getPaddingStart();
        int paddingRight = getPaddingRight() != 0 ? getPaddingRight() : getPaddingEnd();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        boolean hasShadowChild = false;
        for (int i = 0, size = childCount; i < size; i++) {
            PercentLayoutViewParams childViewParams = new PercentLayoutViewParams();
            View childView = getChildAt(i);
            ViewGroup.LayoutParams lp = childView.getLayoutParams();
            int childViewWidth = childView.getMeasuredWidth();
            int childViewHeight = childView.getMeasuredHeight();
            if (checkLayoutParams(lp)) {
                PercentLayoutParams elp = (PercentLayoutParams) lp;
                PercentLayoutParamsData data = elp.getData();
                hasShadowChild |= data.hasShadow;
                childViewParams.initParams(data);
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) lp;

                setPaddingPercent(parentWidth, parentHeight, childViewParams, childView);
                childViewWidth = setWidthPercent(parentWidth, childViewParams, childViewWidth, layoutParams);
                childViewHeight = setHeightPercent(parentHeight, childViewParams, childViewHeight, layoutParams);

                setHorizontalMarginPercent(parentWidth, childViewParams, childViewWidth, layoutParams, paddingLeft, paddingRight);
                setVerticalMarginPercent(parentHeight, childViewParams, childViewHeight, layoutParams, paddingTop, paddingBottom);

                correctLayoutParams(layoutParams);
                childView.setLayoutParams(layoutParams);
            }
        }
        updateLayerType(hasShadowChild);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void updateLayerType(boolean hasShadowChild) {
        int layerType = hasShadowChild ? View.LAYER_TYPE_SOFTWARE : View.LAYER_TYPE_NONE;
        if (getLayerType() != layerType) {
            setLayerType(layerType, null);
        }
    }

    private void setPaddingPercent(int screenWidth, int screenHeight, PercentLayoutViewParams childViewParams, View childView) {
        if (childViewParams.paddingLeftPercent == 0 && childViewParams.paddingRightPercent == 0
                && childViewParams.paddingTopPercent == 0 && childViewParams.paddingBottomPercent == 0) {
            return;
        }
        int paddingLeft = childViewParams.paddingLeftPercent != 0 ? (int) (screenWidth * childViewParams.paddingLeftPercent) : childView.getPaddingLeft();
        int paddingRight = childViewParams.paddingRightPercent != 0 ? (int) (screenWidth * childViewParams.paddingRightPercent) : childView.getPaddingRight();
        int paddingTop = childViewParams.paddingTopPercent != 0 ? (int) (screenHeight * childViewParams.paddingTopPercent) : childView.getPaddingTop();
        int paddingBottom = childViewParams.paddingBottomPercent != 0 ? (int) (screenHeight * childViewParams.paddingBottomPercent) : childView.getPaddingBottom();
        childView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    private void setVerticalMarginPercent(int screenHeight, PercentLayoutViewParams childViewParams, int childViewHeight, LayoutParams layoutParams, int paddingTop, int paddingBottom) {
        if (childViewParams.marginTopPercent != 0 && childViewParams.marginBottomPercent == 0) {
            layoutParams.verticalBias = (paddingTop + screenHeight * childViewParams.marginTopPercent) / (float) (screenHeight - childViewHeight);
        } else if (childViewParams.marginTopPercent == 0 && childViewParams.marginBottomPercent != 0) {
            layoutParams.verticalBias = 1 - (paddingBottom + screenHeight * childViewParams.marginBottomPercent / (float) (screenHeight - childViewHeight));
        } else if (childViewParams.marginTopPercent != 0 && childViewParams.marginBottomPercent != 0) {
            layoutParams.height = 0; // 设置width和height为0dp才会生效
            layoutParams.matchConstraintPercentHeight = 1 - ((float) (paddingTop + screenHeight * childViewParams.marginTopPercent + paddingBottom + screenHeight * childViewParams.marginBottomPercent) / (float) screenHeight);
            childViewHeight = (int) (screenHeight * layoutParams.matchConstraintPercentHeight);
            layoutParams.verticalBias = (paddingTop + screenHeight * childViewParams.marginTopPercent) / (float) (screenHeight - childViewHeight);
        }
    }

    private void setHorizontalMarginPercent(int screenWidth, PercentLayoutViewParams childViewParams, int childViewWidth, LayoutParams layoutParams, int paddingLeft, int paddingRight) {
        if (childViewParams.marginLeftPercent != 0 && childViewParams.marginRightPercent == 0) {
            layoutParams.horizontalBias = (paddingLeft + screenWidth * childViewParams.marginLeftPercent) / (float) (screenWidth - childViewWidth);
        } else if (childViewParams.marginLeftPercent == 0 && childViewParams.marginRightPercent != 0) {
            layoutParams.horizontalBias = 1 - ((paddingRight + screenWidth * childViewParams.marginRightPercent) / (float) (screenWidth - childViewWidth));
        } else if (childViewParams.marginLeftPercent != 0 && childViewParams.marginRightPercent != 0) {
            layoutParams.width = 0; // 设置width和height为0dp才会生效
            layoutParams.matchConstraintPercentWidth = 1 - ((float) (paddingLeft + screenWidth * childViewParams.marginLeftPercent + paddingRight + screenWidth * childViewParams.marginRightPercent) / (float) screenWidth);
            childViewWidth = (int) (screenWidth * layoutParams.matchConstraintPercentWidth);
            layoutParams.horizontalBias = (paddingLeft + screenWidth * childViewParams.marginLeftPercent) / (float) (screenWidth - childViewWidth);
        }
    }

    private int setHeightPercent(int screenHeight, PercentLayoutViewParams childViewParams, int childViewHeight, LayoutParams layoutParams) {
        if (childViewParams.heightPercent != 0) {
            layoutParams.matchConstraintPercentHeight = childViewParams.heightPercent;
            layoutParams.height = 0; // 设置width和height为0dp才会生效
            childViewHeight = (int) (screenHeight * layoutParams.matchConstraintPercentHeight);
        }
        return childViewHeight;
    }

    private int setWidthPercent(int screenWidth, PercentLayoutViewParams childViewParams, int childViewWidth, LayoutParams layoutParams) {
        if (childViewParams.widthPercent != 0) {
            layoutParams.matchConstraintPercentWidth = childViewParams.widthPercent;
            layoutParams.width = 0; // 设置width和height为0dp才会生效
            childViewWidth = (int) (screenWidth * layoutParams.matchConstraintPercentWidth);
        }
        return childViewWidth;
    }


    private void correctLayoutParams(ConstraintLayout.LayoutParams layoutParams) {
        if (layoutParams.matchConstraintPercentWidth < 0) {
            layoutParams.matchConstraintPercentWidth = 0;
        } else if (layoutParams.matchConstraintPercentWidth > 1) {
            layoutParams.matchConstraintPercentWidth = 1;
        }

        if (layoutParams.matchConstraintPercentHeight < 0) {
            layoutParams.matchConstraintPercentHeight = 0;
        } else if (layoutParams.matchConstraintPercentHeight > 1) {
            layoutParams.matchConstraintPercentHeight = 1;
        }

        if (layoutParams.horizontalBias < 0) {
            layoutParams.horizontalBias = 0;
        } else if (layoutParams.horizontalBias > 1) {
            layoutParams.horizontalBias = 1;
        }

        if (layoutParams.verticalBias < 0) {
            layoutParams.verticalBias = 0;
        } else if (layoutParams.verticalBias > 1) {
            layoutParams.verticalBias = 1;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHelper.onLayout(changed, left, top, right, bottom);
        for (int i = 0, size = getChildCount(); i < size; i++) {
            View v = getChildAt(i);
            ViewGroup.LayoutParams lp = v.getLayoutParams();

            if (checkLayoutParams(lp)) {
                PercentLayoutParams elp = (PercentLayoutParams) lp;
                PercentLayoutParamsData data = elp.getData();
                data.initPaths(v);
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (!checkLayoutParams(lp)) {
            return super.drawChild(canvas, child, drawingTime);
        }

        PercentLayoutParams elp = (PercentLayoutParams) lp;
        PercentLayoutParamsData data = elp.getData();
        if (isInEditMode()) {//预览模式采用裁剪
            if (data.hasShadow) {
                drawShadow(canvas, data);
            }
            if (data.needClip) {
                canvas.save();
                canvas.clipPath(data.widgetPath);
                boolean ret = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return ret;
            }
            return super.drawChild(canvas, child, drawingTime);
        }

        if (!data.hasShadow && !data.needClip) {
            return super.drawChild(canvas, child, drawingTime);
        }
        //为解决锯齿问题，正式环境采用xfermode
        if (data.hasShadow) {
            drawShadow(canvas, data);
        }
        if (data.needClip) {
            return drawClipChild(canvas, child, drawingTime, data);
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    private void drawShadow(Canvas canvas, PercentLayoutParamsData data) {
        int count = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        shadowPaint.setShadowLayer(data.shadowEvaluation, data.shadowDx, data.shadowDy, data.shadowColor);
        shadowPaint.setColor(data.shadowColor);
        canvas.drawPath(data.widgetPath, shadowPaint);
        shadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        shadowPaint.setColor(Color.WHITE);
        canvas.drawPath(data.widgetPath, shadowPaint);
        shadowPaint.setXfermode(null);
        canvas.restoreToCount(count);
    }

    private boolean drawClipChild(Canvas canvas, View child, long drawingTime, PercentLayoutParamsData data) {
        int count = canvas.saveLayer(child.getLeft(), child.getTop(), child.getRight(), child.getBottom(), null, Canvas.ALL_SAVE_FLAG);
        boolean ret = super.drawChild(canvas, child, drawingTime);
        // PorterDuff.Mode.CLEAR 擦除子控件的四个角，露出圆角效果
        clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        clipPaint.setColor(Color.WHITE);
        canvas.drawPath(data.clipPath, clipPaint);
        clipPaint.setXfermode(null);
        canvas.restoreToCount(count);
        return ret;
    }
}
