package com.moneyifyapp.activities.analytics.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.utils.JsonServiceYearTransactions;
import com.moneyifyapp.utils.Utils;

/**
 */
public class TopCategoryFragment extends Fragment
{
    private int mMonth;
    private int mYear;
    private MonthTransactions mMonthTransactions;
    private YearTransactions mYearTransactions;
    private View mRootView;
    private static JsonServiceYearTransactions mJsonService;
    private boolean mNoInsights = false;

    /**
     */
    public static TopCategoryFragment newInstance(int month, int year, YearTransactions transactions)
    {
        mJsonService = JsonServiceYearTransactions.getInstance();
        String yearTransJson = mJsonService.toJson(transactions);

        TopCategoryFragment fragment = new TopCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ExpenseListFragment.YEAR_JSON_KEY, yearTransJson);
        args.putInt(ExpenseListFragment.MONTH_KEY, month);
        args.putInt(ExpenseListFragment.YEAR_KEY, year);
        fragment.setArguments(args);
        return fragment;
    }

    public TopCategoryFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.initializeParse(getActivity());
        Utils.initializeActionBar(getActivity());
        Utils.setLogo(getActivity(), R.drawable.chart);

        if (getArguments() != null)
        {
            mMonth = getArguments().getInt(ExpenseListFragment.MONTH_KEY);
            mYear = getArguments().getInt(ExpenseListFragment.YEAR_KEY);

            initYearTransactionsFromJson();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_top_category, container, false);

        if(mRootView == null)
        {

        }

        if (mYearTransactions != null && mMonthTransactions != null)
        {
            initTopCategory();
        }

        return mRootView;
    }

    /**
     */
    private void initTopCategory()
    {
        MonthTransactions.Couple<Integer, Double> categorySum = mMonthTransactions.getTopCategory(MonthTransactions.TopFilter.CATEGORY);

        if (categorySum != null)
        {
            updateHasInsignts();

            LinearLayout topCategoryLayout = (LinearLayout) mRootView.findViewById(R.id.month_analytics_top_category_layout);
            topCategoryLayout.setVisibility(View.VISIBLE);

            String sum = Utils.formatDoubleToTextCurrency(categorySum.mSecondField);
            int resource = categorySum.mFirstField;

            ImageView image = (ImageView) mRootView.findViewById(R.id.month_analytics_top_category_image);
            loadTextViewAndSetText(R.id.month_analytics_top_category_sum, sum);
            loadTextViewAndSetText(R.id.month_analytics_top_category_name, Images.getCaptionByImage(resource));
            image.setImageResource(resource);
        }
    }

    private void updateHasInsignts()
    {
        if(mNoInsights == false)
        {
            mNoInsights = true;
            LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.month_analytics_top_category_layout);
            if(layout != null)
                layout.setVisibility(View.VISIBLE);
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
}
