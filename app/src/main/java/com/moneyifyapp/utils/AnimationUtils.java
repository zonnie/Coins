package com.moneyifyapp.utils;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.moneyifyapp.R;

/**
 * Created by Zonnie_Work on 17/07/2014.
 */
public class AnimationUtils
{
    private static AlphaAnimation sAlphaDown;
    private static AlphaAnimation sAlphaUp;
    private static Animation mBounceAnimation;
    private static Animation mZoomInBounceAnimation;
    private static Animation mZoomInBounceLongAnimation;

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

    /**
     */
    public static Animation getBounceAnimtion(Context context)
    {
        if(mBounceAnimation == null)
            mBounceAnimation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.bounce);

        return mBounceAnimation;
    }

    public static Animation getZoomInBounceAnimation(Context context)
    {
        if(mZoomInBounceAnimation == null)
            mZoomInBounceAnimation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.zoom_in_bounce);

        return mZoomInBounceAnimation;
    }

    public static Animation getZoomInBounceLongAnimation(Context context)
    {
        if(mZoomInBounceLongAnimation == null)
            mZoomInBounceLongAnimation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.zoom_in_bounce_long);

        return mZoomInBounceLongAnimation;
    }

}
