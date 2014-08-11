package com.moneyifyapp.activities.analytics.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.adapters.CategoryTileAdapter;
import com.moneyifyapp.activities.analytics.dialogs.TransactionListDialog;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.utils.JsonServiceYearTransactions;
import com.moneyifyapp.utils.Utils;

import java.util.List;

/**
 */
public class ByCategoryFragment extends Fragment
{
    private int mMonth;
    private int mYear;
    private MonthTransactions mMonthTransactions;
    private YearTransactions mYearTransactions;
    private View mRootView;
    private boolean mNoInsights = false;
    private GridView mGridView;
    private List<MonthTransactions.Couple<Integer,Double>> mCategoryList;

    /**
     */
    public static ByCategoryFragment newInstance(int month, int year, YearTransactions transactions)
    {
        String yearTransJson = JsonServiceYearTransactions.getInstance().toJson(transactions);

        ByCategoryFragment fragment = new ByCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ExpenseListFragment.YEAR_JSON_KEY, yearTransJson);
        args.putInt(ExpenseListFragment.MONTH_KEY, month);
        args.putInt(ExpenseListFragment.YEAR_KEY, year);
        fragment.setArguments(args);
        return fragment;
    }

    public ByCategoryFragment()
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

        mCategoryList = mMonthTransactions.getTopCategoriesValues();
        mGridView = (GridView) mRootView.findViewById(R.id.month_analytics_top_category_layout);
        mGridView.setAdapter(new CategoryTileAdapter(getActivity(), mCategoryList));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                String title = "Expenses on " + getCategoryCaption(position);
                MonthTransactions.Couple<Integer,Double> category = mCategoryList.get(position);
                MonthTransactions transactions = mMonthTransactions.getTransactionByCategory(MonthTransactions.SubsetType.EXPENSE, category.mFirstField);
                TransactionListDialog dialog = new TransactionListDialog(getActivity(), transactions, title);
                dialog.show();
            }
        });

        return mRootView;
    }

    /**
     */
    private String getCategoryCaption(int position)
    {
        int resIndex = mCategoryList.get(position).mFirstField;
        int resource = Images.getImageByPosition(resIndex);
        return Images.getCaptionByImage(resource);
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
            if(mGridView != null)
                mGridView.setVisibility(View.VISIBLE);
            TextView hint = (TextView) mRootView.findViewById(R.id.month_analytics_top_category_hint);
            if(hint != null)
                hint.setText("Tap a category to see the expenses");
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
