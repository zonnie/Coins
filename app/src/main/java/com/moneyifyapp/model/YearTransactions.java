package com.moneyifyapp.model;

/**
 * Created by Zonnie_Work on 03/07/2014.
 */
public class YearTransactions
{

    private MonthTransactions[] mYearTransactions;
    public int mYear;

    /**
     */
    public YearTransactions(int year)
    {
        this.mYearTransactions = new MonthTransactions[12];
        this.mYear = year;
    }

    /**
     */
    public MonthTransactions get(int month)
    {
        return  mYearTransactions[month];
    }

    public MonthTransactions[] getItems() {return mYearTransactions;}

    /**
     */
    public void addMonth(int month)
    {
        if(mYearTransactions != null)
            if(mYearTransactions[month] == null)
                mYearTransactions[month] = new MonthTransactions(month);
    }

    /**
     */
    public void addTransactionToMonth(int month, Transaction transactions, int index)
    {
        if(this.mYearTransactions[month] == null)
            addMonth(month);

        this.mYearTransactions[month].mTransactions.add(transactions);
    }

    /**
     *
     */
    public int size()
    {
        return this.mYearTransactions.length;
    }

    public Integer maxMonth()
    {
        double maxMonth = 0;

        for(int i = 0; i < size(); ++i)
        {
            if(get(i) != null)
            {
                double monthSum = get(i).sumExpenses(MonthTransactions.SubsetType.EXPENSE);
                if (monthSum > maxMonth)
                    maxMonth = monthSum;
            }
        }

        return new Integer((int)maxMonth);
    }
}
