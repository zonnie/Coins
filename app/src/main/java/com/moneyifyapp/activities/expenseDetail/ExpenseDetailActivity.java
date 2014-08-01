package com.moneyifyapp.activities.expenseDetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

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
    private String mTransactionDay;
    private int mImageName;
    private boolean mIsExpense;
    private boolean mSaved;
    private int mMonth;

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
        Utils.setupBackButton(this);
        Utils.setLogo(this, R.drawable.transaction);

        if(getIntent().getExtras() != null)
        {
            if(getIntent().hasExtra(ExpenseListFragment.REQ_CODE_KEY))
                mRequestCode = getIntent().getExtras().getInt(ExpenseListFragment.REQ_CODE_KEY);
            if(getIntent().hasExtra(ExpenseListFragment.MONTH_KEY))
                mMonth = getIntent().getExtras().getInt(ExpenseListFragment.MONTH_KEY);
        }

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
                    .add(R.id.container, ExpenseDetailFragment.newInstance(isEdit, tempExpense, mMonth))
                    .commit();
        }
    }

    /**
     *
     */
    private Transaction createTransactionFromIntent()
    {
        mItemPosition = getIntent().getExtras().getInt(ExpenseListFragment.ITEM_POS_KEY);
        mMonth = getIntent().getExtras().getInt(ExpenseListFragment.MONTH_KEY);
        mDescription = getIntent().getExtras().getString(Transaction.KEY_DESCRIPTION);
        mValue = getIntent().getExtras().getString(Transaction.KEY_VALUE);
        mTransactionDay = getIntent().getExtras().getString(ExpenseListFragment.DAY_KEY);
        mNotes = getIntent().getExtras().getString(Transaction.KEY_NOTES);
        mImageName = getIntent().getExtras().getInt(Transaction.KEY_IMAGE_NAME);
        mIsExpense = getIntent().getExtras().getBoolean(Transaction.KEY_TYPE);
        mSaved = getIntent().getExtras().getBoolean(ExpenseListFragment.TEMPLATE_KEY);

        // Get the default currency
        mCurrency = PreferenceManager.getDefaultSharedPreferences(this).getString(PrefActivity.PREF_LIST_NAME, "$");

        Transaction transaction = new Transaction(Transaction.DEFUALT_TRANSCATION_ID, mDescription, mValue,
                mCurrency, mNotes, mImageName, mIsExpense, mTransactionDay);
        transaction.mSaved = mSaved;

        return transaction;
    }

    /**
     *
     */
    @Override
    public void onAddExpense(String addDescription, String addSum, String addCurrency,
                             String addNote, int addImage, boolean isExpense, boolean isSaved)
    {
        Intent data = getIntent();

        data.putExtra(Transaction.KEY_DESCRIPTION, addDescription);
        data.putExtra(Transaction.KEY_VALUE, addSum);
        data.putExtra(Transaction.KEY_IMAGE_NAME, addImage);
        data.putExtra(Transaction.KEY_CURRENCY, addCurrency);
        data.putExtra(Transaction.KEY_NOTES, addNote);
        data.putExtra(ExpenseListFragment.ITEM_POS_KEY, mItemPosition);
        data.putExtra(ExpenseListFragment.MONTH_KEY, mMonth);
        data.putExtra(Transaction.KEY_TYPE, isExpense);
        data.putExtra(ExpenseListFragment.TEMPLATE_KEY, isSaved);

        setResult(ExpensesActivity.EXPENSE_RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    /**
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
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
            {
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
