package com.moneyifyapp.activities.analytics.fragments;

import android.os.Bundle;
import android.view.View;
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
 */
public class YearAnalyticsFragment extends MonthAnalyticsFragment
{
    /**
     */
    public static YearAnalyticsFragment newInstance(int month, int year, YearTransactions transactions)
    {
        String yearTransJson = JsonServiceYearTransactions.getInstance().toJson(transactions);

        YearAnalyticsFragment fragment = new YearAnalyticsFragment();
        Bundle args = new Bundle();
        args.putString(ExpenseListFragment.YEAR_JSON_KEY, yearTransJson);
        args.putInt(ExpenseListFragment.MONTH_KEY, month);
        args.putInt(ExpenseListFragment.YEAR_KEY, year);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     */
    @Override
    protected void initTotalSums()
    {
        String totalExepense = Utils.formatDoubleToTextCurrency(mYearTransactions.sum(MonthTransactions.SubsetType.EXPENSE));
        String totalIncome = Utils.formatDoubleToTextCurrency(mYearTransactions.sum(MonthTransactions.SubsetType.INCOME));
        loadTextViewAndSetText(R.id.analytics_monthly_expense_sum, totalExepense);
        loadTextViewAndSetText(R.id.analytics_monthly_income_sum, totalIncome);
    }

    /**
     */
    @Override
    protected void initWorstDate()
    {
        double maxMonth = 0.0;
        double curMonth;
        int busiestMonth = 0;

        loadTextViewAndSetText(R.id.report_worst_date_label, "Busiest Month");

        for(int i = 0; i < mYearTransactions.size(); i++)
        {
            if(mYearTransactions.get(i) != null)
            {
                curMonth = mYearTransactions.get(i).sum(MonthTransactions.SubsetType.EXPENSE);
                busiestMonth = (curMonth > maxMonth) ? i : busiestMonth;
                maxMonth = (curMonth > maxMonth) ? curMonth : maxMonth;
            }
        }

        if (maxMonth > 0)
        {
            updateHasInsignts();

            LinearLayout worstDayLayout = (LinearLayout) mRootView.findViewById(R.id.month_analytics_worst_day_layout);
            worstDayLayout.setVisibility(View.VISIBLE);
            String month = Months.getMonthNameByNumber(busiestMonth);

            loadTextViewAndSetText(R.id.analytics_monthly_worst_day_date_label, month);
            loadTextViewAndSetText(R.id.analytics_monthly_worst_day_sum, Utils.formatDoubleToTextCurrency(maxMonth));
        }

    }

    /**
     */
    protected void initProfitOrLossLabels()
    {
        double totalSpent = mYearTransactions.sum(MonthTransactions.SubsetType.EXPENSE);
        double totalRevenue = mYearTransactions.sum(MonthTransactions.SubsetType.INCOME);
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

        loadTextViewAndSetText(R.id.report_bottom_line_label, "Yearly Bottom Line");
    }

    /**
     */
    protected void loadBiggestExpense()
    {
        MonthTransactions expenses = mYearTransactions.getTopMonthFromSubset(MonthTransactions.SubsetType.EXPENSE);

        if (expenses.getItems().size() > 0)
        {
            updateHasInsignts();

            mBiggestExpenseLayout = (LinearLayout) mRootView.findViewById(R.id.month_analytics_biggest_expense_layout);
            mBiggestExpenseLayout.setVisibility(View.VISIBLE);
            mBiggestExpenseList = (ListView) mRootView.findViewById(R.id.month_analytics_biggest_expense_list);
            ExpenseItemAdapterRead expenseAdapter = new ExpenseItemAdapterRead(getActivity(), R.layout.adapter_expense_item_read, expenses, this);
            mBiggestExpenseList.setAdapter(expenseAdapter);
        }
    }

    /**
     */
    protected void loadBiggestIncome()
    {
        MonthTransactions incomes = mYearTransactions.getTopMonthFromSubset(MonthTransactions.SubsetType.INCOME);

        if (incomes.getItems().size() > 0)
        {
            updateHasInsignts();

            mBiggestIncomeLayout = (LinearLayout) mRootView.findViewById(R.id.month_analytics_biggest_income_layout);
            mBiggestIncomeLayout.setVisibility(View.VISIBLE);
            mBiggestIncomeList = (ListView) mRootView.findViewById(R.id.month_analytics_biggest_income_list);
            ExpenseItemAdapterRead incomeAdapter = new ExpenseItemAdapterRead(getActivity(), R.layout.adapter_expense_item_read, incomes, this);
            mBiggestIncomeList.setAdapter(incomeAdapter);
        }
    }

}
