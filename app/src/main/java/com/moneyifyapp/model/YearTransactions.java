package com.moneyifyapp.model;

import com.moneyifyapp.activities.analytics.intrfaces.Sumable;

import java.util.Calendar;

/**
 */
public class YearTransactions implements Sumable
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
        // Add month if it's null
        addMonth(month);

        if(month >= 0)
            return  mYearTransactions[month];
        else
            return mYearTransactions[Calendar.getInstance().get(Calendar.MONTH)];
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

    /**
     */
    public Integer maxMonthExpeneses()
    {
        int maxMonth = 0;

        for(int i = 0; i < size(); ++i)
        {
            if(get(i) != null)
            {
                int monthSum = get(i).sumTransactions(MonthTransactions.SubsetType.EXPENSE);
                if (monthSum > maxMonth)
                    maxMonth = monthSum;
            }
        }

        return new Integer((int)maxMonth);
    }

    /**
     */
    @Override
    public int sum(MonthTransactions.SubsetType type)
    {
        int sum = 0;

        for(int i = 0; i < mYearTransactions.length; i++)
        {
            if(mYearTransactions[i] != null)
                sum += mYearTransactions[i].sum(type);
        }

        return sum;
    }

    public MonthTransactions getTopMonthFromSubset(MonthTransactions.SubsetType type)
    {
        MonthTransactions maxMonth = null;
        MonthTransactions curMonth;

        for(int i = 0; i < mYearTransactions.length; i++)
        {
            if(mYearTransactions.length > 0 && mYearTransactions[i] != null)
            {
                if (maxMonth == null)
                    maxMonth = mYearTransactions[i].getTopFromSubset(type);
                else
                {
                    curMonth = mYearTransactions[i].getTopFromSubset(type);
                    Transaction maxTrans = (maxMonth.getItems().size() > 0) ? maxMonth.getItems().get(0) : null;
                    Transaction curTrans = (curMonth.getItems().size() > 0) ? curMonth.getItems().get(0) : null;

                    if(maxTrans != null && curTrans != null)
                        if (Integer.valueOf(curTrans.mValue) > Integer.valueOf(maxTrans.mValue))
                            maxMonth = curMonth;
                }
            }
        }

        return maxMonth;
    }

    /**
     */
    public MonthTransactions getYearSavedTransactions()
    {
        MonthTransactions transactions = new MonthTransactions(0);

        for(int i = 0; i < mYearTransactions.length; ++i)
        {
            if(mYearTransactions[i] != null)
                transactions.getItems().addAll(mYearTransactions[i].getSavedTemplates().getItems());
        }

        return transactions;
    }
}
