package com.github.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatTextView;

import com.github.layout.helper.SubTextViewHelper;
import com.github.layout.iface.RHelper;

/**
 * SubTextView
 *
 * @author ZhongDaFeng
 */
public class SubTextView extends AppCompatTextView implements RHelper<SubTextViewHelper> {

    private SubTextViewHelper mHelper;

    public SubTextView(Context context) {
        this(context, null);
    }

    public SubTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHelper = new SubTextViewHelper(context, this, attrs);
    }

    @Override
    public SubTextViewHelper getHelper() {
        return mHelper;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mHelper != null) mHelper.setEnabled(enabled);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mHelper != null) mHelper.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void setSelected(boolean selected) {
        if (mHelper != null) mHelper.setSelected(selected);
        super.setSelected(selected);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mHelper != null) mHelper.drawIconWithText();
    }


}
