package com.moneyifyapp.model;

import android.app.Activity;
import android.widget.Toast;

import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.drawer.DrawerUtils;
import com.moneyifyapp.utils.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
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
    private Map<String, Map<String, YearTransactions>> mAllWalletsTransactions;
    private List<String> mRepeatTransactions;
    private List<String> mReusableTransactions;
    private Queue<onFetchingCompleteListener> mFetchCompleteListeners;
    private boolean mIsFirstFatch;
    private Activity mContext;
    public static final String WALLET_ID = "walletId";
    public static final String WALLET_NOTES = "walletNotes";
    public static final String WALLET_TITLE = "walletTitle";
    public static final String WALLET_ICON_INDEX = "walletIcon";

    private String mCurrentWalletId;
    public static final String DEFAULT_WALLET_ID = "1";
    public static final String WALLET_CLASS = "wallet";

    /**
     */
    private TransactionHandler(Activity context)
    {
        Utils.initializeParse(context);
        initTransactionHandler(context);
    }

    /**
     */
    private void initTransactionHandler(Activity context)
    {
        mContext = context;
        mAllWalletsTransactions = new HashMap<String, Map<String, YearTransactions>>();
        mFetchCompleteListeners = new LinkedList<onFetchingCompleteListener>();
        mRepeatTransactions = new ArrayList<String>();
        mReusableTransactions = new ArrayList<String>();
        mIsFirstFatch = true;
        mCurrentWalletId = Utils.getCurrentWalletId(mContext);
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
    public void registerListenerAndFetchAll(onFetchingCompleteListener listener, int year)
    {
        registerToFetchComplete(listener);
        fetchTransactionsForAllWalletsByYear(year);
    }

    /**
     */
    public void fetchTransactionsForAllWalletsByYear(int year)
    {
        if(!mFetchCompleteListeners.isEmpty())
            fetchWalletsForCurrentUser(year);
    }

    /**
     */
    public void registerToFetchComplete(onFetchingCompleteListener listener)
    {
        this.mFetchCompleteListeners.add(listener);
    }



    public void fetchWalletsForCurrentUser(final int year)
    {
        ParseQuery<ParseObject> list = createParseQueryForWallet();
        list.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e)
            {
                for(ParseObject wallet : parseObjects)
                {
                    String id = wallet.getString(WALLET_ID);
                    String title = wallet.getString(WALLET_TITLE);
                    int iconIndex = wallet.getInt(WALLET_ICON_INDEX);
                    String notes = wallet.getString(WALLET_NOTES);
                    DrawerUtils.addNewWalletItem(title, iconIndex, id);
                    addWalletToAllTransactions(id);
                }

                queryDatabaseAndBuildTransactions(year);
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
     * Initializes the expenses from the remote DB.
     */
    public void buildExpenseListFromParse(List<ParseObject> list)
    {
        if (list != null)
        {
            // Clear wallets interior
            for(String walletId : mAllWalletsTransactions.keySet())
                clearWalletTransactions(walletId);

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
        //query.whereEqualTo(WALLET_ID, mCurrentWalletId);
        query.addDescendingOrder(ExpenseListFragment.PARSE_DATE_KEY);

        return query;
    }

    /**
     */
    private ParseQuery<ParseObject> createParseQueryForWallet()
    {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = null;

        if(user != null)
        {
            query = ParseQuery.getQuery(WALLET_CLASS);
            query.whereEqualTo(ExpensesActivity.PARSE_USER_KEY, user);
        }

        return query;
    }

    /**
     * Add a transaction w/o creating a new instance and not saving in DB.
     */
    public void addNewTransaction(Transaction newTransaction, ParseObject parseObject)
    {
        // The adapter will add the expense to the model collection so it can update observer
        // as well.
        String walletId = parseObject.getString(WALLET_ID);
        String year = String.valueOf(parseObject.getInt(ExpenseListFragment.YEAR_KEY));
        int month = parseObject.getInt(ExpenseListFragment.MONTH_KEY);
        if(mAllWalletsTransactions.get(walletId) == null)
            mAllWalletsTransactions.put(walletId, new LinkedHashMap<String, YearTransactions>());
        Map<String, YearTransactions> allTransactions = mAllWalletsTransactions.get(walletId);
        if(allTransactions.get(year) == null)
            allTransactions.put(year, new YearTransactions(Integer.valueOf(year)));
        allTransactions.get(year).addTransactionToMonth(month,newTransaction,0);
    }

    /**
     */
    public YearTransactions getYearTransactions(String year)
    {
        Map<String, YearTransactions> allTransactions = mAllWalletsTransactions.get(mCurrentWalletId);

        if(allTransactions.get(year) == null)
            allTransactions.put(year, new YearTransactions(Integer.valueOf(year)));

        return allTransactions.get(year);
    }

    public void updateTransaction(final Transaction transaction)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Transaction.CLASS_NAME);
        query.whereEqualTo(Transaction.KEY_ID, transaction.mId);
        query.whereEqualTo(WALLET_ID, mCurrentWalletId);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> list, ParseException e)
            {
                if (list.size() != 0)
                {
                    ParseObject expenseObjectInDb = list.get(0);
                    saveDataInBackground(transaction, expenseObjectInDb);
                    Utils.showPrettyToast(mContext, "Updated \"" + transaction.mDescription +"\"", Toast.LENGTH_LONG);
                }
            }
        });

    }

    /**
     */
    public interface onFetchingCompleteListener
    {
        public void onFetchComplete();
    }

    /**
     */
    public void clearWalletTransactions(String walletId)
    {
        mAllWalletsTransactions.get(walletId).clear();
    }

    public void clearAllWallets()
    {
        // Clear wallets interior
        this.mAllWalletsTransactions.clear();

        // Reset wallet to default
        Utils.setCurrentWalletId(mContext, DEFAULT_WALLET_ID);
        setCurrentWalletId(Utils.getCurrentWalletId(mContext));
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
        Map<String, YearTransactions> allTransactions = mAllWalletsTransactions.get(mCurrentWalletId);
        if(allTransactions == null)
            return null;
        for(YearTransactions year : allTransactions.values())
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
        Map<String, YearTransactions> allTransactions = mAllWalletsTransactions.get(mCurrentWalletId);

        if(allTransactions == null)
            return null;
        for(String year : allTransactions.keySet())
            allSaved.getItems().addAll(allTransactions.get(year).getYearSavedTransactions().getItems());

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
        expenseObject.put(WALLET_ID, mCurrentWalletId);

        expenseObject.saveInBackground();
    }

    /**
     */
    private void createWalletInBackground(String id, String title, int drawable, String notes)
    {
        ParseUser user = ParseUser.getCurrentUser();
        ParseObject expenseObject = new ParseObject(WALLET_CLASS);

        expenseObject.put(WALLET_ID, id);
        expenseObject.put(WALLET_TITLE, title);
        expenseObject.put(WALLET_NOTES, notes);
        expenseObject.put(WALLET_ICON_INDEX, drawable);
        expenseObject.put(ExpensesActivity.PARSE_USER_KEY, user);

        expenseObject.saveInBackground();
    }

    private void addWalletToAllTransactions(String id)
    {
        Map<String, YearTransactions> allTransactions = mAllWalletsTransactions.get(id);
        if(allTransactions == null)
            mAllWalletsTransactions.put(id,new HashMap<String, YearTransactions>());
    }

    public void addWallet(String id, String title, int drawable, String notes)
    {
        createWalletInBackground(id, title, drawable, notes);
        addWalletToAllTransactions(id);
    }

    /**
     */
    public String getCurrentWalletId()
    {
        return mCurrentWalletId;
    }


    /**
     */
    public void setCurrentWalletId(String mCurrentWalletId)
    {
        this.mCurrentWalletId = mCurrentWalletId;
    }

}
