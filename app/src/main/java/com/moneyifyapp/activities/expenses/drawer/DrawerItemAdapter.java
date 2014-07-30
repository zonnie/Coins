package com.moneyifyapp.activities.expenses.drawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.model.drawer.DrawerItem;
import com.moneyifyapp.model.drawer.DrawerUtils;

import java.util.List;

/**
 * An expense item adapter.
 */
public class DrawerItemAdapter extends ArrayAdapter<DrawerItem>
{
    private TextView mItemTitle;
    private List<DrawerItem> mDrawerItems;
    private View mMyView;
    private int mLayoutResourceId;
    public static int PICK_IMAGE_DIMENSIONS = 130;
    private Animation mItemsLoadAnimation;

    /**
     */
    public DrawerItemAdapter(Context context, int resource)
    {
        super(context, resource, DrawerUtils.drawerItems);
        mDrawerItems = DrawerUtils.drawerItems;
        mLayoutResourceId = resource;
    }

    /**
     * Generates the fragments view for display for each list view item.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        mMyView = convertView;

        if (mMyView == null)
        {

            LayoutInflater viewInflator;
            viewInflator = LayoutInflater.from(getContext());

            mMyView = viewInflator.inflate(mLayoutResourceId, null);

            // Load animation lazy
            if(mItemsLoadAnimation == null)
            {
                mItemsLoadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            }

            mMyView.startAnimation(mItemsLoadAnimation);
        }

        return getRegularView(position);
    }

    /**
     */
    private View getRegularView(int position)
    {
        DrawerItem currentTransactionView = mDrawerItems.get(position);

        // Populate the current view according to collection item.
        if (currentTransactionView != null)
        {
            mItemTitle = (TextView) mMyView.findViewById(R.id.drawerItemText);

            handleDrawerItemTitle(currentTransactionView);
            updateImage(currentTransactionView.getmResourceId());
        }
        return mMyView;
    }

    /**
     */
    private void handleDrawerItemTitle(DrawerItem currentItem)
    {
        if (mItemTitle != null)
        {
            mItemTitle.setText(currentItem.getmItemTitle());
        }

    }

    /**
     */
    public void remove(int position)
    {
        DrawerItem item = mDrawerItems.get(position);
        super.remove(item);
    }

    /**
     * Get the expense items.
     */
    public List<DrawerItem> getItems()
    {
        return mDrawerItems;
    }

    /**
     */
    public void update(int position, DrawerItem item)
    {
        // Update an item
        updateDrawerItem(position, item);

        // Reflect on display
        notifyDataSetChanged();
    }

    /**
     */
    private void updateDrawerItem(int position, DrawerItem item)
    {
        DrawerItem updatedExpense = mDrawerItems.get(position);

        if (item != null)
        {
            updatedExpense.setmItemTitle(item.getmItemTitle());
            updatedExpense.setmItemHint(item.getmItemHint());

            updateImage(item.getmResourceId());
        }
    }

    /**
     */
    @Override
    public void insert(DrawerItem item, int position)
    {
        super.insert(item, position);
    }

    /**
     * Update the description's left drawable
     */
    private void updateImage(int resourceIndex)
    {
        Drawable img = getContext().getResources().getDrawable(resourceIndex);
        img.setBounds( 0, 0, PICK_IMAGE_DIMENSIONS, PICK_IMAGE_DIMENSIONS);
        mItemTitle.setCompoundDrawables(img, null, null, null);
    }
}