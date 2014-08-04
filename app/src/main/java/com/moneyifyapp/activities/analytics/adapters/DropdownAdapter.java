package com.moneyifyapp.activities.analytics.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.moneyifyapp.R;

import java.util.List;

/**
 *
 */
public class DropdownAdapter extends ArrayAdapter<String>
{
    private Context mContext;
    private View mView;
    private List<String> mItems;

    /**
     */
    public DropdownAdapter(Context context, List<String> items)
    {
        super(context, android.R.layout.simple_list_item_1, items);
        mContext = context;
        mItems = items;
    }

    /**
     * create a new ImageView for each item referenced by the Adapter
     */
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        mView = convertView;

        if (convertView == null)
        {
            LayoutInflater viewInflator;
            viewInflator = LayoutInflater.from(mContext);
            mView = viewInflator.inflate(R.layout.adapter_dropdown_item, null);
        }

        TextView dropdownText = (TextView) mView.findViewById(R.id.dropdown_item_textview);
        dropdownText.setText(mItems.get(position));

        return mView;
    }

    /**
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        TextView v = (TextView) super.getView(position, convertView, parent);
        return v;
    }

    /**
     */
    @Override
    public int getCount()
    {
        return mItems.size();
    }
}