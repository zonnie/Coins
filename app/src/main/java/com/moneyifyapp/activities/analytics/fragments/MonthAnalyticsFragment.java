package com.moneyifyapp.activities.analytics.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.model.enums.Months;
import com.moneyifyapp.utils.Utils;

/**
 * An analytics fragment that represents a month
 *
 *
 */
public class MonthAnalyticsFragment extends Fragment
{
    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String MONTH_TRANS_KEY = "monthTrans";
    private int mPageId;
    private int mMonth;
    private int mYear;
    private MonthTransactions mMonthTransactions;
    private YearTransactions mYearTransactions;
    private TextView mMonthLabelTextView;
    private TextView mYearLabelTextView;
    private TextView mMonthTotalExepenseTextView;
    private TextView mMonthTotalIncomeTextView;
    private TextView mExpenseCurrenyTextView;
    private TextView mIncomeCurrencyTextView;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MonthAnalyticsFragment newInstance(int month, int year, YearTransactions transactions)
    {
        Gson gson = new Gson();
        String yearTransJson = "";
        if(transactions != null)
        {
            yearTransJson = gson.toJson(transactions);
        }
        MonthAnalyticsFragment fragment = new MonthAnalyticsFragment();
        Bundle args = new Bundle();
        args.putString(ExpenseListFragment.YEAR_JSON_KEY, yearTransJson);
        args.putInt(ExpenseListFragment.MONTH_KEY, month);
        args.putInt(ExpenseListFragment.YEAR_KEY, year);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     */
    public MonthAnalyticsFragment(){}

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Init Parse for data storing
        Utils.initializeParse(getActivity());
        mMonth = getArguments().getInt(ExpenseListFragment.MONTH_KEY);
        mYear = getArguments().getInt(ExpenseListFragment.YEAR_KEY);

        if (getArguments() != null)
        {
            // Get the month number, transaction and page id
            String yearTransJson = getArguments().getString(ExpenseListFragment.YEAR_JSON_KEY);
            if(!yearTransJson.isEmpty())
            {
                mYearTransactions = new Gson().fromJson(yearTransJson, YearTransactions.class);
                mMonthTransactions = mYearTransactions.get(mMonth);
            }
            else
            {
                mYearTransactions = null;
                mMonthTransactions = null;
            }
        }
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_analytics_total, container, false);

        if(mYearTransactions != null && mMonthTransactions != null)
        {
            /** Handle the date labels **/
            // Update the date labels
            mMonthLabelTextView = (TextView)rootView.findViewById(R.id.analytics_month_label);
            mYearLabelTextView = (TextView)rootView.findViewById(R.id.analytics_year_label);
            mYearLabelTextView = (TextView)rootView.findViewById(R.id.analytics_year_label);

            mMonthLabelTextView.setText(Months.getMonthNameByNumber(mMonth + 1));
            mYearLabelTextView.setText(String.valueOf(mYear));

            /** Handle the basic sum **/

            String totalExepense = String.valueOf(mMonthTransactions.sumExpenses(true));
            String totalIncome = String.valueOf(mMonthTransactions.sumExpenses(false));

            mMonthTotalExepenseTextView = (TextView)rootView.findViewById(R.id.analytics_by_date_expense_sum);
            mMonthTotalIncomeTextView = (TextView)rootView.findViewById(R.id.analytics_by_date_income_sum);
            mMonthTotalExepenseTextView.setText(totalExepense);
            mMonthTotalIncomeTextView.setText(totalIncome);

            // Update currency
            mExpenseCurrenyTextView = (TextView)rootView.findViewById(R.id.analytics_by_date_expense_currency);
            mIncomeCurrencyTextView = (TextView)rootView.findViewById(R.id.analytics_by_date_income_currency);
            mExpenseCurrenyTextView.setText(Utils.getDefaultCurrency(getActivity()));
            mIncomeCurrencyTextView.setText(Utils.getDefaultCurrency(getActivity()));
        }

        return rootView;
    }
}