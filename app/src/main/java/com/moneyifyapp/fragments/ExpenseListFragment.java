package com.moneyifyapp.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.moneyifyapp.R;
import com.moneyifyapp.adapters.ExpenseItemAdapter;
import com.moneyifyapp.dialogs.NewExpenseDialog;
import com.moneyifyapp.model.MonthExpenses;
import com.moneyifyapp.model.SingleExpense;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 *
 * A fragment representing a list of Items.
 *
 */
public class ExpenseListFragment extends ListFragment
        implements NewExpenseDialog.onSubmitListener
{

    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String SHOW_EMPTY = "empty";
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
    public static ExpenseListFragment newInstance(String showEmpty)
    {
        ExpenseListFragment fragment = new ExpenseListFragment();
        Bundle args = new Bundle();
        args.putString(SHOW_EMPTY, showEmpty);
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
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mExpenses = new MonthExpenses();

        // Init Parse for data storing
        Parse.initialize(getActivity(), "7BjKxmwKAG3nVfaDHWxWusowkJJ4kGNyMlwjrbT8", "c6uhzWLV5SPmCx259cPjHhW8qvw5VUCvDwpVVjFD");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("expense");
        query.findInBackground(new FindCallback<ParseObject>()
        {
            public void done(List<ParseObject> expenseList, ParseException e)
            {
                if (e == null)
                {
                    initFromList(expenseList);
                }
                else
                {
                    Toast toast = Toast.makeText(getActivity(), "We have some DB issues... :(", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(SHOW_EMPTY);
        }

        if(!mParam1.equals("true"))
        {
            mAdapter = new ExpenseItemAdapter(getActivity(), R.layout.adapter_expense_item, mExpenses);
            // Load from static model
        }
        else
        {
            // Load from static model
            mAdapter = new ExpenseItemAdapter(getActivity(), R.layout.adapter_expense_item, mExpenses);
        }

        setListAdapter(mAdapter);
    }

    /**
     *
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

        mAddNewExpenseButton = (Button)view.findViewById(R.id.addNewExpenseButton);
        mAddNewExpenseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NewExpenseDialog dialog = new NewExpenseDialog();
                dialog.mListener = ExpenseListFragment.this;
                dialog.show(getFragmentManager(), "tag");
            }
        });

        return view;
    }

    /**
     *
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

                for(int i = 0; i < expenses.size(); ++i)
                {
                    if(i == position)
                    {
                        removeItemWithId(Integer.valueOf(expenses.get(i).mId));
                        mAdapter.remove(expenses.get(i));

                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     *
     * @param id
     */
    private void removeItemWithId(int id)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(SingleExpense.EXPENSE_CLASS_NAME);
        query.whereEqualTo(SingleExpense.EXPENSE_KEY_ID,id);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> list, ParseException e)
            {

                // TODO Auto-generated method stub
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
                                Toast.makeText(getActivity(), "Deleted Successfully!", Toast.LENGTH_LONG).show();
                            } else
                            {
                                Toast.makeText(getActivity(), "Cant Delete Expense!" + e.toString(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });
    }

    /**
     *
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
     *
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
     *
     * @param addDescription
     * @param addSum
     */
    @Override
    public void onAddExpenseInDialog(String addDescription, String addSum)
    {
        String newId = String.valueOf(mAdapter.getItems().size());
        SingleExpense newSingleExpense = new SingleExpense(newId, addDescription, addSum, "$");

        ParseObject expenseObject = new ParseObject("expense");
        expenseObject.put(SingleExpense.EXPENSE_KEY_ID, newSingleExpense.mId);
        expenseObject.put(SingleExpense.EXPENSE_KEY_DESCRIPTION, newSingleExpense.mDescription);
        expenseObject.put(SingleExpense.EXPENSE_KEY_VALUE, newSingleExpense.mValue);
        expenseObject.put(SingleExpense.EXPENSE_KEY_CURRENCY, newSingleExpense.mCurrency);
        expenseObject.saveInBackground();

        mAdapter.add(newSingleExpense);
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
     *
     * Initializes the expenses from the remote DB.
     *
     * @param list
     */
    public void initFromList(List<ParseObject> list)
    {
        if(list != null)
        {
            for(ParseObject curExpense : list)
            {
                mAdapter.add(new SingleExpense(curExpense.getString(SingleExpense.EXPENSE_KEY_ID),
                        curExpense.getString(SingleExpense.EXPENSE_KEY_DESCRIPTION),
                        curExpense.getString(SingleExpense.EXPENSE_KEY_VALUE),
                        curExpense.getString(SingleExpense.EXPENSE_KEY_CURRENCY)));
            }
        }
    }
}
