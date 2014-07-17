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
    private ListView mBiggestIncomeList;
    private View mRootView;
    private static JsonServiceYearTransactions mJsonService;


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

    public MonthAnalyticsFragment(){}

    /**
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Init Parse for data storing
        Utils.initializeParse(getActivity());
        mMonth = getArguments().getInt(ExpenseListFragment.MONTH_KEY);
        mYear = getArguments().getInt(ExpenseListFragment.YEAR_KEY);

        // Load animations
        loadAnimations();

        if (getArguments() != null)
        {
            initYearTransactionsFromJson();
        }
    }

    /**
     *
     * On create view
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_analytics_total, container, false);

        if(mYearTransactions != null && mMonthTransactions != null)
        {
            initDateLabels();
            initTotalSums();
            initProfitOrLossLabels();

            startAppearanceAnimations();

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
        if(!yearTransJson.isEmpty())
        {
            mYearTransactions = mJsonService.fromJsonToYearTransactions(yearTransJson);
            mMonthTransactions = mYearTransactions.get(mMonth);
        }
        else
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
        if(mAppearAnimation == null)
            mAppearAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        if(mAppearAnimationLong == null)
            mAppearAnimationLong = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_long);
    }

    /**
     */
    private void initTotalSums()
    {
        String totalExepense = String.valueOf(mMonthTransactions.sumExpenses(MonthTransactions.SubsetType.EXPENSE));
        String totalIncome = String.valueOf(mMonthTransactions.sumExpenses(MonthTransactions.SubsetType.INCOME));
        loadTextViewAndSetText(R.id.analytics_monthly_expense_sum,totalExepense);
        loadTextViewAndSetText(R.id.analytics_monthly_income_sum,totalIncome);

    }

    /**
     */
    private void initProfitOrLossLabels()
    {
        double totalSpent = mMonthTransactions.sumExpenses(MonthTransactions.SubsetType.EXPENSE);
        double totalRevenue = mMonthTransactions.sumExpenses(MonthTransactions.SubsetType.INCOME);
        double totalProfit = totalRevenue - totalSpent;

        TextView profitTextView = loadTextViewAndSetText(R.id.analytics_monthly_profit_sum, String.valueOf(totalProfit));
        TextView profitCurrency = (TextView)mRootView.findViewById(R.id.analytics_monthly_profit_currency);
        TextView profitLabel = (TextView)mRootView.findViewById(R.id.analytics_monthly_profit_label);

        int color = getResources().getColor(android.R.color.black);

        if (totalProfit > 0)
        {
            color = getResources().getColor(R.color.income_color);
            profitLabel.setText(PROFITED_LABEL);
        }
        else if(totalProfit < 0)
        {
            color = getResources().getColor(R.color.expense_color);
            totalProfit = (-totalProfit);
            profitLabel.setText(LOST_LABEL);
        }
        else
            profitLabel.setText(BROKE_EVEN);

        String profitStr = Utils.formatDoubleToTextCurrency(totalProfit);
        profitTextView.setText(profitStr);
        profitTextView.setTextColor(color);
        profitCurrency.setTextColor(color);
    }

    /**
     */
    private void initDateLabels()
    {
        loadTextViewAndSetText(R.id.analytics_month_label, Months.getMonthNameByNumber(mMonth + 1));
        loadTextViewAndSetText(R.id.analytics_year_label, String.valueOf(mYear));
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

        if(expenses.getItems().size() > 0)
        {
            mBiggestExpenseList = (ListView)mRootView.findViewById(R.id.month_analytics_biggest_expense_list);
            ExpenseItemAdapterRead expenseAdapter = new ExpenseItemAdapterRead(getActivity(), R.layout.adapter_expense_item_read, expenses);
            mBiggestExpenseList.setAdapter(expenseAdapter);
        }
        else
        {
            TextView emptyExpenses = (TextView)mRootView.findViewById(R.id.analytics_month_empty_expense);
            emptyExpenses.setVisibility(View.VISIBLE);
        }
    }

    /**
     */
    private void loadBiggestIncome()
    {
        MonthTransactions incomes = mMonthTransactions.getTopFromSubset(MonthTransactions.SubsetType.INCOME);

        if(incomes.getItems().size() > 0)
        {
            mBiggestIncomeList = (ListView)mRootView.findViewById(R.id.month_analytics_biggest_income_list);
            ExpenseItemAdapterRead incomeAdapter = new ExpenseItemAdapterRead(getActivity(), R.layout.adapter_expense_item_read, incomes);
            mBiggestIncomeList.setAdapter(incomeAdapter);
        }
        else
        {
            TextView emptyIncome = (TextView)mRootView.findViewById(R.id.analytics_month_empty_income);
            emptyIncome.setVisibility(View.VISIBLE);
        }
    }
}