package com.moneyifyapp.activities.graphs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Spinner;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.graphs.fragments.BarGraphFragment;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.utils.Utils;

import java.util.Calendar;

/**
 *
 */
public class DashboardActivity extends Activity
{
    private Spinner mSpinner;
    private YearTransactions mYearTransactions;
    private TransactionHandler mTransactionHandler;

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
            getFragmentManager().beginTransaction()
                    .add(R.id.dashboard_graph_container, BarGraphFragment.newInstance(BarGraphFragment.BIG_GRAPH))
                    .commit();

            /*
            getFragmentManager().beginTransaction()
                    .add(R.id.dashboard_graph_container, new BarGraphFragment())
                    .commit();
            getFragmentManager().beginTransaction()
                    .add(R.id.dashboard_top_spent_container, new BarGraphFragment())
                    .commit();*/
        }
    }
}