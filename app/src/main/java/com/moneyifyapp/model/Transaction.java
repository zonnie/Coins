package com.moneyifyapp.model;

/**
 * Created by Zonnie_Work on 30/06/2014.
 */
public class Transaction
{
    /********************************************************************/
    /**                          Members                               **/
    /**
     * ****************************************************************
     */

    public String mId;
    public String mDescription;
    public String mValue;
    public String mCurrency;
    public String mImageName;
    public String mNotes;
    public boolean mIsExpense;

    /********************************************************************/
    /**                          Methods                               **/
    /**
     * ****************************************************************
     */

    public static final String CLASS_NAME = "expense";
    public static final String KEY_ID = "code";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_VALUE = "value";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_IMAGE_NAME = "image";
    public static final String KEY_NOTES = "note";
    public static final String KEY_TYPE = "type";
    public static final boolean IS_EXPENSE_DEFAULT = true;


    /**
     * @param id
     * @param description
     * @param value
     * @param currency
     */
    public Transaction(String id, String description, String value,
                       String currency, String note, String imageName, boolean isExpense)
    {
        this.mId = id;
        this.mDescription = description;
        this.mValue = value;
        this.mCurrency = currency;
        this.mImageName = imageName;
        this.mNotes = note;
        this.mIsExpense = isExpense;
    }

    /**
     * @return
     */
    @Override
    public String toString()
    {
        return "Blaa";
    }

    /**
     * @param mImageName
     */
    public void setmImageName(String mImageName)
    {
        this.mImageName = mImageName;
    }

    /**
     * @param mNotes
     */
    public void setmNotes(String mNotes)
    {
        this.mNotes = mNotes;
    }
}
