package com.moneyifyapp.activities.graphs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.graphs.fragments.BarGraphFragment;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.model.enums.Months;
import com.moneyifyapp.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 */
public class DashboardActivity extends Activity
{
    private YearTransactions mYearTransactions;
    private TransactionHandler mTransactionHandler;
    private final String SPEND_GRAPH = "Spending";
    private final String REVENUE_GRAPH = "Revenues";
    private final String X_AXIS_TITLE = "Month";

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Utils.initializeActionBar(this);
        Utils.setLogo(this,R.drawable.chart);
        mTransactionHandler = TransactionHandler.getInstance(this);
        mYearTransactions = mTransactionHandler.getYearTransactions(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        if (savedInstanceState == null)
        {
            List<String> xLabels = new ArrayList<String>();
            for(String str : Months.getMonthList())
            {
               String label =(str != null) ? str.substring(0, 3) : "";
               xLabels.add(label);
            }

            BarGraphFragment.BarGraphParameters expenseParams = buildGraphParams(SPEND_GRAPH, MonthTransactions.SubsetType.EXPENSE, xLabels,
                    new ArrayList<String>(), R.drawable.graph_bar_back_red, X_AXIS_TITLE);

            boolean noExpense = true;

            for(Integer cur : expenseParams.mValues)
            {
                if(cur > 0)
                {
                    noExpense = false;
                    break;
                }
            }

            if(!noExpense)
            {
                TextView hint = (TextView)findViewById(R.id.dashboard_empty_hint_textview);
                hint.setVisibility(View.GONE);
            }

            if(!noExpense)
            {
                findViewById(R.id.dashboard_spending_graph_container).setVisibility(View.VISIBLE);
                getFragmentManager().beginTransaction()
                        .add(R.id.dashboard_spending_graph_container, BarGraphFragment.newInstance(expenseParams))
                        .commit();
            }
        }
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

    /**
     */
    private BarGraphFragment.BarGraphParameters buildGraphParams(String title, MonthTransactions.SubsetType type,
                                                                 List<String> x, List<String> y,
                                                                 int resourceId, String xTitle)
    {
        BarGraphFragment.BarGraphParameters expenseParams = new BarGraphFragment.BarGraphParameters(title);
        expenseParams.setValues(createMaxListByType(type));
        expenseParams.setXLabels(x);
        expenseParams.setYLabels(y);
        expenseParams.mResourceId = resourceId;
        expenseParams.setXAxisTitle(xTitle);

        return expenseParams;
    }
}