package com.github.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class PercentLayoutParamsData {

    /**
     * 圆角原始值（用于 RTL 恢复）
     */
    private final float[] originalRounds;
    /**
     * 圆角（RTL 下可能被交换）
     */
    private final float[] rounds;
    int shadowColor;
    int shadowDx;
    int shadowDy;
    int shadowEvaluation;
    /**
     * 控件矩形 （widgetRect = widgetPath + clipPath）
     */
    RectF widgetRect;
    /**
     * 控件圆角矩形 路径
     */
    Path widgetPath;
    /**
     * 外矩形减去内圆角矩形" 的形状，即控件四个角区域
     */
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
    float layout_paddingLeftPercent;
    float layout_paddingRightPercent;
    float layout_paddingTopPercent;
    float layout_paddingBottomPercent;
    float layout_paddingStartPercent;
    float layout_paddingEndPercent;

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
        layout_paddingLeftPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_paddingLeft);
        layout_paddingRightPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_paddingRight);
        layout_paddingTopPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_paddingTop);
        layout_paddingBottomPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_paddingBottom);
        layout_paddingStartPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_paddingStart);
        layout_paddingEndPercent = getPercent(a, R.styleable.PercentConstraintLayout_Layout_percent_constraint_paddingEnd);

        a.recycle();
        if (radius > 0 || roundLT > 0 || roundRT > 0 || roundRB > 0 || roundLB > 0) {
            needClip = radius > 0 || roundLT > 0 || roundRT > 0 || roundRB > 0 || roundLB > 0;
        }
        if (radius > 0 && (roundLT == 0 || roundRT == 0 || roundRB == 0 || roundLB == 0)) {
            originalRounds = new float[]{
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
            originalRounds = new float[]{
                    lt, lt,
                    rt, rt,
                    rb, rb,
                    lb, lb
            };
        } else if (radius == 0 && (roundLT > 0 || roundRT > 0 || roundRB > 0 || roundLB > 0)) {
            originalRounds = new float[]{
                    roundLT, roundLT,
                    roundRT, roundRT,
                    roundRB, roundRB,
                    roundLB, roundLB
            };
        } else {
            originalRounds = new float[]{
                    0, 0,
                    0, 0,
                    0, 0,
                    0, 0
            };
        }
        rounds = new float[8];
        System.arraycopy(originalRounds, 0, rounds, 0, 8);
        hasShadow = shadowEvaluation > 0;
    }

    public void initPaths(View v) {
        if (!needClip && !hasShadow) {
            return;
        }
        if (widgetRect == null) {
            widgetRect = new RectF();
        }
        if (widgetPath == null) {
            widgetPath = new Path();
        } else {
            widgetPath.reset();
        }
        if (clipPath == null) {
            clipPath = new Path();
        } else {
            clipPath.reset();
        }
        // 从原始值恢复，再根据 RTL 方向决定是否交换左右圆角
        System.arraycopy(originalRounds, 0, rounds, 0, 8);
        if (v.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            // 交换左上 ↔ 右上
            float temp0 = rounds[0], temp1 = rounds[1];
            rounds[0] = rounds[2];
            rounds[1] = rounds[3];
            rounds[2] = temp0;
            rounds[3] = temp1;
            // 交换左下 ↔ 右下
            float temp6 = rounds[6], temp7 = rounds[7];
            rounds[6] = rounds[4];
            rounds[7] = rounds[5];
            rounds[4] = temp6;
            rounds[5] = temp7;
        }
        widgetRect.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        clipPath.addRect(widgetRect, Path.Direction.CCW);
        clipPath.addRoundRect(widgetRect, rounds, Path.Direction.CW);
        widgetPath.addRoundRect(widgetRect, rounds, Path.Direction.CW);
    }

    private static float getPercent(TypedArray array, int index) {
        String percentStr = array.getString(index);
        if (percentStr == null || !percentStr.contains("/")) {
            return 0;
        }
        String[] split = percentStr.split("/");
        if (split.length != 2) {
            return 0;
        }
        try {
            float numerator = Float.parseFloat(split[0].trim());
            float denominator = Float.parseFloat(split[1].trim());
            if (denominator == 0) {
                return 0;
            }
            float percent = numerator / denominator;
            if (Float.isNaN(percent) || Float.isInfinite(percent)) {
                return 0;
            }
            return percent;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
