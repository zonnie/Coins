package com.moneyifyapp.model.drawer;

/**
 */
public class DrawerChildItem
{
    private String mItemTitle;
    private int mResourceIdLarge;
    private int mResourceIdSmall;
    private String mItemNote;
    private String mId;

    /**
     */
    public DrawerChildItem(String title, int resource, int resourceSmall, String id)
    {
        this(title, resource, resourceSmall, id, "");
    }

    /**
     */
    public DrawerChildItem(String title, int resource, int resourceSmall, String id, String note)
    {
        this.mItemTitle = title;
        this.mResourceIdLarge = resource;
        this.mResourceIdSmall = resourceSmall;
        this.mId = id;
        this.mItemNote = note;
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
        return mResourceIdLarge;
    }

    /**
     */
    public int getResourceSmallId()
    {
        return mResourceIdSmall;
    }

    /**
     */
    public String getNote()
    {
        return mItemNote;
    }

    /**
     */
    public String getId()
    {
        return mId;
    }

    /**
     */
    public void setItemTitle(String mItemTitle)
    {
        this.mItemTitle = mItemTitle;
    }

    /**
     */
    public void setResourceIdLarge(int mResourceIdLarge)
    {
        this.mResourceIdLarge = mResourceIdLarge;
    }

    /**
     */
    public void setResourceIdSmall(int mResourceIdSmall)
    {
        this.mResourceIdSmall = mResourceIdSmall;
    }

    /**
     */
    public void setNote(String note)
    {
        this.mItemNote = note;
    }
}
