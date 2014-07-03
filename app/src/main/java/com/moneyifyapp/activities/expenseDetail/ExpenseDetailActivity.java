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

public class ExpenseDetailActivity extends Activity
        implements ExpenseDetailFragment.OnFragmentInteractionListener
{
    /********************************************************************/
    /**                          Section                               **/
    /********************************************************************/

    private int mRequestCode;
    private int mItemPosition;
    private String mDescription;
    private String mValue;
    private String mCurrency;
    private String mNotes;
    private String mImageName;
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
        setContentView(R.layout.activity_create_expense_layout);

        mRequestCode = getIntent().getExtras().getInt(ExpenseListFragment.REQ_CODE_KEY);
        boolean isEdit = false;

        if(mRequestCode == ExpensesActivity.REQ_EDIT_ITEM)
        {
            isEdit = true;
        }

        if (savedInstanceState == null)
        {
            if(!isEdit)
            {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, ExpenseDetailFragment.newInstance(isEdit, null))
                        .commit();
            }
            else
            {
                mItemPosition = getIntent().getExtras().getInt(ExpenseListFragment.ITEM_CLICKED_POS);
                mDescription = getIntent().getExtras().getString(Transaction.KEY_DESCRIPTION);
                mValue = getIntent().getExtras().getString(Transaction.KEY_VALUE);
                mCurrency = getIntent().getExtras().getString(Transaction.KEY_CURRENCY);
                mNotes = getIntent().getExtras().getString(Transaction.KEY_NOTES);
                //TODO this is for the image
                //mImageName = getIntent().getExtras().getString(SingleExpense.KEY_NOTES);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
     *
     * @param addDescription
     * @param addSum
     */
    @Override
    public void onAddExpense(String addDescription, String addSum, String addCurrency, String addNote, String addImage, boolean isExpense)
    {
        Intent data = getIntent();
        data.putExtra(Transaction.KEY_DESCRIPTION, addDescription);
        data.putExtra(Transaction.KEY_VALUE, addSum);
        data.putExtra(Transaction.KEY_IMAGE_NAME, addImage);
        data.putExtra(Transaction.KEY_CURRENCY, addCurrency);
        data.putExtra(Transaction.KEY_NOTES, addNote);
        data.putExtra(ExpenseListFragment.ITEM_CLICKED_POS, mItemPosition);
        data.putExtra(Transaction.KEY_TYPE, isExpense);
        setResult(ExpensesActivity.EXPENSE_RESULT_OK, data);

        finish();
    }

    @Override
    public void cancel()
    {
        Intent data = getIntent();
        setResult(ExpensesActivity.EXPENSE_RESULT_CANCELED, null);

        finish();
    }
}
