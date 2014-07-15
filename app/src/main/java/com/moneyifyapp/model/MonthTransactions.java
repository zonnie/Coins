package com.moneyifyapp.model;

import java.util.ArrayList;
import java.util.Collections;
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
    public long sumExpenses(SubsetType type)
    {
        long sum = 0;

        for (Transaction curTrans : mTransactions)
        {
            if(type == SubsetType.EXPENSE && curTrans.mIsExpense == true)
            {
                long curTransSum = Long.valueOf(curTrans.mValue);
                sum += curTransSum;
            }
            else if(type == SubsetType.INCOME && curTrans.mIsExpense == false)
            {
                long curTransSum = Long.valueOf(curTrans.mValue);
                sum += curTransSum;
            }
        }

        return sum;
    }

    /**
     * Get the max expense/income.
     *
     * @param type - if 'true' then get max expense, otherwise get max income.
     *
     * @return the max transaction
     */
    public long getMaxTransaction(SubsetType type)
    {
        long maxExpense = 0;

        for(Transaction curTrans : mTransactions)
        {
            Long curValue = Long.valueOf(curTrans.mValue);

            if(type == SubsetType.EXPENSE && curTrans.mIsExpense == true)
            {
                if(maxExpense < curValue)
                {
                    maxExpense = curValue;
                }
            }
            else if(type == SubsetType.INCOME && curTrans.mIsExpense == false)
            {
                if(maxExpense < curValue)
                {
                    maxExpense = curValue;
                }
            }
        }

        return maxExpense;
    }

    /**
     *
     * @param type
     * @return
     */
    private MonthTransactions getTransactionSubset(SubsetType type)
    {
        MonthTransactions res = new MonthTransactions(mMonthNumber);

        for(Transaction curTrans : mTransactions)
        {
            if(type == SubsetType.EXPENSE && curTrans.mIsExpense == true)
            {
                res.getItems().add(curTrans);
            }
            else if(type == SubsetType.INCOME && curTrans.mIsExpense == false)
            {
                res.getItems().add(curTrans);
            }
        }

        return res;
    }

    /**
     *
     * Returns the top transaction from a subset of transactions.
     *
     * @param type
     * @return
     */
    public MonthTransactions getTopFromSubset(SubsetType type)
    {
        MonthTransactions transactions = getTransactionSubset(type);
        if(transactions.getItems().size() > 0)
        {
            Transaction transaction = transactions.getItems().get(0);
            transactions.getItems().clear();
            transactions.getItems().add(transaction);
        }

        return transactions;
    }

    /**
     * Enum for extracting subsets of the transactions
     */
    public static enum SubsetType {INCOME, EXPENSE};

    /**
     *
     * Sort the transactions according to the transcation value.
     * This sorts in a decending order.
     *
     */
    public void sort()
    {
        Collections.sort(this.mTransactions);
        Collections.reverse(this.mTransactions);
    }

    /**
     *
     * Get transaction subset sorted in decending order.
     *
     * @param type
     * @return
     */
    public MonthTransactions getTransactionSubsetSorted(SubsetType type)
    {
        MonthTransactions result = getTransactionSubset(type);
        result.sort();

        return result;
    }
}
