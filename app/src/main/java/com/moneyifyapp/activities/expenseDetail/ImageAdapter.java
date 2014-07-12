package com.moneyifyapp.activities.expenseDetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moneyifyapp.R;
import com.moneyifyapp.model.Images;

public class ImageAdapter extends BaseAdapter
{
    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    // Container context
    private ImagePickerActivity mContext;
    private View mView;
    public static int IMAGE_PICKER_IMAGE_SIZE = 210;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     *
     * @param context
     */
    public ImageAdapter(ImagePickerActivity context)
    {
        super();
        mContext = context;
    }

    /**
     *
     * create a new ImageView for each item referenced by the Adapter
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        mView = view;

        if (view == null)
        {

            LayoutInflater viewInflator;
            viewInflator = LayoutInflater.from(mContext);

            mView = viewInflator.inflate(R.layout.image_adapter_layout, null);
        }

        ImageButton imageButton = (ImageButton)mView.findViewById(R.id.categoryImage);
        //TextView imageCaption = (TextView)mView.findViewById(R.id.categoryCaption);
        imageButton.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        imageButton.setLayoutParams(new LinearLayout.LayoutParams(IMAGE_PICKER_IMAGE_SIZE, IMAGE_PICKER_IMAGE_SIZE));
        imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //imageButton.setPadding(8, 8, 8, 8);


        // Call the containing activity with the item's position
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mContext.onItemClick(position);
            }
        });
        int resourceId = Images.getImageByPosition(position);
        //imageCaption.setText(Images.getCaptions().get(position));
        imageButton.setImageResource(resourceId);

        return mView;
    }

    /**
     *
     * @return
     */
    public int getCount()
    {
        return Images.getCount();
    }

    /**
     *
     * @param position
     * @return
     */
    public Integer getItem(int position)
    {
        return null;
    }

    /**
     *
     * @param position
     * @return
     */
    public long getItemId(int position)
    {
        return 0;
    }
}