package com.moneyifyapp.activities.expenses.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
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
import android.widget.GridLayout;
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
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.utils.Utils;
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
        implements ExpenseItemAdapter.ListItemHandler, TransactionHandler.onFetchingCompleteListener
{
    public static final String ITEM_POS_KEY = "itemPos";
    public static final String PAGE_ID_KEY = "frag_id";
    public static final String MONTH_KEY = "month";
    public static final String DAY_KEY = "day";
    public static final String YEAR_JSON_KEY = "yearJson";
    public static final String YEAR_KEY = "year";
    public static final String PARSE_DATE_KEY = "createdAt";
    public static final String REQ_CODE_KEY = "req";
    public static final String REMOVE_ITEM_PROMPT_MSG = "Are you sure you want to remove this?";
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
    private Queue<Integer> mRemoveQueue;
    private Animation mRemoveAnimation;
    private View mView;
    private TransactionHandler mTransactionHandler;

    /**
     * Factory to pass some data for different fragments creation.
     */
    public static ExpenseListFragment newInstance(int pageId)
    {
        ExpenseListFragment fragment = new ExpenseListFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_ID_KEY, pageId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     */
    public ExpenseListFragment(){}

    /**
     * On create
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Init Parse for data storing
        mTransactionHandler = TransactionHandler.getInstance(getActivity());
        if(mRemoveAnimation == null)
            mRemoveAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        mRemoveQueue = new LinkedList<Integer>();
        setHasOptionsMenu(true);

        initDataFromIntentArgs();
        initListAdapter();
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
        mYearTransactions = mTransactionHandler.getYearTransactions(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        mYearTransactions.addMonth(pageId);
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
        clearTotalsValues();
        updateTotalsForAllTransactions();

        // On total's click go to month total
        LinearLayout mTotalLayout = (LinearLayout) mView.findViewById(R.id.totalLayout);
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
    }

    /**
     */
    private void clearTotalsValues()
    {
        mTotalSavings.setText(String.valueOf(0));
        mTotalIncome.setText(String.valueOf(0));
        mTotalExpense.setText(String.valueOf(0));
    }

    /**
     */
    private void startNewTransactionActivity()
    {
        Intent intent = new Intent(getActivity(), ExpenseDetailActivity.class);
        intent.putExtra(MONTH_KEY, mTransactions.mMonthNumber);
        intent.putExtra(REQ_CODE_KEY, ExpensesActivity.REQ_NEW_ITEM);
        startActivityForResult(intent, ExpensesActivity.REQ_NEW_ITEM);
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
                    Toast.makeText(getActivity(), "We are having some issues, sorry", Toast.LENGTH_LONG).show();
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
                addNewTransactionAndUpdateTotals(transaction);
            }
        }
    }

    /**
     */
    private Transaction createTransactionFromParseObject(ParseObject curExpense)
    {
        return new Transaction(curExpense.getString(Transaction.KEY_ID),
                curExpense.getString(Transaction.KEY_DESCRIPTION),
                curExpense.getString(Transaction.KEY_VALUE),
                curExpense.getString(Transaction.KEY_CURRENCY),
                curExpense.getString(Transaction.KEY_NOTES),
                curExpense.getInt(Transaction.KEY_IMAGE_NAME),
                curExpense.getBoolean(Transaction.KEY_TYPE),
                curExpense.getString(DAY_KEY));
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
            Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT).show();
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
    }

    /**
     */
    private void removeItemFromList(int position)
    {
        // Put the next ID so that async functions could use it
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
            }

        }, mRemoveAnimation.getDuration());
        removeItemWithId(position);
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
        if(e == null)
        {
            if (list.size() != 0)
            {
                list.get(0).deleteInBackground(new DeleteCallback()
                {
                    @Override
                    public void done(ParseException e)
                    {
                        if (e == null)
                            mRemoveQueue.remove();
                        else
                        {
                            // Remove the id from queue w/o removing the item
                            Toast.makeText(getActivity(), "Unable to delete item.." + e.toString(), Toast.LENGTH_LONG).show();
                            refreshFromDatabase();
                        }

                    }
                });
            }
        }
        else
            Toast.makeText(getActivity(), "Can't remove the item, couldn't find it..", Toast.LENGTH_LONG).show();
    }

    /**
     */
    private void verifyRemoveDialog(final int position)
    {
        new AlertDialog.Builder(getActivity()).setMessage(REMOVE_ITEM_PROMPT_MSG).setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        removeItemFromList(position);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                }).show();
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
                    + " must implement OnFragmentInteractionListener");
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

        if (resultCode == ExpensesActivity.EXPENSE_RESULT_OK)
        {
            String desc = data.getExtras().getString(Transaction.KEY_DESCRIPTION);
            String sum = data.getExtras().getString(Transaction.KEY_VALUE);
            int image = data.getExtras().getInt(Transaction.KEY_IMAGE_NAME);
            String currency = data.getExtras().getString(Transaction.KEY_CURRENCY);
            String note = data.getExtras().getString(Transaction.KEY_NOTES);
            Boolean isExpense = data.getExtras().getBoolean(Transaction.KEY_TYPE);

            int position = data.getExtras().getInt(ITEM_POS_KEY);

            if (!desc.isEmpty() && !sum.isEmpty())
            {
                if (requestCode == ExpensesActivity.REQ_NEW_ITEM)
                {
                    currency = Transaction.CURRENCY_DEFAULT;
                    createNewTransaction(desc, sum, currency, note, image, isExpense);
                    Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                    if(getListView().getChildCount() > 0)
                    {
                        getListView().getChildAt(0).startAnimation(anim);
                    }

                } else if (requestCode == ExpensesActivity.REQ_EDIT_ITEM)
                {
                    // Remove the old transaction from the totals
                    updateTotalsOnAddedTransaction(mTransactions.getItems().get(position), true);

                    // Update adapter
                    Transaction tempExpense = new Transaction("0", desc, sum, currency, note, image, isExpense);
                    mAdapter.update(position, tempExpense);
                    // ! We now MUST pass the item from the collection to preserve
                    // the ID, the tempExpense object has an 'empty' ID.
                    updateDataInBackground(mAdapter.getItems().get(position));
                    updateTotalsOnAddedTransaction(tempExpense, false);
                }
            }
        }

    }

    /**
     */
    public void createNewTransaction(String addDescription, String addSum, String currency, String note, int image, boolean isExpense)
    {
        String newId = generateId(addDescription, addSum, currency);

        Transaction newTransaction = new Transaction(newId, addDescription, addSum, currency, note, image, isExpense);
        ParseObject expenseObject = new ParseObject(Transaction.CLASS_NAME);
        saveDataInBackground(newTransaction, expenseObject);

        // The adapter will add the expense to the model collection so it can update observer
        // as well.
        mAdapter.insert(newTransaction, 0);
        updateTotalsOnAddedTransaction(newTransaction, false);
    }

    /**
     * Add a transaction w/o creating a new instance and not saving in DB.
     */
    public void addNewTransactionAndUpdateTotals(Transaction newTransaction)
    {
        // The adapter will add the expense to the model collection so it can update observer
        // as well.
        mAdapter.insert(newTransaction, 0);
        updateTotalsOnAddedTransaction(newTransaction, false);
    }

    /**
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        public void expenseItemClickedInFragment(Transaction transaction);
    }

    /**
     * This encapsulates the saving of date using Parse.
     */
    private void saveDataInBackground(Transaction newTransaction, ParseObject expenseObject)
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
        expenseObject.put(MONTH_KEY, mTransactions.mMonthNumber);
        expenseObject.put(DAY_KEY, newTransaction.mTransactionDay);
        expenseObject.put(YEAR_KEY, mYearTransactions.mYear);

        expenseObject.saveInBackground();
    }

    /**
     * Updates the matching object in the database.
     */
    private void updateDataInBackground(final Transaction updatedExpense)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Transaction.CLASS_NAME);
        query.whereEqualTo(Transaction.KEY_ID, updatedExpense.mId);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> list, ParseException e)
            {
                if (list.size() != 0)
                {
                    ParseObject expenseObjectInDb = list.get(0);
                    saveDataInBackground(updatedExpense, expenseObjectInDb);
                }
            }
        });

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
            updateTotalsOnAddedTransaction(transaction, false);
    }

    /**
     */
    private void updateTotalsOnAddedTransaction(Transaction newTransaction, boolean isRemoval)
    {
        boolean isExpense = newTransaction.mIsExpense;
        double initIncome = Double.valueOf(mTotalIncome.getText().toString());
        double initExpense = Double.valueOf(mTotalExpense.getText().toString());
        double curTransValue = Double.valueOf(newTransaction.mValue);

        // Collect info
        if (isExpense)
            initExpense += (isRemoval)? (-curTransValue):curTransValue;
        else
            initIncome += (isRemoval)? (-curTransValue):curTransValue;

        double initTotal = initIncome - initExpense;

        int color;
        if (initTotal < 0)
        {
            color = getResources().getColor(R.color.expense_color);
            initTotal = (-initTotal);
        }
        else
            color = getResources().getColor(R.color.income_color);

        // Update UI
        mTotalSavings.setTextColor(color);
        mTotalSavingsSign.setTextColor(color);


        String incomeStr = Utils.formatDoubleToTextCurrency(initIncome);
        String expenseStr = Utils.formatDoubleToTextCurrency(initExpense);
        String savingStr = Utils.formatDoubleToTextCurrency(initTotal);
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
        final int position = getListView().getPositionForView((GridLayout) viewInAdapter.getParent().getParent());
        if (position >= 0)
            verifyRemoveDialog(position);
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

    @Override
    public void onFetchComplete()
    {

    }

}
