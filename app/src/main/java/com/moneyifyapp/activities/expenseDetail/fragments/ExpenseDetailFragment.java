package com.moneyifyapp.activities.expenseDetail.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenseDetail.ImagePickerActivity;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.Transaction;

/**
 * A fragment for detail transaction view.
 * This is used for both creation and edition.
 */
public class ExpenseDetailFragment extends Fragment
{
    /********************************************************************/
    /**                          Members                               **/
    /**
     * ****************************************************************
     */

    Button mSubmitButton;
    Button mCancelButton;
    EditText mExpenseDescription;
    EditText mExpenseValue;
    ImageButton mExpenseIcon;
    TextView mExpenseCurrency;
    EditText mExpenseNotes;
    ToggleButton mToggleIsExpense;
    private OnFragmentInteractionListener mListener;
    private Transaction mTempExpenseObject;
    boolean mIsEdit;
    private int mCurrentImage;
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
        if (expense != null)
        {
            args.putBoolean(EXPENSE_EDIT_KEY, isEdit);
            args.putString(Transaction.KEY_DESCRIPTION, expense.mDescription);
            args.putString(Transaction.KEY_VALUE, expense.mValue);
            args.putString(Transaction.KEY_CURRENCY, expense.mCurrency);
            args.putString(Transaction.KEY_NOTES, expense.mNotes);
            args.putInt(Transaction.KEY_IMAGE_NAME, expense.mImageResourceIndex);
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
            int image = getArguments().getInt(Transaction.KEY_IMAGE_NAME);
            boolean isExpense = getArguments().getBoolean(Transaction.KEY_TYPE);

            mTempExpenseObject = new Transaction("", description, value, currency, note, image, isExpense);

            mCurrentImage = image;
        }
    }

    /**
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
        mSubmitButton = (Button) view.findViewById(R.id.submitButton);
        mCancelButton = (Button) view.findViewById(R.id.cancelAddButton);
        mExpenseDescription = (EditText) view.findViewById(R.id.addExpenseDescription);
        mExpenseCurrency = (TextView) view.findViewById(R.id.addExpenseCurrency);
        mExpenseValue = (EditText) view.findViewById(R.id.addExpenseSum);
        mExpenseValue.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == R.id.sum_action)
                {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mExpenseValue.getWindowToken(), 0);
                    onSumbitPressed();
                    handled = true;
                }
                return handled;
            }
        });


        //TODO need to rectify this
        mExpenseIcon = (ImageButton) view.findViewById(R.id.addExpenseImage);
        mExpenseNotes = (EditText) view.findViewById(R.id.addExpenseNotes);
        mExpenseNotes.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == R.id.note_action)
                {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mExpenseNotes.getWindowToken(), 0);
                    onSumbitPressed();
                    handled = true;
                }
                return handled;
            }
        });
        mToggleIsExpense = (ToggleButton) view.findViewById(R.id.isExpenseToggle);

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
        // Set default image
        mExpenseIcon.setImageResource(Images.getImageByPosition(mTempExpenseObject.mImageResourceIndex));
        mExpenseIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
                startActivityForResult(intent, ExpensesActivity.IMAGE_PICK_REQ);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        if (mIsEdit)
        {
            //mCurrencySpinner.setSelection(Utils.findIndextByString(mTempExpenseObject.mCurrency));
            mExpenseDescription.setText(mTempExpenseObject.mDescription);
            mExpenseValue.setText(mTempExpenseObject.mValue);
            mExpenseCurrency.setText(mTempExpenseObject.mCurrency);
            mExpenseNotes.setText(mTempExpenseObject.mNotes);
            mExpenseIcon.setImageResource(Images.getImageByPosition(mTempExpenseObject.mImageResourceIndex));
            mToggleIsExpense.setChecked(!mTempExpenseObject.mIsExpense);
        }

        return view;
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == ExpensesActivity.IMAGE_PICK_OK)
        {

            mCurrentImage = data.getExtras().getInt(Transaction.KEY_IMAGE_NAME);
            mExpenseIcon.setImageResource(Images.getImageByPosition(mCurrentImage));
        } else if (resultCode == ExpensesActivity.IMAGE_PICK_CANCEL)
        {
            //Nothing yet
        }
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
            int imageName = mCurrentImage;
            String note = mExpenseNotes.getText().toString();
            String sum = mExpenseValue.getText().toString();
            String currency = mExpenseCurrency.getText().toString();
            //String currency = mCurrencySpinner.getSelectedItem().toString();
            boolean isExpense = !mToggleIsExpense.isChecked();  // If it's un-toggled this means this is an expense

            if (description.isEmpty() || sum.isEmpty())
            {
                Toast.makeText(getActivity(), "Please fill all required info", Toast.LENGTH_SHORT).show();
            } else
            {
                mListener.onAddExpense(description, sum, currency, note, imageName, isExpense);
            }
        }
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
     * Interface for containing activity.
     */
    public interface OnFragmentInteractionListener
    {
        public void onAddExpense(String addDescription, String addSum, String currency, String addNote, int addImage, boolean isExpense);

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
