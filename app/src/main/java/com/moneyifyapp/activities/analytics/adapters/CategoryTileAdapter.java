package com.moneyifyapp.activities.analytics.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.utils.Utils;

import java.util.List;

/**
 *
 */
public class CategoryTileAdapter extends BaseAdapter
{
    private Context mContext;
    private List<MonthTransactions.Couple<Integer, Double>> mCategoryList;
    private View mView;
    private TextView mCaptionTextView;
    private ImageView mCategoryImageView;
    private TextView mCategorySum;

    /**
     */
    public CategoryTileAdapter(Context c, List<MonthTransactions.Couple<Integer, Double>> categoryList)
    {
        super();
        mContext = c;
        mCategoryList = categoryList;
    }

    /**
     */
    public int getCount()
    {
        return mCategoryList.size();
    }

    /**
     */
    public Object getItem(int position)
    {
        return mCategoryList.get(position);
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
            mView = viewInflator.inflate(R.layout.adapter_category_tile, null);
        }

        //TODO add a view holder
        mCaptionTextView = (TextView) mView.findViewById(R.id.month_analytics_top_category_name);
        mCategoryImageView = (ImageView) mView.findViewById(R.id.month_analytics_top_category_image);
        mCategorySum = (TextView) mView.findViewById(R.id.month_analytics_top_category_sum);

        if(mCategoryList.get(position) != null)
        {
            String sum = Utils.formatDoubleToTextCurrency(mCategoryList.get(position).mSecondField);
            int resIndex = mCategoryList.get(position).mFirstField;
            int resource = Images.getImageByPosition(resIndex);
            mCaptionTextView.setText(Images.getCaptionByImage(resource));
            mCategoryImageView.setImageResource(resource);
            mCategorySum.setText(sum);
        }

        return mView;
    }
}
