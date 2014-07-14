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
 */
public class MonthAnalytics extends Activity
{

    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_analytics);

        // Get the month
        int month = getIntent().getExtras().getInt(ExpenseListFragment.MONTH_KEY);
        int year = getIntent().getExtras().getInt(ExpenseListFragment.YEAR_KEY);
        String yearTransString = getIntent().getExtras().getString(ExpenseListFragment.YEAR_JSON_KEY);
        YearTransactions yearTransactions = new Gson().fromJson(yearTransString, YearTransactions.class);

        if (savedInstanceState == null)
        {
            // Init Parse for data storing
            Utils.initializeParse(this);
            Utils.initializeActionBar(this);
            Utils.setLogo(this,R.drawable.chart);

            // Instantiate a fragment
            MonthAnalyticsFragment fragment = MonthAnalyticsFragment.newInstance(month, year, yearTransactions);

            // Tell activity to load fragment
            getFragmentManager().beginTransaction().add(R.id.container,fragment).commit();
        }
    }
}
