package com.moneyifyapp.model;

/**
 * Created by Zonnie_Work on 03/07/2014.
 */
public class YearTransactions
{
    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    MonthTransactions[] mYearTransactions;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     *
     */
    public YearTransactions()
    {
        this.mYearTransactions = new MonthTransactions[12];
    }

    /**
     *
     * @param month
     * @return
     */
    public MonthTransactions get(int month)
    {
        return  mYearTransactions[month];
    }

    /**
     *
     * @param month
     */
    public void addMonth(int month)
    {
        if(mYearTransactions != null)
        {
            if(mYearTransactions[month] == null)
            {
                mYearTransactions[month] = new MonthTransactions(month);
            }
        }
    }

    /**
     *
     */
    public int size()
    {
        return this.mYearTransactions.length;
    }
}
