package com.moneyifyapp.model.tutorial;

import android.app.Fragment;

import com.moneyifyapp.guide.fragments.WelcomeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class Tutorial
{
    List<Fragment> mFragList;
    int[] mImagesArray;
    String[] mHeadlinesArray;
    String[] mTopTextArray;
    String[] mBottomTextArray;

    /**
     */
    public Tutorial(TutorialData.TutorialType type)
    {
        mFragList = new ArrayList<Fragment>();
        switch (type)
        {
            case EXPENSE:
                mImagesArray = TutorialData.mExpenseTutorialImages;
                mHeadlinesArray = TutorialData.mExpenseTutorialHeadlines;
                mTopTextArray = TutorialData.mExpenseTutorialTopText;
                mBottomTextArray = TutorialData.mExpenseTutorialBottomText;
                break;
            case MAIN_LIST:
                mImagesArray = TutorialData.mListTutorialImages;
                mHeadlinesArray = TutorialData.mListTutorialHeadlines;
                mTopTextArray = TutorialData.mListTutorialTopText;
                mBottomTextArray = TutorialData.mListTutorialBottomText;
                break;
        }

        buildFragments();
    }

    /**
     */
    private void buildFragments()
    {
        for(int i = 0; i < mImagesArray.length; i++)
        {
            TutorialParams params = new TutorialParams(mImagesArray[i],mHeadlinesArray[i]
                                        ,mTopTextArray[i],mBottomTextArray[i]);

            mFragList.add(WelcomeFragment.newInstance(params));
        }
    }

    /**
     * This class contains tutorial params for the creation of a tutorial
     */
    public static class TutorialParams
    {
        public int mImageResource;
        public String mHeadline;
        public String mTopText;
        public String mBottomText;

        public TutorialParams(int image, String headline, String top, String bottom)
        {
            this.mImageResource = image;
            this.mHeadline = headline;
            this.mTopText = top;
            this.mBottomText = bottom;
        }
    }

    /**
     */
    public Fragment getTutorialFragment(int position)
    {
        if(position >= 0 && position < mFragList.size())
            return mFragList.get(position);
        else
            return null;
    }

    /**
     */
    public boolean isFirst(int position)
    {
        return (position == 0);
    }

    /**
     */
    public boolean isLast(int position)
    {
        return (position == mFragList.size()-1);
    }

    /**
     */
    public int size()
    {
        return mFragList.size();
    }
}
