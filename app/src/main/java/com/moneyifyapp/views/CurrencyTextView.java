package com.moneyifyapp.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.moneyifyapp.utils.Utils;

/**
 */
public class CurrencyTextView extends TextView
{
    private String mCurrency;
    private Typeface mTypeFace;

    /**
     */
    public CurrencyTextView(Context context)
    {
        super(context);
        init(context);
    }

    /**
     */
    public CurrencyTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    /**
     */
    public CurrencyTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     *
     */
    private void init(Context context)
    {
        if (!isInEditMode())
        {
            mCurrency = Utils.getDefaultCurrency(context);
            setText(mCurrency);
        }
        mTypeFace = Typeface.create(Utils.FONT_CONDENSED, Typeface.NORMAL);
        this.setTypeface(mTypeFace);
    }
}
