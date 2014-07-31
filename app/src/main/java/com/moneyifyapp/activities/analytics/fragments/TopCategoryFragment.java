package com.moneyifyapp.activities.analytics.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.adapters.CategoryTileAdapter;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.utils.JsonServiceYearTransactions;
import com.moneyifyapp.utils.Utils;

import java.util.List;

/**
 */
public class TopCategoryFragment extends Fragment
{
    private int mMonth;
    private int mYear;
    private MonthTransactions mMonthTransactions;
    private YearTransactions mYearTransactions;
    private View mRootView;
    private boolean mNoInsights = false;
    private GridView mGridView;

    /**
     */
    public static TopCategoryFragment newInstance(int month, int year, YearTransactions transactions)
    {
        String yearTransJson = JsonServiceYearTransactions.getInstance().toJson(transactions);

        TopCategoryFragment fragment = new TopCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ExpenseListFragment.YEAR_JSON_KEY, yearTransJson);
        args.putInt(ExpenseListFragment.MONTH_KEY, month);
        args.putInt(ExpenseListFragment.YEAR_KEY, year);
        fragment.setArguments(args);
        return fragment;
    }

    public TopCategoryFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.initializeParse(getActivity());
        Utils.initializeActionBar(getActivity());
        Utils.setLogo(getActivity(), R.drawable.chart);

        if (getArguments() != null)
        {
            mMonth = getArguments().getInt(ExpenseListFragment.MONTH_KEY);
            mYear = getArguments().getInt(ExpenseListFragment.YEAR_KEY);

            initYearTransactionsFromJson();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_top_category, container, false);

        if (mYearTransactions != null && mMonthTransactions != null)
            initTopCategory();

        mGridView = (GridView) mRootView.findViewById(R.id.month_analytics_top_category_layout);
        mGridView.setAdapter(new CategoryTileAdapter(getActivity(), mMonthTransactions.getTopCategoriesValues()));

        return mRootView;
    }

    /**
     */
    private void initTopCategory()
    {
        List<MonthTransactions.Couple<Integer, Double>> categorySum = mMonthTransactions.getTopCategoriesValues();

        if (categorySum != null && !categorySum.isEmpty())
            updateHasInsignts();
    }

    private void updateHasInsignts()
    {
        if(!mNoInsights)
        {
            mNoInsights = true;
            GridView layout = (GridView) mRootView.findViewById(R.id.month_analytics_top_category_layout);
            if(layout != null)
                layout.setVisibility(View.VISIBLE);
            TextView hint = (TextView) mRootView.findViewById(R.id.month_analytics_top_category_hint);
            if(hint != null)
                hint.setVisibility(View.GONE);
        }
    }

    /**
     */
    private void initYearTransactionsFromJson()
    {
        // Get the month number, transaction and page id
        String yearTransJson = getArguments().getString(ExpenseListFragment.YEAR_JSON_KEY);
        if (!yearTransJson.isEmpty())
        {
            mYearTransactions = JsonServiceYearTransactions.getInstance().fromJsonToYearTransactions(yearTransJson);
            mMonthTransactions = mYearTransactions.get(mMonth);
        } else
        {
            mYearTransactions = null;
            mMonthTransactions = null;
        }
    }
}
