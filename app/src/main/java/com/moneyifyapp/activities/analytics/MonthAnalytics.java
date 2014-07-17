package com.moneyifyapp.activities.analytics;

import android.app.Activity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.fragments.MonthAnalyticsFragment;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.utils.Utils;

/**
 *
 * This is the per-month analytics activity.
 * It is used to detail the transactions and some insights on that month.
 *
 */
public class MonthAnalytics extends Activity
{
    private int mMonth;
    private int mYear;
    private YearTransactions mYearTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_analytics);

        if (savedInstanceState == null)
        {
            // Init Parse for data storing
            Utils.initializeParse(this);
            Utils.initializeActionBar(this);
            Utils.setLogo(this,R.drawable.chart);

            initYearTransactiosnFromIntent();

            // Instantiate a fragment and load with fragment manager
            MonthAnalyticsFragment fragment = MonthAnalyticsFragment.newInstance(mMonth, mYear, mYearTransactions);
            getFragmentManager().beginTransaction().add(R.id.container,fragment).commit();
        }
    }

    private void initYearTransactiosnFromIntent()
    {
        mMonth = getIntent().getExtras().getInt(ExpenseListFragment.MONTH_KEY);
        mYear = getIntent().getExtras().getInt(ExpenseListFragment.YEAR_KEY);
        String yearTransString = getIntent().getExtras().getString(ExpenseListFragment.YEAR_JSON_KEY);
        mYearTransactions = new Gson().fromJson(yearTransString, YearTransactions.class);
    }
}