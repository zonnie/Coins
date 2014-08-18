package com.moneyifyapp.model.drawer;

/**
 */
public class DrawerGroupItem
{
    private String mItemTitle;
    private String mItemHint;
    private int mResourceId;

    public DrawerGroupItem(String title, String hint, int resource)
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
