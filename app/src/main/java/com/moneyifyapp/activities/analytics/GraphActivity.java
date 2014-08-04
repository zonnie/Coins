package com.moneyifyapp.activities.analytics;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.dialogs.PickDateDialog;
import com.moneyifyapp.activities.analytics.fragments.BarGraphFragment;
import com.moneyifyapp.model.Images;
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
public class GraphActivity extends Activity implements PickDateDialog.DialogClicked
{
    private YearTransactions mYearTransactions;
    private final String X_AXIS_TITLE = "Month";
    private boolean mNoMonthExpense = false;
    private boolean mNoYearExpense = false;
    private int mMonth;
    private int MAX_CATEGORY_NUM = 5;
    private String YEARLY_GRAPH_TITLE = "Yearly Expenses";
    private String CATEGORY_GRAPH_TITLE = "Top Expenses by Category";
    private FrameLayout mCategoryLayout;
    private FrameLayout mYearlyLayout;
    private Button mPickDateButton;

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Utils.initializeActionBar(this);
        Utils.setupBackButton(this);
        Utils.removeActionBar(this);
        Utils.animateForward(this);

        mMonth = Calendar.getInstance().get(Calendar.MONTH)+1;

        TransactionHandler mTransactionHandler = TransactionHandler.getInstance(this);
        mYearTransactions = mTransactionHandler.getYearTransactions(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        mCategoryLayout = (FrameLayout)findViewById(R.id.graphs_category_graph_container);
        mYearlyLayout = (FrameLayout)findViewById(R.id.graphs_year_graph_container);
        mPickDateButton = (Button)findViewById(R.id.graph_pick_month_button);
        mPickDateButton.setText(Months.getMonthNameByNumber(mMonth-1));

        if (savedInstanceState == null)
            buildGraphs();
    }

    /**
     */
    private void buildGraphs()
    {
        TextView hint = (TextView)findViewById(R.id.graph_empty_hint_textview);
        int categoryVisibility = mCategoryLayout.getVisibility();
        int yearlyVisibility = mCategoryLayout.getVisibility();

        BarGraphFragment.BarGraphParameters categoryParams = buildCategoryGraph();
        if(!mNoMonthExpense)
        {
            hint.setVisibility(View.GONE);
            mCategoryLayout.setVisibility((categoryVisibility == View.GONE) ? View.VISIBLE : View.VISIBLE);

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_object, R.anim.slide_out_object)
                    .replace(R.id.graphs_category_graph_container, BarGraphFragment.newInstance(categoryParams))
                    .commit();
        }
        else
            mCategoryLayout.setVisibility(View.GONE);

        BarGraphFragment.BarGraphParameters yearParams = buildYearGraph();
        if(!mNoYearExpense)
        {
            hint.setVisibility(View.GONE);
            mYearlyLayout.setVisibility((yearlyVisibility == View.GONE) ? View.VISIBLE : View.VISIBLE);

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_object, R.anim.slide_out_object)
                    .replace(R.id.graphs_year_graph_container, BarGraphFragment.newInstance(yearParams))
                    .commit();
        }
        else
            mYearlyLayout.setVisibility(View.GONE);

        if(mNoYearExpense && mNoMonthExpense)
            hint.setVisibility(View.VISIBLE);

    }

    /**
     */
    private BarGraphFragment.BarGraphParameters buildCategoryGraph()
    {
        mNoMonthExpense = true;
        MonthTransactions transactions = mYearTransactions.get(mMonth-1);
        BarGraphFragment.BarGraphParameters params = null;

        if(transactions != null)
        {
            int topNumber = Math.min(MAX_CATEGORY_NUM, transactions.getNumberOfExpenses());
            List<MonthTransactions.Couple<Integer, Double>> coupleValues = transactions.getTopCategoriesValues(topNumber);

            List<Integer> values = new ArrayList<Integer>();
            List<String> xValues = new ArrayList<String>();
            List<Integer> xIcons = new ArrayList<Integer>();

            // Build the graph details
            for (MonthTransactions.Couple<Integer, Double> cur : coupleValues)
                xIcons.add(Images.getSmallImageByPosition(cur.mFirstField));
            for (MonthTransactions.Couple<Integer, Double> cur : coupleValues)
                values.add(cur.mSecondField.intValue());
            for (MonthTransactions.Couple<Integer, Double> cur : coupleValues)
                xValues.add(Images.getCaptionByImage(Images.getImageByPosition(cur.mFirstField)));

            for (Integer cur : values)
            {
                if (cur > 0)
                {
                    mNoMonthExpense = false;
                    break;
                }
            }

            params = new BarGraphFragment.BarGraphParameters(CATEGORY_GRAPH_TITLE);
            params.setValues(values);
            params.setXIcons(xIcons);
            params.mGraphSize = BarGraphFragment.BIG_GRAPH;
            params.mUseIcons = true;
            params.setYLabels(new ArrayList<String>());
            params.mResourceId = R.drawable.graph_bar_back_red;
            params.setXLabels(xValues);
            params.mGraphTitleImage = R.drawable.top_small;
        }

        return params;
    }

    /**
     */
    private BarGraphFragment.BarGraphParameters buildYearGraph()
    {
        List<String> xLabels = new ArrayList<String>();
        List<Integer> xIcons = new ArrayList<Integer>();
        mNoYearExpense = true;
        BarGraphFragment.BarGraphParameters expenseParams;

        for(String str : Months.getMonthList())
        {
            String label =(str != null) ? str.substring(0, 3) : "";
            xLabels.add(label);
        }

        expenseParams = buildGraphParams(YEARLY_GRAPH_TITLE, MonthTransactions.SubsetType.EXPENSE, xLabels,
                new ArrayList<String>(), R.drawable.graph_bar_back_red, X_AXIS_TITLE, xIcons, R.drawable.top_small);

        for(Integer cur : expenseParams.mValues)
        {
            if(cur > 0)
            {
                mNoYearExpense = false;
                break;
            }
        }

        return expenseParams;
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
                                                                 int resourceId, String xTitle, List<Integer> xIcons,
                                                                 int titleResourceId)
    {

        BarGraphFragment.BarGraphParameters expenseParams = new BarGraphFragment.BarGraphParameters(title);
        expenseParams.setValues(createMaxListByType(type));
        expenseParams.setXLabels(x);
        expenseParams.setYLabels(y);
        expenseParams.mGraphSize = BarGraphFragment.BIG_GRAPH;
        expenseParams.mResourceId = resourceId;
        expenseParams.setXAxisTitle(xTitle);
        expenseParams.setXIcons(xIcons);
        expenseParams.mGraphTitleImage = titleResourceId;
        expenseParams.mSpecialBarsId = mMonth-1;

        return expenseParams;
    }

    /**
     */
    public void OnBackClicked(View view)
    {
        onBackPressed();
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
    @Override
    public void onDialogClick(String selected)
    {
        mMonth = Months.getMonthByName(selected);
        mPickDateButton.setText(selected);
        if(mMonth > 0)
            buildGraphs();
    }

    /**
     */
    public void pickMonthClicked(View view)
    {
        PickDateDialog dialog = new PickDateDialog(this);
        dialog.show();
    }
}
