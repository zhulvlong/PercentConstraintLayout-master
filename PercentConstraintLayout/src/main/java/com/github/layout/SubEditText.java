package com.github.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

import com.github.layout.helper.SubTextViewHelper;
import com.github.layout.iface.RHelper;

/**
 * SubEditText
 *
 * @author ZhongDaFeng
 */
public class SubEditText extends AppCompatEditText implements RHelper<SubTextViewHelper> {

    private SubTextViewHelper mHelper;

    public SubEditText(Context context) {
        this(context, null);
    }

    public SubEditText(Context context, AttributeSet attrs) {
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

}
