package com.moneyifyapp.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.moneyifyapp.utils.Utils;

/**
 */
public class CondensedTextView extends TextView
{
    private Typeface mTypeFace;

    /**
     */
    public CondensedTextView(Context context)
    {
        super(context);
        init(context);
    }

    /**
     */
    public CondensedTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    /**
     */
    public CondensedTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     *
     */
    private void init(Context context)
    {
        mTypeFace = Typeface.create(Utils.FONT_CONDENSED, Typeface.NORMAL);
        this.setTypeface(mTypeFace);
    }
}
