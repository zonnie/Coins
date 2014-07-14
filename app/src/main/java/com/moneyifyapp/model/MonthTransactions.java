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

    /**
     *
     * @param monthNumber
     */
    public MonthTransactions(int monthNumber)
    {
        mTransactions = new ArrayList<Transaction>();
        mMonthNumber = monthNumber;
    }

    /**
     *
     * @return
     */
    public List<Transaction> getItems()
    {
        return mTransactions;
    }

    /**
     *
     * Sums up all the expenses in this month
     *
     * @return
     */
    public long sumExpenses(boolean sumExpenses)
    {
        long sum = 0;

        for (Transaction curTrans : mTransactions)
        {
            if(curTrans.mIsExpense == true && sumExpenses == true)
            {
                long curTransSum = Long.valueOf(curTrans.mValue);
                sum += curTransSum;
            }
            else if(curTrans.mIsExpense == false && sumExpenses == false)
            {
                long curTransSum = Long.valueOf(curTrans.mValue);
                sum += curTransSum;
            }
        }

        return sum;
    }
}
