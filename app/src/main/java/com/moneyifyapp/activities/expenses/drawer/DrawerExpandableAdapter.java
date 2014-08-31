package com.moneyifyapp.activities.expenses.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.model.drawer.DrawerChildItem;
import com.moneyifyapp.model.drawer.DrawerGroupItem;
import com.moneyifyapp.model.drawer.DrawerUtils;

import java.util.List;
import java.util.Map;

/**
 * An expense item adapter.
 */
public class DrawerExpandableAdapter extends BaseExpandableListAdapter
{
    private TextView mItemTitle;
    private List<DrawerGroupItem> mDrawerGroupItems;
    private View mMyView;
    private int mLayoutResourceId;
    private Map<String, List<DrawerChildItem>> mDrawerItemMap;
    private Context mContext;
    private boolean mIsWalletAdapter;

    /**
     */
    public DrawerExpandableAdapter(Context context, int resource)
    {
        mContext = context;
        mDrawerGroupItems = DrawerUtils.drawerGroupItems;
        mDrawerItemMap = DrawerUtils.drawerGroupMap;
        mLayoutResourceId = resource;
        mIsWalletAdapter = false;
    }

    /**
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent)
    {
        mMyView = convertView;

        if (mMyView == null)
        {
            LayoutInflater viewInflator;
            viewInflator = LayoutInflater.from(mContext);
            mMyView = viewInflator.inflate(mLayoutResourceId, null);
        }

        return createGroupView(groupPosition);
    }

    /**
     */
    private View createGroupView(int position)
    {
        DrawerGroupItem currentTransactionView = (DrawerGroupItem)getGroup(position);

        if (currentTransactionView != null)
        {
            mItemTitle = (TextView) mMyView.findViewById(R.id.drawerItemText);
            handleDrawerItemTitle(currentTransactionView);
        }
        return mMyView;
    }

    /**
     */
    private void handleDrawerItemTitle(DrawerGroupItem currentItem)
    {
        if (mItemTitle != null)
            mItemTitle.setText(currentItem.getmItemTitle());
    }

    /**
     * Get the expense items.
     */
    public List<DrawerGroupItem> getItems()
    {
        return mDrawerGroupItems;
    }

    /**
     */
    private void updateImage(View view, int resourceIndex)
    {
        ImageView image = (ImageView) view.findViewById(R.id.drawer_child_item_image);
        image.setImageResource(resourceIndex);
    }

    /**
     */
    @Override
    public Object getChild(int groupPosition, int childPosititon)
    {
        DrawerGroupItem group = (DrawerGroupItem)getGroup(groupPosition);
        if(group != null)
            return mDrawerItemMap.get(group.getmItemTitle()).get(childPosititon);
        else
            return null;
    }

    /**
     */
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    /**
     */
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent)
    {

        DrawerChildItem child = (DrawerChildItem)getChild(groupPosition, childPosition);

        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.drawer_list_child_item_note, null);
        }

        TextView childText = (TextView) convertView.findViewById(R.id.drawer_child_item_text);
        TextView childNote = (TextView) convertView.findViewById(R.id.drawer_child_item_note);
        childText.setText(child.getItemTitle());
        childNote.setText(child.getNote());
        updateImage(convertView, child.getResourceSmallId());

        return convertView;
    }

    /**
     */
    @Override
    public int getChildrenCount(int groupPosition)
    {
        DrawerGroupItem group = (DrawerGroupItem)getGroup(groupPosition);
        if(group != null)
            return this.mDrawerItemMap.get(group.getmItemTitle()).size();
        else
            return 0;
    }

    /**
     */
    @Override
    public Object getGroup(int groupPosition)
    {
        return this.mDrawerGroupItems.get(groupPosition);
    }

    /**
     */
    @Override
    public int getGroupCount()
    {
        return this.mDrawerGroupItems.size();
    }

    /**
     */
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    /**
     */
    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    /**
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}