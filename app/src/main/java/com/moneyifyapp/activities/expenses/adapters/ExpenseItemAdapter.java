package com.moneyifyapp.activities.expenses.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.model.MonthExpenses;
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
    private MonthExpenses mExpenses;


    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/


    /**
     * @param context
     * @param resource
     */
    public ExpenseItemAdapter(Context context, int resource, MonthExpenses expenses)
    {
        super(context, resource, expenses.getItems());
        mExpenses = expenses;
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

        if (view == null)
        {

            LayoutInflater viewInflator;
            viewInflator = LayoutInflater.from(getContext());

            view = viewInflator.inflate(mLayoutResourceId, null);

            // Update ht look of the the view accordingly
            if(mExpenses.getItems().get(position).mIsExpense == false)
            {
                updateViewToIncome(view);
            }

        }

        Transaction p = mExpenses.getItems().get(position);

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
     *
     * @param view
     */
    private void updateViewToIncome(View view)
    {
        /*
        LinearLayout cardLayout = (LinearLayout)view.findViewById(R.id.transaction_card_layout);
        cardLayout.setBackgroundResource(R.drawable.income_item_card);

        View divider = view.findViewById(R.id.expenseItemSeperator);
        divider.setBackgroundColor(view.getResources().getColor(android.R.color.darker_gray));

        TextView noteText = (TextView)view.findViewById(R.id.expenseItemNote);
        noteText.setTextColor(view.getResources().getColor(android.R.color.darker_gray));
        */
    }

    /**
     * Adds a new item to the data set and notifies the data set observer on change.
     *
     * @param transaction
     */
    /*
    public void add(Transaction transaction)
    {
        if (transaction != null)
        {
            mExpenses.getItems().add(0, transaction);
        }

        notifyDataSetChanged();
    }*/

    /**
     * @param position
     */
    public void remove(int position)
    {
        Transaction transaction = mExpenses.getItems().get(position);

        if (transaction != null)
        {
            mExpenses.getItems().remove(transaction);
        }

        notifyDataSetChanged();
    }

    /**
     * Get the expense items.
     */
    public List<Transaction> getItems()
    {
        return mExpenses.getItems();
    }

    /**
     * Removes all items from list and updates the observer
     * that the list has changed.
     */
    public void removeAll()
    {
        for (Transaction expense : mExpenses.getItems())
        {
            remove(expense);
        }

        notifyDataSetChanged();
    }

    /**
     *
     * @param position
     * @param expense
     */
    public void update(int position, Transaction expense)
    {
        Transaction updatedExpense = mExpenses.getItems().get(position);

        if(expense != null)
        {
            updatedExpense.mDescription = expense.mDescription;
            updatedExpense.mValue = expense.mValue;
            updatedExpense.mCurrency = expense.mCurrency;
            updatedExpense.mNotes = expense.mNotes;
            updatedExpense.mImageName = expense.mImageName;
            updatedExpense.mIsExpense = expense.mIsExpense;
        }

        notifyDataSetChanged();
    }
}
