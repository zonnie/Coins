package com.moneyifyapp.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.moneyifyapp.utils.Utils;

/**
 */
public class ThinTextView extends TextView
{
    private Typeface mTypeFace;

    /**
     */
    public ThinTextView(Context context)
    {
        super(context);
        init(context);
    }

    /**
     */
    public ThinTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    /**
     */
    public ThinTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     *
     */
    private void init(Context context)
    {
        mTypeFace = Typeface.create(Utils.FONT_THIN, Typeface.NORMAL);
        this.setTypeface(mTypeFace);
    }
}
