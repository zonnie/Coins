package com.moneyifyapp.activities.expenses.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.moneyifyapp.R;
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
import java.util.List;
import java.util.UUID;

/**
 * A fragment representing a list of Items.
 */
public class ExpenseListFragment extends ListFragment
{

    /********************************************************************/
    /**                          Members                               **/
    /**
     * ****************************************************************
     */

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String PAGE_ID_KEY = "frag_id";
    public static final String MONTH_KEY = "month";
    public static final String REQ_CODE_KEY = "req";
    private Button mNewTransactionButton;
    private MonthTransactions mTransactions;
    private YearTransactions mYearTransactions;
    private ExpenseItemAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    private ListView mList;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     * Factory to pass some data for different fragments creation.
     *
     * @param pageId
     *
     * @return
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
        setHasOptionsMenu(true);

        if(mYearTransactions == null)
        {
            mYearTransactions = new YearTransactions();
        }

        if (getArguments() != null)
        {
            // Create a new month
            mYearTransactions.addMonth(getArguments().getInt(PAGE_ID_KEY));
            mTransactions = mYearTransactions.get(getArguments().getInt(PAGE_ID_KEY));
        }

        mAdapter = new ExpenseItemAdapter(getActivity(), R.layout.adapter_expense_item, mTransactions);

        // Init Parse for data storing
        initializeExpenses();

        setListAdapter(mAdapter);
    }

    /**
     *
     */
    private void initializeExpenses()
    {
        // Init Parse for data storing
        Utils.initializeParse(getActivity());

        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("expense");
        query.whereEqualTo(ExpensesActivity.PARSE_USER_KEY, user);
        query.whereEqualTo(MONTH_KEY, mTransactions.mMonthNumber);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            public void done(List<ParseObject> expenseList, ParseException e)
            {
                if (e == null)
                {
                    buildExpenseListFromParse(expenseList);
                } else
                {
                    Toast toast = Toast.makeText(getActivity(), "We have some DB issues... :(", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
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
            initializeExpenses();
            Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT);
        }

        return super.onOptionsItemSelected(item);
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

        return view;
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
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id)
            {
                int itemPosition;
                List<Transaction> expenses = mAdapter.getItems();

                for (int i = 0; i < expenses.size(); ++i)
                {
                    if (i == position)
                    {
                        verifyRemoveDialog(position);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
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
                            // TODO Auto-generated method stub
                            if (e == null)
                            {
                                Toast.makeText(getActivity(), "DEBUG : Deleted Successfully!", Toast.LENGTH_LONG).show();
                            } else
                            {
                                Toast.makeText(getActivity(), "DEBUG : Cant Delete Expense!" + e.toString(), Toast.LENGTH_LONG).show();
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
                .setMessage("You selected to remove " + "\"" + expenseDescription +
                        "\"" + ".\nAre you sure you want to remove this expense ?")
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
        removeItemWithId(position);
        mAdapter.remove(position);
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
            String image = data.getExtras().getString(Transaction.KEY_IMAGE_NAME);
            String currency = data.getExtras().getString(Transaction.KEY_CURRENCY);
            String note = data.getExtras().getString(Transaction.KEY_NOTES);
            Boolean isExpense = data.getExtras().getBoolean(Transaction.KEY_TYPE);
            //TODO: image when its done
            //String imageName = data.getExtras().getString(SingleExpense.KEY_NOTES);
            int position = data.getExtras().getInt(MONTH_KEY);

            if (!desc.isEmpty() && !sum.isEmpty())
            {
                if (requestCode == ExpensesActivity.REQ_NEW_ITEM)
                {
                    // TODO: This needs to be dynamic
                    currency = "$";
                    addNewTransaction(desc, sum, currency, note, image, isExpense);
                } else if (requestCode == ExpensesActivity.REQ_EDIT_ITEM)
                {
                    Transaction tempExpense = new Transaction("0", desc, sum, currency, note, image, isExpense);
                    mAdapter.update(position, tempExpense);
                    // ! We now MUST pass the item from the collection to preserver
                    // the ID, the tempExpense object has an 'empty' ID.
                    updateDataInBackground(mAdapter.getItems().get(position));
                }
            }
        }

    }

    /**
     * @param addDescription
     * @param addSum
     */
    public void addNewTransaction(String addDescription, String addSum, String currency, String note, String image, boolean isExpense)
    {
        String newId = generateId(addDescription, addSum, currency);

        // Create a new expense model
        Transaction newTransaction = new Transaction(newId, addDescription, addSum, currency, note, image, isExpense);

        // Save to Parse
        ParseObject expenseObject = new ParseObject(Transaction.CLASS_NAME);
        saveDataInBackground(newTransaction, expenseObject);

        // The adapter will add the expense to the model collection so it can update observer
        // as well.
        //mAdapter.insert(newTransaction, 0);
        mAdapter.add(newTransaction);
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
        expenseObject.put(Transaction.KEY_IMAGE_NAME, newTransaction.mImageName);
        expenseObject.put(Transaction.KEY_NOTES, newTransaction.mNotes);
        expenseObject.put(Transaction.KEY_TYPE, newTransaction.mIsExpense);
        expenseObject.put(ExpensesActivity.PARSE_USER_KEY, user);
        expenseObject.put(MONTH_KEY, mTransactions.mMonthNumber);
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
                mAdapter.add(new Transaction(curExpense.getString(Transaction.KEY_ID),
                        curExpense.getString(Transaction.KEY_DESCRIPTION),
                        curExpense.getString(Transaction.KEY_VALUE),
                        curExpense.getString(Transaction.KEY_CURRENCY),
                        curExpense.getString(Transaction.KEY_NOTES),
                        curExpense.getString(Transaction.KEY_IMAGE_NAME),
                        curExpense.getBoolean(Transaction.KEY_TYPE)));
            }
        }
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
}
