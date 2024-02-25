package com.github.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.github.layout.helper.SubViewBaseHelper;
import com.github.layout.iface.RHelper;

/**
 * SubView
 *
 * @author ZhongDaFeng
 */
public class SubView extends View implements RHelper<SubViewBaseHelper> {


    private SubViewBaseHelper mHelper;

    public SubView(Context context) {
        this(context, null);
    }

    public SubView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHelper = new SubViewBaseHelper(context, this, attrs);
    }

    @Override
    public SubViewBaseHelper getHelper() {
        return mHelper;
    }
}
