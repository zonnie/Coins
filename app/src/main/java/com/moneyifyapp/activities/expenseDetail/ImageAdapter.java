package com.moneyifyapp.activities.expenseDetail;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.moneyifyapp.model.Images;

public class ImageAdapter extends BaseAdapter
{
    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    // Container context
    private ImagePickerActivity mContext;
    private int a;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    public ImageAdapter(ImagePickerActivity context)
    {
        mContext = context;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ImageButton imageButton;
        if (convertView == null)
        {  // if it's not recycled, initialize some attributes
            imageButton = new ImageButton(mContext);
            imageButton.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            imageButton.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageButton.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageButton = (ImageButton) convertView;
        }

        // Call the containing activity with the item's position
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mContext.onItemClick(position);
            }
        });

        int resourceId = Images.get(position);

        imageButton.setImageResource(resourceId);

        return imageButton;
    }

    public int getCount()
    {
        return Images.getCount();
    }

    public Integer getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return 0;
    }
}