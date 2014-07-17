package com.moneyifyapp.utils;

import android.view.animation.AlphaAnimation;

/**
 * Created by Zonnie_Work on 17/07/2014.
 */
public class AnimationUtils
{
    private static AlphaAnimation sAlphaDown;
    private static AlphaAnimation sAlphaUp;

    /**
     */
    public static AlphaAnimation getAlphaDownAnimation()
    {
        if(sAlphaDown == null)
        {
            sAlphaDown = new AlphaAnimation(1.0f, 0.3f);
            sAlphaDown.setDuration(500);
            sAlphaDown.setFillAfter(true);
        }

        return sAlphaDown;
    }

    /**
     */
    public static AlphaAnimation getmAlphaUpAnimation()
    {
        if(sAlphaUp == null)
        {
            sAlphaUp = new AlphaAnimation(0.3f, 1.0f);
            sAlphaUp.setFillAfter(true);
            sAlphaUp.setDuration(500);
        }

        return sAlphaUp;
    }
}
