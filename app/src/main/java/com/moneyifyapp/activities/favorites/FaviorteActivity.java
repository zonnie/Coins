package com.moneyifyapp.activities.favorites;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.expenses.adapters.ExpenseItemAdapter;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.utils.Utils;
import com.moneyifyapp.views.PrettyToast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 *
 */
public class FaviorteActivity extends ListActivity implements ExpenseItemAdapter.ListItemHandler
{
    private Animation mItemsLoadAnimation;
    private ExpenseItemAdapter mItemAdapter;
    private EditText mFilterField;
    private MonthTransactions mTransactions;
    private Animation mRemoveAnimation;

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faviorte);

        if (savedInstanceState == null)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_faviorte);
            Utils.initializeActionBar(this);
            Utils.removeActionBar(this);
            Utils.animateForward(this);

            mTransactions = TransactionHandler.getInstance(this).getAllSavedTransactions();

            if (mRemoveAnimation == null)
                mRemoveAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);

            mItemAdapter = new ExpenseItemAdapter(this, R.layout.adapter_expense_item ,mTransactions, this);
            getListView().setAdapter(mItemAdapter);
            getListView().setTextFilterEnabled(true);
            initFilterField();

            // Load animation lazy
            if (mItemsLoadAnimation == null)
                mItemsLoadAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            getListView().startAnimation(mItemsLoadAnimation);

        }
    }

    /**
     */
    private void initFilterField()
    {
        mFilterField = (EditText) findViewById(R.id.favorites_filter);
        mFilterField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
                mItemAdapter.getFilter().filter(arg0);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3)
            {
            }

            @Override
            public void afterTextChanged(Editable arg0)
            {
            }
        });
    }

    /**
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent mIntent = getIntent();
        setResult(ExpensesActivity.IMAGE_PICK_CANCEL, mIntent);
        Utils.animateBack(this);
    }

    @Override
    public void removeFromFragmentList(View view)
    {
        final int position = getListView().getPositionForView((LinearLayout) view.getParent().getParent());
        if (position >= 0)
        {
            // Put the next ID so that async functions could use it
            if (getListView().getChildAt(position) != null)
            {
                View removedItem = getListView().getChildAt(position);
                removedItem.startAnimation(mRemoveAnimation);
                removeFavorite(mItemAdapter.getItems().get(position));
                mItemAdapter.remove(position);
            }
        }
    }

    /**
     */
    private void removeFavorite(final Transaction favoriteToUpdate)
    {
        favoriteToUpdate.mSaved = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Transaction.CLASS_NAME);
        query.whereEqualTo(Transaction.KEY_ID, favoriteToUpdate.mId);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> list, ParseException e)
            {
                if (list.size() != 0)
                {
                    ParseObject expenseObjectInDb = list.get(0);
                    TransactionHandler.getInstance(FaviorteActivity.this).saveDataInBackground(favoriteToUpdate, expenseObjectInDb);
                    Utils.showPrettyToast(FaviorteActivity.this, "\"" + favoriteToUpdate.mDescription +"\" is not a favorite anymore", PrettyToast.VERY_LONG);
                }
            }
        });
    }

    @Override
    public void showItem(View view)
    {

    }
}
