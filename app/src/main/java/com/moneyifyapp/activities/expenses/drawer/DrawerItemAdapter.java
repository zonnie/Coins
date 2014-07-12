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

    /********************************************************************/
    /**                          Members                               **/
    /**
     * ****************************************************************
     */

    private TextView mItemTitle;
    private TextView mItemHint;
    private List<DrawerItem> mDrawerItems;
    private View mMyView;
    private int mLayoutResourceId;
    public static int PICK_IMAGE_DIMENSIONS = 70;
    private Animation mItemsLoadAnimation;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/


    /**
     * @param context
     * @param resource
     */
    public DrawerItemAdapter(Context context, int resource)
    {
        super(context, resource, DrawerUtils.drawerItems);
        mDrawerItems = DrawerUtils.drawerItems;
        mLayoutResourceId = resource;
    }

    /**
     * Generates the fragments view for display for each list view item.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View view = convertView;
        mMyView = view;

        if (view == null)
        {

            LayoutInflater viewInflator;
            viewInflator = LayoutInflater.from(getContext());

            view = viewInflator.inflate(mLayoutResourceId, null);

            // Load animation lazy
            if(mItemsLoadAnimation == null)
            {
                mItemsLoadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            }

            view.startAnimation(mItemsLoadAnimation);
        }

        return getRegularView(position, view);
    }

    /**
     *
     * @param position
     * @param view
     * @return
     */
    private View getRegularView(int position, View view)
    {
        DrawerItem currentTransactionView = mDrawerItems.get(position);

        // Populate the current view according to collection item.
        if (currentTransactionView != null)
        {

            mItemTitle = (TextView) view.findViewById(R.id.drawerItemText);
            mItemHint = (TextView) view.findViewById(R.id.drawer_menu_item_hint);

            /**     Build the view **/

            // Handle view's title
            handleDrawerItemTitle(currentTransactionView);
            // Handle view's hint amount
            handleDrawerItemHint(currentTransactionView);
            // Handle view's left drawable
            updateImage(currentTransactionView.getmResourceId());
        }
        return view;
    }

    /**
     *
     * @param currentItem
     */
    private void handleDrawerItemTitle(DrawerItem currentItem)
    {
        if (mItemTitle != null)
        {
            mItemTitle.setText(currentItem.getmItemTitle());
        }

    }

    /**
     *
     * @param currentItem
     */
    private void handleDrawerItemHint(DrawerItem currentItem)
    {
        if (mItemHint != null)
        {

            mItemHint.setText(currentItem.getmItemHint());
        }
    }


    /**
     * @param position
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
     * @param position
     * @param item
     */
    public void update(int position, DrawerItem item)
    {
        // Update an item
        updateDrawerItem(position, item);

        // Reflect on display
        notifyDataSetChanged();
    }

    /**
     *
     * @param position
     * @param item
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
     *
     * @param item
     * @param position
     */
    @Override
    public void insert(DrawerItem item, int position)
    {
        super.insert(item, position);
        //Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        //View view = getView(position, null, null);
        //view.startAnimation(animation);
    }

    /**
     * Update the description's left drawable
     *
     * @param resourceIndex
     */
    private void updateImage(int resourceIndex)
    {
        Drawable img = getContext().getResources().getDrawable(resourceIndex);
        img.setBounds( 0, 0, PICK_IMAGE_DIMENSIONS, PICK_IMAGE_DIMENSIONS);
        mItemTitle.setCompoundDrawables(img, null, null, null);
        //Image.setImageResource(resourceIndex);
    }
}