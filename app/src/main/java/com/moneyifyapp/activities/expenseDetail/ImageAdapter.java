package com.moneyifyapp.activities.expenseDetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.model.Images;

/**
 *
 */
public class ImageAdapter extends BaseAdapter
{
    private ImagePickerActivity mContext;
    private View mView;
    public static int IMAGE_PICKER_IMAGE_SIZE = 210;

    /**
     */
    public ImageAdapter(ImagePickerActivity context)
    {
        super();
        mContext = context;
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

        ImageButton imageButton = (ImageButton)mView.findViewById(R.id.categoryImage);
        TextView imageCaption = (TextView)mView.findViewById(R.id.categoryCaption);
        imageButton.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        RelativeLayout buttonLayout = (RelativeLayout)mView.findViewById(R.id.image_picker_layout);

        buttonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mContext.onItemClick(position);
            }
        });

        /*
        // Call the containing activity with the item's position
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mContext.onItemClick(position);
            }
        });

        imageCaption.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mContext.onItemClick(position);
            }
        });
        */

        int resourceId = Images.getImageByPosition(position);
        imageCaption.setText(Images.getCaptions().get(position));
        imageButton.setImageResource(resourceId);

        return mView;
    }

    /**
     */
    public int getCount()
    {
        return Images.getCount();
    }

    /**
     */
    public Integer getItem(int position)
    {
        return null;
    }

    /**
     */
    public long getItemId(int position)
    {
        return 0;
    }
}