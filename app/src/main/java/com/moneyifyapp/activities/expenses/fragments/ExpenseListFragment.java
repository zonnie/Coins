package com.moneyifyapp.activities.expenses.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.MonthAnalytics;
import com.moneyifyapp.activities.expenseDetail.ExpenseDetailActivity;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.expenses.adapters.ExpenseItemAdapter;
import com.moneyifyapp.activities.login.dialogs.DeleteDialog;
import com.moneyifyapp.guide.WelcomeActivity;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.model.tutorial.TutorialData;
import com.moneyifyapp.utils.Utils;
import com.moneyifyapp.views.PrettyToast;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * A fragment representing a list of Items.
 */
public class ExpenseListFragment extends ListFragment
        implements ExpenseItemAdapter.ListItemHandler, TransactionHandler.onFetchingCompleteListener,
        DeleteDialog.OnDeleteClicked
{
    public static final String ITEM_POS_KEY = "itemPos";
    public static final String PAGE_ID_KEY = "frag_id";
    public static final String MONTH_KEY = "month";
    public static final String DAY_KEY = "day";
    public static final String YEAR_JSON_KEY = "yearJson";
    public static final String YEAR_KEY = "year";
    public static final String TEMPLATE_KEY = "template";
    public static final String REPEAT_KEY = "repeat";
    public static final String PARSE_DATE_KEY = "createdAt";
    public static final String REQ_CODE_KEY = "req";
    private MonthTransactions mTransactions;
    private YearTransactions mYearTransactions;
    private ExpenseItemAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    private TextView mTotalIncome;
    private TextView mTotalIncomeSign;
    private TextView mTotalExpense;
    private TextView mTotalExpenseSign;
    private TextView mTotalSavings;
    private TextView mTotalSavingsSign;
    private LinearLayout mTotalLayout;
    private Queue<Integer> mRemoveQueue;
    private Animation mRemoveAnimation;
    private View mView;
    private int mPageId;
    private int mYear;
    private int mDeletePosition;
    private boolean mIsFirst;

    /**
     * Factory to pass some data for different fragments creation.
     */
    public static ExpenseListFragment newInstance(int pageId, int year)
    {
        ExpenseListFragment fragment = new ExpenseListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR_KEY, year);
        args.putInt(PAGE_ID_KEY, pageId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     */
    public ExpenseListFragment()
    {
    }

    /**
     * On create
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mPageId = getArguments().getInt(PAGE_ID_KEY);
            mYear = getArguments().getInt(YEAR_KEY);
        }

        mIsFirst = Utils.isFirstRunDetails(getActivity());
        Utils.setFirstRunDetails(getActivity(), false);


        if (mRemoveAnimation == null)
            mRemoveAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        mRemoveQueue = new LinkedList<Integer>();

        setHasOptionsMenu(true);

        initDataFromIntentArgs();
        initListAdapter();
    }

    /**
     */
    private void reFeatchDataIfWeResumed()
    {
        if (TransactionHandler.getInstance(getActivity()).isFirstFeatch())
            TransactionHandler.getInstance(getActivity()).registerListenerAndFetchAll(this, mYear);
    }

    /**
     */
    private void initListAdapter()
    {
        mAdapter = new ExpenseItemAdapter(getActivity(), R.layout.adapter_expense_item, mTransactions, this);
        setListAdapter(mAdapter);
    }

    /**
     */
    private void initDataFromIntentArgs()
    {
        // Create a new month
        int pageId = getArguments().getInt(PAGE_ID_KEY);
        String year = mYear == 0 ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) : String.valueOf(mYear);
        mYearTransactions = TransactionHandler.getInstance(getActivity()).getYearTransactions(year);
        mTransactions = mYearTransactions.get(pageId);
    }

    /**
     * On create view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_expenses, container, false);

        storeViews();

        reFeatchDataIfWeResumed();
        buildListIfFetchNotDone();
        //TODO implement this
        handleRepeatedTasks();

        initTotalsViews();
        updateTotalsForAllTransactions();

        // On total's click go to month total
        mTotalLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startMonthlyOverview();
            }
        });

        return mView;
    }

    private void handleRepeatedTasks()
    {

    }

    /**
     */
    private void buildListIfFetchNotDone()
    {
        if(mAdapter.isEmpty() && mTransactions != null)
            buildExpenseListFromTransactionHandler();
    }

    /**
     */
    private void storeViews()
    {
        mTotalIncome = (TextView) mView.findViewById(R.id.plusAmount);
        mTotalIncomeSign = (TextView) mView.findViewById(R.id.plusCurrency);
        mTotalExpense = (TextView) mView.findViewById(R.id.minusAmount);
        mTotalExpenseSign = (TextView) mView.findViewById(R.id.minusCurrency);
        mTotalSavings = (TextView) mView.findViewById(R.id.totalAmount);
        mTotalSavingsSign = (TextView) mView.findViewById(R.id.totalCurrency);
        mTotalLayout = (LinearLayout) mView.findViewById(R.id.totalLayout);
    }

    /**
     */
    private void initTotalsViews()
    {
        mTotalSavings.setText(String.valueOf(0));
        mTotalIncome.setText(String.valueOf(0));
        mTotalExpense.setText(String.valueOf(0));
    }

    /**
     */
    private void startNewTransactionActivity()
    {
        if(mIsFirst)
        {
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.putExtra(WelcomeActivity.TUTORIAL_TYPE_KEY, TutorialData.TutorialType.EXPENSE.toString());
            startActivityForResult(intent, ExpensesActivity.REQ_NEW_ITEM);
        }
        else
        {
            Intent intent = new Intent(getActivity(), ExpenseDetailActivity.class);
            intent.putExtra(MONTH_KEY, mTransactions.mMonthNumber);
            intent.putExtra(REQ_CODE_KEY, ExpensesActivity.REQ_NEW_ITEM);
            startActivityForResult(intent, ExpensesActivity.REQ_NEW_ITEM);
        }
    }

    /**
     */
    private void startMonthlyOverview()
    {
        Gson gson = new Gson();
        String yearString = gson.toJson(mYearTransactions);

        Intent intent = new Intent(getActivity(), MonthAnalytics.class);
        Bundle data = new Bundle();
        data.putInt(MONTH_KEY, mTransactions.mMonthNumber);
        data.putInt(YEAR_KEY, mYearTransactions.mYear);
        data.putString(YEAR_JSON_KEY, yearString);
        intent.putExtras(data);

        startActivity(intent);
    }

    /**
     * Updates the fragment list item's currency if a pref change was done
     */
    public void updateFragmentCurrency()
    {
        if (mTransactions.getItems().size() != 0)
        {
            if (!Utils.getDefaultCurrency(getActivity()).equals(mTransactions.getItems().get(0).mCurrency))
            {
                for (int i = 0; i < mTransactions.getItems().size(); ++i)
                    mTransactions.getItems().get(i).mCurrency = Utils.getDefaultCurrency(getActivity());
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Called from the containg activity when a wallet was changed
     */
    public void updateOnWalletChange()
    {
        clearTotals();

        mYearTransactions = TransactionHandler.getInstance(getActivity()).getYearTransactions(""+mYear);
        mTransactions = mYearTransactions.get(mPageId);
        mAdapter = new ExpenseItemAdapter(getActivity(), R.layout.adapter_expense_item, mTransactions, this);
        setListAdapter(mAdapter);
        initTotalsViews();
        updateTotalsForAllTransactions();
    }

    /**
     */
    public void refreshFromDatabase()
    {
        ParseQuery<ParseObject> query = createParseQueryByListMonthAndYear();
        query.findInBackground(new FindCallback<ParseObject>()
        {
            public void done(List<ParseObject> expenseList, ParseException e)
            {
                if (e == null)
                {
                    clearTotals();
                    buildExpenseListFromParse(expenseList);
                } else
                    Utils.showPrettyToast(getActivity(), "We are having some issues, sorry", PrettyToast.VERY_LONG);
            }
        });
    }

    /**
     */
    private ParseQuery<ParseObject> createParseQueryByListMonthAndYear()
    {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Transaction.CLASS_NAME);
        query.whereEqualTo(ExpensesActivity.PARSE_USER_KEY, user);
        query.whereEqualTo(YEAR_KEY, mYearTransactions.mYear);
        query.whereEqualTo(MONTH_KEY, mTransactions.mMonthNumber);
        query.whereEqualTo(TransactionHandler.WALLET_ID, TransactionHandler.getInstance(getActivity()).getCurrentWalletId());
        query.addAscendingOrder(PARSE_DATE_KEY);

        return query;
    }

    /**
     * Initializes the expenses from the remote DB.
     */
    public void buildExpenseListFromParse(List<ParseObject> list)
    {
        if (list != null)
        {
            mAdapter.clear();

            for (ParseObject curExpense : list)
            {
                Transaction transaction = createTransactionFromParseObject(curExpense);
                // Normalize all currencies according to default
                transaction.mCurrency = Utils.getDefaultCurrency(getActivity());
                addNewTransactionToStartAndUpdateTotals(transaction);
            }
        }
    }

    /**
     * Initializes the expenses from the remote DB.
     */
    public void buildExpenseListFromTransactionHandler()
    {
        String year = (mYear == 0) ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) : String.valueOf(mYear);
        mYearTransactions = TransactionHandler.getInstance(getActivity()).getYearTransactions(year);

        mYearTransactions.addMonth(mPageId);
        mTransactions = mYearTransactions.get(mPageId);

        if (mTransactions != null && mTransactions.getItems() != null)
        {
            mAdapter.clear();
            for (Transaction transaction : mTransactions.getItems())
            {
                // Normalize all currencies according to default
                transaction.mCurrency = Utils.getDefaultCurrency(getActivity());
                appendNewTransactionAndUpdateTotals(transaction);
            }
        }
    }

    /**
     */
    private Transaction createTransactionFromParseObject(ParseObject curExpense)
    {
        Transaction transaction = new Transaction(curExpense.getString(Transaction.KEY_ID),
                curExpense.getString(Transaction.KEY_DESCRIPTION),
                curExpense.getString(Transaction.KEY_VALUE),
                curExpense.getString(Transaction.KEY_CURRENCY),
                curExpense.getString(Transaction.KEY_NOTES),
                curExpense.getInt(Transaction.KEY_IMAGE_NAME),
                curExpense.getBoolean(Transaction.KEY_TYPE),
                curExpense.getString(DAY_KEY));
        transaction.mSaved = curExpense.getBoolean(TEMPLATE_KEY);
        transaction.mMonth = curExpense.getInt(MONTH_KEY);
        transaction.mYear = curExpense.getInt(YEAR_KEY);

        return transaction;
    }

    /**
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.expense_actions, menu);
    }

    /**
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.refresh_list)
        {
            refreshFromDatabase();
            Utils.showPrettyToast(getActivity(), "Refreshing...", Toast.LENGTH_SHORT);
        }
        else if (id == R.id.add_expense)
            startNewTransactionActivity();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Used to update the total's bar currencies from outside
     */
    public void updateTotalCurrencyToPrefDefault()
    {
        mTotalExpenseSign.setText(Utils.getDefaultCurrency(getActivity()));
        mTotalIncomeSign.setText(Utils.getDefaultCurrency(getActivity()));
        mTotalSavingsSign.setText(Utils.getDefaultCurrency(getActivity()));
    }

    /**
     */
    @Override
    public void onActivityCreated(Bundle savedState)
    {
        super.onActivityCreated(savedState);
        getListView().setDivider(null);
        getListView().setTextFilterEnabled(true);
    }

    /**
     */
    private void removeItemFromList(int position)
    {
        // Put the next ID so that async functions could use it
        if (getListView().getChildAt(position) != null)
        {
            mRemoveQueue.add(position);
            View removedItem = getListView().getChildAt(position);
            removedItem.startAnimation(mRemoveAnimation);

            // After animation is done, remove item from list
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {

                    int itemId = mRemoveQueue.peek();
                    updateTotalsOnAddedTransaction(mAdapter.getItem(itemId), true);
                    mAdapter.remove(itemId);
                    //hideTotalsIfTransactionListEmpty();

                }

            }, mRemoveAnimation.getDuration());
            removeItemWithId(position);
        }
    }

    /**
     * Remove an item given it's list position.
     */
    private void removeItemWithId(int position)
    {
        Transaction expenseToRemove = mAdapter.getItems().get(position);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Transaction.CLASS_NAME);
        query.whereEqualTo(Transaction.KEY_ID, expenseToRemove.mId);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> list, ParseException e)
            {
                removeAfterParseQuery(list, e);
            }
        });
    }

    /**
     */
    private void removeAfterParseQuery(List<ParseObject> list, ParseException e)
    {
        if (e == null)
        {
            if (list.size() != 0)
            {
                list.get(0).deleteInBackground(new DeleteCallback()
                {
                    @Override
                    public void done(ParseException e)
                    {
                        if (e == null)
                        {
                            mRemoveQueue.remove();
                        }
                        else
                        {
                            // Remove the id from queue w/o removing the item
                            Toast.makeText(getActivity(), "Unable to delete item.." + e.toString(), Toast.LENGTH_LONG).show();
                            refreshFromDatabase();
                        }

                    }
                });
            }
        } else
            Toast.makeText(getActivity(), "Can't remove the item, couldn't find it..", Toast.LENGTH_LONG).show();
    }

    /**
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDetailFragmentSubmit");
        }
    }

    /**
     */
    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * Propagate the click to the containing activity.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        startEditTransactionActivity(position);
    }


    /**
     */
    public void startEditTransactionActivity(int position)
    {
        if (null != mListener)
        {
            mListener.expenseItemClickedInFragment(mAdapter.getItems().get(position));
            final Transaction expense = mAdapter.getItems().get(position);
            Intent intent = new Intent(getActivity(), ExpenseDetailActivity.class);
            intent.putExtra(ITEM_POS_KEY, position);
            intent.putExtra(MONTH_KEY, mTransactions.mMonthNumber);
            intent.putExtra(DAY_KEY, expense.mTransactionDay);
            intent.putExtra(Transaction.KEY_DESCRIPTION, expense.mDescription);
            intent.putExtra(Transaction.KEY_VALUE, expense.mValue);
            intent.putExtra(Transaction.KEY_CURRENCY, expense.mCurrency);
            intent.putExtra(Transaction.KEY_NOTES, expense.mNotes);
            intent.putExtra(Transaction.KEY_TYPE, expense.mIsExpense);
            intent.putExtra(Transaction.KEY_IMAGE_NAME, expense.mImageResourceIndex);
            intent.putExtra(REQ_CODE_KEY, ExpensesActivity.REQ_EDIT_ITEM);
            intent.putExtra(TEMPLATE_KEY, expense.mSaved);
            intent.putExtra(REPEAT_KEY, expense.mRepeatType);

            startActivityForResult(intent, ExpensesActivity.REQ_EDIT_ITEM);

        }
    }

    /**
     * Occurs when activity started from fragments finishes with result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //Needed so that if it's tutorial end we will start a "new" activity
        mIsFirst = Utils.isFirstRunDetails(getActivity());

        if (resultCode == ExpensesActivity.EXPENSE_RESULT_OK)
        {
            String desc = data.getExtras().getString(Transaction.KEY_DESCRIPTION);
            String sum = data.getExtras().getString(Transaction.KEY_VALUE);
            int image = data.getExtras().getInt(Transaction.KEY_IMAGE_NAME);
            String currency = data.getExtras().getString(Transaction.KEY_CURRENCY);
            String note = data.getExtras().getString(Transaction.KEY_NOTES);
            Boolean isExpense = data.getExtras().getBoolean(Transaction.KEY_TYPE);
            Boolean isSaved = data.getExtras().getBoolean(TEMPLATE_KEY);
            Transaction.REPEAT_TYPE repeatType = Transaction.REPEAT_TYPE.valueOf(data.getExtras().getString(REPEAT_KEY));
            int position = data.getExtras().getInt(ITEM_POS_KEY);

            if (!desc.isEmpty() && !sum.isEmpty())
            {
                if (requestCode == ExpensesActivity.REQ_NEW_ITEM)
                    handleNewTransaction(desc, sum, note, image, isExpense, isSaved, repeatType);
                else if (requestCode == ExpensesActivity.REQ_EDIT_ITEM)
                    handleEditTransaction(desc, sum, currency, note, image, isExpense, isSaved, repeatType,
                            position);
            }
        }
        else if(resultCode == WelcomeActivity.TUTORIAL_DONE)
            startNewTransactionActivity();
    }

    //TODO this has WAAAAY to many arguments
    /**
     */
    private void handleNewTransaction(String desc, String sum, String note, int image, boolean isExpense,
                                      boolean isSaved, Transaction.REPEAT_TYPE repeatType)
    {
        String currency = Utils.getDefaultCurrency(getActivity());
        Transaction createTransaction = createNewTransaction(desc, sum, currency, note, image, isExpense, isSaved, repeatType);
        createTransaction.mMonth = mTransactions.mMonthNumber;
        createTransaction.mYear = mYear;
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);

        if (getListView().getChildCount() > 0)
            getListView().getChildAt(0).startAnimation(anim);

        handleNewTransactionRepeat(repeatType, createTransaction);
    }

    //TODO this has WAAAAY to many arguments
    /**
     */
    private void handleEditTransaction(String desc, String sum, String currency, String note,
                                       int image, boolean isExpense, boolean isSaved, Transaction.REPEAT_TYPE repeatType, int position)
    {
        // Remove the old transaction from the totals
        updateTotalsOnAddedTransaction(mTransactions.getItems().get(position), true);
        Transaction tempExpense = new Transaction("0", desc, sum, currency, note, image, isExpense);
        tempExpense.mSaved = isSaved;
        tempExpense.mRepeatType = repeatType;
        tempExpense.mMonth = mTransactions.mMonthNumber;
        tempExpense.mYear = mYear;
        mAdapter.update(position, tempExpense);
        // ! We now MUST pass the item from the collection to preserve
        // the ID, the tempExpense object has an 'empty' ID.
        //updateDataInBackground(mAdapter.getItems().get(position));
        TransactionHandler.getInstance(getActivity()).updateTransaction(mAdapter.getItems().get(position));
        updateTotalsOnAddedTransaction(tempExpense, false);

        handleEditedTransactionRepeat(repeatType, position);
    }

    /**
     */
    private void handleNewTransactionRepeat(Transaction.REPEAT_TYPE repeatType, Transaction createTransaction)
    {
        if(repeatType != Transaction.REPEAT_TYPE.NONE)
            TransactionHandler.getInstance(getActivity()).addRepeatedTransaction(createTransaction);
    }

    /**
     */
    private void handleEditedTransactionRepeat(Transaction.REPEAT_TYPE repeatType, int position)
    {
        Transaction updatedTransaction = mTransactions.getItems().get(position);

        if(repeatType == Transaction.REPEAT_TYPE.NONE)
            TransactionHandler.getInstance(getActivity()).removeRepeatedTransaction(updatedTransaction.mId);
        else
            TransactionHandler.getInstance(getActivity()).addRepeatedTransaction(updatedTransaction);
    }

    /**
     */
    public Transaction createNewTransaction(String addDescription, String addSum, String currency,
                                     String note, int image, boolean isExpense, boolean isSaved, Transaction.REPEAT_TYPE repeatType)
    {
        String newId = generateId(addDescription, addSum, currency);

        int curMonth = Calendar.getInstance().get(Calendar.MONTH);
        int curDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String transactionDay = (mTransactions.mMonthNumber != curMonth) ? "1" : String.valueOf(curDay);

        // If not current month, add transaction to 1st of month
        Transaction newTransaction = new Transaction(newId, addDescription, addSum, currency, note, image, isExpense, transactionDay);
        newTransaction.mSaved = isSaved;
        newTransaction.mRepeatType = repeatType;
        newTransaction.mMonth = mTransactions.mMonthNumber;
        newTransaction.mYear = mYear;
        ParseObject expenseObject = new ParseObject(Transaction.CLASS_NAME);
        TransactionHandler.getInstance(getActivity()).saveDataInBackground(newTransaction, expenseObject);
        Utils.showPrettyToast(getActivity(), "Added \"" + addDescription +"\"", Toast.LENGTH_LONG);

        // The adapter will add the expense to the model collection so it can update observer
        // as well.
        mAdapter.insert(newTransaction, 0);
        updateTotalsOnAddedTransaction(newTransaction, false);

        return newTransaction;
    }

    /**
     * Add a transaction w/o creating a new instance and not saving in DB.
     */
    public void addNewTransactionToStartAndUpdateTotals(Transaction newTransaction)
    {
        // The adapter will add the expense to the model collection so it can update observer
        // as well.
        mAdapter.insert(newTransaction, 0);
        updateTotalsOnAddedTransaction(newTransaction, false);
    }

    public void appendNewTransactionAndUpdateTotals(Transaction newTransaction)
    {
        mAdapter.add(newTransaction);
        updateTotalsOnAddedTransaction(newTransaction, false);
    }

    @Override
    public void deleteClicked()
    {
        removeItemFromList(mDeletePosition);
    }

    /**
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        public void expenseItemClickedInFragment(Transaction transaction);
    }

    /**
     */
    //TODO This should be a part of the transaction model
    private String generateId(String description, String sum, String currency)
    {
        String output;

        output = (description + sum + currency + Calendar.getInstance().get(Calendar.MINUTE) + UUID.randomUUID());
        output = output.replaceAll("\\s+", "-");

        return output;
    }

    /**
     */
    private void updateTotalsForAllTransactions()
    {
        for (Transaction transaction : mTransactions.getItems())
        {
            updateTotalsOnAddedTransaction(transaction, false);
        }
    }

    /**
     */
    private void updateTotalsOnAddedTransaction(Transaction newTransaction, boolean isRemoval)
    {
        boolean isExpense = newTransaction.mIsExpense;
        int initIncome = Integer.valueOf(mTotalIncome.getText().toString());
        int initExpense = Integer.valueOf(mTotalExpense.getText().toString());
        int curTransValue = Integer.valueOf(newTransaction.mValue);

        // Collect info
        if (isExpense)
            initExpense += (isRemoval) ? (-curTransValue) : curTransValue;
        else
            initIncome += (isRemoval) ? (-curTransValue) : curTransValue;

        int initTotal = initIncome - initExpense;

        int color;
        if (initTotal < 0)
        {
            color = getResources().getColor(R.color.expense_color);
            initTotal = (-initTotal);
        } else
            color = getResources().getColor(R.color.income_color);

        // Update UI
        mTotalSavings.setTextColor(color);
        mTotalSavingsSign.setTextColor(color);

        String incomeStr = "" + initIncome;
        String expenseStr = "" + initExpense;
        String savingStr = "" + initTotal;
        mTotalSavings.setText(savingStr);
        mTotalIncome.setText(incomeStr);
        mTotalExpense.setText(expenseStr);
    }

    /**
     * Clears the totals bar
     */
    private void clearTotals()
    {
        mTotalSavings.setText(String.valueOf(0));
        mTotalIncome.setText(String.valueOf(0));
        mTotalExpense.setText(String.valueOf(0));
    }

    /**
     * Callback to be called by contained adapter
     */
    @Override
    public void removeFromFragmentList(View viewInAdapter)
    {
        final int position = getListView().getPositionForView((LinearLayout) viewInAdapter.getParent().getParent());
        if (position >= 0)
        {
            mDeletePosition = position;
            DeleteDialog dialog = new DeleteDialog(getActivity(), this,
                    "Are you sure you want to delete", "\"" + mAdapter.getItem(position).mDescription + "\"");
            dialog.show();
        }
    }

    /**
     * Callback to be called by contained adapter
     */
    @Override
    public void showItem(View view)
    {
        final int position = getListView().getPositionForView((LinearLayout) view.getParent());
        if (position >= 0)
            startEditTransactionActivity(position);
    }

    /**
     */
    @Override
    public void onFetchComplete()
    {
        buildExpenseListFromTransactionHandler();
    }
}
