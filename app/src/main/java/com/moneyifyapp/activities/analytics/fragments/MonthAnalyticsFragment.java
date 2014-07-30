package com.moneyifyapp.activities.analytics.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.adapters.ExpenseItemAdapterRead;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.model.enums.Months;
import com.moneyifyapp.utils.JsonServiceYearTransactions;
import com.moneyifyapp.utils.Utils;

/**
 * An analytics fragment that represents a month
 * It can be used in any activity that can supply it's
 * basic info.
 */
public class MonthAnalyticsFragment extends Fragment
{
    private int mMonth;
    private int mYear;
    private MonthTransactions mMonthTransactions;
    private YearTransactions mYearTransactions;
    private Animation mAppearAnimation;
    private Animation mAppearAnimationLong;
    private LinearLayout mDateLabelLayout;
    private LinearLayout mMainContainerLayout;
    private ListView mBiggestExpenseList;
    private LinearLayout mBiggestExpenseLayout;
    private ListView mBiggestIncomeList;
    private LinearLayout mBiggestIncomeLayout;
    private View mRootView;
    private static JsonServiceYearTransactions mJsonService;
    private boolean mNoInsights;


    private static final String PROFITED_LABEL = "Profit";
    private static final String LOST_LABEL = "Loss";
    private static final String BROKE_EVEN = "Broke even";

    /**
     */
    public static MonthAnalyticsFragment newInstance(int month, int year, YearTransactions transactions)
    {
        mJsonService = JsonServiceYearTransactions.getInstance();
        String yearTransJson = mJsonService.toJson(transactions);

        MonthAnalyticsFragment fragment = new MonthAnalyticsFragment();
        Bundle args = new Bundle();
        args.putString(ExpenseListFragment.YEAR_JSON_KEY, yearTransJson);
        args.putInt(ExpenseListFragment.MONTH_KEY, month);
        args.putInt(ExpenseListFragment.YEAR_KEY, year);
        fragment.setArguments(args);
        return fragment;
    }

    public MonthAnalyticsFragment()
    {
    }

    /**
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Init Parse for data storing
        mNoInsights = false;
        Utils.initializeParse(getActivity());
        Utils.initializeActionBar(getActivity());
        Utils.setLogo(getActivity(), R.drawable.chart);
        mMonth = getArguments().getInt(ExpenseListFragment.MONTH_KEY);
        mYear = getArguments().getInt(ExpenseListFragment.YEAR_KEY);

        // Load animations
        loadAnimations();

        if (getArguments() != null)
            initYearTransactionsFromJson();
    }

    /**
     * On create view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_analytics_total, container, false);

        if (mYearTransactions != null && mMonthTransactions != null)
        {
            initTotalSums();
            initProfitOrLossLabels();
            initWorstDay();
            loadBiggestExpense();
            loadBiggestIncome();
        }

        return mRootView;
    }

    /**
     */
    private void initYearTransactionsFromJson()
    {
        // Get the month number, transaction and page id
        String yearTransJson = getArguments().getString(ExpenseListFragment.YEAR_JSON_KEY);
        if (!yearTransJson.isEmpty())
        {
            mYearTransactions = mJsonService.fromJsonToYearTransactions(yearTransJson);
            mMonthTransactions = mYearTransactions.get(mMonth);
        } else
        {
            mYearTransactions = null;
            mMonthTransactions = null;
        }
    }

    /**
     */
    private TextView loadTextViewAndSetText(int resourceId, String text)
    {
        TextView textView = null;

        if (mRootView != null)
        {
            textView = (TextView) mRootView.findViewById(resourceId);
            textView.setText(text);
        }

        return textView;
    }

