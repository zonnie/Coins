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
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.enums.Months;
import com.moneyifyapp.utils.JsonServiceYearTransactions;

import java.util.Collections;

/**
 * A bar graph fragment
 */
public class BarGraphFragment extends Fragment
{
    private LinearLayout mLinearChart;
    private LinearLayout mXAxisContainer;
    private View mView;
    private MonthTransactions mMonthTransaction;
    public static final int BAR_MARGIN = 5;
    private int mMaxHeight;
    private int mMaxBarHeight = 300;
    public static final int BIG_GRAPH = 300;
    public static final int MEDIUM_GRAPH = 200;
    public static final int SMALL_GRAPH = 150;
    public static final String GRAPH_SIZE_ARG = "graphSize";
    private Animation mBarAnimation;

    /**
     * Factory to pass some data for different fragments creation.
     *
     * @param graphSize - Choose between BIG_GRAPH, MEDIUM_GRAPH and SMALL_GRAPH
     */
    public static BarGraphFragment newInstance(int pageId, MonthTransactions monthTransaction, int graphSize)
    {
        String yearTransJson = JsonServiceYearTransactions.getInstance().toJson(monthTransaction);
        BarGraphFragment fragment = new BarGraphFragment();
        Bundle args = new Bundle();
        args.putString(ExpenseListFragment.YEAR_JSON_KEY, yearTransJson);
        args.putInt(GRAPH_SIZE_ARG, graphSize);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     */
    public BarGraphFragment()
    {
    }

    /**
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (getArguments() != null)
        {
            String monthJson = getArguments().getString(ExpenseListFragment.YEAR_JSON_KEY);
            mMaxBarHeight = getArguments().getInt(GRAPH_SIZE_ARG);
            mMonthTransaction = JsonServiceYearTransactions.getInstance().fromJsonToMonthTransactions(monthJson);
        }

        mView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        int weigtSum = mMonthTransaction.getItems().size();

        mLinearChart = (LinearLayout) mView.findViewById(R.id.linearChart);
        mLinearChart.setWeightSum(weigtSum);
        mXAxisContainer = (LinearLayout) mView.findViewById(R.id.xAxisLayout);
        mXAxisContainer.setWeightSum(weigtSum);

        mMaxHeight = Integer.valueOf(Collections.max(mMonthTransaction.getItems()).mValue);

        for (int j = 0; j < mMonthTransaction.getItems().size(); j++)
            drawChart(1, Integer.valueOf(mMonthTransaction.getItems().get(j).mValue));

        for (int j = 0; j < mMonthTransaction.getItems().size(); j++)
        {
            createXvalue(j+1);
        }

        return mView;
    }

    /**
     */
    public void drawChart(int count, int height)
    {
        Drawable resourceId = getResources().getDrawable(R.drawable.graph_bar_back);

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

    private void createXvalue(int xValue)
    {
        TextView textView = new TextView(getActivity());
        textView.setText(Months.getMonthNameByNumber(xValue).substring(0,3));
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
        textView.setGravity(Gravity.CENTER);
        params.setMargins(BAR_MARGIN, BAR_MARGIN, 0, 0);
        mXAxisContainer.addView(textView);
    }

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
}