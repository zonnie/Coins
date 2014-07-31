package com.moneyifyapp.activities.analytics.fragments;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.utils.JsonServiceYearTransactions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A bar graph fragment
 */
public class BarGraphFragment extends Fragment
{
    private LinearLayout mLinearChart;
    private LinearLayout mXAxisContainer;
    private View mView;
    private List<Integer> mValues;
    private List<String> mXAxisLabels;
    private List<String> mYAxisLabels;
    private List<Integer> mXAxisIcons;
    private String mXAxisTitle;
    public int mBarMargin = 140;
    public int mXItemTopMargin = 20;
    private int mMaxHeight;
    private int mMaxBarHeight = 300;
    public static final int BIG_GRAPH = 300;
    public static final int MEDIUM_GRAPH = 200;
    public static final int SMALL_GRAPH = 150;
    private int DEFAULT_TEXT_SIZE = 9;
    public static final String GRAPH_ARGS = "graphArgs";
    private Animation mBarAnimation;
    private BarGraphParameters mParameters;
    private boolean mNoInsights;

    /**
     * Factory to pass some data for different fragments creation.
     * @param parameters - Parameters to draw the graph.
     */
    public static BarGraphFragment newInstance(BarGraphParameters parameters)
    {
        String json = JsonServiceYearTransactions.getInstance().toJson(parameters);
        BarGraphFragment fragment = new BarGraphFragment();
        Bundle args = new Bundle();
        args.putString(GRAPH_ARGS, json);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     */
    public BarGraphFragment(){}

    /**
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mNoInsights = false;

        if (getArguments() != null)
        {
            String json = getArguments().getString(GRAPH_ARGS);
            mParameters = JsonServiceYearTransactions.getInstance().fromJsonToGraphParams(json);
            mMaxBarHeight = mParameters.mGraphSize;
            mValues = mParameters.mValues;
            mXAxisLabels = mParameters.mXAxisLabels;
            mYAxisLabels = mParameters.mYAxisLabels;
            mXAxisTitle = mParameters.mXAxisTitle;
            mBarMargin = mMaxBarHeight/mValues.size();
            mXAxisIcons = mParameters.mXAxisIcons;
        }

        mView = inflater.inflate(R.layout.fragment_bar_graph, container, false);

        initViewsByParameters();

        if(shouldDraw())
        {
            mMaxHeight = Collections.max(mValues);
            drawBars();
            drawXLabels();
        }

        return mView;
    }

    /**
     */
    private void initViewsByParameters()
    {
        int weigtSum = mValues.size();

        if(shouldDraw())
        {
            updateHasInsignts();
            mLinearChart = (LinearLayout) mView.findViewById(R.id.linearChart);
            mLinearChart.setWeightSum(weigtSum);
            mXAxisContainer = (LinearLayout) mView.findViewById(R.id.xAxisLayout);
            mXAxisContainer.setWeightSum(weigtSum);
            TextView title = (TextView) mView.findViewById(R.id.graph_bar_title_label);
            title.setText(mParameters.mTitle);
            TextView xTitle = (TextView) mView.findViewById(R.id.graph_x_axis_title);
            xTitle.setText(mParameters.mXAxisTitle);
        }
    }

    private boolean shouldDraw()
    {
        return mValues.size() > 2;
    }

    /**
     */
    private void drawBars()
    {
        if(!mValues.isEmpty())
        {
            for (int j = 0; j < mValues.size(); j++)
            {
                int leftMargin = (j == 0) ? 0 : mBarMargin;
                int height = mValues.get(j);
                drawSingleBar(height, leftMargin, mXItemTopMargin);
            }
        }
    }

    /**
     */
    private void drawXLabels()
    {
        if(!mXAxisLabels.isEmpty())
        {
            for (int j = 0; j < mValues.size(); j++)
            {
                int leftMargin = (j == 0) ? 0 : mBarMargin;
                if (!mParameters.mUseIcons)
                    createXvalue(mXAxisLabels.get(j), leftMargin, mXItemTopMargin);
                else
                    createXicon(mXAxisIcons.get(j), leftMargin, mXItemTopMargin);
            }
        }
    }

    /**
     */
    public void drawSingleBar(int height, int leftMargin, int topMargin)
    {
        // Default background
        Drawable resourceId = (mParameters.mResourceId != 0) ?
                getResources().getDrawable(mParameters.mResourceId) :
                getResources().getDrawable(R.drawable.graph_bar_back_yellow);

        final LinearLayout view = new LinearLayout(getActivity());
        view.setBackground(resourceId);
        final int normHeight = normalizeHeight(height);
        view.setLayoutParams(new LinearLayout.LayoutParams(0, normHeight, 1));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(leftMargin, topMargin, 0, 0);
        startBarAnimation(view);
        mLinearChart.addView(view);
    }

    /**
     */
    private void createXvalue(String xValue, int leftMargin, int topMargin)
    {
        TextView textView = new TextView(getActivity());
        textView.setTextSize(DEFAULT_TEXT_SIZE);
        textView.setText(xValue);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
        textView.setGravity(Gravity.CENTER);
        params.setMargins(leftMargin, topMargin, 0, 0);
        mXAxisContainer.addView(textView);
    }

    /**
     */
    private void createXicon(Integer xIcon, int leftMargin, int topMargin)
    {
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(xIcon);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        params.setMargins(leftMargin, topMargin, 0, 0);
        mXAxisContainer.addView(imageView);
    }

    /**
     */
    private int normalizeHeight(int height)
    {
        if(mMaxHeight != 0)
            return (mMaxBarHeight * height)/mMaxHeight;
        else
            return 0;

    }

    /**
     */
    private void startBarAnimation(View view)
    {
        if (mBarAnimation == null)
        {
            mBarAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
            mBarAnimation.setDuration(1000);
        }
        view.startAnimation(mBarAnimation);
    }

    /**
     */
    private void updateHasInsignts()
    {
        if(!mNoInsights)
        {
            mNoInsights = true;
            LinearLayout layout = (LinearLayout) mView.findViewById(R.id.bar_graph_root);
            if(layout != null)
                layout.setVisibility(View.VISIBLE);
            TextView hint = (TextView) mView.findViewById(R.id.month_analytics_graph_hint);
            if(hint != null)
                hint.setVisibility(View.GONE);
        }
    }

    /**
     * Used to pass parameters to the graph.
     */
    public static class BarGraphParameters
    {
        public int mResourceId;
        public List<Integer> mValues;
        public List<String> mXAxisLabels;
        public List<Integer> mXAxisIcons;
        public List<String> mYAxisLabels;
        public int mGraphSize;
        public String mTitle;
        public String mXAxisTitle;
        public List<Integer> mSpecialBarsId;
        public boolean mUseIcons;

        public BarGraphParameters(String title)
        {
            mValues = new ArrayList<Integer>();
            mXAxisLabels = new ArrayList<String>();
            mYAxisLabels = new ArrayList<String>();
            mXAxisIcons = new ArrayList<Integer>();
            mGraphSize = BIG_GRAPH;
            mTitle = title;
            mSpecialBarsId = new ArrayList<Integer>();
            mUseIcons = false;
            mXAxisTitle = "";
        }

        /**
         */
        public void setValues(List<Integer> list)
        {
            mValues.clear();

            for(Integer value : list)
                mValues.add(value);
        }

        /**
         */
        public void setXLabels(List<String> list)
        {
            mXAxisLabels.clear();

            for(String value : list)
                mXAxisLabels.add(value);
        }

        public void setXIcons(List<Integer> list)
        {
            mXAxisIcons.clear();
            for(Integer image : list)
                mXAxisIcons.add(image);
        }

        /**
         */
        public void setYLabels(List<String> list)
        {
            mYAxisLabels.clear();

            for(String value : list)
                mYAxisLabels.add(value);
        }

        /**
         */
        public void setXAxisTitle(String title)
        {
            this.mXAxisTitle = title;
        }

        /**
         */
        public void addSpecialBar(int barNumber)
        {
            mSpecialBarsId.add(barNumber);
        }
    }
}