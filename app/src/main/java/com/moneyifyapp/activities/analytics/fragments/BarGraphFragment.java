package com.moneyifyapp.activities.analytics.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
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
import com.moneyifyapp.utils.Utils;
import com.moneyifyapp.views.CondensedTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A bar graph fragment
 */
public class BarGraphFragment extends Fragment
{
    private Context mContext;
    private LinearLayout mLinearChart;
    private View mView;
    private List<Integer> mValues;
    private List<String> mXAxisLabels;
    private List<Integer> mXAxisIcons;
    private TextView mXAxisTitleTextView;
    public int mBarMargin = 255;
    private int mMaxHeight;
    private int mMaxBarHeight = 300;
    public static final int BIG_GRAPH = 300;
    public static final int MEDIUM_GRAPH = 200;
    public static final int SMALL_GRAPH = 150;
    private int DEFAULT_TEXT_SIZE = 130;
    public static final String GRAPH_ARGS = "graphArgs";
    private Animation mBarAnimation;
    private BarGraphParameters mParameters;
    private boolean mNoInsights;
    private int mBarTextSize;

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
        mContext = getActivity();
        mNoInsights = false;

        if (getArguments() != null)
        {
            String json = getArguments().getString(GRAPH_ARGS);
            mParameters = JsonServiceYearTransactions.getInstance().fromJsonToGraphParams(json);
            mMaxBarHeight = mParameters.mGraphSize;
            mValues = mParameters.mValues;
            mXAxisLabels = mParameters.mXAxisLabels;
            mXAxisIcons = mParameters.mXAxisIcons;
            mBarMargin /= mValues.size();
        }

        mView = inflater.inflate(R.layout.fragment_bar_graph, container, false);

        initViewsByParameters();

        if(shouldDraw())
        {
            mMaxHeight = Collections.max(mValues);
            drawBars();
            //drawXLabels();
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
            TextView title = (TextView) mView.findViewById(R.id.graph_bar_title_label);
            title.setText(mParameters.mTitle);

            if(mParameters.mGraphTitleImage > 0)
                title.setCompoundDrawables(getResources().getDrawable(mParameters.mGraphTitleImage), null, null, null);
            mXAxisTitleTextView = (TextView) mView.findViewById(R.id.graph_x_axis_title);

            if(mParameters.mXAxisTitle != null && !mParameters.mXAxisTitle.isEmpty())
                mXAxisTitleTextView.setText(mParameters.mXAxisTitle);
            else
                mXAxisTitleTextView.setVisibility(View.GONE);

            mBarTextSize = mParameters.mFontSize;
        }
    }

    /**
     */
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
                int height = mValues.get(j);
                drawSingleBarWithValue(height, j);
            }
        }
    }

    /**
     */
    public void drawSingleBarWithValue(long height, int barId)
    {
        // Default background
        int barBackground;
        int leftMargin = (barId == 0) ? 0 : mBarMargin;
        if(barId == mParameters.mSpecialBarsId)
            barBackground = R.drawable.graph_bar_back_yellow;
        else
            barBackground = mParameters.mResourceId;
        Drawable resourceId = getResources().getDrawable(barBackground);

        final long normHeight = normalizeHeight(height);
        final TextView value = generateBarValue(height);

        final LinearLayout bar = new LinearLayout(mContext);
        bar.setBackground(resourceId);
        bar.setId(mParameters.mResourceId);
        bar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) normHeight, 0));
        LinearLayout.LayoutParams barParams = (LinearLayout.LayoutParams) bar.getLayoutParams();
        barParams.setMargins(mBarMargin, 0, mBarMargin, 0);

        final LinearLayout masterLayout = new LinearLayout(mContext);
        masterLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        masterLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) masterLayout.getLayoutParams();
        linearParams.setMargins(0, 0, 0, 0);
        startBarAnimation(masterLayout);

        masterLayout.addView(value);
        masterLayout.addView(bar);

        if(mParameters.mUseIcons)
        {
            ImageView xIcon = createXIcon(mXAxisIcons.get(barId));
            masterLayout.addView(xIcon);
        }
        else
        {
            TextView xLabel = createXTextView(mXAxisLabels.get(barId));
            masterLayout.addView(xLabel);
        }

        mLinearChart.addView(masterLayout);
    }

    /**
     */
    private TextView generateBarValue(long height)
    {
        final TextView textView = new CondensedTextView(mContext);
        textView.setText(Utils.sumWithSuffix(height) + Utils.getDefaultCurrency(mContext));
        textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,mBarTextSize);

        return textView;
    }

    /**
     */
    private TextView createXTextView(String xValue)
    {
        TextView textView = new TextView(mContext);
        textView.setText(xValue);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,mBarTextSize);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    /**
     */
    private ImageView createXIcon(Integer xIcon)
    {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(xIcon);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        return imageView;
    }

    /**
     */
    private long normalizeHeight(long height)
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
            mBarAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_up);
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
            LinearLayout layout = (LinearLayout) mView.findViewById(R.id.category_bar_graph_root);
            if(layout != null)
                layout.setVisibility(View.VISIBLE);
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
        public int mGraphSize;
        public String mTitle;
        public String mXAxisTitle;
        public int mSpecialBarsId;
        public boolean mUseIcons;
        public int mGraphTitleImage;
        public int mFontSize;

        public BarGraphParameters(String title)
        {
            mValues = new ArrayList<Integer>();
            mXAxisLabels = new ArrayList<String>();
            mXAxisIcons = new ArrayList<Integer>();
            mGraphSize = BIG_GRAPH;
            mTitle = title;
            mUseIcons = false;
            mXAxisTitle = "";
            mFontSize = 40;
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
        public void setXAxisTitle(String title)
        {
            this.mXAxisTitle = title;
        }
    }
}