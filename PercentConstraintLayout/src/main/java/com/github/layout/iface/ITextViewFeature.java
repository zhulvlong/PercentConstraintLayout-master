package com.github.layout.iface;

import android.view.MotionEvent;

/**
 * TextView特性功能接口
 *
 * @author ZhongDaFeng
 */
public interface ITextViewFeature {
    public void setEnabled(boolean enabled);

    public void onTouchEvent(MotionEvent event);

    public void setSelected(boolean selected);

    public void drawIconWithText();

}