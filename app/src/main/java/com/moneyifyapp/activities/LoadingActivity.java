package com.moneyifyapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.utils.AnimationUtils;

/**
 */
public class LoadingActivity extends Activity
{
    private View mProgressView;
    private View mBodyForm;
    private LinearLayout mAnimationLayout;
    private String mAnimationText;

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAnimationText = "Loading stuff...";
    }

    /**
     */
    protected void storeViews()
    {
        mBodyForm = findViewById(R.id.body_form);
        mProgressView = findViewById(R.id.progress_view);
        mAnimationLayout = (LinearLayout)findViewById(R.id.animation_form);
    }


    /**
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show)
    {
        // Set the animation text
        ((TextView)findViewById(R.id.animation_text)).setText(mAnimationText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int mediumTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

            mBodyForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mBodyForm.animate().setDuration(mediumTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mBodyForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mAnimationLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAnimationLayout.animate().setDuration(mediumTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mProgressView.startAnimation(AnimationUtils.getmRotateAnimation());
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    mAnimationLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else
        {
            mAnimationLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            mBodyForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     */
    public void setAnimationText(String text)
    {
        this.mAnimationText = text;
    }
}
