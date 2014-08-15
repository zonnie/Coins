package com.moneyifyapp.utils;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.moneyifyapp.R;

/**
 * Created by Zonnie_Work on 17/07/2014.
 */
public class AnimationUtils
{
    private static AlphaAnimation sAlphaDown;
    private static AlphaAnimation sAlphaUp;
    private static Animation mRotateAnimation;

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
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.bounce);
    }

    public static Animation getZoomInBounceAnimation(Context context)
    {
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.zoom_in_bounce);
    }

    public static Animation getZoomInBounceLongAnimation(Context context)
    {
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.zoom_in_bounce_long);
    }

    public static Animation getZoomInBounceSmallAnimation(Context context)
    {
       return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.zoom_in_bounce_small);
    }

    public static Animation getmRotateAnimation()
    {
        if(mRotateAnimation == null)
        {
            mRotateAnimation = new RotateAnimation(0.0f, 360.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            mRotateAnimation.setRepeatCount(Animation.INFINITE);
            mRotateAnimation.setDuration(500);
            mRotateAnimation.setInterpolator(new LinearInterpolator());
            mRotateAnimation.setFillAfter(true);
        }

        return mRotateAnimation;
    }
}
