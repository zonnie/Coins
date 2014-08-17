package com.moneyifyapp.model;

import android.app.Activity;
import android.content.Context;

import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.utils.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
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
    private List<String> mRepeatTransactions;
    private List<String> mReusableTransactions;
    private Queue<onFetchingCompleteListener> mFetchCompleteListeners;
    private boolean mIsFirstFatch;
    private Context mContext;

    /**
     */
    private TransactionHandler(Activity context)
    {
        Utils.initializeParse(context);
        mContext = context;
        mAllTransactions = new LinkedHashMap<String, YearTransactions>();
        mFetchCompleteListeners = new LinkedList<onFetchingCompleteListener>();
        mRepeatTransactions = new ArrayList<String>();
        mReusableTransactions = new ArrayList<String>();
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
                else
                {

                }
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
                Utils.getDefaultCurrency(mContext),     //TODO this effectivly ignores the DB currency value
                //curExpense.getString(Transaction.KEY_CURRENCY),
                curExpense.getString(Transaction.KEY_NOTES),
                curExpense.getInt(Transaction.KEY_IMAGE_NAME),
                curExpense.getBoolean(Transaction.KEY_TYPE),
                curExpense.getString(ExpenseListFragment.DAY_KEY));
        transaction.mSaved = curExpense.getBoolean(ExpenseListFragment.TEMPLATE_KEY);
        transaction.mRepeatType = (curExpense.getString(ExpenseListFragment.REPEAT_KEY) == null)
                ? Transaction.REPEAT_TYPE.NONE : Transaction.REPEAT_TYPE.valueOf(curExpense.getString(ExpenseListFragment.REPEAT_KEY));
        transaction.mMonth = curExpense.getInt(Transaction.KEY_MONTH);
        transaction.mYear = curExpense.getInt(Transaction.KEY_YEAR);

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


    /*********************************************************************************************/
    /**                                     Repeated Tasks                                      **/
    /*********************************************************************************************/

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

    /**
     */
    public Transaction removeReusableTransaction(String id)
    {
        Transaction removed = getReusableTransactionById(id);

        if(removed != null)
            mReusableTransactions.remove(id);

        return removed;
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
    public List<Transaction> getReusableTransactions()
    {
        List<Transaction> reusableTransactions = new ArrayList<Transaction>();

        for(String curId : mReusableTransactions)
            reusableTransactions.add(getTransactionById(curId));

        return reusableTransactions;
    }

    /**
     */
    public Transaction getRepeatedTransactionById(String id)
    {
        for(Transaction cur : getRepeatTransactions())
        {
            if(cur.mId.equals(id))
                return cur;
        }

        return null;
    }

    /**
     */
    public Transaction getReusableTransactionById(String id)
    {
        for(Transaction cur : getReusableTransactions())
        {
            if(cur.mId.equals(id))
                return cur;
        }

        return null;
    }

    /**
     */
    public Transaction getTransactionById(String id)
    {
        for(YearTransactions year : mAllTransactions.values())
            for(MonthTransactions month : year.getItems())
            {
                if(month != null)
                    for (Transaction curTransaction : month.getItems())
                    {
                        if (curTransaction.mId.equals(id))
                            return curTransaction;
                    }
            }

        return null;
    }

    /**
     */
    public MonthTransactions getAllSavedTransactions()
    {
        MonthTransactions allSaved = new MonthTransactions(0);

        for(String year : mAllTransactions.keySet())
            allSaved.getItems().addAll(mAllTransactions.get(year).getYearSavedTransactions().getItems());

        return allSaved;
    }

    /**
     * This encapsulates the saving of date using Parse.
     */
    public void saveDataInBackground(final Transaction newTransaction,
                                      final ParseObject expenseObject)
    {
        ParseUser user = ParseUser.getCurrentUser();

        expenseObject.put(Transaction.KEY_ID, newTransaction.mId);
        expenseObject.put(Transaction.KEY_DESCRIPTION, newTransaction.mDescription);
        expenseObject.put(Transaction.KEY_VALUE, newTransaction.mValue);
        expenseObject.put(Transaction.KEY_CURRENCY, newTransaction.mCurrency);
        expenseObject.put(Transaction.KEY_IMAGE_NAME, newTransaction.mImageResourceIndex);
        expenseObject.put(Transaction.KEY_NOTES, newTransaction.mNotes);
        expenseObject.put(Transaction.KEY_TYPE, newTransaction.mIsExpense);
        expenseObject.put(ExpensesActivity.PARSE_USER_KEY, user);
        expenseObject.put(ExpenseListFragment.MONTH_KEY, newTransaction.mMonth);
        expenseObject.put(ExpenseListFragment.DAY_KEY, newTransaction.mTransactionDay);
        expenseObject.put(ExpenseListFragment.YEAR_KEY, newTransaction.mYear);
        expenseObject.put(ExpenseListFragment.TEMPLATE_KEY, newTransaction.mSaved);
        expenseObject.put(ExpenseListFragment.REPEAT_KEY, newTransaction.mRepeatType.toString());

        expenseObject.saveInBackground();
    }

}
