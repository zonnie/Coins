package com.moneyifyapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zonnie_Work on 03/07/2014.
 */
public class YearTransactions
{
    private MonthTransactions[] mYearTransactions;
    private List<String> mRepeatTransactions;
    public int mYear;

    /**
     */
    public YearTransactions(int year)
    {
        this.mYearTransactions = new MonthTransactions[12];
        this.mRepeatTransactions = new ArrayList<String>();
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
     */
    public Transaction getTransactionById(String id)
    {
        for(MonthTransactions month : getItems())
        {
            for(Transaction curTransaction : month.getItems())
            {
                if(curTransaction.mId.equals(id))
                    return curTransaction;
            }
        }

        return null;
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
        double maxMonth = 0;

        for(int i = 0; i < size(); ++i)
        {
            if(get(i) != null)
            {
                double monthSum = get(i).sumTransactions(MonthTransactions.SubsetType.EXPENSE);
                if (monthSum > maxMonth)
                    maxMonth = monthSum;
            }
        }

        return new Integer((int)maxMonth);
    }

    /**
     */
    public List<Transaction> getRepeatTransactions()
    {
        List<Transaction> repeatTransactions = new ArrayList<Transaction>();

        for(String curId : mRepeatTransactions)
            repeatTransactions.add(getTransactionById(curId));

        return repeatTransactions;
    }

    /**
     */
    public Transaction getRepeatedTransactionById(String id)
    {
        for(Transaction cur : getRepeatTransactions())
        {
            if(cur.mId == id)
                return cur;
        }

        return null;
    }

    /**
     */
    public void addRepeatedTransaction(Transaction transaction)
    {
        Transaction existingTransaction = getRepeatedTransactionById(transaction.mId);

        if(existingTransaction == null)
            mRepeatTransactions.add(transaction.mId);
        else
            existingTransaction.mRepeatType = transaction.mRepeatType;
    }

    /**
     */
    public Transaction removeRepeatedTransaction(String id)
    {
        Transaction removed = getRepeatedTransactionById(id);

        if(removed != null)
            mRepeatTransactions.remove(id);

        return removed;
    }
}
