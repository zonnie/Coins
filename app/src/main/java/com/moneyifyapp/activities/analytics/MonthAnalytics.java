package com.moneyifyapp.activities.analytics;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.fragments.ByCategoryFragment;
import com.moneyifyapp.activities.analytics.fragments.ByDateFragment;
import com.moneyifyapp.activities.analytics.fragments.MonthAnalyticsFragment;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.utils.JsonServiceYearTransactions;
import com.moneyifyapp.utils.Utils;

/**
 * This is the per-month analytics activity.
 * It is used to detail the transactions and some insights on that month.
 */
public class MonthAnalytics extends Activity
{
    private int mMonth;
    private int mYear;
    private YearTransactions mYearTransactions;

    /**
     * On create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_analytics);
        Utils.animateForward(this);

        if (savedInstanceState == null)
        {
            // Init Parse for data storing
            Utils.initializeParse(this);
            Utils.initializeActionBar(this);
            Utils.setLogo(this, R.drawable.chart);
            Utils.removeActionBar(this);

            initYearTransactiosnFromIntent();

            // Instantiate a fragment and load with fragment manager
            SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(sectionsPagerAdapter);
            initDateLabels();
        }
    }

    /**
     */
    public void onBackClicked(View view)
    {
        onBackPressed();
    }

    /**
     */
    private void initDateLabels()
    {
        loadTextViewAndSetText(R.id.analytics_month_label, Utils.getMonthNameByIndex(mMonth));
        loadTextViewAndSetText(R.id.analytics_year_label, String.valueOf(mYear));
    }


    /**
     */
    private MonthAnalyticsFragment initMonthlyOverviewFragment()
    {
        return MonthAnalyticsFragment.newInstance(mMonth, mYear, mYearTransactions);
    }

    /**
     */
    private ByCategoryFragment initTopFragment()
    {
        return ByCategoryFragment.newInstance(mMonth, mYear, mYearTransactions);
    }

    /**
     */
    private ByDateFragment initByDateFragment()
    {
        return ByDateFragment.newInstance(mMonth, mYear, mYearTransactions);
    }

    /**
     */
    private void initYearTransactiosnFromIntent()
    {
        mMonth = getIntent().getExtras().getInt(ExpenseListFragment.MONTH_KEY);
        mYear = getIntent().getExtras().getInt(ExpenseListFragment.YEAR_KEY);
        String yearTransString = getIntent().getExtras().getString(ExpenseListFragment.YEAR_JSON_KEY);
        mYearTransactions = JsonServiceYearTransactions.getInstance().fromJsonToYearTransactions(yearTransString);
    }

    /**
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Utils.animateBack(this);
    }

    /**
     */
    private TextView loadTextViewAndSetText(int resourceId, String text)
    {
        TextView textView = (TextView) findViewById(resourceId);
        textView.setText(text);

        return textView;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }

    /**
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        /**
         */
        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        /**
         */
        @Override
        public Fragment getItem(int position)
        {
            if (position == 0)
                return initMonthlyOverviewFragment();
            else if (position == 1)
                return initTopFragment();
            else if (position == 2)
                return initByDateFragment();
            else
                return initMonthlyOverviewFragment();
        }

        /**
         */
        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        /**
         */
        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return getString(R.string.analytics_monthly_overview_title);
                case 1:
                    return getString(R.string.analytics_monthly_categories_title);
                case 2:
                    return getString(R.string.analytics_monthly_by_date_title);
            }
            return null;
        }
    }
}
