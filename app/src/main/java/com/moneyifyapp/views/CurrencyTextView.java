package com.moneyifyapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.moneyifyapp.utils.Utils;

/**
 * Created by Zonnie_Work on 14/07/2014.
 */
public class CurrencyTextView extends TextView
{
    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    private String mCurrency;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     *
     * @param context
     */
    public CurrencyTextView(Context context)
    {
        super(context);
        init(context);
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public CurrencyTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyle
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
    }
}