    private void loadAnimations()
    {
        if (mAppearAnimation == null)
            mAppearAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        if (mAppearAnimationLong == null)
            mAppearAnimationLong = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_long);
    }

    /**
     */
    private void initTotalSums()
    {
        String totalExepense = Utils.formatDoubleToTextCurrency(mMonthTransactions.sumTransactions(MonthTransactions.SubsetType.EXPENSE));
        String totalIncome = Utils.formatDoubleToTextCurrency(mMonthTransactions.sumTransactions(MonthTransactions.SubsetType.INCOME));
        loadTextViewAndSetText(R.id.analytics_monthly_expense_sum, totalExepense);
        loadTextViewAndSetText(R.id.analytics_monthly_income_sum, totalIncome);

    }

    /**
     */
    private void initWorstDay()
    {
        MonthTransactions.Couple<Integer, Double> daySum = mMonthTransactions.getTopCategory(MonthTransactions.TopFilter.BUSIEST_DAY);

        if (daySum != null)
        {
            updateHasInsignts();

            LinearLayout worstDayLayout = (LinearLayout) mRootView.findViewById(R.id.month_analytics_worst_day_layout);
            worstDayLayout.setVisibility(View.VISIBLE);
            String dayStr = String.valueOf(daySum.mFirstField);
            String suffix = Utils.generateDayInMonthSuffix(dayStr);
            String month = Months.getMonthNameByNumber(mMonth);

            loadTextViewAndSetText(R.id.analytics_monthly_worst_day_date_label, month + " " + dayStr + suffix);
            loadTextViewAndSetText(R.id.analytics_monthly_worst_day_sum, Utils.formatDoubleToTextCurrency(daySum.mSecondField));
        }
    }


    /**
     */
    private void initProfitOrLossLabels()
    {
        double totalSpent = mMonthTransactions.sumTransactions(MonthTransactions.SubsetType.EXPENSE);
        double totalRevenue = mMonthTransactions.sumTransactions(MonthTransactions.SubsetType.INCOME);
        double totalProfit = totalRevenue - totalSpent;

        TextView profitTextView = loadTextViewAndSetText(R.id.analytics_monthly_profit_sum, String.valueOf(totalProfit));
        TextView profitCurrency = (TextView) mRootView.findViewById(R.id.analytics_monthly_profit_currency);
        TextView profitLabel = (TextView) mRootView.findViewById(R.id.analytics_monthly_profit_label);

        int color = getResources().getColor(android.R.color.black);

        if (totalProfit > 0)
        {
            color = getResources().getColor(R.color.income_color);
            profitLabel.setText(PROFITED_LABEL);
        } else if (totalProfit < 0)
        {
            color = getResources().getColor(R.color.expense_color);
            totalProfit = (-totalProfit);
            profitLabel.setText(LOST_LABEL);
        } else
            profitLabel.setText(BROKE_EVEN);

        String profitStr = Utils.formatDoubleToTextCurrency(totalProfit);
        profitTextView.setText(profitStr);
        profitTextView.setTextColor(color);
        profitCurrency.setTextColor(color);
    }

    /**
     */
    private void startAppearanceAnimations()
    {
        mDateLabelLayout = (LinearLayout) mRootView.findViewById(R.id.month_analytics_date_layout);
        mMainContainerLayout = (LinearLayout) mRootView.findViewById(R.id.month_analytics_main_layout);
        mDateLabelLayout.startAnimation(mAppearAnimation);
        mMainContainerLayout.startAnimation(mAppearAnimationLong);
    }

    /**
     */
    private void loadBiggestExpense()
    {
        MonthTransactions expenses = mMonthTransactions.getTopFromSubset(MonthTransactions.SubsetType.EXPENSE);

        if (expenses.getItems().size() > 0)
        {
            updateHasInsignts();

            mBiggestExpenseLayout = (LinearLayout) mRootView.findViewById(R.id.month_analytics_biggest_expense_layout);
            mBiggestExpenseLayout.setVisibility(View.VISIBLE);
            mBiggestExpenseList = (ListView) mRootView.findViewById(R.id.month_analytics_biggest_expense_list);
            ExpenseItemAdapterRead expenseAdapter = new ExpenseItemAdapterRead(getActivity(), R.layout.adapter_expense_item_read, expenses);
            mBiggestExpenseList.setAdapter(expenseAdapter);
        }
    }

    /**
     */
    private void loadBiggestIncome()
    {
        MonthTransactions incomes = mMonthTransactions.getTopFromSubset(MonthTransactions.SubsetType.INCOME);

        if (incomes.getItems().size() > 0)
        {
            updateHasInsignts();

            mBiggestIncomeLayout = (LinearLayout) mRootView.findViewById(R.id.month_analytics_biggest_income_layout);
            mBiggestIncomeLayout.setVisibility(View.VISIBLE);
            mBiggestIncomeList = (ListView) mRootView.findViewById(R.id.month_analytics_biggest_income_list);
            ExpenseItemAdapterRead incomeAdapter = new ExpenseItemAdapterRead(getActivity(), R.layout.adapter_expense_item_read, incomes);
            mBiggestIncomeList.setAdapter(incomeAdapter);
        }
    }

    /**
     */
    private void updateHasInsignts()
    {
        if (!mNoInsights)
        {
            mNoInsights = true;
            TextView insigntsLabel = (TextView) mRootView.findViewById(R.id.month_analytics_insignts_label);
            insigntsLabel.setText("Some Insights we Found");
        }
    }

    /**
     */
    @Override
    public void onResume()
    {
        super.onResume();
        mNoInsights = false;
    }
}