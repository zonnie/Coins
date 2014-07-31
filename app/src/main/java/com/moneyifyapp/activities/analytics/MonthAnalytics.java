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
import com.moneyifyapp.activities.analytics.fragments.BarGraphFragment;
import com.moneyifyapp.activities.analytics.fragments.MonthAnalyticsFragment;
import com.moneyifyapp.activities.analytics.fragments.TopCategoryFragment;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.model.enums.Months;
import com.moneyifyapp.utils.JsonServiceYearTransactions;
import com.moneyifyapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

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
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private int MAX_CATEGORY_NUM = 5;

    /**
     * On create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_analytics);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        if (savedInstanceState == null)
        {
            // Init Parse for data storing
            Utils.initializeParse(this);
            Utils.initializeActionBar(this);
            Utils.setLogo(this, R.drawable.chart);

            initYearTransactiosnFromIntent();

            // Instantiate a fragment and load with fragment manager
            mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
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
        return MonthAnalyticsFragment.newInstance(mMonth, mYear, mYearTransactions);
    }

    /**
     */
    private TopCategoryFragment initTopFragment()
    {
        return TopCategoryFragment.newInstance(mMonth, mYear, mYearTransactions);
    }

    /**
     */
    private BarGraphFragment initGraphFragment()
    {
        return BarGraphFragment.newInstance(buildGraph());
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
        MonthTransactions transactions = mYearTransactions.get(mMonth);
        int topNumber = Math.min(MAX_CATEGORY_NUM, transactions.getNumberOfExpenses());
        List<MonthTransactions.Couple<Integer, Double>> coupleValues = transactions.getTopCategoriesValues(topNumber);

        List<Integer> values = new ArrayList<Integer>();
        List<String> xValues = new ArrayList<String>();
        List<Integer> xIcons = new ArrayList<Integer>();

        // Build the graph details
        for(MonthTransactions.Couple<Integer,Double> cur : coupleValues)
            xIcons.add(Images.getImageByPosition(cur.mFirstField));
        for(MonthTransactions.Couple<Integer,Double> cur : coupleValues)
            values.add(cur.mSecondField.intValue());

        for(MonthTransactions.Couple<Integer,Double> cur : coupleValues)
            xValues.add(Images.getCaptionByImage(Images.getImageByPosition(cur.mFirstField)));

        BarGraphFragment.BarGraphParameters params = new BarGraphFragment.BarGraphParameters(MONTH_BAR_GRAPH_TITLE);
        params.setValues(values);
        params.setXIcons(xIcons);
        params.mGraphSize = BarGraphFragment.MEDIUM_GRAPH;
        params.mUseIcons = true;
        params.setYLabels(new ArrayList<String>());
        params.mResourceId = R.drawable.graph_bar_back_red;
        params.setXLabels(xValues);

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
        TextView textView = (TextView) findViewById(resourceId);
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


        boolean allZeros = true;
        for(Integer cur : result)
            if(cur != 0)
                allZeros = false;

        if(allZeros)
            result.clear();

        return result;
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
            switch (position)
            {
                case 0:
                    return getString(R.string.analytics_monthly_overview_title);
                case 1:
                    return getString(R.string.analytics_monthly_categories_title);
                case 2:
                    return getString(R.string.analytics_monthly_Insights_title);
            }
            return null;
        }
    }
}
