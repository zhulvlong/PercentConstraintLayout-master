package com.github.layout;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

public class PercentLayoutParams extends ConstraintLayout.LayoutParams{

    private PercentLayoutParamsData data;

    public PercentLayoutParams(Context c, AttributeSet attrs) {
        super(c, attrs);
        data = new PercentLayoutParamsData(c, attrs);
    }

    public PercentLayoutParamsData getData() {
        return data;
    }
}
