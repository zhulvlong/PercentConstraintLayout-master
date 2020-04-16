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

    float layout_marginLeftPercent;
    float layout_marginRightPercent;
    float layout_marginTopPercent;
    float layout_marginBottomPercent;
    float layout_marginStartPercent;
    float layout_marginEndPercent;

    public PercentLayoutParamsData(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PercentLayout);
        radius = a.getDimensionPixelOffset(R.styleable.PercentLayout_layout_radius, 0);
        shadowDx = a.getDimensionPixelOffset(R.styleable.PercentLayout_layout_shadowDx, 0);
        shadowDy = a.getDimensionPixelOffset(R.styleable.PercentLayout_layout_shadowDy, 0);
        shadowColor = a.getColor(R.styleable.PercentLayout_layout_shadowColor, 0x99999999);
        shadowEvaluation = a.getDimensionPixelOffset(R.styleable.PercentLayout_layout_shadowEvaluation, 0);

        layout_marginLeftPercent =a.getFloat(R.styleable.PercentLayout_layout_marginLeftPercent,0);
        layout_marginRightPercent =a.getFloat(R.styleable.PercentLayout_layout_marginRightPercent,0);
        layout_marginTopPercent =a.getFloat(R.styleable.PercentLayout_layout_marginTopPercent,0);
        layout_marginBottomPercent =a.getFloat(R.styleable.PercentLayout_layout_marginBottomPercent,0);

        layout_marginStartPercent=a.getFloat(R.styleable.PercentLayout_layout_marginStartPercent,0);
        layout_marginEndPercent=a.getFloat(R.styleable.PercentLayout_layout_marginEndPercent,0);

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
}
