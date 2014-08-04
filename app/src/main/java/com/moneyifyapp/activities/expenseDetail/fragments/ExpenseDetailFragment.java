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
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenseDetail.ImagePickerActivity;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
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
    private EditText mExpenseDescription;
    private EditText mExpenseValue;
    private ImageButton mExpenseIcon;
    private EditText mExpenseNotes;
    private ToggleButton mToggleIsExpense;
    private OnDetailFragmentSubmit mListener;
    private Transaction mTempExpenseObject;
    private boolean mIsEdit;
    private int mCurrentImage;
    public static final String EXPENSE_EDIT_KEY = "edit";
    private AlphaAnimation mAlphaDown;
    private AlphaAnimation mAlphaUp;
    private View mView;
    private final String ERROR_MISSING_DESC = "What is this transaction";
    private final String ERROR_MISSING_SUM = "How much did it cost";

    /**
     */
    public static ExpenseDetailFragment newInstance(boolean isEdit, Transaction expense)
    {
        ExpenseDetailFragment fragment = new ExpenseDetailFragment();
        Bundle args = new Bundle();
        if (expense != null)
        {
            String transactionJson = JsonServiceYearTransactions.getInstance().toJson(expense);

            args.putString(Transaction.TRANS_JSON, transactionJson);
            args.putBoolean(EXPENSE_EDIT_KEY, isEdit);

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

            String transactionJson = getArguments().getString(Transaction.TRANS_JSON);
            mTempExpenseObject = JsonServiceYearTransactions.getInstance().fromJsonToTransaction(transactionJson);

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
        mView = inflater.inflate(R.layout.fragment_expense_detail, container, false);

        storeViews();
        bindViewsToEventListeners();

        initImageView();
        initDataIfTransactionEdited();

        return mView;
    }

    /**
     */
    private void storeViews()
    {
        mExpenseDescription = (EditText) mView.findViewById(R.id.addExpenseDescription);
        mExpenseValue = (EditText) mView.findViewById(R.id.addExpenseSum);
        mExpenseIcon = (ImageButton) mView.findViewById(R.id.addExpenseImage);
        mExpenseNotes = (EditText) mView.findViewById(R.id.addExpenseNotes);
        mToggleIsExpense = (ToggleButton) mView.findViewById(R.id.isExpenseToggle);
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
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                mExpenseIcon.startAnimation(mAlphaDown);
            }
        });

    }

    /**
     */
    private void initImageView()
    {
        mExpenseIcon.setImageResource(Images.getImageByPosition(mTempExpenseObject.mImageResourceIndex));
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
    public boolean onSumbitPressed()
    {
        mExpenseDescription.setError(null);
        mExpenseValue.setError(null);

        if (mListener != null)
        {
            String description = mExpenseDescription.getText().toString();
            int imageName = mCurrentImage;
            String note = mExpenseNotes.getText().toString();
            String sum = mExpenseValue.getText().toString();
            String currency = Utils.getDefaultCurrency(getActivity());
            boolean isExpense = !mToggleIsExpense.isChecked();

            if (description.isEmpty() || sum.isEmpty())
            {
                if(description.isEmpty())
                    mExpenseDescription.setError(ERROR_MISSING_DESC);
                else if(sum.isEmpty())
                    mExpenseValue.setError(ERROR_MISSING_SUM);
                return false;
            }
            else
            {
                mTempExpenseObject.mDescription = description;
                mTempExpenseObject.mImageResourceIndex = imageName;
                mTempExpenseObject.mNotes = note;
                mTempExpenseObject.mValue = sum;
                mTempExpenseObject.mCurrency = currency;
                mTempExpenseObject.mIsExpense = isExpense;
                mListener.onTransactionSubmit(mTempExpenseObject);
                return true;
            }
        }

        return false;
    }

    /**
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnDetailFragmentSubmit) activity;
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
     * Interface for containing activity.
     */
    public interface OnDetailFragmentSubmit
    {
        public void onTransactionSubmit(Transaction transaction);
        public void cancel();
    }
}
