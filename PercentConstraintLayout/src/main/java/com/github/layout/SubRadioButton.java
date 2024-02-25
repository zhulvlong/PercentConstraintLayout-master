package com.github.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.github.layout.helper.SubCheckHelper;
import com.github.layout.iface.RHelper;

/**
 * SubRadioButton
 *
 * @author ZhongDaFeng
 */
public class SubRadioButton extends AppCompatRadioButton implements RHelper<SubCheckHelper> {

    private SubCheckHelper mHelper;

    public SubRadioButton(Context context) {
        this(context, null);
    }

    public SubRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHelper = new SubCheckHelper(context, this, attrs);
    }

    @Override
    public SubCheckHelper getHelper() {
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
    public void setChecked(boolean checked) {
        if (mHelper != null) mHelper.setChecked(checked);
        super.setChecked(checked);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mHelper != null) mHelper.drawIconWithText();
    }

}
