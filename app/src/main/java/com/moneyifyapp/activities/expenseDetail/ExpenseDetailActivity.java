package com.moneyifyapp.activities.expenseDetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenseDetail.fragments.ExpenseDetailFragment;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.utils.Utils;

public class ExpenseDetailActivity extends Activity
        implements ExpenseDetailFragment.OnFragmentInteractionListener
{
    /********************************************************************/
    /**                          Section                               **/
    /**
     * ****************************************************************
     */

    private int mRequestCode;
    private int mItemPosition;
    private String mDescription;
    private String mValue;
    private String mCurrency;
    private String mNotes;
    private int mImageName;
    private boolean mIsExpense;

    /********************************************************************/
    /**                          Section                               **/
    /********************************************************************/


    /**
     * @param savedInstanceState
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
        {
            isEdit = true;
        }

        if (savedInstanceState == null)
        {
            // This is a new item to create
            if (!isEdit)
            {
                Transaction tempExpense = new Transaction("0");
                getFragmentManager().beginTransaction()
                        .add(R.id.container, ExpenseDetailFragment.newInstance(isEdit, tempExpense))
                        .commit();
            }
            // We are editing an existing item
            else
            {
                mItemPosition = getIntent().getExtras().getInt(ExpenseListFragment.MONTH_KEY);
                mDescription = getIntent().getExtras().getString(Transaction.KEY_DESCRIPTION);
                mValue = getIntent().getExtras().getString(Transaction.KEY_VALUE);
                mCurrency = getIntent().getExtras().getString(Transaction.KEY_CURRENCY);
                mNotes = getIntent().getExtras().getString(Transaction.KEY_NOTES);
                mImageName = getIntent().getExtras().getInt(Transaction.KEY_IMAGE_NAME);
                mIsExpense = getIntent().getExtras().getBoolean(Transaction.KEY_TYPE);

                Transaction tempExpense = new Transaction("0", mDescription, mValue, mCurrency, mNotes, mImageName, mIsExpense);

                getFragmentManager().beginTransaction()
                        .add(R.id.container, ExpenseDetailFragment.newInstance(isEdit, tempExpense))
                        .commit();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_expense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // The action
        int id = item.getItemId();

        /*
        if (id == R.id.action_settings)
        {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    /**
     * @param addDescription
     * @param addSum
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

    @Override
    public void cancel()
    {
        Intent data = getIntent();
        setResult(ExpensesActivity.EXPENSE_RESULT_CANCELED, null);

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
