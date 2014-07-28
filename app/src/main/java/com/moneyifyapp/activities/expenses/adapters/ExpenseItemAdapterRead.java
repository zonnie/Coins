package com.moneyifyapp.activities.expenses.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.utils.Utils;

/**
 * An expense item adapter.
 * This adapter should be used to display lists of transactions.
 *
 */
public class ExpenseItemAdapterRead extends ArrayAdapter<Transaction>
{
    private TextView mExpenseDescription;
    private TextView mExpenseValue;
    private TextView mExpenseCurrency;
    private TextView mExpenseDayOfMonth;
    private TextView mExpenseMonth;
    private ImageView mExpenseImage;
    private int mLayoutResourceId;
    private MonthTransactions mTransactions;
    private View mMyView;
    public static int PICK_IMAGE_DIMENSIONS = 150;

    /**
     *
     */
    public ExpenseItemAdapterRead(Context context, int resource, MonthTransactions expenses)
    {
        super(context, resource, expenses.getItems());
        mTransactions = expenses;
        mLayoutResourceId = resource;

        // Load animation lazy
        //if (mItemsLoadAnimation == null)
        //    mItemsLoadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
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
            inflateGivenLayoutAndAnimate();

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
        //mMyView.startAnimation(mItemsLoadAnimation);
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
            handleViewDate(currentTransactionView);
            handleViewDescription(currentTransactionView);
            handleViewValue(currentTransactionView);
            handleViewCurrency(currentTransactionView);
        }
        return mMyView;
    }

    /**
     */
    private void storeViews()
    {
        // Take care of click to layout to be able to edit item in view
        mExpenseDescription = (TextView) mMyView.findViewById(R.id.expenseDesc);
        mExpenseValue = (TextView) mMyView.findViewById(R.id.expenseValue);
        mExpenseCurrency = (TextView) mMyView.findViewById(R.id.currency);
        mExpenseDayOfMonth = (TextView) mMyView.findViewById(R.id.expense_item_date_text);
        mExpenseMonth = (TextView) mMyView.findViewById(R.id.expense_item_date_month);
        mExpenseImage = (ImageView) mMyView.findViewById(R.id.expense_item_image);
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
        img.setBounds( 0, 0, PICK_IMAGE_DIMENSIONS, PICK_IMAGE_DIMENSIONS);
        mExpenseImage.setImageDrawable(img);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(PICK_IMAGE_DIMENSIONS, PICK_IMAGE_DIMENSIONS);
        mExpenseImage.setLayoutParams(layoutParams);    }

    /**
     */
    private void handleViewDate(Transaction currentTransactionView)
    {
        if (mExpenseDescription != null)
        {
            String date = currentTransactionView.mTransactionDay;
            String dateSuffix = Utils.getMonthPrefixByIndex(mTransactions.mMonthNumber).toUpperCase();

            mExpenseDayOfMonth.setText(date);
            mExpenseMonth.setText(dateSuffix);
        }

    }
}