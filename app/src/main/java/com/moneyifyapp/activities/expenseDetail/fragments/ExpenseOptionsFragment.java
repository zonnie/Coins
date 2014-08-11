package com.moneyifyapp.activities.expenseDetail.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.moneyifyapp.R;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.utils.AnimationUtils;
import com.moneyifyapp.utils.JsonServiceYearTransactions;

/**
 * A fragment for detail transaction view.
 * This is used for both creation and edition.
 */
public class ExpenseOptionsFragment extends Fragment
{
    private ToggleButton mToggleSave;
    private OnOptionsFragmentSubmit mListener;
    private Transaction mTempExpenseObject;
    private RadioGroup mRepeatGroup;
    private boolean mIsEdit;
    public static final String EXPENSE_EDIT_KEY = "edit";
    private View mView;
    private Animation mBounceAnimation;

    /**
     */
    public static ExpenseOptionsFragment newInstance(boolean isEdit, Transaction expense)
    {
        ExpenseOptionsFragment fragment = new ExpenseOptionsFragment();
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
    public ExpenseOptionsFragment()
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
        mBounceAnimation = AnimationUtils.getBounceAnimtion(getActivity());

        if (getArguments() != null)
        {
            mIsEdit = getArguments().getBoolean(EXPENSE_EDIT_KEY);
            String transactionJson = getArguments().getString(Transaction.TRANS_JSON);
            mTempExpenseObject = JsonServiceYearTransactions.getInstance().fromJsonToTransaction(transactionJson);
        }
    }

    /**
     * On Create View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_expense_options, container, false);

        storeViews();
        initDataIfTransactionEdited();
        initRepeatRadioButton();

        return mView;
    }

    /**
     */
    private void storeViews()
    {
        mToggleSave = (ToggleButton)mView.findViewById(R.id.toggle_template_save);
        mToggleSave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                mToggleSave.startAnimation(mBounceAnimation);
            }
        });

        mRepeatGroup = (RadioGroup)mView.findViewById(R.id.detail_reoccur_group);
        mRepeatGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId)
                    {
                        switch (checkedId)
                        {
                            case R.id.detail_reoccur_none_radio:

                        }
                    }
                });
    }

    /**
     */
    private void initDataIfTransactionEdited()
    {
        if (mIsEdit)
            mToggleSave.setChecked(mTempExpenseObject.mSaved);
    }

    /**
     */
    private void initRepeatRadioButton()
    {
        if (mIsEdit)
        {
            if(mTempExpenseObject.mRepeatType != null)
            {
                switch (mTempExpenseObject.mRepeatType)
                {
                    case NONE:
                        mRepeatGroup.check(R.id.detail_reoccur_none_radio);
                        break;
                    case MONTHLY:
                        mRepeatGroup.check(R.id.detail_reoccur_monthly_radio);
                        break;
                    case YEARLY:
                        mRepeatGroup.check(R.id.detail_reoccur_yearly_radio);
                        break;
                }
            }
            else
            {
                mRepeatGroup.check(R.id.detail_reoccur_none_radio);
                mTempExpenseObject.mRepeatType = Transaction.REPEAT_TYPE.NONE;
            }
        }
    }

    /**
     */
    public void onSumbitPressed()
    {
        if (mListener != null)
        {
            boolean isSaved = mToggleSave.isChecked();
            mTempExpenseObject.mSaved = isSaved;
            mTempExpenseObject.mRepeatType = Transaction.REPEAT_TYPE.getTypeById(mRepeatGroup.getCheckedRadioButtonId());
            mListener.OnOptionsSubmit(mTempExpenseObject);
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
            mListener = (OnOptionsFragmentSubmit) activity;
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
    public interface OnOptionsFragmentSubmit
    {
        public void OnOptionsSubmit(Transaction transaction);
        public void cancel();
    }
}