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
import com.moneyifyapp.activities.graphs.fragments.BarGraphFragment;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.model.enums.Months;
import com.moneyifyapp.utils.JsonServiceYearTransactions;
import com.moneyifyapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

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
    private final String MONTH_BAR_GRAPH_TITLE = "Top Categories";
    private final String MONTH_BAR_GRAPH_X_LABELS = "Categories";

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
            initMonthlyOverviewFragment();
            initTopFragment();
            initGraphFragment();
        }
    }

    /**
     */
    private void initMonthlyOverviewFragment()
    {
        MonthAnalyticsFragment fragment = MonthAnalyticsFragment.newInstance(mMonth, mYear, mYearTransactions);
        getFragmentManager().beginTransaction().add(R.id.container,fragment).commit();
    }

    /**
     */
    private void initTopFragment()
    {
        TopCategoryFragment topFragment = TopCategoryFragment.newInstance(mMonth, mYear, mYearTransactions);
        getFragmentManager().beginTransaction().add(R.id.analytics_monthly_top_category_container, topFragment).commit();
    }

    /**
     */
    private void initGraphFragment()
    {
        BarGraphFragment graphFragment = BarGraphFragment.newInstance(buildGraph());
        getFragmentManager().beginTransaction().add(R.id.analytics_monthly_top_category_graph, graphFragment).commit();
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
    private BarGraphFragment.BarGraphParameters buildGraph()
    {
        BarGraphFragment.BarGraphParameters params = new BarGraphFragment.BarGraphParameters(MONTH_BAR_GRAPH_TITLE);
        params.setValues(createMaxListByType(MonthTransactions.SubsetType.EXPENSE));
        params.setYLabels(new ArrayList<String>());
        params.setXAxisTitle(MONTH_BAR_GRAPH_X_LABELS);
        params.mResourceId =  R.drawable.graph_bar_back_red;
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

            textView = (TextView)findViewById(resourceId);
            textView.setText(text);

        return textView;
    }

    /**
     */
    private List<Integer> createMaxListByType(MonthTransactions.SubsetType type)
    {
        List<Integer> result = new ArrayList<Integer>();

        for(MonthTransactions month : mYearTransactions.getItems())
        {
            if(month != null)
            {
                int sum = (int) (month.sumTransactions(type));
                result.add(sum);
            }
            else
                result.add(0);
        }

        return result;
    }
}
