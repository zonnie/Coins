package com.moneyifyapp.activities.expenses.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.Transaction;

import java.util.List;

/**
 * An expense item adapter.
 */
public class ExpenseItemAdapter extends ArrayAdapter<Transaction>
{

    /********************************************************************/
    /**                          Members                               **/
    /**
     * ****************************************************************
     */

    private TextView mExpenseDescription;
    private int mLayoutResourceId;
    private MonthTransactions mTransactions;
    private ImageView mImage;
    private View mMyView;
    public static int PICK_IMAGE_DIMENSIONS = 100;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/


    /**
     * @param context
     * @param resource
     */
    public ExpenseItemAdapter(Context context, int resource, MonthTransactions expenses)
    {
        super(context, resource, expenses.getItems());
        mTransactions = expenses;
        mLayoutResourceId = resource;
    }

    /**
     * Generates the fragments view for display for each list view item.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View view = convertView;
        mMyView = view;

        if (view == null)
        {

            LayoutInflater viewInflator;
            viewInflator = LayoutInflater.from(getContext());

            view = viewInflator.inflate(mLayoutResourceId, null);
        }

        return getRegularView(position, view);
    }

    /**
     *
     * @param position
     * @param view
     * @return
     */
    private View getRegularView(int position, View view)
    {
        Transaction currentTransactionView = mTransactions.getItems().get(position);

        // Populate the current view according to collection item.
        if (currentTransactionView != null)
        {
            // Update ht look of the the view accordingly
            if (mTransactions.getItems().get(position).mIsExpense == false)
            {
                updateViewToIncome(view);
            }
            else
            {
                updateViewToExpense(view);
            }

            mExpenseDescription = (TextView) view.findViewById(R.id.expenseDesc);
            mImage = (ImageView)view.findViewById(R.id.image_container);
            TextView expenseValue = (TextView) view.findViewById(R.id.expenseValue);
            TextView expenseCurrency = (TextView) view.findViewById(R.id.currency);
            TextView expenseNote = (TextView) view.findViewById(R.id.expenseItemNote);

            //Update image from object
            updateImage(Images.getImageByPosition(currentTransactionView.mImageResourceIndex));

            if (mExpenseDescription != null)
            {
                mExpenseDescription.setText(currentTransactionView.mDescription);
            }
            if (expenseValue != null)
            {

                expenseValue.setText(currentTransactionView.mValue);
            }
            if (expenseCurrency != null)
            {
                expenseCurrency.setText(currentTransactionView.mCurrency);
            }
            if (expenseNote != null)
            {
                if (!currentTransactionView.mNotes.isEmpty())
                {
                    expenseNote.setText(currentTransactionView.mNotes);
                }
            }
        }
        return view;
    }

    /**
     * Updates the item to be income item.
     *
     * @param view
     */
    private void updateViewToIncome(View view)
    {
        TextView amount = (TextView)view.findViewById(R.id.expenseValue);
        amount.setTextColor(view.getResources().getColor(android.R.color.holo_green_dark));

        TextView currency = (TextView)view.findViewById(R.id.currency);
        currency.setTextColor(view.getResources().getColor(android.R.color.holo_green_dark));
    }

    /**
     *
     * Updates the item to be expense item.
     *
     * @param view
     */
    private void updateViewToExpense(View view)
    {
        TextView amount = (TextView)view.findViewById(R.id.expenseValue);
        amount.setTextColor(view.getResources().getColor(android.R.color.black));

        TextView currency = (TextView)view.findViewById(R.id.currency);
        currency.setTextColor(view.getResources().getColor(android.R.color.black));

    }

    /**
     * @param position
     */
    public void remove(int position)
    {
        Transaction transaction = mTransactions.getItems().get(position);

        if (transaction != null)
        {
            mTransactions.getItems().remove(transaction);
        }

        notifyDataSetChanged();
    }

    /**
     * Get the expense items.
     */
    public List<Transaction> getItems()
    {
        return mTransactions.getItems();
    }

    /**
     * Removes all items from list and updates the observer
     * that the list has changed.
     */
    public void removeAll()
    {
        for (Transaction expense : mTransactions.getItems())
        {
            remove(expense);
        }

        notifyDataSetChanged();
    }

    /**
     * @param position
     * @param expense
     */
    public void update(int position, Transaction expense)
    {
        // Update a single transaction
        updateTransaction(position, expense);

        // Reflect on display
        notifyDataSetChanged();
    }

    /**
     *
     * @param position
     * @param expense
     */
    private void updateTransaction(int position, Transaction expense)
    {
        Transaction updatedExpense = mTransactions.getItems().get(position);

        if (expense != null)
        {
            updatedExpense.mDescription = expense.mDescription;
            updatedExpense.mValue = expense.mValue;
            updatedExpense.mCurrency = expense.mCurrency;
            updatedExpense.mNotes = expense.mNotes;
            updatedExpense.mImageResourceIndex = expense.mImageResourceIndex;
            // Update image
            updateImage(Images.getImageByPosition(updatedExpense.mImageResourceIndex));

            // Update the transaction look according to type
            updateViewType(updatedExpense, expense);
        }
    }

    /**
     *
     * Update the view look according to it's type.
     *
     * @param updatedExpense
     * @param expense
     */
    private void updateViewType(Transaction updatedExpense, Transaction expense)
    {
        boolean changeTextColor = false;

        if(updatedExpense.mIsExpense != expense.mIsExpense)
        {
            changeTextColor = true;
        }

        updatedExpense.mIsExpense = expense.mIsExpense;

        // Update the amount color according to transaction type
        if(expense.mIsExpense && changeTextColor)
        {
            if(mMyView != null)
            {
                updateViewToIncome(mMyView);
            }
        }
        else if(!expense.mIsExpense && changeTextColor)
        {
            if(mMyView != null)
            {
                updateViewToExpense(mMyView);
            }
        }
    }

    /**
     * Update the description's left drawable
     *
     * @param resourceIndex
     */
    private void updateImage(int resourceIndex)
    {
        Drawable img = getContext().getResources().getDrawable(resourceIndex);
        img.setBounds( 0, 0, PICK_IMAGE_DIMENSIONS, PICK_IMAGE_DIMENSIONS);
        mExpenseDescription.setCompoundDrawables(img, null, null, null);

        //Image.setImageResource(resourceIndex);
    }
}