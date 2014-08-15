package com.moneyifyapp.guide.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moneyifyapp.R;

/**
 */
public class WelcomeFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;
    private int mTutorialImage;
    private String mTutorialHeadline;
    private String mTutorialTopText;
    private String mTutorialBottomText;
    private View mView;
    private static final String TUTORIAL_IMAGE_KEY = "image";
    private static final String TUTORIAL_HEADLINE_KEY = "head";
    private static final String TUTORIAL_TOP_TEXT_KEY = "top";
    private static final String TUTORIAL_BOTTOM_TEXT_KEY = "bottom";
    private static final String TUTORIAL_FIRST_KEY = "first";
    private static final String TUTORIAL_LAST_KEY = "last";

    /**
     */
    public static WelcomeFragment newInstance(TutorialParams params)
    {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putInt(TUTORIAL_IMAGE_KEY, params.mImageResource);
        args.putString(TUTORIAL_HEADLINE_KEY, params.mHeadline);
        args.putString(TUTORIAL_TOP_TEXT_KEY, params.mTopText);
        args.putString(TUTORIAL_BOTTOM_TEXT_KEY, params.mBottomText);
        args.putBoolean(TUTORIAL_FIRST_KEY, params.mFirst);
        args.putBoolean(TUTORIAL_LAST_KEY, params.mLast);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     */
    public WelcomeFragment()
    {
        // Required empty public constructor
    }

    /**
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            storeArgs();
    }

    /**
     */
    private void storeArgs()
    {
        mTutorialImage = getArguments().getInt(TUTORIAL_IMAGE_KEY);
        mTutorialHeadline = getArguments().getString(TUTORIAL_HEADLINE_KEY);
        mTutorialTopText = getArguments().getString(TUTORIAL_TOP_TEXT_KEY);
        mTutorialBottomText = getArguments().getString(TUTORIAL_BOTTOM_TEXT_KEY);
    }


    /**
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_welcome, container, false);

        initViews();

        return mView;
    }

    /**
     */
    private void initViews()
    {
        ((TextView) mView.findViewById(R.id.welcome_headline)).setText(mTutorialHeadline);
        ((TextView) mView.findViewById(R.id.welcome_main_body)).setText(mTutorialTopText);
        ((ImageView) mView.findViewById(R.id.welcome_main_pic)).setImageResource(mTutorialImage);
        ((TextView) mView.findViewById(R.id.welcome_bottom_text)).setText(mTutorialBottomText);
    }

    /**
     */
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
            mListener.onFragmentInteraction(uri);
    }

    /**
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     */
    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     */
    public interface OnFragmentInteractionListener
    {
        public void onFragmentInteraction(Uri uri);
    }

    /**
     *
     */
    public static class TutorialParams
    {
        public int mImageResource;
        public String mHeadline;
        public String mTopText;
        public String mBottomText;
        public boolean mFirst;
        public boolean mLast;

        public TutorialParams(int image, String headline, String top, String bottom)
        {
            this.mImageResource = image;
            this.mHeadline = headline;
            this.mTopText = top;
            this.mBottomText = bottom;
            this.mFirst = false;
            this.mLast = false;
        }
    }
}
