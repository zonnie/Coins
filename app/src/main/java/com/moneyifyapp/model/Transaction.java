package com.moneyifyapp.model;

import java.util.Calendar;

/**
 * This is the main modeling for a transaction
 */
public class Transaction implements Comparable<Transaction>
{
    public String mId;
    public String mDescription;
    public String mValue;
    public String mCurrency;
    public int mImageResourceIndex;
    public String mNotes;
    public boolean mIsExpense;
    public String mTransactionDay;
    public static final String TRANS_JSON = "transactionJson";
    public static final String CLASS_NAME = "expense";
    public static final String KEY_ID = "code";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_VALUE = "value";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_IMAGE_NAME = "image";
    public static final String KEY_NOTES = "note";
    public static final String KEY_TYPE = "type";
    public static final String CURRENCY_DEFAULT = "₪";
    public static final String DEFUALT_TRANSCATION_ID = "0";


    /**
     *
     */
    public Transaction(String id, String description, String value,
                       String currency, String note, int imageName, boolean isExpense)
    {
        this.mId = id;
        this.mDescription = description;
        this.mValue = value;
        this.mCurrency = currency;
        this.mImageResourceIndex = imageName;
        this.mNotes = note;
        this.mIsExpense = isExpense;
        this.mTransactionDay = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    /**
     *
     */
    public Transaction(String id, String description, String value,
                       String currency, String note, int imageName, boolean isExpense,
                       String day)
    {
        this(id, description, value, currency, note, imageName, isExpense);
        this.mTransactionDay = day;
    }

    /**
     *
     */
    public Transaction(String id)
    {
        this.mId = id;
        this.mDescription = "";
        this.mValue = "";
        this.mCurrency = "";
        this.mImageResourceIndex = Images.getDefaultImage();
        this.mNotes = "";
        this.mIsExpense = true;
        this.mTransactionDay = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    /**
     *
     */
    @Override
    public String toString()
    {
        return "Blaa";
    }

    /**
     *
     */
    @Override
    public int compareTo(Transaction another)
    {
        double myValue = Double.valueOf(this.mValue);
        double anotherValue = Double.valueOf(another.mValue);

        if(myValue < anotherValue)
            return -1;
        else if(myValue > anotherValue)
            return 1;
        else
            return 0;
    }
}
