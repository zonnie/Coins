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

import com.google.gson.Gson;
import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.adapters.ExpenseItemAdapterRead;
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
    private Animation mAppearAnimation;
    private Animation mAppearAnimationLong;
    private LinearLayout mDateLabelLayout;
    private LinearLayout mMainContainerLayout;
    private ListView mBiggestExpenseList;
    private ListView mBiggestIncomeList;
    private View mRootView;

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

        // Load animations
        loadAnimations();

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
     * Loads animations
     */
    private void loadAnimations()
    {
        if(mAppearAnimation == null)
        {
            mAppearAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        }
        if(mAppearAnimationLong == null)
        {
            mAppearAnimationLong = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_long);
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
        mRootView = inflater.inflate(R.layout.fragment_analytics_total, container, false);

        if(mYearTransactions != null && mMonthTransactions != null)
        {
            /** Handle the date labels **/
            loadTextViewAndSetText(R.id.analytics_month_label, Months.getMonthNameByNumber(mMonth + 1));
            loadTextViewAndSetText(R.id.analytics_year_label, String.valueOf(mYear));

            /** Handle the basic sum **/
            String totalExepense = String.valueOf(mMonthTransactions.sumExpenses(MonthTransactions.SubsetType.EXPENSE));
            String totalIncome = String.valueOf(mMonthTransactions.sumExpenses(MonthTransactions.SubsetType.INCOME));
            loadTextViewAndSetText(R.id.analytics_by_date_expense_sum,totalExepense);
            loadTextViewAndSetText(R.id.analytics_by_date_income_sum,totalIncome);

            /** Handle animations **/
            mDateLabelLayout = (LinearLayout) mRootView.findViewById(R.id.month_analytics_date_layout);
            mMainContainerLayout = (LinearLayout) mRootView.findViewById(R.id.month_analytics_main_layout);
            mDateLabelLayout.startAnimation(mAppearAnimation);
            mMainContainerLayout.startAnimation(mAppearAnimationLong);

            /** Add the biggest income/expense **/

            MonthTransactions expenses = mMonthTransactions.getTopFromSubset(MonthTransactions.SubsetType.EXPENSE);
            MonthTransactions incomes = mMonthTransactions.getTopFromSubset(MonthTransactions.SubsetType.INCOME);

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

        return mRootView;
    }

    /**
     *
     * Loads a text view from the view, sets it text and returns the text view.
     *
     * @param resourceId
     * @param text
     * @return
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
}