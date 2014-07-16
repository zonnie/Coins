package com.moneyifyapp.activities.expenses.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.Transaction;

/**
 * An expense item adapter.
 * This adapter should be used to display lists of transactions.
 *
 */
public class ExpenseItemAdapterRead extends ArrayAdapter<Transaction>
{
    private TextView mExpenseDescription;
    private LinearLayout mItemLayout;
    private TextView mExpenseValue;
    private TextView mExpenseCurrency;
    private TextView mExpenseNote;
    private int mLayoutResourceId;
    private MonthTransactions mTransactions;
    private Button mRemoveItemButton;
    private int mItemsLoaded;
    private View mMyView;
    public static int PICK_IMAGE_DIMENSIONS = 80;
    private final String EMPTY_NOTE_HINT = "Please enter a note...";
    private Animation mItemsLoadAnimation;

    /**
     *
     */
    public ExpenseItemAdapterRead(Context context, int resource, MonthTransactions expenses)
    {
        super(context, resource, expenses.getItems());
        mTransactions = expenses;
        mLayoutResourceId = resource;
        mItemsLoaded = 0;

        // Load animation lazy
        if (mItemsLoadAnimation == null)
            mItemsLoadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
    }

    /**
     * Generates the fragments view for display for each list view item.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View view = convertView;
        mMyView = view;

        if (mMyView == null)
        {
            inflateGivenLayoutAndAnimate();
        }

        getRegularView(position);

        return mMyView;
    }

    /**
     */
    private void inflateGivenLayoutAndAnimate()
    {
        LayoutInflater viewInflator;
        viewInflator = LayoutInflater.from(getContext());
        mMyView = viewInflator.inflate(mLayoutResourceId, null);
        mMyView.startAnimation(mItemsLoadAnimation);
    }

    /**
     */
    private View getRegularView(int position)
    {
        Transaction currentTransactionView = mTransactions.getItems().get(position);

        // Populate the current view according to collection item.
        if (currentTransactionView != null)
        {
            storeViews();

            // Update ht look of the the view accordingly
            if(mTransactions.getItems().get(position).mIsExpense == true)
                updateTransactionSumColor(mMyView, R.color.expense_color);
            else
                updateTransactionSumColor(mMyView, R.color.income_color);

            updateDescriptionLeftDrawable(Images.getImageByPosition(currentTransactionView.mImageResourceIndex));
            handleViewDescription(currentTransactionView);
            handleViewValue(currentTransactionView);
            handleViewCurrency(currentTransactionView);
            handleViewNote(currentTransactionView);
        }
        return mMyView;
    }

    /**
     */
    private void storeViews()
    {
        // Take care of click to layout to be able to edit item in view
        mItemLayout = (LinearLayout) mMyView.findViewById(R.id.transaction_card_layout);
        mExpenseDescription = (TextView) mMyView.findViewById(R.id.expenseDesc);
        mExpenseValue = (TextView) mMyView.findViewById(R.id.expenseValue);
        mExpenseCurrency = (TextView) mMyView.findViewById(R.id.currency);
        mExpenseNote = (TextView) mMyView.findViewById(R.id.expenseItemNote);

    }

    /**
     */
    private void handleViewDescription(Transaction currentTransactionView)
    {
        if (mExpenseDescription != null)
            mExpenseDescription.setText(currentTransactionView.mDescription);
    }

    /**
     */
    private void handleViewValue(Transaction currentTransactionView)
    {
        if (mExpenseValue != null)
            mExpenseValue.setText(currentTransactionView.mValue);
    }

    /**
     */
    private void handleViewCurrency(Transaction currentTransactionView)
    {
        if (mExpenseCurrency != null)
            mExpenseCurrency.setText(currentTransactionView.mCurrency);
    }

    /**
     */
    private void handleViewNote(Transaction currentTransactionView)
    {
        if (mExpenseNote != null)
        {
            if (!currentTransactionView.mNotes.isEmpty())
                mExpenseNote.setText(currentTransactionView.mNotes);
            else
                mExpenseNote.setText(EMPTY_NOTE_HINT);
        }
    }

    /**
     */
    private void updateTransactionSumColor(View view, int colorId)
    {
        TextView amount = (TextView) view.findViewById(R.id.expenseValue);
        TextView currency = (TextView) view.findViewById(R.id.currency);

        amount.setTextColor(view.getResources().getColor(colorId));
        currency.setTextColor(view.getResources().getColor(colorId));

    }

    /**
     */
    private void updateDescriptionLeftDrawable(int resourceIndex)
    {
        Drawable img = getContext().getResources().getDrawable(resourceIndex);
        img.setBounds(0, 0, PICK_IMAGE_DIMENSIONS, PICK_IMAGE_DIMENSIONS);
        mExpenseDescription.setCompoundDrawables(img, null, null, null);
    }
}