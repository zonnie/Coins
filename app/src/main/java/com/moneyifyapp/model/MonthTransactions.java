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
    public double sumExpenses(SubsetType type)
    {
        double sum = 0;

        for (Transaction curTrans : mTransactions)
        {
            if(type == SubsetType.EXPENSE && curTrans.mIsExpense == true)
            {
                double curTransSum = Double.valueOf(curTrans.mValue);
                sum += curTransSum;
            }
            else if(type == SubsetType.INCOME && curTrans.mIsExpense == false)
            {
                double curTransSum = Double.valueOf(curTrans.mValue);
                sum += curTransSum;
            }
        }

        return sum;
    }

    /**
     * Get the max expense/income.
     * @param type - if 'true' then get max expense, otherwise get max income.
     */
    public double getMaxTransaction(SubsetType type)
    {
        double maxExpense = 0;

        for(Transaction curTrans : mTransactions)
        {
            Double curValue = Double.valueOf(curTrans.mValue);
            if(type == SubsetType.EXPENSE && curTrans.mIsExpense == true)
            {
                if(maxExpense < curValue)
                    maxExpense = curValue;
            }
            else if(type == SubsetType.INCOME && curTrans.mIsExpense == false)
            {
                if(maxExpense < curValue)
                    maxExpense = curValue;
            }
        }

        return maxExpense;
    }

    /**
     * Returns a subset of the monthly transactions according
     * to the given type.
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
     * Returns the top transaction from a subset of transactions.
     */
    public MonthTransactions getTopFromSubset(SubsetType type)
    {
        MonthTransactions transactions = getTransactionSubsetSorted(type);
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
     * Sort the transactions according to the transcation value.
     * This sorts in a decending order.
     */
    public void sort()
    {
        Collections.sort(this.mTransactions);
        Collections.reverse(this.mTransactions);
    }

    /**
     * Get transaction subset sorted in decending order.
     */
    public MonthTransactions getTransactionSubsetSorted(SubsetType type)
    {
        MonthTransactions result = getTransactionSubset(type);
        result.sort();

        return result;
    }

    /**
    public Couple<Integer, Double> getWorstDay()
    {
        Double[] dayExpenses = new Double[32];
        int maxDay = 0;
        double maxSum = 0;

        for(int i = 0; i < dayExpenses.length; ++i)
            dayExpenses[i] = new Double(0);

        for(Transaction curTransaction : mTransactions)
        {
            int curTransactionDay = Integer.valueOf(curTransaction.mTransactionDay);
            double curTransactionSum = Double.parseDouble(curTransaction.mValue);
            dayExpenses[curTransactionDay] += (curTransaction.mIsExpense) ? curTransactionSum : 0;
        }

        for(int i = 1; i < dayExpenses.length; ++i)
        {
            if(maxSum < dayExpenses[i])
            {
                maxDay = i;
                maxSum = dayExpenses[i];
            }
        }

        Couple<Integer, Double> daySum = (maxDay == 0) ? null : new Couple<Integer, Double>(maxDay, maxSum);

        return daySum;
    }*/

    /**
     */
    public Couple getTopCategory(TopFilter filter)
    {
        int arrLength = 0;
        int fillIndex = 0;
        int checkIndex = 0;
        switch (filter)
        {
            case BUSIEST_DAY:
                arrLength = 32;
                fillIndex = 1;
                checkIndex = 1;
                break;
            case CATEGORY:
                arrLength = Images.getCount();
                fillIndex = 0;
                checkIndex = 1;
                break;
            default:
                return null;
        }

        Double[] dayExpenses = new Double[arrLength];
        int maxKey = 0;
        double maxSum = 0;

        // Init the array
        for(; fillIndex < dayExpenses.length; ++fillIndex)
            dayExpenses[fillIndex] = new Double(0);

        // Fill with sums per
        for(Transaction curTransaction : mTransactions)
        {
            int arrayKey = 0;

            switch (filter)
            {
                case CATEGORY:
                    arrayKey = curTransaction.mImageResourceIndex;
                    break;
                case BUSIEST_DAY:
                    arrayKey = Integer.valueOf(curTransaction.mTransactionDay);
                    break;
            }
            double curTransactionSum = Double.parseDouble(curTransaction.mValue);
            dayExpenses[arrayKey] += (curTransaction.mIsExpense) ? curTransactionSum : 0;
        }

        for(; checkIndex < dayExpenses.length; ++checkIndex)
        {
            if(maxSum < dayExpenses[checkIndex])
            {
                switch (filter)
                {
                    case CATEGORY:
                        maxKey = Images.getImageByPosition(checkIndex);
                        break;
                    case BUSIEST_DAY:
                        maxKey = checkIndex;
                }
                maxSum = dayExpenses[checkIndex];
            }
        }

        Couple<Integer, Double> categorySum = (maxSum > 0) ? new Couple<Integer, Double>(maxKey, maxSum) : null;

        return categorySum;
    }

    /**
     *
     */
    public class Couple<T, M>
    {
        public T mFirstField;
        public M mSecondField;

        public Couple(T resouceId, M sum)
        {
            this.mFirstField = resouceId;
            this.mSecondField = sum;
        }
    }

    /**
     * An enum representing filters for the top subset function.
     */
    public enum TopFilter {CATEGORY, BUSIEST_DAY};
}
