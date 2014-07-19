package com.moneyifyapp.activities.graphs.fragments;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    public static final int BAR_MARGIN = 5;
    private int mMaxHeight;
    private int mMaxBarHeight = 300;
    public static final int BIG_GRAPH = 300;
    public static final int MEDIUM_GRAPH = 200;
    public static final int SMALL_GRAPH = 150;
    private int DEFAULT_TEXT_SIZE = 7;
    public static final String GRAPH_SIZE_ARG = "graphSize";
    public static final String GRAPH_COLOR_ARG = "color";
    public static final String GRAPH_ARGS = "graphArgs";
    private Animation mBarAnimation;
    private BarGraphParameters mParameters;

    /**
     * Factory to pass some data for different fragments creation.
     *
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
        if (getArguments() != null)
        {
            String json = getArguments().getString(GRAPH_ARGS);
            mParameters = JsonServiceYearTransactions.getInstance().fromJsonToGraphParams(json);
            mMaxBarHeight = mParameters.mGraphSize;
            mValues = mParameters.mValues;
            mXAxisLabels = mParameters.mXAxisLabels;
            mYAxisLabels = mParameters.mYAxisLabels;
        }

        mView = inflater.inflate(R.layout.fragment_bar_graph, container, false);

        int weigtSum = mValues.size();

        mLinearChart = (LinearLayout) mView.findViewById(R.id.linearChart);
        mLinearChart.setWeightSum(weigtSum);
        mXAxisContainer = (LinearLayout) mView.findViewById(R.id.xAxisLayout);
        mXAxisContainer.setWeightSum(weigtSum);
        TextView title = (TextView) mView.findViewById(R.id.graph_bar_title_label);
        title.setText(mParameters.mTitle);

        mMaxHeight = Collections.max(mValues);

        drawBars();

        drawXLabels();

        return mView;
    }

    /**
     */
    private void drawBars()
    {
        for (int j = 0; j < mValues.size(); j++)
        {
            int height = mValues.get(j);
            drawSingleBar(1, height);
        }
    }

    /**
     */
    private void drawXLabels()
    {
        for (int j = 0; j < mValues.size(); j++)
            createXvalue(mXAxisLabels.get(j));
    }

    /**
     */
    public void drawSingleBar(int count, int height)
    {

        Drawable resourceId = (mParameters.mResourceId != 0) ?
                getResources().getDrawable(mParameters.mResourceId) :
                getResources().getDrawable(R.drawable.graph_bar_back_yellow);

        for (int i = 1; i <= count; i++)
        {
            final View view = new View(getActivity());
            view.setBackground(resourceId);
            final int normHeight = normalizeHeight(height);
            view.setLayoutParams(new LinearLayout.LayoutParams(0, normHeight, 1));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.setMargins(BAR_MARGIN, BAR_MARGIN, 0, 0);
            startBarAnimation(view);
            mLinearChart.addView(view);
        }
    }

    private void createXvalue(String xValue)
    {
        TextView textView = new TextView(getActivity());
        textView.setTextSize(DEFAULT_TEXT_SIZE);
        textView.setText(xValue);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
        textView.setGravity(Gravity.CENTER);
        params.setMargins(BAR_MARGIN, BAR_MARGIN, 0, 0);
        mXAxisContainer.addView(textView);
    }

    /**
     */
    private int normalizeHeight(int height)
    {
        //return (1-alpha)&height + alpha*target;
        return (mMaxBarHeight * height) / mMaxHeight;
    }

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
     * Used to pass parameters to the graph.
     */
    public static class BarGraphParameters
    {
        public int mResourceId;
        public List<Integer> mValues;
        public List<String> mXAxisLabels;
        public List<String> mYAxisLabels;
        public int mGraphSize;
        public String mTitle;

        public BarGraphParameters(String title)
        {
            mValues = new ArrayList<Integer>();
            mXAxisLabels = new ArrayList<String>();
            mYAxisLabels = new ArrayList<String>();
            mGraphSize = MEDIUM_GRAPH;
            mTitle = title;
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

        /**
         */
        public void setYLabels(List<String> list)
        {
            mYAxisLabels.clear();

            for(String value : list)
                mYAxisLabels.add(value);
        }
    }
}