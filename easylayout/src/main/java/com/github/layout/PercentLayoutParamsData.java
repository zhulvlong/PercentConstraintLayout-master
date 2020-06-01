package com.github.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.github.layout.R;


public class PercentLayoutParamsData {
    int radius;
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
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PercentLayout);
        radius = a.getDimensionPixelOffset(R.styleable.PercentLayout_layout_radiusCP, 0);
        shadowDx = a.getDimensionPixelOffset(R.styleable.PercentLayout_layout_shadowDxCP, 0);
        shadowDy = a.getDimensionPixelOffset(R.styleable.PercentLayout_layout_shadowDyCP, 0);
        shadowColor = a.getColor(R.styleable.PercentLayout_layout_shadowColorCP, 0x99999999);
        shadowEvaluation = a.getDimensionPixelOffset(R.styleable.PercentLayout_layout_shadowEvaluationCP, 0);


        layout_widthPercent = getPercent(a, R.styleable.PercentLayout_layout_widthCP);
        layout_heightPercent = getPercent(a, R.styleable.PercentLayout_layout_heightCP);
        layout_marginLeftPercent = getPercent(a, R.styleable.PercentLayout_layout_marginLeftCP);
        layout_marginRightPercent = getPercent(a, R.styleable.PercentLayout_layout_marginRightCP);
        layout_marginTopPercent = getPercent(a, R.styleable.PercentLayout_layout_marginTopCP);
        layout_marginBottomPercent = getPercent(a, R.styleable.PercentLayout_layout_marginBottomCP);
        layout_marginStartPercent = getPercent(a, R.styleable.PercentLayout_layout_marginStartCP);
        layout_marginEndPercent = getPercent(a, R.styleable.PercentLayout_layout_marginEndCP);

        a.recycle();
        needClip = radius > 0;
        hasShadow = shadowEvaluation > 0;
    }

    public void initPath(View v) {
        widgetRect = new RectF(v.getLeft(),
                v.getTop(),
                v.getRight(),
                v.getBottom());

        widgetPath = new Path();
        clipPath = new Path();
        clipPath.addRect(widgetRect, Path.Direction.CCW);
        clipPath.addRoundRect(
                widgetRect,
                radius,
                radius,
                Path.Direction.CW
        );
        widgetPath.addRoundRect(
                widgetRect,
                radius,
                radius,
                Path.Direction.CW
        );

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
                radius,
                radius,
                Path.Direction.CW
        );
        widgetPath.addRoundRect(
                widgetRect,
                radius,
                radius,
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
