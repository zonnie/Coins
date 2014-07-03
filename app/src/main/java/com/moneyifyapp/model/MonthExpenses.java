package com.moneyifyapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MonthExpenses
{

    /**
     * An array of sample (dummy) items.
     */
    public List<Transaction> mTransactions = new ArrayList<Transaction>();
    public List<Transaction> mEmptyList = new ArrayList<Transaction>();

    public MonthExpenses()
    {
        mTransactions = new ArrayList<Transaction>();
        mEmptyList = new ArrayList<Transaction>();
    }

    /*
    static
    {
        // Add 3 sample items.
        addItem(new SingleExpense("1", "IPhone 5", "500", "$"));
        addItem(new SingleExpense("2", "IPhone 4S", "320", "$"));
        addItem(new SingleExpense("3", "Galaxy Note III", "700", "$"));
        addItem(new SingleExpense("4", "Mazzarati X500", "300,000", "$"));
        addItem(new SingleExpense("5", "Nexus 5", "400", "$"));
        addItem(new SingleExpense("6", "Cash", "178", "$"));
    }
    */

    /**
     * @param item
     */
    public void addItem(Transaction item)
    {
        mTransactions.add(item);
    }

    public List<Transaction> getItems()
    {
        return mTransactions;
    }
}
