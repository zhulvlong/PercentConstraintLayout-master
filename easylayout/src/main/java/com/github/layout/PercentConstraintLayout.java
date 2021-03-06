package com.github.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

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
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int screenWidthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int screenWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int screenHeightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int screenHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        int childCount = getChildCount();

        int paddingLeft = this.getPaddingLeft();
        int paddingRight = this.getPaddingRight();
        int paddingTop = this.getPaddingTop();
        int paddingBottom = this.getPaddingBottom();

        boolean flag = (paddingLeft == 0) && (paddingRight == 0) && (paddingTop == 0) && (paddingBottom == 0);

        //如果PercentConstraintLayout 没有padding值,并且宽高都是固定大小或者mathParent
        if (flag && screenWidthMode == View.MeasureSpec.EXACTLY && screenHeightMode == View.MeasureSpec.EXACTLY) {
            for (int i = 0, size = childCount; i < size; i++) {
                float widthPercent = 0;
                float heightPercent = 0;
                float marginLeftPercent = 0;
                float marginRightPercent = 0;
                float marginTopPercent = 0;
                float marginBottomPercent = 0;

                View childView = getChildAt(i);
                ViewGroup.LayoutParams lp = childView.getLayoutParams();
                int childViewWidth = childView.getMeasuredWidth();
                int childViewHeight = childView.getMeasuredHeight();

                if (lp instanceof PercentLayoutParams && lp instanceof ConstraintLayout.LayoutParams) {
                    PercentLayoutParams elp = (PercentLayoutParams) lp;
                    PercentLayoutParamsData data = elp.getData();

                    widthPercent = data.layout_widthPercent;
                    heightPercent = data.layout_heightPercent;


                    if (data.layout_marginLeftPercent != 0) {
                        marginLeftPercent = data.layout_marginLeftPercent;
                    } else if (data.layout_marginStartPercent != 0) {
                        marginLeftPercent = data.layout_marginStartPercent;
                    }

                    if (data.layout_marginRightPercent != 0) {
                        marginRightPercent = data.layout_marginRightPercent;
                    } else if (data.layout_marginEndPercent != 0) {
                        marginRightPercent = data.layout_marginEndPercent;
                    }

                    marginTopPercent = data.layout_marginTopPercent;
                    marginBottomPercent = data.layout_marginBottomPercent;

                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) childView.getLayoutParams();
                    if (widthPercent != 0) {
                        layoutParams.matchConstraintPercentWidth = widthPercent;
                        childViewWidth = (int) (screenWidth * widthPercent);
                    }
                    if (heightPercent != 0) {
                        layoutParams.matchConstraintPercentHeight = heightPercent;
                        childViewHeight = (int) (screenHeight * heightPercent);
                    }

                    if (marginLeftPercent != 0) {
                        layoutParams.horizontalBias = (screenWidth * marginLeftPercent) / (float) (screenWidth - childViewWidth);
                    } else if (marginRightPercent != 0) {
                        layoutParams.horizontalBias = 1 - ((screenWidth * marginRightPercent) / (float) (screenWidth - childViewWidth));
                    } else if (marginLeftPercent != 0 && marginRightPercent != 0) {
                        layoutParams.horizontalBias = (screenWidth * marginLeftPercent) / (float) (screenWidth - childViewWidth);
                    }

                    if (marginTopPercent != 0) {
                        layoutParams.verticalBias = (screenHeight * marginTopPercent) / (float) (screenHeight - childViewHeight);
                    } else if (marginBottomPercent != 0) {
                        layoutParams.verticalBias = 1 - (screenHeight * marginBottomPercent / (float) (screenHeight - childViewHeight));
                    } else if (marginTopPercent != 0 && marginBottomPercent != 0) {
                        layoutParams.verticalBias = (screenHeight * marginTopPercent) / (float) (screenHeight - childViewHeight);
                    }
                    correctLayoutParams(layoutParams);
                    childView.setLayoutParams(layoutParams);
                }
            }
        }
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

            if (lp instanceof PercentLayoutParams && lp instanceof ConstraintLayout.LayoutParams) {
                PercentLayoutParams elp = (PercentLayoutParams) lp;
                PercentLayoutParamsData data = elp.getData();
                data.initPaths(v);
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) lp;

            }

        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        boolean ret = false;
        if (lp instanceof PercentLayoutParams) {
            PercentLayoutParams elp = (PercentLayoutParams) lp;
            PercentLayoutParamsData data = elp.getData();
            if (isInEditMode()) {//预览模式采用裁剪
                canvas.save();
                canvas.clipPath(data.widgetPath);
                ret = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return ret;
            }
            if (!data.hasShadow && !data.needClip)
                return super.drawChild(canvas, child, drawingTime);
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

    static class LayoutParams extends ConstraintLayout.LayoutParams implements PercentLayoutParams {

        private PercentLayoutParamsData data;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            data = new PercentLayoutParamsData(c, attrs);
        }

        @Override
        public PercentLayoutParamsData getData() {
            return data;
        }
    }

    public static boolean isStatusBarShown(Activity context) {
        WindowManager.LayoutParams params = context.getWindow().getAttributes();
        int paramsFlag = params.flags & (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.i("zhulvlong", paramsFlag == params.flags ? "显示状态栏" : "隐藏状态栏");
        return paramsFlag == params.flags;
    }

    public static int getStatusBarHeight(Activity context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        Log.i("zhulvlong", "状态栏高度" + result);
        return result;
    }

}
