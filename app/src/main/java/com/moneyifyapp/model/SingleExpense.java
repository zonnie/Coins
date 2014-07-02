package com.moneyifyapp.model;

/**
 * Created by Zonnie_Work on 30/06/2014.
 */
public class SingleExpense
{
    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    public String mId;
    public String mDescription;
    public String mValue;
    public String mCurrency;
    public String mImageName;
    public String mNotes;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    public static final String EXPENSE_CLASS_NAME = "expense";
    public static final String EXPENSE_KEY_ID = "code";
    public static final String EXPENSE_KEY_DESCRIPTION = "description";
    public static final String EXPENSE_KEY_VALUE = "value";
    public static final String EXPENSE_KEY_CURRENCY = "currency";
    public static final String EXPENSE_KEY_IMAGE_NAME = "image";
    public static final String EXPENSE_KEY_NOTES = "note";


    /**
     * @param id
     * @param description
     * @param value
     * @param currency
     */
    public SingleExpense(String id, String description, String value,
                         String currency, String imageName, String note)
    {
        this.mId = id;
        this.mDescription = description;
        this.mValue = value;
        this.mCurrency = currency;
        this.mImageName = imageName;
        this.mNotes = note;
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
     *
     * @param mImageName
     */
    public void setmImageName(String mImageName)
    {
        this.mImageName = mImageName;
    }

    /**
     *
     * @param mNotes
     */
    public void setmNotes(String mNotes)
    {
        this.mNotes = mNotes;
    }
}
