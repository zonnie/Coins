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
import com.moneyifyapp.activities.analytics.adapters.DateTileAdapter;
import com.moneyifyapp.activities.analytics.dialogs.TransactionListDialog;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.utils.JsonServiceYearTransactions;
import com.moneyifyapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class ByDateFragment extends Fragment
{
    private int mMonth;
    private MonthTransactions mMonthTransactions;
    private YearTransactions mYearTransactions;
    private View mRootView;
    private boolean mNoInsights = false;
    private GridView mGridView;
    private Map<String, Double> mByDateList;

    /**
     */
    public static ByDateFragment newInstance(int month, int year, YearTransactions transactions)
    {
        String yearTransJson = JsonServiceYearTransactions.getInstance().toJson(transactions);

        ByDateFragment fragment = new ByDateFragment();
        Bundle args = new Bundle();
        args.putString(ExpenseListFragment.YEAR_JSON_KEY, yearTransJson);
        args.putInt(ExpenseListFragment.MONTH_KEY, month);
        args.putInt(ExpenseListFragment.YEAR_KEY, year);
        fragment.setArguments(args);
        return fragment;
    }

    public ByDateFragment()
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
            initYearTransactionsFromJson();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_by_date, container, false);

        if (mYearTransactions != null && mMonthTransactions != null)
        {
            mGridView = (GridView) mRootView.findViewById(R.id.month_analytics_by_date_layout);

            initTopCategory();

            mByDateList = mMonthTransactions.getDayToSumMap(MonthTransactions.SubsetType.EXPENSE);
            final List<String> days = new ArrayList<String>();

            for(String key : mByDateList.keySet())
                days.add(key);

            mGridView.setAdapter(new DateTileAdapter(getActivity(), mByDateList));
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    String day =  days.get(position);
                    String title = "Expenses on the " + day + Utils.generateDayInMonthSuffix(day);
                    MonthTransactions transactions = mMonthTransactions.getTransactionByDate(MonthTransactions.SubsetType.EXPENSE, day);
                    TransactionListDialog dialog = new TransactionListDialog(getActivity(), transactions, title);
                    dialog.show();
                }
            });
        }

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
            if(mGridView != null)
                mGridView.setVisibility(View.VISIBLE);
            TextView hint = (TextView) mRootView.findViewById(R.id.month_analytics_by_date_hint);
            if(hint != null)
                hint.setText("Tap a day to see the expenses");
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
