package com.moneyifyapp.activities.expenses.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
public class ExpenseListFragment extends ListFragment implements ExpenseItemAdapter.ListItemHandler
{

    /********************************************************************/
    /**                          Members                               **/
    /**
     * ****************************************************************
     */

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String PAGE_ID_KEY = "frag_id";
    public static final String MONTH_KEY = "month";
    public static final String DAY_KEY = "day";
    public static final String YEAR_JSON_KEY = "yearJson";
    public static final String YEAR_KEY = "year";
    public static final String PARSE_DATE_KEY = "createdAt";
    public static final String REQ_CODE_KEY = "req";
    private Button mNewTransactionButton;
    private MonthTransactions mTransactions;
    private YearTransactions mYearTransactions;
    private ExpenseItemAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    private LinearLayout mTotalLayout;
    private LinearLayout mItemLayout;
    private TextView mTotalIncome;
    private TextView mTotalIncomeSign;
    private TextView mTotalExpense;
    private TextView mTotalExpenseSign;
    private TextView mTotalSavings;
    private TextView mTotalSavingsSign;
    private SharedPreferences mPreferences;
    private Queue<Integer> mRemoveQueue;
    private int mPageId;


    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     * Factory to pass some data for different fragments creation.
     *
     * @param pageId
     * @return
     */
    public static ExpenseListFragment newInstance(int pageId, YearTransactions yearTransactions)
    {
        Gson gson = new Gson();
        String yearString = gson.toJson(yearTransactions);
        ExpenseListFragment fragment = new ExpenseListFragment();
        Bundle args = new Bundle();
        args.putString(YEAR_JSON_KEY, yearString);
        args.putInt(PAGE_ID_KEY, pageId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExpenseListFragment()
    {
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Init Parse for data storing
        Utils.initializeParse(getActivity());

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mRemoveQueue = new LinkedList<Integer>();
        setHasOptionsMenu(true);

        if (getArguments() != null)
        {
            // Create a new month
            String yearJson = getArguments().getString(YEAR_JSON_KEY);
            mPageId = getArguments().getInt(PAGE_ID_KEY);
            mYearTransactions = new Gson().fromJson(yearJson, YearTransactions.class);
            mYearTransactions.addMonth(mPageId);
            mTransactions = mYearTransactions.get(mPageId);
        }

        mAdapter = new ExpenseItemAdapter(getActivity(), R.layout.adapter_expense_item, mTransactions, this);

        // Init Parse for data storing
        initializeExpenses();

        setListAdapter(mAdapter);
    }

    /**
     * Needed in order to use the custom layout.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        mNewTransactionButton = (Button) view.findViewById(R.id.addNewExpenseButton);
        mNewTransactionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), ExpenseDetailActivity.class);
                intent.putExtra(REQ_CODE_KEY, ExpensesActivity.REQ_NEW_ITEM);
                startActivityForResult(intent, ExpensesActivity.REQ_NEW_ITEM);
            }
        });

        mTotalLayout = (LinearLayout) view.findViewById(R.id.totalLayout);
        mTotalIncome = (TextView) view.findViewById(R.id.plusAmount);
        mTotalIncomeSign = (TextView) view.findViewById(R.id.plusCurrency);
        mTotalExpense = (TextView) view.findViewById(R.id.minusAmount);
        mTotalExpenseSign = (TextView) view.findViewById(R.id.minusCurrency);
        mTotalSavings = (TextView) view.findViewById(R.id.totalAmount);
        mTotalSavingsSign = (TextView) view.findViewById(R.id.totalCurrency);

        // Clear the total bar's data
        mTotalSavings.setText(String.valueOf(0));
        mTotalIncome.setText(String.valueOf(0));
        mTotalExpense.setText(String.valueOf(0));

        // Update total's currency to default
        updateTotalCurrencyToPrefDefault();

        for (Transaction transaction : mTransactions.getItems())
        {
            updateTotals(transaction, false);
        }

        // On total's click go to month total
        mTotalLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Gson gson = new Gson();
                String yearString = gson.toJson(mYearTransactions);
                Intent intent = new Intent(getActivity(), MonthAnalytics.class);
                Bundle data = new Bundle();
                data.putInt(MONTH_KEY, mPageId);
                data.putInt(YEAR_KEY, mYearTransactions.mYear);
                data.putString(YEAR_JSON_KEY, yearString);
                intent.putExtras(data);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();
    }

    /**
     * Updates the fragment list item's currency if a pref change was done
     */
    public void updateFragmentCurrency()
    {
        if (mTransactions.getItems().size() != 0)
        {
            // Normalize all currencies according to default only if different
            if (!Utils.getDefaultCurrency(getActivity()).equals(mTransactions.getItems().get(0)))
            {

                for (int i = 0; i < mTransactions.getItems().size(); ++i)
                {
                    mTransactions.getItems().get(i).mCurrency = Utils.getDefaultCurrency(getActivity());
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     *
     */
    public void initializeExpenses()
    {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("expense");
        query.whereEqualTo(ExpensesActivity.PARSE_USER_KEY, user);
        query.whereEqualTo(YEAR_KEY, mYearTransactions.mYear);
        query.whereEqualTo(MONTH_KEY, mTransactions.mMonthNumber);
        query.addAscendingOrder(PARSE_DATE_KEY);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            public void done(List<ParseObject> expenseList, ParseException e)
            {
                if (e == null)
                {
                    buildExpenseListFromParse(expenseList);
                } else
                {
                    Toast toast = Toast.makeText(getActivity(), "We are having some issues, sorry", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    /**
     *
     */
    public void refreshFromDatabase()
    {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("expense");
        query.whereEqualTo(ExpensesActivity.PARSE_USER_KEY, user);
        query.whereEqualTo(YEAR_KEY, mYearTransactions.mYear);
        query.whereEqualTo(MONTH_KEY, mTransactions.mMonthNumber);
        query.addAscendingOrder(PARSE_DATE_KEY);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            public void done(List<ParseObject> expenseList, ParseException e)
            {
                if (e == null)
                {
                    clearTotals();
                    buildExpenseListFromParse(expenseList);
                } else
                {
                    Toast toast = Toast.makeText(getActivity(), "We are having some issues, sorry", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    /**
     * Initializes the expenses from the remote DB.
     *
     * @param list
     */
    public void buildExpenseListFromParse(List<ParseObject> list)
    {
        if (list != null)
        {
            mAdapter.clear();

            for (ParseObject curExpense : list)
            {
                Transaction transaction = new Transaction(curExpense.getString(Transaction.KEY_ID),
                        curExpense.getString(Transaction.KEY_DESCRIPTION),
                        curExpense.getString(Transaction.KEY_VALUE),
                        curExpense.getString(Transaction.KEY_CURRENCY),
                        curExpense.getString(Transaction.KEY_NOTES),
                        curExpense.getInt(Transaction.KEY_IMAGE_NAME),
                        curExpense.getBoolean(Transaction.KEY_TYPE),
                        curExpense.getString(DAY_KEY)
                );

                // Normalize all currencies according to default
                transaction.mCurrency = Utils.getDefaultCurrency(getActivity());

                // Add a transaction w/o creating a new instance and not saving in DB
                addNewTransaction(transaction);
            }
        }
    }

    /**
     * @param menu
     * @return
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.expense_actions, menu);
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.refresh_list)
        {
            refreshFromDatabase();
            Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT);
        } else if (id == R.id.add_expense)
        {
            Intent intent = new Intent(getActivity(), ExpenseDetailActivity.class);
            intent.putExtra(REQ_CODE_KEY, ExpensesActivity.REQ_NEW_ITEM);
            startActivityForResult(intent, ExpensesActivity.REQ_NEW_ITEM);
        }

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
     * @param savedState
     */
    @Override
    public void onActivityCreated(Bundle savedState)
    {
        super.onActivityCreated(savedState);

        //Remove dividers
        getListView().setDivider(null);
    }

    /**
     * Remove an item given it's list position.
     *
     * @param position
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

                if (list.size() != 0)
                {
                    list.get(0).deleteInBackground(new DeleteCallback()
                    {
                        @Override
                        public void done(ParseException e)
                        {
                            if (e == null)
                            {
                                int removeId = mRemoveQueue.peek();
                                // Remove the next item set in the queue
                                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                                getListView().getChildAt(removeId).startAnimation(anim);

                                new Handler().postDelayed(new Runnable()
                                {
                                    public void run()
                                    {
                                        int removeId = mRemoveQueue.remove();
                                        updateTotals(mAdapter.getItem(removeId), true);
                                        mAdapter.remove(removeId);
                                    }

                                }, anim.getDuration());
                            } else
                            {
                                // Remove the id from queue w/o removing the item
                                Toast.makeText(getActivity(), "Something went wrong :(" + e.toString(), Toast.LENGTH_LONG).show();
                                mRemoveQueue.remove();
                            }

                        }
                    });
                }
            }
        });
    }

    /**
     * @param position
     */
    private void verifyRemoveDialog(final int position)
    {
        String expenseDescription = mAdapter.getItems().get(position).mDescription;

        new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure you want to remove this?")
                .setCancelable(false)
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
     * @param position
     */
    private void removeItemFromList(int position)
    {
        // Put the next ID to be removed async from the list
        mRemoveQueue.add(position);
        removeItemWithId(position);
    }

    /**
     * @param activity
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
     *
     */
    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * Propagate the click to the containing activity.
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        listItemClicked(position);
    }

    /**
     *
     * @param position
     */
    public void listItemClicked(int position)
    {
        if (null != mListener)
        {
            mListener.expenseItemClickedInFragment(mAdapter.getItems().get(position));

            final Transaction expense = mAdapter.getItems().get(position);

            Intent intent = new Intent(getActivity(), ExpenseDetailActivity.class);
            intent.putExtra(MONTH_KEY, position);
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
     *
     * @param requestCode
     * @param resultCode
     * @param data
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

            int position = data.getExtras().getInt(MONTH_KEY);

            if (!desc.isEmpty() && !sum.isEmpty())
            {
                if (requestCode == ExpensesActivity.REQ_NEW_ITEM)
                {
                    // TODO: This needs to be dynamic
                    currency = Transaction.CURRENCY_DEFAULT;
                    createNewTransaction(desc, sum, currency, note, image, isExpense);
                    Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                    getListView().getChildAt(0).startAnimation(anim);

                } else if (requestCode == ExpensesActivity.REQ_EDIT_ITEM)
                {
                    // Remove the old transaction from the totals
                    updateTotals(mTransactions.getItems().get(position), true);

                    // Update adapter
                    Transaction tempExpense = new Transaction("0", desc, sum, currency, note, image, isExpense);
                    mAdapter.update(position, tempExpense);
                    // ! We now MUST pass the item from the collection to preserver
                    // the ID, the tempExpense object has an 'empty' ID.
                    updateDataInBackground(mAdapter.getItems().get(position));

                    // Add the updated transaction
                    updateTotals(tempExpense, false);
                }
            }
        }

    }

    /**
     * @param addDescription
     * @param addSum
     */
    public void createNewTransaction(String addDescription, String addSum, String currency, String note, int image, boolean isExpense)
    {
        String newId = generateId(addDescription, addSum, currency);

        // Create a new expense model
        Transaction newTransaction = new Transaction(newId, addDescription, addSum, currency, note, image, isExpense);

        // Save to Parse
        ParseObject expenseObject = new ParseObject(Transaction.CLASS_NAME);
        saveDataInBackground(newTransaction, expenseObject);

        // The adapter will add the expense to the model collection so it can update observer
        // as well.
        mAdapter.insert(newTransaction, 0);
        updateTotals(newTransaction, false);
    }

    /**
     * Add a transaction w/o creating a new instance and not saving in DB.
     *
     * @param newTransaction
     */
    public void addNewTransaction(Transaction newTransaction)
    {
        // The adapter will add the expense to the model collection so it can update observer
        // as well.
        mAdapter.insert(newTransaction, 0);
        updateTotals(newTransaction, false);
    }

    /**
     *
     *
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        public void expenseItemClickedInFragment(Transaction transaction);
    }

    /**
     * This encapsulates the saving of date using Parse.
     *
     * @param newTransaction
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
     *
     * @param updatedExpense
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
                    // Save the updated expense in Parse
                    saveDataInBackground(updatedExpense, expenseObjectInDb);
                }
            }
        });

    }

    /**
     * @return
     */
    private String generateId(String description, String sum, String currency)
    {
        String output;

        output = (description + sum + currency + Calendar.getInstance().get(Calendar.MINUTE) + UUID.randomUUID());
        output = output.replaceAll("\\s+", "-");

        return output;
    }

    /**
     * @param newTransaction
     * @param isRemoval
     */
    private void updateTotals(Transaction newTransaction, boolean isRemoval)
    {
        boolean isExpense = newTransaction.mIsExpense;
        int initIncome = Integer.valueOf(mTotalIncome.getText().toString());
        int initExpense = Integer.valueOf(mTotalExpense.getText().toString());
        int curTransValue = Integer.valueOf(newTransaction.mValue);

        // Collect info
        if (isExpense)
        {
            if (isRemoval)
                initExpense -= curTransValue;
            else
                initExpense += curTransValue;
        } else
        {
            if (isRemoval)
                initIncome -= curTransValue;
            else
                initIncome += curTransValue;
        }

        int initTotal = initIncome - initExpense;

        int color;

        // Set the total and it's colors
        if (initTotal < 0)
        {
            color = getResources().getColor(R.color.expense_color);
            initTotal = (-initTotal);
        } else
        {
            color = getResources().getColor(R.color.income_color);
        }

        // Update UI
        mTotalSavings.setTextColor(color);
        mTotalSavingsSign.setTextColor(color);
        mTotalSavings.setText(String.valueOf(initTotal));
        mTotalIncome.setText(String.valueOf(initIncome));
        mTotalExpense.setText(String.valueOf(initExpense));
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
     *
     * Callback to be called by contained adapter
     *
     * @param view
     */
    @Override
    public void removeFromFragmentList(View view)
    {
        final int position = getListView().getPositionForView((LinearLayout) view.getParent().getParent());
        if (position >= 0)
        {
            verifyRemoveDialog(position);
        }
    }

    /**
     *
     * Callback to be called by contained adapter
     *
     * @param view
     */
    @Override
    public void showItem(View view)
    {
        final int position = getListView().getPositionForView((LinearLayout) view.getParent());
        if (position >= 0)
        {
            listItemClicked(position);
        }
    }
}
