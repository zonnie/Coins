package com.moneyifyapp.activities.expenseDetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenseDetail.fragments.ExpenseDetailFragment;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.activities.preferences.PrefActivity;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.utils.Utils;

/**
 * This activity holds a fragment that handles the
 * insertion or viewing of a transaction's detailed info.
 * This is used for both addition of new transactions and editing existing ones.
 */
public class ExpenseDetailActivity extends Activity
        implements ExpenseDetailFragment.OnFragmentInteractionListener
{
    private int mRequestCode;
    private int mItemPosition;
    private String mDescription;
    private String mValue;
    private String mCurrency;
    private String mNotes;
    private int mImageName;
    private boolean mIsExpense;

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        setContentView(R.layout.activity_create_expense_layout);

        Utils.initializeActionBar(this);
        Utils.removeLogo(this);

        mRequestCode = getIntent().getExtras().getInt(ExpenseListFragment.REQ_CODE_KEY);
        boolean isEdit = false;

        if (mRequestCode == ExpensesActivity.REQ_EDIT_ITEM)
            isEdit = true;

        if (savedInstanceState == null)
        {
            Transaction tempExpense;

            // If this is not an edit, clone the transcation for edit
            if (!isEdit)
                tempExpense = new Transaction(Transaction.DEFUALT_TRANSCATION_ID);
            else
                tempExpense = createTransactionFromIntent();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, ExpenseDetailFragment.newInstance(isEdit, tempExpense))
                    .commit();
        }
    }

    /**
     *
     */
    private Transaction createTransactionFromIntent()
    {
        mItemPosition = getIntent().getExtras().getInt(ExpenseListFragment.MONTH_KEY);
        mDescription = getIntent().getExtras().getString(Transaction.KEY_DESCRIPTION);
        mValue = getIntent().getExtras().getString(Transaction.KEY_VALUE);
        //mCurrency = getIntent().getExtras().getString(Transaction.KEY_CURRENCY);
        mNotes = getIntent().getExtras().getString(Transaction.KEY_NOTES);
        mImageName = getIntent().getExtras().getInt(Transaction.KEY_IMAGE_NAME);
        mIsExpense = getIntent().getExtras().getBoolean(Transaction.KEY_TYPE);
        // Get the default currency
        mCurrency = PreferenceManager.getDefaultSharedPreferences(this).getString(PrefActivity.PREF_LIST_NAME, "$");

        return new Transaction(Transaction.DEFUALT_TRANSCATION_ID, mDescription, mValue, mCurrency, mNotes, mImageName, mIsExpense);
    }

    /**
     *
     */
    @Override
    public void onAddExpense(String addDescription, String addSum, String addCurrency, String addNote, int addImage, boolean isExpense)
    {
        Intent data = getIntent();

        data.putExtra(Transaction.KEY_DESCRIPTION, addDescription);
        data.putExtra(Transaction.KEY_VALUE, addSum);
        data.putExtra(Transaction.KEY_IMAGE_NAME, addImage);
        data.putExtra(Transaction.KEY_CURRENCY, addCurrency);
        data.putExtra(Transaction.KEY_NOTES, addNote);
        data.putExtra(ExpenseListFragment.MONTH_KEY, mItemPosition);
        data.putExtra(Transaction.KEY_TYPE, isExpense);

        setResult(ExpensesActivity.EXPENSE_RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    /**
     *
     */
    @Override
    public void cancel()
    {
        Intent data = getIntent();
        setResult(ExpensesActivity.EXPENSE_RESULT_CANCELED, data);

        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
