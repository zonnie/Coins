package com.moneyifyapp.activities.expenseDetail.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.moneyifyapp.R;
import com.moneyifyapp.model.Transaction;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpenseDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpenseDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseDetailFragment extends Fragment
{
    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    Button mSubmitButton;
    Button mCancelButton;
    EditText mExpenseDescription;
    EditText mExpenseValue;
    ImageView mExpenseIcon;
    TextView mExpenseCurrency;
    EditText mExpenseNotes;
    ToggleButton mToggleIsExpense;
    private OnFragmentInteractionListener mListener;
    private Transaction mTempExpenseObject;
    boolean mIsEdit;
    public static final String EXPENSE_EDIT_KEY = "edit";


    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     *
     *
     */
    public static ExpenseDetailFragment newInstance(boolean isEdit, Transaction expense)
    {
        ExpenseDetailFragment fragment = new ExpenseDetailFragment();
        Bundle args = new Bundle();
        if(expense != null)
        {
            args.putBoolean(EXPENSE_EDIT_KEY, isEdit);
            args.putString(Transaction.KEY_DESCRIPTION, expense.mDescription);
            args.putString(Transaction.KEY_VALUE, expense.mValue);
            args.putString(Transaction.KEY_CURRENCY, expense.mCurrency);
            args.putString(Transaction.KEY_NOTES, expense.mNotes);
            args.putString(Transaction.KEY_IMAGE_NAME, expense.mImageName);
            args.putBoolean(Transaction.KEY_TYPE, expense.mIsExpense);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public ExpenseDetailFragment()
    {
        // Required empty public constructor
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mIsEdit = getArguments().getBoolean(EXPENSE_EDIT_KEY);
            String description = getArguments().getString(Transaction.KEY_DESCRIPTION);
            String value = getArguments().getString(Transaction.KEY_VALUE);
            String currency = getArguments().getString(Transaction.KEY_CURRENCY);
            String note = getArguments().getString(Transaction.KEY_NOTES);
            String image = getArguments().getString(Transaction.KEY_IMAGE_NAME);
            boolean isExpense = getArguments().getBoolean(Transaction.KEY_TYPE);

            mTempExpenseObject = new Transaction("", description, value, currency, note, image,isExpense);
        }
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_create_expense_layout, container, false);

        // Save the views
        mSubmitButton = (Button)view.findViewById(R.id.submitButton);
        mCancelButton = (Button)view.findViewById(R.id.cancelAddButton);
        mExpenseDescription = (EditText)view.findViewById(R.id.addExpenseDescription);
        mExpenseCurrency = (TextView)view.findViewById(R.id.addExpenseCurrency);
        mExpenseValue = (EditText)view.findViewById(R.id.addExpenseSum);
        //TODO need to rectify this
        //mExpenseIcon = (ImageView)view.
        mExpenseNotes = (EditText)view.findViewById(R.id.addExpenseNotes);
        mToggleIsExpense = (ToggleButton)view.findViewById(R.id.isExpenseToggle);

        // Bind listener
        mSubmitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSumbitPressed();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onCancelPressed();
            }
        });

        if(mIsEdit)
        {
            mExpenseDescription.setText(mTempExpenseObject.mDescription);
            mExpenseValue.setText(mTempExpenseObject.mValue);
            mExpenseCurrency.setText(mTempExpenseObject.mCurrency);
            mExpenseNotes.setText(mTempExpenseObject.mNotes);
            //TODO for image need to do
            //mExpenseIcon.setText(mTempExpenseObject.mDescription);
            mToggleIsExpense.setChecked(!mTempExpenseObject.mIsExpense);
        }

        return view;
    }

    /**
     *
     *
     */
    public void onSumbitPressed()
    {
        if (mListener != null)
        {
            String description = mExpenseDescription.getText().toString();
            String imageName = "";
            String note = mExpenseNotes.getText().toString();
            String sum = mExpenseValue.getText().toString();
            String currency = mExpenseCurrency.getText().toString();
            boolean isExpense = !mToggleIsExpense.isChecked();  // If it's un-toggled this means this is an expense

            if(description.isEmpty() || sum.isEmpty())
            {
                Toast.makeText(getActivity(), "Please fill all required info", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mListener.onAddExpense(description, sum, currency, note, imageName, isExpense);
            }
        }
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
     * Interface for containing activity.
     */
    public interface OnFragmentInteractionListener
    {
        public void onAddExpense(String addDescription, String addSum, String currency, String addNote, String addImage, boolean isExpense);

        public void cancel();
    }

    /**
     *
     */
    private void onCancelPressed()
    {
        mListener.cancel();
    }
}
