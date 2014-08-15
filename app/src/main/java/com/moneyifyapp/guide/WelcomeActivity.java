package com.moneyifyapp.guide;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.moneyifyapp.R;
import com.moneyifyapp.guide.fragments.WelcomeFragment;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends Activity
        implements WelcomeFragment.OnFragmentInteractionListener
{
    private List<WelcomeFragment> mFragList;
    private static int[] mTutorialImages = {R.drawable.basic, R.drawable.spend_earn, R.drawable.category_pick, R.drawable.bookmark};
    private static String[] mTutorialHeadlines = {"Let's start with the Basics", "Expense & Income", "Categories", "Favorites"};
    private static String[] mTutorialTopText =
            {"Basically, a transaction must have a description and it\'s amount",
            "Each transaction is either an income or an expense",
            "You can categorize each transaction from our wide veriaty of categories",
            "Use your \"Favorites\" to create new transactions in a pinch"};
    private static String[] mTutorialBottomText =
            {"Optionally you can provide a note for further detail",
            "Tap the button to choose between the two",
            "This will help us share some insights on where you spend your money",
            "Just tap the 'Favorite' to save this transaction for future use"};
    private List<WelcomeFragment.TutorialParams> mParams = new ArrayList<WelcomeFragment.TutorialParams>();
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        mNextButton = (ImageButton)findViewById(R.id.tutorial_next);
        mPrevButton = (ImageButton)findViewById(R.id.tutorial_prev);
        mFragList = new ArrayList<WelcomeFragment>();
        mCurrentPage = 0;

        for(int i = 0; i < mTutorialImages.length; i++)
        {
            WelcomeFragment.TutorialParams params =
                    new WelcomeFragment.TutorialParams(mTutorialImages[i],
                    mTutorialHeadlines[i], mTutorialTopText[i], mTutorialBottomText[i]);
            if(i == 0)
                params.mFirst = true;
            else if(i == mTutorialImages.length-1)
                params.mLast = true;

            mParams.add(params);
            mFragList.add(WelcomeFragment.newInstance(params));
        }

        moveToPage(mCurrentPage);

    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }

    /**
     */
    public void OnNextClick(View view)
    {
        mCurrentPage++;
        moveToPage(mCurrentPage);
    }

    /**
     */
    public void OnPrevClick(View view)
    {
        mCurrentPage--;
        moveToPage(mCurrentPage);
    }

    private void moveToPage(int position)
    {
        if(mParams.get(position).mFirst)
            mPrevButton.setVisibility(View.INVISIBLE);
        else
            mPrevButton.setVisibility(View.VISIBLE);

        if(mParams.get(position).mLast)
            mNextButton.setVisibility(View.INVISIBLE);
        else
            mNextButton.setVisibility(View.VISIBLE);

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_object, R.anim.slide_out_object)
                .replace(R.id.tutorial_container, mFragList.get(position))
                .commit();
    }
}
