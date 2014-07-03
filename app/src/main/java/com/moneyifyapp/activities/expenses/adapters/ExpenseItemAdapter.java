package com.moneyifyapp.activities.expenses.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.moneyifyapp.R;
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


    private int mLayoutResourceId;
    private MonthTransactions mTransactions;
    private View mMyView;


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

            // Update ht look of the the view accordingly
            if (mTransactions.getItems().get(position).mIsExpense == false)
            {
                updateViewToIncome(view);
            }

        }

        Transaction p = mTransactions.getItems().get(position);

        // Populate the current view according to collection item.
        if (p != null)
        {

            TextView expenseDescription = (TextView) view.findViewById(R.id.expenseDesc);
            TextView expenseValue = (TextView) view.findViewById(R.id.expenseValue);
            TextView expenseCurrency = (TextView) view.findViewById(R.id.currency);
            TextView expenseNote = (TextView) view.findViewById(R.id.expenseItemNote);
            // TODO this will be the image
            //Drawable img = getContext().getResources().getDrawable( R.drawable.smiley );
            //img.setBounds( 0, 0, 60, 60 );
            //txtVw.setCompoundDrawables( img, null, null, null );

            if (expenseDescription != null)
            {
                expenseDescription.setText(p.mDescription);
            }
            if (expenseValue != null)
            {

                expenseValue.setText(p.mValue);
            }
            if (expenseCurrency != null)
            {
                expenseCurrency.setText(p.mCurrency);
            }
            if (expenseNote != null)
            {
                if (!p.mNotes.isEmpty())
                {
                    expenseNote.setText(p.mNotes);
                }
            }
        }
        return view;
    }

    /**
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
        Transaction updatedExpense = mTransactions.getItems().get(position);
        boolean changeTextColor = false;

        if (expense != null)
        {
            updatedExpense.mDescription = expense.mDescription;
            updatedExpense.mValue = expense.mValue;
            updatedExpense.mCurrency = expense.mCurrency;
            updatedExpense.mNotes = expense.mNotes;
            updatedExpense.mImageName = expense.mImageName;

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

        notifyDataSetChanged();
    }
}
