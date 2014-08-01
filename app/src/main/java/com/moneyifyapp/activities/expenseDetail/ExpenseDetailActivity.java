package com.moneyifyapp.activities.expenseDetail;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenseDetail.fragments.ExpenseDetailFragment;
import com.moneyifyapp.activities.expenseDetail.fragments.ExpenseOptionsFragment;
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
        implements ExpenseDetailFragment.OnDetailFragmentSubmit,
        ExpenseOptionsFragment.OnOptionsFragmentSubmit
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
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private boolean mIsEdit;
    private Transaction mTransaction;
    private ExpenseDetailFragment mDetailFragment;
    private ExpenseOptionsFragment mOptionsFragment;

    /**
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

        mIsEdit = false;

        if (mRequestCode == ExpensesActivity.REQ_EDIT_ITEM)
            mIsEdit = true;

        if (savedInstanceState == null)
        {
            // If this is not an edit, clone the transcation for edit
            if (!mIsEdit)
                mTransaction = new Transaction(Transaction.DEFUALT_TRANSCATION_ID);
            else
                mTransaction = createTransactionFromIntent();
        }

        mDetailFragment = ExpenseDetailFragment.newInstance(mIsEdit, mTransaction, mMonth);
        mOptionsFragment = ExpenseOptionsFragment.newInstance(mIsEdit, mTransaction);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    /**
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
     */
    @Override
    public void onTransactionSubmit(Transaction transaction)
    {
        Intent data = getIntent();

        data.putExtra(Transaction.KEY_DESCRIPTION, transaction.mDescription);
        data.putExtra(Transaction.KEY_VALUE, transaction.mValue);
        data.putExtra(Transaction.KEY_IMAGE_NAME, transaction.mImageResourceIndex);
        data.putExtra(Transaction.KEY_CURRENCY, transaction.mCurrency);
        data.putExtra(Transaction.KEY_NOTES, transaction.mNotes);
        data.putExtra(ExpenseListFragment.ITEM_POS_KEY, mItemPosition);
        data.putExtra(ExpenseListFragment.MONTH_KEY, mMonth);
        data.putExtra(Transaction.KEY_TYPE, transaction.mIsExpense);

    }

    @Override
    public void OnOptionsSubmit(Transaction transaction)
    {
        Intent data = getIntent();
        data.putExtra(ExpenseListFragment.TEMPLATE_KEY, transaction.mSaved);

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
            case R.id.submit_item:
            {
                mDetailFragment.onSumbitPressed();
                mOptionsFragment.onSumbitPressed();
                setResult(ExpensesActivity.EXPENSE_RESULT_OK, getIntent());
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.expense_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }



    /**
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    /**
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        /**
         */
        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        /**
         */
        @Override
        public Fragment getItem(int position)
        {
            if (position == 0)
                return mDetailFragment;
            else if (position == 1)
                return mOptionsFragment;
            else
                return mDetailFragment;
        }

        /**
         */
        @Override
        public int getCount()
        {
            return 2;
        }

        /**
         */
        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return getString(R.string.title_expense_detail_page);
                case 1:
                    return getString(R.string.title_expense_options_page);
                default:
                    return getString(R.string.title_expense_detail_page);
            }
        }
    }
}
