package com.moneyifyapp.activities.analytics.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class DateTileAdapter extends BaseAdapter
{
    private Context mContext;
    private Map<String, Double> mTransactions;
    private View mView;
    private TextView mDayTextView;
    private TextView mCategorySum;
    private List<String> mDayList;
    private List<Double> mDaySumList;

    /**
     */
    public DateTileAdapter(Context c, Map<String, Double> expenseByDayList)
    {
        super();
        mContext = c;
        mTransactions = expenseByDayList;

        mDayList = new ArrayList<String>();
        mDaySumList = new ArrayList<Double>();

        for(String key : mTransactions.keySet())
        {
            mDayList.add(key);
            mDaySumList.add(mTransactions.get(key));
        }
    }

    /**
     */
    public int getCount()
    {
        return mTransactions.size();
    }

    /**
     */
    public Object getItem(int position)
    {
        return mTransactions.get(position);
    }

    /**
     */
    public long getItemId(int position)
    {
        return 0;
    }

    /**
     */
    public View getView(int position, View convertView, ViewGroup parent)
    {
        mView = convertView;

        if (mView == null)
        {
            LayoutInflater viewInflator;
            viewInflator = LayoutInflater.from(mContext);
            mView = viewInflator.inflate(R.layout.adapter_date_tile, null);
        }

        //TODO add a view holder
        mDayTextView = (TextView) mView.findViewById(R.id.date_title_day_label);
        mCategorySum = (TextView) mView.findViewById(R.id.month_analytics_by_date_sum);

        if(mDaySumList.get(position) != null && mDaySumList.get(position) > 0)
        {
            Double curDaySum = mDaySumList.get(position);
            String curDay = mDayList.get(position);
            mDayTextView.setText(curDay + Utils.generateDayInMonthSuffix(curDay));
            mCategorySum.setText(Utils.formatDoubleToTextCurrency(curDaySum));
        }

        return mView;
    }
}
