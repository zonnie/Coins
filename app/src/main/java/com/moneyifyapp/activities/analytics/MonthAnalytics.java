package com.moneyifyapp.activities.analytics;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.fragments.MonthAnalyticsFragment;
import com.moneyifyapp.activities.analytics.fragments.TopCategoryFragment;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.model.enums.Months;
import com.moneyifyapp.utils.JsonServiceYearTransactions;
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
            Utils.setLogo(this,R.drawable.chart);

            initYearTransactiosnFromIntent();
            initDateLabels();

            // Instantiate a fragment and load with fragment manager
            MonthAnalyticsFragment fragment = MonthAnalyticsFragment.newInstance(mMonth, mYear, mYearTransactions);
            getFragmentManager().beginTransaction().add(R.id.container,fragment).commit();

            TopCategoryFragment topFragment = TopCategoryFragment.newInstance(mMonth, mYear, mYearTransactions);
            getFragmentManager().beginTransaction().add(R.id.analytics_monthly_top_category_container, topFragment).commit();

        }
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
    private void initDateLabels()
    {
        loadTextViewAndSetText(R.id.analytics_month_label, Months.getMonthNameByNumber(mMonth));
        loadTextViewAndSetText(R.id.analytics_year_label, String.valueOf(mYear));
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

            textView = (TextView)findViewById(resourceId);
            textView.setText(text);

        return textView;
    }
}
