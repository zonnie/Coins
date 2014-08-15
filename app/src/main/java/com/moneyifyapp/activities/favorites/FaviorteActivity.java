package com.moneyifyapp.activities.favorites;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.moneyifyapp.activities.login.dialogs.DeleteDialog;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.utils.Utils;
import com.moneyifyapp.views.PrettyToast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 */
public class FaviorteActivity extends ListActivity
        implements ExpenseItemAdapter.ListItemHandler,
        DeleteDialog.OnDeleteClicked
{
    private Animation mItemsLoadAnimation;
    private ExpenseItemAdapter mItemAdapter;
    private EditText mFilterField;
    private MonthTransactions mTransactions;
    private Animation mRemoveAnimation;
    private Queue<Integer> mRemoveQueue;
    private int mDeletePosition;

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
            mRemoveQueue = new LinkedList<Integer>();

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

    /**
     */
    @Override
    public void removeFromFragmentList(View view)
    {
        final int position = getListView().getPositionForView((LinearLayout) view.getParent().getParent());
        if (position >= 0)
        {
            mDeletePosition = position;
            DeleteDialog dialog = new DeleteDialog(this, this,
                    "The following will be removed from the favorite list",
                    "Are you sure you want to remove \"" + mItemAdapter.getItem(position).mDescription + "\" ?");
            dialog.setDialogImage(R.drawable.fave);
            dialog.show();
        }

    }

    /**
     */
    @Override
    public void deleteClicked()
    {
        removeFavoriteFromList(mDeletePosition);
    }

    /**
     */
    private void removeFavoriteFromList(int position)
    {
        if (position >= 0)
        {
            // Put the next ID so that async functions could use it
            if (getListView().getChildAt(position) != null)
            {
                mRemoveQueue.add(position);
                View removedItem = getListView().getChildAt(position);
                removedItem.startAnimation(mRemoveAnimation);
                Transaction transaction = mItemAdapter.getItems().get(position);
                transaction.mSaved = false;
                // After animation is done, remove item from list
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        int itemId = mRemoveQueue.remove();
                        mItemAdapter.remove(itemId);
                    }

                }, mRemoveAnimation.getDuration());
                removeFavorite(transaction);
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
