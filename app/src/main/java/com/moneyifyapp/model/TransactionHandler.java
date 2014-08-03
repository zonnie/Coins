package com.moneyifyapp.model;

import android.app.Activity;

import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.utils.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Handlse the transactions.
 * It is responsible for loading the transactions from Parse.
 */
public class TransactionHandler
{
    private static TransactionHandler instance;
    private Map<String, YearTransactions> mAllTransactions;
    private Queue<onFetchingCompleteListener> mFetchCompleteListeners;
    private boolean mIsFirstFatch;

    /**
     */
    private TransactionHandler(Activity context)
    {
        Utils.initializeParse(context);
        mAllTransactions = new LinkedHashMap<String, YearTransactions>();
        mFetchCompleteListeners = new LinkedList<onFetchingCompleteListener>();
        mIsFirstFatch = true;
    }

    /**
     */
    public static TransactionHandler getInstance(Activity context)
    {
        if(instance == null)
            instance = new TransactionHandler(context);

        return  instance;
    }

    /**
     */
    public void registerListenerAndFetchTransactions(onFetchingCompleteListener listener, int year)
    {
        registerToFetchComplete(listener);
        fetchYearTransactions(year);
    }

    /**
     */
    public void fetchYearTransactions(int year)
    {
        if(!mFetchCompleteListeners.isEmpty())
            queryDatabaseAndBuildTransactions(year);
    }

    /**
     */
    public void registerToFetchComplete(onFetchingCompleteListener listener)
    {
        this.mFetchCompleteListeners.add(listener);
    }

    /**
     */
    public void queryDatabaseAndBuildTransactions(int year)
    {
        ParseQuery<ParseObject> query = createParseQueryByListMonthAndYear(year);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            public void done(List<ParseObject> expenseList, ParseException e)
            {
                if (e == null)
                    buildExpenseListFromParse(expenseList);
                else{}
            }
        });
    }

    /**
     */
    public boolean isFirstFeatch()
    {
        return mIsFirstFatch;
    }

    /**
     * Initializes the expenses from the remote DB.
     */
    public void buildExpenseListFromParse(List<ParseObject> list)
    {
        if (list != null)
        {
            this.clearUserTransactions();

            for (ParseObject curExpense : list)
            {
                Transaction transaction = createTransactionFromParseObject(curExpense);
                // Normalize all currencies according to default
                addNewTransaction(transaction, curExpense);
            }
        }

        mFetchCompleteListeners.remove().onFetchComplete();
        mIsFirstFatch = false;
    }

    /**
     */
    private Transaction createTransactionFromParseObject(ParseObject curExpense)
    {
        Transaction transaction =  new Transaction(curExpense.getString(Transaction.KEY_ID),
                curExpense.getString(Transaction.KEY_DESCRIPTION),
                curExpense.getString(Transaction.KEY_VALUE),
                curExpense.getString(Transaction.KEY_CURRENCY),
                curExpense.getString(Transaction.KEY_NOTES),
                curExpense.getInt(Transaction.KEY_IMAGE_NAME),
                curExpense.getBoolean(Transaction.KEY_TYPE),
                curExpense.getString(ExpenseListFragment.DAY_KEY));
        transaction.mSaved = curExpense.getBoolean(ExpenseListFragment.TEMPLATE_KEY);
        transaction.mRepeatType = (curExpense.getString(ExpenseListFragment.REPEAT_KEY) == null)
                ? Transaction.REPEAT_TYPE.NONE : Transaction.REPEAT_TYPE.valueOf(curExpense.getString(ExpenseListFragment.REPEAT_KEY));

        return transaction;
    }

    /**
     */
    private ParseQuery<ParseObject> createParseQueryByListMonthAndYear(int year)
    {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Transaction.CLASS_NAME);
        query.whereEqualTo(ExpensesActivity.PARSE_USER_KEY, user);
        query.whereEqualTo(ExpenseListFragment.YEAR_KEY, year);
        query.addDescendingOrder(ExpenseListFragment.PARSE_DATE_KEY);

        return query;
    }

    /**
     * Add a transaction w/o creating a new instance and not saving in DB.
     */
    public void addNewTransaction(Transaction newTransaction, ParseObject parseObject)
    {
        // The adapter will add the expense to the model collection so it can update observer
        // as well.
        String year = String.valueOf(parseObject.getInt(ExpenseListFragment.YEAR_KEY));
        int month = parseObject.getInt(ExpenseListFragment.MONTH_KEY);
        if(mAllTransactions.get(year) == null)
            mAllTransactions.put(year, new YearTransactions(Integer.valueOf(year)));
        mAllTransactions.get(year).addTransactionToMonth(month,newTransaction,0);
    }

    /**
     */
    public YearTransactions getYearTransactions(String year)
    {
        if(mAllTransactions.get(year) == null)
            mAllTransactions.put(year, new YearTransactions(Integer.valueOf(year)));

        return mAllTransactions.get(year);
    }

    /**
     */
    public interface onFetchingCompleteListener
    {
        public void onFetchComplete();
    }

    /**
     */
    public void clearUserTransactions()
    {
        mAllTransactions.clear();
    }
}
