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

public class PercentConstraintLayout extends ConstraintLayout {
    private Paint shadowPaint;
    private Paint clipPaint;


    public PercentConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    @Override
    public ConstraintLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PercentLayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams lp) {
        return lp instanceof PercentLayoutParams;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        int childCount = getChildCount();
        int paddingLeft = getPaddingLeft() != 0 ? getPaddingLeft() : getPaddingStart();
        int paddingRight = getPaddingRight() != 0 ? getPaddingRight() : getPaddingEnd();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        boolean flag = View.MeasureSpec.getMode(widthMeasureSpec) == View.MeasureSpec.EXACTLY && View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.EXACTLY;
        //如果PercentConstraintLayout宽高都是固定大小或者mathParent
        if (flag) {
            for (int i = 0, size = childCount; i < size; i++) {
                ChildViewCustomParams childViewParams = new ChildViewCustomParams();
                View childView = getChildAt(i);
                ViewGroup.LayoutParams lp = childView.getLayoutParams();
                int childViewWidth = childView.getMeasuredWidth();
                int childViewHeight = childView.getMeasuredHeight();
                if (checkLayoutParams(lp)) {
                    PercentLayoutParams elp = (PercentLayoutParams) lp;
                    childViewParams.initParams(elp.getData());
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) childView.getLayoutParams();

                    childViewWidth = setWidthPercent(parentWidth, childViewParams, childViewWidth, layoutParams);
                    childViewHeight = setHeightPercent(parentHeight, childViewParams, childViewHeight, layoutParams);

                    setHorizontalMarginPercent(parentWidth, childViewParams, childViewWidth, layoutParams, paddingLeft, paddingRight);
                    setVerticalMarginPercent(parentHeight, childViewParams, childViewHeight, layoutParams, paddingTop, paddingBottom);

                    correctLayoutParams(layoutParams);
                    childView.setLayoutParams(layoutParams);
                }
            }
        }
    }

    private void setVerticalMarginPercent(int screenHeight, ChildViewCustomParams childViewParams, int childViewHeight,
        LayoutParams layoutParams, int paddingTop, int paddingBottom) {
        if (childViewParams.marginTopPercent != 0 && childViewParams.marginBottomPercent == 0) {
            layoutParams.verticalBias = (paddingTop + screenHeight * childViewParams.marginTopPercent) / (float) (screenHeight - childViewHeight);
        } else if (childViewParams.marginTopPercent == 0 && childViewParams.marginBottomPercent != 0) {
            layoutParams.verticalBias = 1 - (paddingBottom + screenHeight * childViewParams.marginBottomPercent / (float) (screenHeight - childViewHeight));
        } else if (childViewParams.marginTopPercent != 0 && childViewParams.marginBottomPercent != 0) {
            layoutParams.matchConstraintPercentHeight = 1 - ((float) (paddingTop + screenHeight * childViewParams.marginTopPercent + paddingBottom + screenHeight * childViewParams.marginBottomPercent) / (float) screenHeight);
            childViewHeight = (int) (screenHeight * layoutParams.matchConstraintPercentHeight);
            layoutParams.verticalBias = (paddingTop + screenHeight * childViewParams.marginTopPercent) / (float) (screenHeight - childViewHeight);
        }
    }

    private void setHorizontalMarginPercent(int screenWidth, ChildViewCustomParams childViewParams, int childViewWidth,
        LayoutParams layoutParams, int paddingLeft, int paddingRight) {
        if (childViewParams.marginLeftPercent != 0 && childViewParams.marginRightPercent == 0) {
            layoutParams.horizontalBias = (paddingLeft + screenWidth * childViewParams.marginLeftPercent) / (float) (screenWidth - childViewWidth);
        } else if (childViewParams.marginLeftPercent == 0 && childViewParams.marginRightPercent != 0) {
            layoutParams.horizontalBias = 1 - ((paddingRight + screenWidth * childViewParams.marginRightPercent) / (float) (screenWidth - childViewWidth));
        } else if (childViewParams.marginLeftPercent != 0 && childViewParams.marginRightPercent != 0) {
            layoutParams.matchConstraintPercentWidth = 1 - ((float) (paddingLeft + screenWidth * childViewParams.marginLeftPercent + paddingRight + screenWidth * childViewParams.marginRightPercent) / (float) screenWidth);
            childViewWidth = (int) (screenWidth * layoutParams.matchConstraintPercentWidth);
            layoutParams.horizontalBias = (paddingLeft + screenWidth * childViewParams.marginLeftPercent) / (float) (screenWidth - childViewWidth);
        }
    }

    private int setHeightPercent(int screenHeight, ChildViewCustomParams childViewParams, int childViewHeight, LayoutParams layoutParams) {
        if (childViewParams.heightPercent != 0) {
            layoutParams.matchConstraintPercentHeight = childViewParams.heightPercent;
            childViewHeight = (int) (screenHeight * layoutParams.matchConstraintPercentHeight);
        } else if (childViewParams.marginTopPercent != 0 && childViewParams.marginBottomPercent != 0) {
            layoutParams.matchConstraintPercentHeight = 1 - childViewParams.marginTopPercent - childViewParams.marginBottomPercent;
            childViewHeight = (int) (screenHeight * layoutParams.matchConstraintPercentHeight);
        }
        return childViewHeight;
    }

    private int setWidthPercent(int screenWidth, ChildViewCustomParams childViewParams, int childViewWidth, LayoutParams layoutParams) {
        if (childViewParams.widthPercent != 0) {
            layoutParams.matchConstraintPercentWidth = childViewParams.widthPercent;
            childViewWidth = (int) (screenWidth * layoutParams.matchConstraintPercentWidth);
        } else if (childViewParams.marginLeftPercent != 0 && childViewParams.marginRightPercent != 0) {
            layoutParams.matchConstraintPercentWidth = 1 - childViewParams.marginLeftPercent - childViewParams.marginRightPercent;
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
        for (int i = 0, size = getChildCount(); i < size; i++) {
            View v = getChildAt(i);
            ViewGroup.LayoutParams lp = v.getLayoutParams();

            if (checkLayoutParams(lp)) {
                PercentLayoutParams elp = (PercentLayoutParams) lp;
                PercentLayoutParamsData data = elp.getData();
                data.initPaths(v);
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) lp;
                v.setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        boolean ret = false;
        if (checkLayoutParams(lp)) {
            PercentLayoutParams elp = (PercentLayoutParams) lp;
            PercentLayoutParamsData data = elp.getData();
            if (isInEditMode()) {//预览模式采用裁剪
                canvas.save();
                canvas.clipPath(data.widgetPath);
                ret = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return ret;
            }
            if (!data.hasShadow && !data.needClip) {
                return super.drawChild(canvas, child, drawingTime);
            }
            //为解决锯齿问题，正式环境采用xfermode
            if (data.hasShadow) {
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
            if (data.needClip) {
                int count = canvas.saveLayer(child.getLeft(), child.getTop(), child.getRight(), child.getBottom(), null, Canvas.ALL_SAVE_FLAG);
                ret = super.drawChild(canvas, child, drawingTime);
                clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                clipPaint.setColor(Color.WHITE);
                canvas.drawPath(data.clipPath, clipPaint);
                clipPaint.setXfermode(null);
                canvas.restoreToCount(count);
            }
        }
        return ret;
    }
}
