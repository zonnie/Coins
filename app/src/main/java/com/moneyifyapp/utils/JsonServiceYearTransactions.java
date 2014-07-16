package com.moneyifyapp.utils;

import com.google.gson.Gson;
import com.moneyifyapp.model.YearTransactions;

/**
 *
 * JSON service for yearly transactions.
 * This service creates an object from JSON and vice-versa.
 *
 */
public class JsonServiceYearTransactions
{
    private static JsonServiceYearTransactions mInstance;
    private Gson mGson;

    /**
     */
    private JsonServiceYearTransactions()
    {
        mGson = new Gson();
    }

    /**
     */
    public static JsonServiceYearTransactions getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new JsonServiceYearTransactions();
        }

        return  mInstance;
    }

    /**
     */
    public String toJson(YearTransactions transactions)
    {
        Gson gson = new Gson();
        String yearTransJson = "";
        if(transactions != null)
            yearTransJson = gson.toJson(transactions);

        return yearTransJson;
    }

    /**
     */
    public YearTransactions fromJson(String transaction)
    {
        return mGson.fromJson(transaction, YearTransactions.class);
    }
}
