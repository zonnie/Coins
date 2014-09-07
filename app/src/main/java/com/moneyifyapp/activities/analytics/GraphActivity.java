package com.moneyifyapp.activities.analytics;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.dialogs.PickDateDialog;
import com.moneyifyapp.activities.analytics.fragments.BarGraphFragment;
import com.moneyifyapp.activities.analytics.fragments.EmptyFragment;
import com.moneyifyapp.activities.analytics.fragments.YearAnalyticsFragment;
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
public class GraphActivity extends Activity
        implements PickDateDialog.DialogClicked,
        AdapterView.OnItemSelectedListener
{
    private YearTransactions mYearTransactions;
    private final String X_AXIS_TITLE = "Month";
    private boolean mNoMonthExpense = false;
    private boolean mNoYearExpense = false;
    private int mMonth;
    private int MAX_CATEGORY_NUM = 5;
    private String YEARLY_GRAPH_TITLE = "Month-to-Month";
    private String CATEGORY_GRAPH_TITLE = "Top Categories";
    private FrameLayout mYearlyViewContainer;
    private ImageButton mPickDateButton;
    private TextView mCurrentMonth;
    private TextView mEmptyHintTextView;
    private Spinner mPickViewSpinner;
    private TextView mCurrentYear;
    private LinearLayout mTitleLayout;
    private Animation mFadeInAnimation;
    private int YEAR_FONT_SIZE = 8;
    private int CAT_FONT_SIZE = 15;
    private int mCurrentSelectedViewPosition = 0;

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

        TransactionHandler mTransactionHandler = TransactionHandler.getInstance(this);
        mYearTransactions = mTransactionHandler.getYearTransactions(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        storeViews();

        mTitleLayout.startAnimation(mFadeInAnimation);
        mCurrentMonth.setText(Months.getMonthNameByNumber(mMonth - 1));
        mCurrentYear.setText("" + mYearTransactions.mYear);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.analytics_views, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPickViewSpinner.setAdapter(adapter);

        if (savedInstanceState == null)
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    buildFragments();
                }
            }, 200);
        }
    }

    /**
     */
    private void storeViews()
    {
        mTitleLayout = (LinearLayout)findViewById(R.id.month_title_layout);
        mYearlyViewContainer = (FrameLayout) findViewById(R.id.yearly_view_container);
        mPickDateButton = (ImageButton)findViewById(R.id.graph_pick_month_button);
        mCurrentMonth = (TextView)findViewById(R.id.graph_activity_month);
        mCurrentYear = (TextView)findViewById(R.id.graph_activity_day);
        mPickViewSpinner = (Spinner) findViewById(R.id.graph_pick_view_spinner);
        mPickViewSpinner.setOnItemSelectedListener(this);
        mEmptyHintTextView = (TextView)findViewById(R.id.graph_empty_hint_textview);
    }

    /**
     */
    private void buildFragments()
    {
        buildCorrectViewBySelectedPosition(mCurrentSelectedViewPosition);
    }

    /**
     */
    private void buildYearlyOverviewAndReplaceFragment()
    {
        YearAnalyticsFragment monthly = YearAnalyticsFragment.newInstance(mMonth-1, mYearTransactions.mYear, mYearTransactions);

        if(mYearTransactions.get(mMonth-1) != null)
        {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_object, R.anim.slide_out_object)
                    .replace(R.id.yearly_view_container, monthly)
                    .commit();
        }
        else
            buildEmptyAndReplaceFragment();
    }

    /**
     */
    private void buildCategoryGraphAndReplaceFragment()
    {
        BarGraphFragment.BarGraphParameters categoryParams = buildCategoryGraph();
        if(!mNoMonthExpense)
        {
            categoryParams.mFontSize = CAT_FONT_SIZE;
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_object, R.anim.slide_out_object)
                    .replace(R.id.yearly_view_container, BarGraphFragment.newInstance(categoryParams))
                    .commit();
        }
        else
            buildEmptyAndReplaceFragment();
    }

    /**
     */
    private void buildYearGraphAndReplaceFragment()
    {
        BarGraphFragment.BarGraphParameters yearParams = buildYearGraph();
        if(!mNoYearExpense)
        {
            yearParams.mFontSize = YEAR_FONT_SIZE;
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_object, R.anim.slide_out_object)
                    .replace(R.id.yearly_view_container, BarGraphFragment.newInstance(yearParams))
                    .commit();
        }
        else
            buildEmptyAndReplaceFragment();
    }

    /**
     */
    private void buildEmptyAndReplaceFragment()
    {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_object, R.anim.slide_out_object)
                .replace(R.id.yearly_view_container, EmptyFragment.newInstance(getResources().getString(R.string.analytics_str_top_graph_hint)))
                .commit();
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
            List<MonthTransactions.Couple<Integer, Integer>> coupleValues = transactions.getTopCategoriesValues(topNumber);

            List<Integer> values = new ArrayList<Integer>();
            List<String> xValues = new ArrayList<String>();
            List<Integer> xIcons = new ArrayList<Integer>();

            // Build the graph details
            for (MonthTransactions.Couple<Integer, Integer> cur : coupleValues)
                xIcons.add(Images.getSmallImageByPosition(cur.mFirstField,  Images.getUnsorted()));
            for (MonthTransactions.Couple<Integer, Integer> cur : coupleValues)
                values.add(cur.mSecondField.intValue());
            for (MonthTransactions.Couple<Integer, Integer> cur : coupleValues)
                xValues.add(Images.getCaptionByImage(Images.getImageByPosition(cur.mFirstField, Images.getUnsorted()), Images.getSorted()));

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
            mCurrentMonth.setText(selected);
            mCurrentYear.setText("" + mYearTransactions.mYear);
            mTitleLayout.startAnimation(mFadeInAnimation);
            if (mMonth > 0)
                buildFragments();
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
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id)
    {
        mCurrentSelectedViewPosition = pos;
        buildFragments();
    }

    /**
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    /**
     */
    private void buildCorrectViewBySelectedPosition(int pos)
    {
        switch (pos)
        {
            case 0:
                buildCategoryGraphAndReplaceFragment();
                break;
            case 1:
                buildYearGraphAndReplaceFragment();
                break;
            case 2:
                buildYearlyOverviewAndReplaceFragment();
                break;
            default:
                buildEmptyAndReplaceFragment();
                break;
        }
    }

    /**
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }

    /**
     */
    private void showHintHideAnalytics(boolean show)
    {
        int showHint = (show) ? View.VISIBLE : View.GONE;
        Animation hintAnimation = (show) ? Utils.createAnimation(this, R.anim.fade_in_long) : Utils.createAnimation(this, R.anim.fade_out);
        int showAnalytics = (show) ? View.GONE : View.VISIBLE;
        Animation analyticsAnimation = (show) ? Utils.createAnimation(this, R.anim.fade_out) : Utils.createAnimation(this, R.anim.fade_in_long);

        mYearlyViewContainer.setVisibility(showAnalytics);
        mYearlyViewContainer.startAnimation(analyticsAnimation);
        mEmptyHintTextView.setVisibility(showHint);
        mEmptyHintTextView.startAnimation(hintAnimation);

    }
}
