package com.moneyifyapp.activities.analytics;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.dialogs.PickDateDialog;
import com.moneyifyapp.activities.analytics.fragments.BarGraphFragment;
import com.moneyifyapp.activities.analytics.fragments.MonthAnalyticsFragment;
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
    private String YEARLY_GRAPH_TITLE = "Month-to-Month";
    private String CATEGORY_GRAPH_TITLE = "Top Categories";
    private FrameLayout mCategoryLayout;
    private FrameLayout mYearlyLayout;
    private FrameLayout mMonthlyReport;
    private LinearLayout mMonthlyReportExpandable;
    private LinearLayout mGraphsExpandable;
    private Button mExpandMonthlyButton;
    private Button mExpandGraphButton;
    private ImageButton mPickDateButton;
    private TextView mCurrentDate;
    private LinearLayout mTitleLayout;
    private Animation mFadeInAnimation;
    private int YEAR_FONT_SIZE = 8;
    private int CAT_FONT_SIZE = 15;
    private Drawable mCollapseDrawable;
    private Drawable mExpandDrawable;

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
        mFadeInAnimation = Utils.loadFadeInAnimation(this);

        mMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
        mExpandDrawable = getResources().getDrawable(R.drawable.expand);
        mCollapseDrawable = getResources().getDrawable(R.drawable.collapse);

        TransactionHandler mTransactionHandler = TransactionHandler.getInstance(this);
        mYearTransactions = mTransactionHandler.getYearTransactions(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        mTitleLayout = (LinearLayout)findViewById(R.id.month_title_layout);
        mTitleLayout.startAnimation(mFadeInAnimation);
        mCategoryLayout = (FrameLayout)findViewById(R.id.graphs_category_graph_container);
        mYearlyLayout = (FrameLayout)findViewById(R.id.graphs_year_graph_container);
        mMonthlyReport = (FrameLayout) findViewById(R.id.graphs_month_report_container);
        mMonthlyReportExpandable = (LinearLayout)findViewById(R.id.monthly_layout_to_expand);
        mGraphsExpandable = (LinearLayout)findViewById(R.id.graph_layout_to_expand);
        mPickDateButton = (ImageButton)findViewById(R.id.graph_pick_month_button);
        mCurrentDate = (TextView)findViewById(R.id.graph_activity_title);
        mCurrentDate.setText(Months.getMonthNameByNumber(mMonth-1));

        // Animations
        mExpandMonthlyButton = (Button)findViewById(R.id.expend_monthly_button);
        mExpandMonthlyButton.startAnimation(mFadeInAnimation);
        mExpandGraphButton = (Button)findViewById(R.id.expend_graph_button);
        mExpandGraphButton.startAnimation(mFadeInAnimation);

        if (savedInstanceState == null)
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    buildGraphs();
                }
            }, 200);
        }
    }

    /**
     */
    private void buildGraphs()
    {
        TextView hint = (TextView)findViewById(R.id.graph_empty_hint_textview);

        buildCategoryGraphAndReplaceFragment();
        buildYearGraphAndReplaceFragment();
        buildMonthlyOverviewAndReplaceFragment();

        mExpandGraphButton.startAnimation(mFadeInAnimation);
        mExpandMonthlyButton.startAnimation(mFadeInAnimation);

        if(mNoYearExpense && mNoMonthExpense)
            hint.setVisibility(View.VISIBLE);
        else
            hint.setVisibility(View.GONE);
    }

    private void buildMonthlyOverviewAndReplaceFragment()
    {
        MonthAnalyticsFragment monthly = MonthAnalyticsFragment.newInstance(mMonth-1, mYearTransactions.mYear, mYearTransactions);

        if(mYearTransactions.get(mMonth) != null)
        {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_object, R.anim.slide_out_object)
                    .replace(R.id.graphs_month_report_container, monthly)
                    .commit();
            mMonthlyReport.setVisibility(View.VISIBLE);
        }
        else
            mMonthlyReport.setVisibility(View.GONE);
    }

    /**
     */
    private void buildCategoryGraphAndReplaceFragment()
    {
        int categoryVisibility = mCategoryLayout.getVisibility();

        BarGraphFragment.BarGraphParameters categoryParams = buildCategoryGraph();
        if(!mNoMonthExpense)
        {
            categoryParams.mFontSize = CAT_FONT_SIZE;
            mCategoryLayout.setVisibility((categoryVisibility == View.GONE) ? View.VISIBLE : View.VISIBLE);

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_object, R.anim.slide_out_object)
                    .replace(R.id.graphs_category_graph_container, BarGraphFragment.newInstance(categoryParams))
                    .commit();
        }
        else
            mCategoryLayout.setVisibility(View.GONE);
    }

    /**
     */
    private void buildYearGraphAndReplaceFragment()
    {
        int yearlyVisibility = mCategoryLayout.getVisibility();

        BarGraphFragment.BarGraphParameters yearParams = buildYearGraph();
        if(!mNoYearExpense)
        {
            yearParams.mFontSize = YEAR_FONT_SIZE;
            mYearlyLayout.setVisibility((yearlyVisibility == View.GONE) ? View.VISIBLE : View.VISIBLE);

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_object, R.anim.slide_out_object)
                    .replace(R.id.graphs_year_graph_container, BarGraphFragment.newInstance(yearParams))
                    .commit();
        }
        else
            mYearlyLayout.setVisibility(View.GONE);
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
                                R.drawable.graph_bar_back_red, X_AXIS_TITLE, xIcons, R.drawable.top_small);

        if(expenseParams.mValues.get(mMonth-1) > 0)
            mNoYearExpense = false;

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
                                                                 List<String> x,
                                                                 int resourceId, String xTitle, List<Integer> xIcons,
                                                                 int titleResourceId)
    {

        BarGraphFragment.BarGraphParameters expenseParams = new BarGraphFragment.BarGraphParameters(title);
        expenseParams.setValues(createMaxListByType(type));
        expenseParams.setXLabels(x);
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
        if(mMonth != Months.getMonthByName(selected))
        {
            mMonth = Months.getMonthByName(selected);
            mCurrentDate.setText(selected);
            mTitleLayout.startAnimation(mFadeInAnimation);
            mMonthlyReportExpandable.startAnimation(mFadeInAnimation);
            mGraphsExpandable.startAnimation(mFadeInAnimation);
            if (mMonth > 0)
                buildGraphs();
        }
    }

    /**
     */
    public void pickMonthClicked(View view)
    {
        PickDateDialog dialog = new PickDateDialog(this);
        dialog.show();
    }

    /**
     */
    public void clickMonthlyExpand(View view)
    {
        if(mMonthlyReportExpandable.getVisibility() == View.VISIBLE)
            collapseMonthly();
        else
            expandMonthly();

    }

    public void expandMonthly()
    {
        mMonthlyReportExpandable.setVisibility(View.VISIBLE);
        mExpandMonthlyButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.collapse, 0);
    }

    public void collapseMonthly()
    {
        mMonthlyReportExpandable.setVisibility(View.GONE);
        mExpandMonthlyButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expand, 0);
    }

    /**
     */
    public void clickGraphExpand(View view)
    {
        if(mGraphsExpandable.getVisibility() == View.VISIBLE)
            collapseGraphs();
        else
            expandGraphs();

    }

    public void expandGraphs()
    {
        mGraphsExpandable.setVisibility(View.VISIBLE);
        mExpandGraphButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.collapse, 0);
    }

    public void collapseGraphs()
    {
        mGraphsExpandable.setVisibility(View.GONE);
        mExpandGraphButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expand, 0);
    }

}
