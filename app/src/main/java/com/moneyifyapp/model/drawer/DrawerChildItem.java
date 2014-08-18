package com.moneyifyapp.model.drawer;

/**
 */
public class DrawerChildItem
{
    private String mItemTitle;
    private int mResourceId;

    public DrawerChildItem(String title, int resource)
    {
        this.mItemTitle = title;
        this.mResourceId = resource;
    }

    /**
     */
    public String getmItemTitle()
    {
        return mItemTitle;
    }

    /**
     */
    public void setmItemTitle(String mItemTitle)
    {
        this.mItemTitle = mItemTitle;
    }

    /**
     */
    public int getmResourceId()
    {
        return mResourceId;
    }

    /**
     */
    public void setmResourceId(int mResourceId)
    {
        this.mResourceId = mResourceId;
    }
}
