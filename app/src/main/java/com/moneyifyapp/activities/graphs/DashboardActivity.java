package com.moneyifyapp.activities.graphs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Spinner;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.activities.graphs.fragments.BarGraphFragment;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.utils.JsonServiceYearTransactions;
import com.moneyifyapp.utils.Utils;

/**
 *
 */
public class DashboardActivity extends Activity
{
    private Spinner mSpinner;
    private YearTransactions mYearTransactions;

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Utils.initializeActionBar(this);
        Utils.initializeParse(this);
        Utils.setLogo(this,R.drawable.chart);

        String yearTransString = getIntent().getExtras().getString(ExpenseListFragment.YEAR_JSON_KEY);
        mYearTransactions = JsonServiceYearTransactions.getInstance().fromJsonToYearTransactions(yearTransString);


        if (savedInstanceState == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.dashboard_graph_container, new BarGraphFragment())
                    .commit();
            getFragmentManager().beginTransaction()
                    .add(R.id.dashboard_top_spent_container, new BarGraphFragment())
                    .commit();
        }
    }

    /*
    public void addListenerOnSpinnerItemSelection()
    {
        mSpinner = (Spinner) findViewById(R.id.month_spinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                getFragmentManager().beginTransaction()
                        .add(R.id.dashboard_graph_container, BarGraphFragment.newInstance(position, mYearTransactions.get(position), BarGraphFragment.BIG_GRAPH))
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }*/
}