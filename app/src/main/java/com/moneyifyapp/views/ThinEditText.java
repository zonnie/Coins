package com.moneyifyapp.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.moneyifyapp.utils.Utils;

/**
 */
public class ThinEditText extends EditText
{
    private Typeface mTypeFace;

    /**
     */
    public ThinEditText(Context context)
    {
        super(context);
        init(context);
    }

    /**
     */
    public ThinEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    /**
     */
    public ThinEditText(Context context, AttributeSet attrs, int defStyle)
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
