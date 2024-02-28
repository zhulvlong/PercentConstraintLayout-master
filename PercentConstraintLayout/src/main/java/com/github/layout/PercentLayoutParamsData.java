package com.github.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class PercentLayoutParamsData {

    /**
     * 圆角
     */
    private final float[] rounds;
    int shadowColor;
    int shadowDx;
    int shadowDy;
    int shadowEvaluation;
    RectF widgetRect;
    Path widgetPath;
    Path clipPath;
    boolean needClip;
    boolean hasShadow;

    float layout_widthPercent;
    float layout_heightPercent;
    float layout_marginLeftPercent;
    float layout_marginRightPercent;
    float layout_marginTopPercent;
    float layout_marginBottomPercent;
    float layout_marginStartPercent;
    float layout_marginEndPercent;

    public PercentLayoutParamsData(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PercentConstraintLayout_Layout);
        int radius = a.getDimensionPixelOffset(R.styleable.PercentConstraintLayout_Layout_percent_constraint_radius, 0);
        float roundLT = a.getDimension(R.styleable.PercentConstraintLayout_Layout_percent_constraint_radiusLeftTop, 0);
        float roundRT = a.getDimension(R.styleable.PercentConstraintLayout_Layout_percent_constraint_radiusRightTop, 0);
        float roundRB = a.getDimension(R.styleable.PercentConstraintLayout_Layout_percent_constraint_radiusRightBottom, 0);
        float roundLB = a.getDimension(R.styleable.PercentConstraintLayout_Layout_percent_constraint_radiusLeftBottom, 0);
        shadowDx = a.getDimensionPixelOffset(R.styleable.PercentConstraintLayout_Layout_percent_constraint_shadowDx, 0);
        shadowDy = a.getDimensionPixelOffset(R.styleable.PercentConstraintLayout_Layout_percent_constraint_shadowDy, 0);
        shadowColor = a.getColor(R.styleable.PercentConstraintLayout_Layout_percent_constraint_shadowColor, 0x99999999);
        shadowEvaluation = a.getDimensionPixelOffset(R.styleable.PercentConstraintLayout_Layout_percent_constraint_shadowEvaluation, 0);

        layout_widthPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_width);
        layout_heightPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_height);
        layout_marginLeftPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_marginLeft);
        layout_marginRightPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_marginRight);
        layout_marginTopPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_marginTop);
        layout_marginBottomPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_marginBottom);
        layout_marginStartPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_marginStart);
        layout_marginEndPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_marginEnd);

        a.recycle();
        if (radius > 0 || roundLT > 0 || roundRT > 0 || roundRB > 0 || roundLB > 0) {
            needClip = radius > 0 || roundLT > 0 || roundRT > 0 || roundRB > 0 || roundLB > 0;
        }
        if (radius > 0 && (roundLT == 0 || roundRT == 0 || roundRB == 0 || roundLB == 0)) {
            rounds = new float[]{
                    radius, radius,
                    radius, radius,
                    radius, radius,
                    radius, radius
            };
        } else if (radius > 0 && (roundLT > 0 || roundRT > 0 || roundRB > 0 || roundLB > 0)) {
            float lt = roundLT > 0 ? roundLT : radius;
            float rt = roundRT > 0 ? roundRT : radius;
            float rb = roundRB > 0 ? roundRB : radius;
            float lb = roundLB > 0 ? roundLB : radius;
            rounds = new float[]{
                    lt, lt,
                    rt, rt,
                    rb, rb,
                    lb, lb
            };
        } else if (radius == 0 && (roundLT > 0 || roundRT > 0 || roundRB > 0 || roundLB > 0)) {
            rounds = new float[]{
                    roundLT, roundLT,
                    roundRT, roundRT,
                    roundRB, roundRB,
                    roundLB, roundLB
            };
        } else {
            rounds = new float[]{
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0
            };
        }
        hasShadow = shadowEvaluation > 0;
    }

    public void initPaths(View v) {
        widgetRect = new RectF(v.getLeft(),
                v.getTop(),
                v.getRight(),
                v.getBottom());

        widgetPath = new Path();
        clipPath = new Path();
        clipPath.addRect(widgetRect, Path.Direction.CCW);
        clipPath.addRoundRect(
                widgetRect,
                rounds,
                Path.Direction.CW
        );
        widgetPath.addRoundRect(
                widgetRect,
                rounds,
                Path.Direction.CW
        );
    }

    private static float getPercent(TypedArray array, int index) {
        float percent = 0;
        String percentStr = array.getString(index);
        if (percentStr != null && percentStr.contains("/")) {
            String[] split = percentStr.split("/");
            percent = Float.valueOf(split[0]) / Float.valueOf(split[1]);
        }
        return percent;
    }
}
