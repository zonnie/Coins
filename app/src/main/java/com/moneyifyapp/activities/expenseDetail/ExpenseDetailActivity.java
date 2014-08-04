package com.moneyifyapp.activities.expenseDetail;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

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
    private Transaction.REPEAT_TYPE mRepeatType;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private boolean mIsEdit;
    private Transaction mTransaction;
    private ExpenseDetailFragment mDetailFragment;
    private ExpenseOptionsFragment mOptionsFragment;
    private int mMonth;
    private TextView mDetailDateDay;
    private TextView mDetailDateMonth;

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Utils.animateForward(this);

        setContentView(R.layout.activity_create_expense_layout);
        Utils.initializeActionBar(this);
        Utils.setupBackButton(this);
        Utils.setLogo(this, R.drawable.transaction);
        Utils.removeActionBar(this);

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

        mDetailDateDay = (TextView) findViewById(R.id.detail_date_day);
        mDetailDateMonth = (TextView) findViewById(R.id.detail_date_month);
        mDetailFragment = ExpenseDetailFragment.newInstance(mIsEdit, mTransaction);
        mOptionsFragment = ExpenseOptionsFragment.newInstance(mIsEdit, mTransaction);
        initDetailDateLabels();

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
        mRepeatType = (Transaction.REPEAT_TYPE)getIntent().getExtras().get(ExpenseListFragment.REPEAT_KEY);

        // Get the default currency
        mCurrency = PreferenceManager.getDefaultSharedPreferences(this).getString(PrefActivity.PREF_LIST_NAME, "$");

        Transaction transaction = new Transaction(Transaction.DEFUALT_TRANSCATION_ID, mDescription, mValue,
                mCurrency, mNotes, mImageName, mIsExpense, mTransactionDay);
        transaction.mSaved = mSaved;
        transaction.mRepeatType = mRepeatType;

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
        data.putExtra(ExpenseListFragment.REPEAT_KEY, transaction.mRepeatType.toString());
    }

    /**
     */
    @Override
    public void cancel()
    {
        Intent data = getIntent();
        setResult(ExpensesActivity.EXPENSE_RESULT_CANCELED, data);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     */
    public void onAcceptClicked(View view)
    {
        if(mDetailFragment.onSumbitPressed())
        {
            mOptionsFragment.onSumbitPressed();
            setResult(ExpensesActivity.EXPENSE_RESULT_OK, getIntent());
            finish();
            Utils.animateBack(this);
        }
    }

    public void OnBackClicked(View view)
    {
        onBackPressed();
    }

    /**
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Utils.animateBack(this);
    }

    /**
     */
    private void initDetailDateLabels()
    {
        String date = mTransaction.mTransactionDay;
        date += Utils.generateDayInMonthSuffix(date);
        mDetailDateDay.setText(date);
        if(mMonth >= 0)
            mDetailDateMonth.setText(Utils.getMonthPrefixByIndex(mMonth).toUpperCase());
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
