package com.moneyifyapp.activities.expenseDetail.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenseDetail.ImagePickerActivity;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.utils.AnimationUtils;
import com.moneyifyapp.utils.JsonServiceYearTransactions;
import com.moneyifyapp.utils.Utils;

/**
 * A fragment for detail transaction view.
 * This is used for both creation and edition.
 */
public class ExpenseDetailFragment extends Fragment
{
    private TextView mDetailDateDay;
    private TextView mDetailDateMonth;
    private EditText mExpenseDescription;
    private EditText mExpenseValue;
    private ImageButton mExpenseIcon;
    private EditText mExpenseNotes;
    private ToggleButton mToggleIsExpense;
    private ToggleButton mToggleSave;
    private OnFragmentInteractionListener mListener;
    private Transaction mTempExpenseObject;
    private boolean mIsEdit;
    private int mCurrentImage;
    public static final String EXPENSE_EDIT_KEY = "edit";
    private AlphaAnimation mAlphaDown;
    private AlphaAnimation mAlphaUp;
    private View mView;
    private int mMonth;
    private String mMonthPrefix;

    /**
     */
    public static ExpenseDetailFragment newInstance(boolean isEdit, Transaction expense, int month)
    {
        ExpenseDetailFragment fragment = new ExpenseDetailFragment();
        Bundle args = new Bundle();
        if (expense != null)
        {
            String transactionJson = JsonServiceYearTransactions.getInstance().toJson(expense);

            args.putString(Transaction.TRANS_JSON, transactionJson);
            args.putBoolean(EXPENSE_EDIT_KEY, isEdit);
            args.putInt(ExpenseListFragment.MONTH_KEY, month);

        }
        fragment.setArguments(args);
        return fragment;
    }

    /**
     */
    public ExpenseDetailFragment()
    {
        // Required empty public constructor
    }

    /**
     * On Create
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            mIsEdit = getArguments().getBoolean(EXPENSE_EDIT_KEY);
            mMonth = getArguments().getInt(ExpenseListFragment.MONTH_KEY);

            String transactionJson = getArguments().getString(Transaction.TRANS_JSON);
            mTempExpenseObject = JsonServiceYearTransactions.getInstance().fromJsonToTransaction(transactionJson);

            mMonthPrefix = Utils.getMonthNameByIndex(mMonth);
            mCurrentImage = mTempExpenseObject.mImageResourceIndex;

            mAlphaDown = AnimationUtils.getAlphaDownAnimation();
            mAlphaUp = AnimationUtils.getmAlphaUpAnimation();
        }
    }

    /**
     * On Create View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_create_expense_layout, container, false);

        storeViews();
        bindViewsToEventListeners();

        initImageView();
        initDataIfTransactionEdited();
        initDetailDateLabels();

        return mView;
    }

    /**
     */
    private void storeViews()
    {
        mDetailDateDay = (TextView) mView.findViewById(R.id.detail_date_day);
        mExpenseDescription = (EditText) mView.findViewById(R.id.addExpenseDescription);
        mExpenseValue = (EditText) mView.findViewById(R.id.addExpenseSum);
        mExpenseIcon = (ImageButton) mView.findViewById(R.id.addExpenseImage);
        mExpenseNotes = (EditText) mView.findViewById(R.id.addExpenseNotes);
        mDetailDateMonth = (TextView) mView.findViewById(R.id.detail_date_month);
        mToggleIsExpense = (ToggleButton) mView.findViewById(R.id.isExpenseToggle);
        mToggleSave = (ToggleButton)mView.findViewById(R.id.toggle_template_save);
    }

    /**
     */
    private void bindViewsToEventListeners()
    {
        mExpenseNotes.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if ((actionId == EditorInfo.IME_ACTION_NEXT))
                {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mExpenseNotes.getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        });
        mExpenseIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
                startActivityForResult(intent, ExpensesActivity.IMAGE_PICK_REQ);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                mExpenseIcon.startAnimation(mAlphaDown);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.submit_item)
            onSumbitPressed();
        else if(id == android.R.id.home)
            onCancelPressed();

        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.expense_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     */
    private void initImageView()
    {
        mExpenseIcon.setImageResource(Images.getImageByPosition(mTempExpenseObject.mImageResourceIndex));
    }

    /**
     */
    private void initDetailDateLabels()
    {
        String date = mTempExpenseObject.mTransactionDay;
        mDetailDateDay.setText(date);
        if(mMonth >= 0)
            mDetailDateMonth.setText(mMonthPrefix.toUpperCase());
    }

    /**
     */
    private void initDataIfTransactionEdited()
    {
        if (mIsEdit)
        {
            mExpenseDescription.setText(mTempExpenseObject.mDescription);
            mExpenseValue.setText(mTempExpenseObject.mValue);
            mExpenseNotes.setText(mTempExpenseObject.mNotes);
            mExpenseIcon.setImageResource(Images.getImageByPosition(mTempExpenseObject.mImageResourceIndex));
            mToggleIsExpense.setChecked(!mTempExpenseObject.mIsExpense);
            mToggleSave.setChecked(mTempExpenseObject.mSaved);
        }
    }

    /**
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        mExpenseIcon.startAnimation(mAlphaUp);

        if (resultCode == ExpensesActivity.IMAGE_PICK_OK)
        {
            mCurrentImage = data.getExtras().getInt(Transaction.KEY_IMAGE_NAME);
            mExpenseIcon.setImageResource(Images.getImageByPosition(mCurrentImage));
        }
    }


    /**
     */
    public void onSumbitPressed()
    {
        if (mListener != null)
        {
            String description = mExpenseDescription.getText().toString();
            int imageName = mCurrentImage;
            String note = mExpenseNotes.getText().toString();
            String sum = mExpenseValue.getText().toString();
            String currency = Utils.getDefaultCurrency(getActivity());
            boolean isExpense = !mToggleIsExpense.isChecked();  // If it's un-toggled this means this is an expense
            boolean isSaved = mToggleSave.isChecked();

            if (description.isEmpty() || sum.isEmpty())
            {
                Toast.makeText(getActivity(), "Please fill all required info", Toast.LENGTH_SHORT).show();
            } else
            {
                mListener.onAddExpense(description, sum, currency, note, imageName, isExpense, isSaved);
            }
        }
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
     * Interface for containing activity.
     */
    public interface OnFragmentInteractionListener
    {
        public void onAddExpense(String addDescription, String addSum, String currency,
                                 String addNote, int addImage, boolean isExpense, boolean isSaved);

        public void cancel();
    }

    /**
     */
    private void onCancelPressed()
    {
        mListener.cancel();
    }
}
