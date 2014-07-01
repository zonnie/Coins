package com.moneyifyapp.fragments;

import android.app.Activity;
import android.app.ListFragment;
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
import com.moneyifyapp.activities.CreateExpenseActivity;
import com.moneyifyapp.activities.ExpensesActivity;
import com.moneyifyapp.adapters.ExpenseItemAdapter;
import com.moneyifyapp.model.MonthExpenses;
import com.moneyifyapp.model.SingleExpense;
import com.moneyifyapp.utils.Utils;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
    public static final String SHOW_EMPTY = "empty";
    public static final String FRAG_ID  = "frag_id";
    private String mFragmentId;
    private String mParam1;
    private Button mAddNewExpenseButton;
    private MonthExpenses mExpenses;
    private ExpenseItemAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    private ListView mList;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     * Factory to pass some data for different fragments creation.
     *
     * @param showEmpty
     * @return
     */
    public static ExpenseListFragment newInstance(String showEmpty, String id)
    {
        ExpenseListFragment fragment = new ExpenseListFragment();
        Bundle args = new Bundle();
        args.putString(SHOW_EMPTY, showEmpty);
        args.putString(FRAG_ID, id);
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
        mExpenses = new MonthExpenses();

        // Init Parse for data storing
        initializeExpenses();


        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(SHOW_EMPTY);
            mFragmentId = getArguments().getString(FRAG_ID);
        }

        if (!mParam1.equals("true"))
        {
            mAdapter = new ExpenseItemAdapter(getActivity(), R.layout.adapter_expense_item, mExpenses);
            // Load from static model
        } else
        {
            // Load from static model
            mAdapter = new ExpenseItemAdapter(getActivity(), R.layout.adapter_expense_item, mExpenses);
        }

        setListAdapter(mAdapter);
    }

    /**
     *
     */
    private void initializeExpenses()
    {
        // Init Parse for data storing
        Utils.initializeParse(getActivity());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("expense");
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
     *
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

        mAddNewExpenseButton = (Button) view.findViewById(R.id.addNewExpenseButton);
        mAddNewExpenseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), CreateExpenseActivity.class);
                startActivityForResult(intent, ExpensesActivity.EXPENSE_RESULT_OK);
            }
        });

        return view;
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getActivity(), "DEBUG : Got the result FRAGMENT", Toast.LENGTH_SHORT).show();

        String desc = data.getExtras().getString(SingleExpense.EXPENSE_KEY_DESCRIPTION);
        String sum = data.getExtras().getString(SingleExpense.EXPENSE_KEY_VALUE);

        if(!desc.isEmpty() && !sum.isEmpty())
        {
            addNewExpense(desc, sum);
        }

    }

    /**
     * @param savedState
     */
    @Override
    public void onActivityCreated(Bundle savedState)
    {
        super.onActivityCreated(savedState);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id)
            {
                int itemPosition;
                List<SingleExpense> expenses = mAdapter.getItems();

                for (int i = 0; i < expenses.size(); ++i)
                {
                    if (i == position)
                    {
                        removeItemWithId(position);
                        mAdapter.remove(position);

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
        SingleExpense expenseToRemove = mAdapter.getItems().get(position);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(SingleExpense.EXPENSE_CLASS_NAME);
        query.whereEqualTo(SingleExpense.EXPENSE_KEY_ID, expenseToRemove.mId);

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
        }
    }

    /**
     * @param addDescription
     * @param addSum
     */
    public void addNewExpense(String addDescription, String addSum)
    {
        String currency = "$";
        String newId = generateId(addDescription, addSum, currency);

        SingleExpense newSingleExpense = new SingleExpense(newId, addDescription, addSum, currency);

        ParseObject expenseObject = new ParseObject("expense");
        expenseObject.put(SingleExpense.EXPENSE_KEY_ID, newSingleExpense.mId);
        expenseObject.put(SingleExpense.EXPENSE_KEY_DESCRIPTION, newSingleExpense.mDescription);
        expenseObject.put(SingleExpense.EXPENSE_KEY_VALUE, newSingleExpense.mValue);
        expenseObject.put(SingleExpense.EXPENSE_KEY_CURRENCY, newSingleExpense.mCurrency);
        expenseObject.saveInBackground();

        mAdapter.add(newSingleExpense);
        Toast.makeText(getActivity(), "DEBUG : Created an item with " + SingleExpense.EXPENSE_KEY_ID +
                " of " + newId, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     *
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        public void expenseItemClickedInFragment(SingleExpense singleExpense);
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
                mAdapter.add(new SingleExpense(curExpense.getString(SingleExpense.EXPENSE_KEY_ID),
                        curExpense.getString(SingleExpense.EXPENSE_KEY_DESCRIPTION),
                        curExpense.getString(SingleExpense.EXPENSE_KEY_VALUE),
                        curExpense.getString(SingleExpense.EXPENSE_KEY_CURRENCY)));
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

    /**
     *
     * @return
     */
    public String getFragId()
    {
        return  mFragmentId;
    }
}
