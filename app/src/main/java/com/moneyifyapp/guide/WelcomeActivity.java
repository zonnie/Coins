package com.moneyifyapp.guide;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.guide.fragments.WelcomeFragment;
import com.moneyifyapp.model.tutorial.Tutorial;
import com.moneyifyapp.model.tutorial.TutorialData;
import com.moneyifyapp.utils.AnimationUtils;
import com.moneyifyapp.utils.Utils;

public class WelcomeActivity extends Activity
        implements WelcomeFragment.OnFragmentInteractionListener
{
    private Tutorial mTutorial;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private int mCurrentPage;
    private TutorialData.TutorialType mType;
    public static final String TUTORIAL_TYPE_KEY = "tutorial_type";
    public static final int TUTORIAL_DONE = 1993;

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.animateForward(this);

        setContentView(R.layout.activity_welcome);

        if(getIntent().getExtras() != null)
            if(getIntent().hasExtra(TUTORIAL_TYPE_KEY))
                mType = TutorialData.TutorialType.valueOf(getIntent().getExtras().getString(TUTORIAL_TYPE_KEY));
            else
                mType = TutorialData.TutorialType.EXPENSE;

        mTutorial = new Tutorial(mType);

        mNextButton = (ImageButton)findViewById(R.id.tutorial_next);
        mPrevButton = (ImageButton)findViewById(R.id.tutorial_prev);
        mCurrentPage = 0;

        moveToPage(mCurrentPage,true);

    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }

    /**
     * Bound to XML onClick
     */
    public void OnNextClick(View view)
    {
        mCurrentPage++;
        if(mCurrentPage == mTutorial.size())
            start();
        else
            moveToPage(mCurrentPage, true);
    }

    /**
     * Bound to XML onClick
     */
    public void OnPrevClick(View view)
    {
        mCurrentPage--;
        moveToPage(mCurrentPage, false);
    }

    private void start()
    {
        Intent intent;

        switch (mType)
        {
            case EXPENSE:
                setResult(TUTORIAL_DONE, getIntent());
                finish();
                break;
            case ANALYTICS:
                break;
            case MAIN_LIST:
                intent = new Intent(WelcomeActivity.this, ExpensesActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    /**
     */
    private void moveToPage(int position, boolean isForward)
    {
        int enter = (isForward) ? R.anim.slide_in_right_object : R.anim.slide_in_object;
        int exit = (isForward) ? R.anim.slide_out_left_object : R.anim.slide_out_object;

        hidePrev(position);
        hideNext(position);

        getFragmentManager().beginTransaction()
                .setCustomAnimations(enter, exit)
                .replace(R.id.tutorial_container, mTutorial.getTutorialFragment(position))
                .commit();
    }

    /**
     */
    @Override
    public void onBackPressed()
    {
    }

    /**
     */
    private void hidePrev(int position)
    {
        if(mTutorial.isFirst(position))
        {
            if(mPrevButton.getVisibility() != View.INVISIBLE)
            {
                mPrevButton.startAnimation(AnimationUtils.getFadeOutAnimation(this));
                mPrevButton.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            if(mPrevButton.getVisibility() != View.VISIBLE)
            {
                mPrevButton.startAnimation(AnimationUtils.getFadeInAnimation(this));
                mPrevButton.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     */
    private void hideNext(int position)
    {
        if(mTutorial.isLast(position))
        {
            mNextButton.startAnimation(AnimationUtils.getFadeInAnimation(this));
            mNextButton.setImageResource(R.drawable.done_tutorial);
        }
        else
            mNextButton.setImageResource(R.drawable.forward);
    }
}
