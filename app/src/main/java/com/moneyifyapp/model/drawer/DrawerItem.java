package com.moneyifyapp.model.drawer;

/**
 * Created by Zonnie_Work on 06/07/2014.
 */
public class DrawerItem
{
    private String mItemTitle;
    private String mItemHint;
    private int mResourceId;

    public DrawerItem(String title, String hint, int resource)
    {
        this.mItemTitle = title;
        this.mItemHint = hint;
        this.mResourceId = resource;
    }

    /**
     *
     * @return
     */
    public String getmItemTitle()
    {
        return mItemTitle;
    }

    /**
     *
     * @return
     */
    public String getmItemHint()
    {
        return mItemHint;
    }

    public void setmItemHint(String mItemHint)
    {
        this.mItemHint = mItemHint;
    }

    public void setmItemTitle(String mItemTitle)
    {
        this.mItemTitle = mItemTitle;
    }

    public int getmResourceId()
    {
        return mResourceId;
    }

    public void setmResourceId(int mResourceId)
    {
        this.mResourceId = mResourceId;
    }
}
