package com.moneyifyapp.activities.analytics;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.fragments.MonthAnalyticsFragment;
import com.moneyifyapp.activities.analytics.fragments.TopCategoryFragment;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.activities.graphs.fragments.BarGraphFragment;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.model.enums.Months;
import com.moneyifyapp.utils.JsonServiceYearTransactions;
import com.moneyifyapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This is the per-month analytics activity.
 * It is used to detail the transactions and some insights on that month.
 */
public class MonthAnalytics extends Activity
{
    private int mMonth;
    private int mYear;
    private YearTransactions mYearTransactions;
    private final String MONTH_BAR_GRAPH_TITLE = "Top Categories";
    private final String MONTH_BAR_GRAPH_X_LABELS = "Categories";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    /**
     * On create
     */
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
            Utils.setLogo(this, R.drawable.chart);

            initYearTransactiosnFromIntent();

            // Instantiate a fragment and load with fragment manager
            mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            initDateLabels();
        }
    }

    /**
     */
    private void initDateLabels()
    {
        loadTextViewAndSetText(R.id.analytics_month_label, Months.getMonthNameByNumber(mMonth));
        loadTextViewAndSetText(R.id.analytics_year_label, String.valueOf(mYear));
    }


    /**
     */
    private MonthAnalyticsFragment initMonthlyOverviewFragment()
    {
        MonthAnalyticsFragment fragment = MonthAnalyticsFragment.newInstance(mMonth, mYear, mYearTransactions);
        return fragment;
    }

    /**
     */
    private TopCategoryFragment initTopFragment()
    {
        TopCategoryFragment topFragment = TopCategoryFragment.newInstance(mMonth, mYear, mYearTransactions);
        return topFragment;
    }

    /**
     */
    private BarGraphFragment initGraphFragment()
    {
        BarGraphFragment graphFragment = BarGraphFragment.newInstance(buildGraph());
        return graphFragment;
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
            {
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     */
    private BarGraphFragment.BarGraphParameters buildGraph()
    {
        BarGraphFragment.BarGraphParameters params = new BarGraphFragment.BarGraphParameters(MONTH_BAR_GRAPH_TITLE);
        params.setValues(createMaxListByType(MonthTransactions.SubsetType.EXPENSE));
        params.setYLabels(new ArrayList<String>());
        params.setXAxisTitle(MONTH_BAR_GRAPH_X_LABELS);
        params.mResourceId = R.drawable.graph_bar_back_red;
        params.setXLabels(new ArrayList<String>());

        return params;
    }

    /**
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    /**
     */
    private TextView loadTextViewAndSetText(int resourceId, String text)
    {
        TextView textView = null;

        textView = (TextView) findViewById(resourceId);
        textView.setText(text);

        return textView;
    }

    /**
     */
    private List<Integer> createMaxListByType(MonthTransactions.SubsetType type)
    {
        List<Integer> result = new ArrayList<Integer>();

        for (MonthTransactions month : mYearTransactions.getItems())
        {
            if (month != null)
            {
                int sum = (int) (month.sumTransactions(type));
                result.add(sum);
            } else
                result.add(0);
        }

        // Clear list if all values are 0's
        boolean allZeros = true;
        for(Integer cur : result)
            if(cur != 0)
                allZeros = false;

        if(allZeros)
            result.clear();

        return result;
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
                return initGraphFragment();
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
            Locale l = Locale.getDefault();
            switch (position)
            {
                case 0:
                    return getString(R.string.analytics_monthly_overview_title).toUpperCase(l);
                case 1:
                    return getString(R.string.analytics_monthly_Insights_title).toUpperCase(l);
                case 2:
                    return getString(R.string.analytics_monthly_categories_title).toUpperCase(l);

            }
            return null;
        }
    }
}
