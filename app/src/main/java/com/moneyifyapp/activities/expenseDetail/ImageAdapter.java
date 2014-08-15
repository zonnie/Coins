package com.moneyifyapp.activities.expenseDetail;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.model.Images;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ImageAdapter extends BaseAdapter implements Filterable
{
    private Activity mContext;
    private OnItemClick mListner;
    private View mView;
    private Filter mFilter;
    private List<Images.ImageWithCaption> mFilteredValues;
    private List<Images.ImageWithCaption> mAllValues;

    /**
     */
    public ImageAdapter(Activity context, List<Images.ImageWithCaption> images, OnItemClick listener)
    {
        super();
        mContext = context;
        mListner = listener;
        mAllValues = images;
        mFilteredValues = new ArrayList<Images.ImageWithCaption>(mAllValues);
    }

    /**
     * create a new ImageView for each item referenced by the Adapter
     */
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        mView = view;

        if (view == null)
        {
            LayoutInflater viewInflator;
            viewInflator = LayoutInflater.from(mContext);
            mView = viewInflator.inflate(R.layout.adapter_image_layout, null);
        }

        ImageButton imageButton = (ImageButton) mView.findViewById(R.id.categoryImage);
        TextView imageCaption = (TextView) mView.findViewById(R.id.categoryCaption);
        imageButton.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        RelativeLayout buttonLayout = (RelativeLayout) mView.findViewById(R.id.image_picker_layout);

        buttonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int globalPosition = 0;
                Images.ImageWithCaption image = mFilteredValues.get(position);
                for(int i = 0; i < Images.getImages().size(); ++i)
                {
                    if (image.getImage().equals(Images.getImages().get(i)))
                        globalPosition = i;
                }
                mListner.onItemClick(globalPosition);
            }
        });

        int resourceId = mFilteredValues.get(position).getImage();
        imageCaption.setText(mFilteredValues.get(position).getCaption());
        imageButton.setImageResource(resourceId);

        return mView;
    }

    /**
     */
    @Override
    public int getCount()
    {
        return mFilteredValues.size();
    }

    /**
     */
    @Override
    public Images.ImageWithCaption getItem(int position)
    {
        return mFilteredValues.get(position);
    }

    /**
     */
    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    /**
     */
    @Override
    public Filter getFilter()
    {
        if (mFilter == null)
            mFilter = new CustomFilter();
        return mFilter;
    }

    /**
     */
    private class CustomFilter extends Filter
    {

        /**
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0)
            {
                ArrayList<Images.ImageWithCaption> list = new ArrayList<Images.ImageWithCaption>(mAllValues);
                results.values = list;
                results.count = list.size();
            }
            else
            {
                ArrayList<Images.ImageWithCaption> newValues = new ArrayList<Images.ImageWithCaption>();
                for (int i = 0; i < mAllValues.size(); i++)
                {
                    Images.ImageWithCaption item = mAllValues.get(i);
                    if (item.getCaption().toUpperCase().contains(constraint.toString().toUpperCase()))
                        newValues.add(item);
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        /**
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results)
        {
            mFilteredValues = (List<Images.ImageWithCaption>) results.values;
            notifyDataSetChanged();
        }

    }

    public interface OnItemClick
    {
        public void onItemClick(int position);
    }
}