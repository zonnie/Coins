package com.moneyifyapp.model.drawer;

/**
 */
public class DrawerChildItem
{
    private String mItemTitle;
    private int mResourceId;
    private String mId;

    public DrawerChildItem(String title, int resource)
    {
        this.mItemTitle = title;
        this.mResourceId = resource;
        this.mId = DrawerUtils.generateId(title);
    }

    public DrawerChildItem(String title, int resource, String id)
    {
        this.mItemTitle = title;
        this.mResourceId = resource;
        this.mId = id;
    }

    /**
     */
    public String getItemTitle()
    {
        return mItemTitle;
    }

    /**
     */
    public int getResourceId()
    {
        return mResourceId;
    }

    /**
     */
    public String getId()
    {
        return mId;
    }

}
