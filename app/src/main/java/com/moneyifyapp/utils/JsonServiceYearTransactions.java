package com.moneyifyapp.utils;

import com.google.gson.Gson;
import com.moneyifyapp.activities.analytics.fragments.BarGraphFragment;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.Transaction;
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
            mInstance = new JsonServiceYearTransactions();
        return  mInstance;
    }

    /**
     */
    public String toJson(Object transactions)
    {
        Gson gson = new Gson();
        String json = "";
        if(transactions != null)
            json = gson.toJson(transactions);

        return json;
    }

    /**
     */
    public YearTransactions fromJsonToYearTransactions(String transactions)
    {
        return mGson.fromJson(transactions, YearTransactions.class);
    }

    /**
     */
    public MonthTransactions fromJsonToMonthTransactions(String transactions)
    {
        return mGson.fromJson(transactions, MonthTransactions.class);
    }
    /**
     */
    public Transaction fromJsonToTransaction(String transactions)
    {
        return mGson.fromJson(transactions, Transaction.class);
    }

    /**
     */
    public BarGraphFragment.BarGraphParameters fromJsonToGraphParams(String parameters)
    {
        return mGson.fromJson(parameters, BarGraphFragment.BarGraphParameters.class);
    }

}
