package com.github.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.github.layout.helper.SubViewBaseHelper;
import com.github.layout.iface.RHelper;

/**
 * SubFrameLayout
 *
 * @author ZhongDaFeng
 */
public class SubFrameLayout extends FrameLayout implements RHelper<SubViewBaseHelper> {

    private SubViewBaseHelper mHelper;

    public SubFrameLayout(Context context) {
        this(context, null);
    }

    public SubFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHelper = new SubViewBaseHelper(context, this, attrs);
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
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHelper.onLayout(changed, left, top, right, bottom);
    }
}
