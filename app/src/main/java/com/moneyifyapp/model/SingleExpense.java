package com.moneyifyapp.model;

/**
 * Created by Zonnie_Work on 30/06/2014.
 */
public class SingleExpense
{
    public String mId;
    public String mDescription;
    public String mValue;
    public String mCurrency;

    public static final String EXPENSE_CLASS_NAME = "expense";
    public static final String EXPENSE_KEY_ID = "code";
    public static final String EXPENSE_KEY_DESCRIPTION = "description";
    public static final String EXPENSE_KEY_VALUE = "value";
    public static final String EXPENSE_KEY_CURRENCY = "currency";


    /**
     * @param id
     * @param description
     * @param value
     * @param currency
     */
    public SingleExpense(String id, String description, String value, String currency)
    {
        this.mId = id;
        this.mDescription = description;
        this.mValue = value;
        this.mCurrency = currency;
    }

    /**
     * @return
     */
    @Override
    public String toString()
    {
        return "Blaa";
    }
}
