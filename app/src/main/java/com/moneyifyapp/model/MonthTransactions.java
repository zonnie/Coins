package com.moneyifyapp.model;

import com.moneyifyapp.activities.analytics.intrfaces.Sumable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MonthTransactions implements Sumable
{

    public List<Transaction> mTransactions = new ArrayList<Transaction>();
    public final int mMonthNumber;

    /**
     */
    public MonthTransactions(int monthNumber)
    {
        mTransactions = new ArrayList<Transaction>();
        mMonthNumber = monthNumber;
    }

    /**
     */
    public List<Transaction> getItems()
    {
        return mTransactions;
    }

    /**
     * Sums up all the expenses in this month
     */

    @Override
    public int sum(SubsetType type)
    {
        return sumTransactions(type);
    }

    public int sumTransactions(SubsetType type)
    {
        int sum = 0;

        for (Transaction curTrans : mTransactions)
        {
            if(type == SubsetType.EXPENSE && curTrans.mIsExpense)
            {
                int curTransSum = Integer.valueOf(curTrans.mValue);
                sum += curTransSum;
            }
            else if(type == SubsetType.INCOME && !curTrans.mIsExpense)
            {
                int curTransSum = Integer.valueOf(curTrans.mValue);
                sum += curTransSum;
            }
        }

        return sum;
    }

    /**
     */
    public MonthTransactions getTransactionByCategory(SubsetType type, int categoryResource)
    {
        MonthTransactions res = new MonthTransactions(mMonthNumber);

        for(Transaction curTrans : mTransactions)
        {
            if(type == SubsetType.EXPENSE && curTrans.mIsExpense
                    && curTrans.mImageResourceIndex == categoryResource)
                res.getItems().add(curTrans);
        }

        return res;
    }

    /**
     */
    public MonthTransactions getTransactionByDate(SubsetType type, String day)
    {
        MonthTransactions res = new MonthTransactions(mMonthNumber);

        for(Transaction curTrans : mTransactions)
        {
            if(type == SubsetType.EXPENSE && curTrans.mIsExpense
                    && curTrans.mTransactionDay.equals(day))
                res.getItems().add(curTrans);
        }

        return res;
    }

    /**
     */
    public Map<String, Integer> getDayToSumMap(SubsetType type)
    {
        Map<String,Integer> res = new LinkedHashMap<String, Integer>();

        for(Transaction curTrans : mTransactions)
        {
            int curSum = (res.get(curTrans.mTransactionDay) == null ? 0 : res.get(curTrans.mTransactionDay));

            if(type == SubsetType.EXPENSE && curTrans.mIsExpense)
                res.put(curTrans.mTransactionDay, curSum + Integer.valueOf(curTrans.mValue));
        }

        return res;
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
            if(type == SubsetType.EXPENSE && curTrans.mIsExpense)
                res.getItems().add(curTrans);
            else if(type == SubsetType.INCOME && !curTrans.mIsExpense)
                res.getItems().add(curTrans);
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
    public static enum SubsetType {INCOME, EXPENSE}

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
     */
    public Couple getTopCategory(TopFilter filter)
    {
        int arrLength;
        int fillIndex;
        int checkIndex;
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

        int[] dayExpenses = new int[arrLength];
        int maxKey = 0;
        int maxSum = 0;

        // Init the array
        for(; fillIndex < dayExpenses.length; ++fillIndex)
            dayExpenses[fillIndex] = 0;

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
            int curTransactionSum = Integer.valueOf(curTransaction.mValue);
            dayExpenses[arrayKey] += (curTransaction.mIsExpense) ? curTransactionSum : 0;
        }

        // Find the max day
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

        return (maxSum > 0) ? new Couple<Integer, Integer>(maxKey, maxSum) : null;
    }

    /**
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
     */
    public int getNumberOfExpenses()
    {
        int result = 0;

        for(Transaction cur : mTransactions)
        {
            if(cur.mIsExpense)
                result++;
        }

        return result;
    }

    /**
     */
    public List<Couple<Integer, Integer>> getTopCategoriesValues()
    {
        List<Couple<Integer, Integer>> result = new ArrayList<Couple<Integer, Integer>>();
        Map<Integer, Integer> expenseMap = new HashMap<Integer, Integer>();

        for(Transaction cur : mTransactions)
            if(cur.mIsExpense)
            {
                Integer curValue = expenseMap.get(cur.mImageResourceIndex);
                // Sum with previous values
                if(curValue == null)
                    curValue = 0;
                curValue += Integer.valueOf(cur.mValue);
                expenseMap.put(cur.mImageResourceIndex, curValue);
            }

        // Group in list
        for(Integer key : expenseMap.keySet())
            result.add(new Couple<Integer, Integer>(key, expenseMap.get(key)));

        // Sort and reverse for descending order
        Collections.sort(result, new Comparator<Couple<Integer, Integer>>()
        {
            @Override
            public int compare(Couple<Integer, Integer> lhs, Couple<Integer, Integer> rhs)
            {
                return lhs.mSecondField.intValue() - rhs.mSecondField.intValue();
            }
        });
        Collections.reverse(result);

        return result;
    }

    /**
     */
    public List<Couple<Integer, Integer>> getTopCategoriesValues(int n)
    {
        List<Couple<Integer,Integer>> list = getTopCategoriesValues();
        if(list.size() >= n)
            return getTopCategoriesValues().subList(0,n);
        else
            return getTopCategoriesValues().subList(0,list.size());
    }

    /**
     * An enum representing filters for the top subset function.
     */
    public enum TopFilter {CATEGORY, BUSIEST_DAY}

    /**
     */
    public MonthTransactions getSavedTemplates()
    {
        MonthTransactions result = new MonthTransactions(mMonthNumber);

        for(Transaction cur : mTransactions)
        {
            if(cur.mSaved)
                result.getItems().add(cur);
        }

        return result;
    }

    public Transaction getTransactionById(String id)
    {
        for(Transaction transaction : mTransactions)
        {
            if(transaction.mId.equals(id))
                return  transaction;
        }

        return null;
    }
}
