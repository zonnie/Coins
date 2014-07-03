package com.moneyifyapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MonthTransactions
{

    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    public List<Transaction> mTransactions = new ArrayList<Transaction>();
    public final int mMonthNumber;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    public MonthTransactions(int monthNumber)
    {
        mTransactions = new ArrayList<Transaction>();
        mMonthNumber = monthNumber;
    }

    public List<Transaction> getItems()
    {
        return mTransactions;
    }
}
