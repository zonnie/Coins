package com.moneyifyapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.model.MonthExpenses;
import com.moneyifyapp.model.SingleExpense;

import java.util.List;

/**
 * An expense item adapter.
 */
public class ExpenseItemAdapter extends ArrayAdapter<SingleExpense>
{

    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/


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

        }

        SingleExpense p = mExpenses.getItems().get(position);

        // Populate the current view according to collection item.
        if (p != null)
        {

            TextView expenseDescription = (TextView) view.findViewById(R.id.expenseDesc);
            TextView expenseValue = (TextView) view.findViewById(R.id.expenseValue);
            TextView expenseCurrency = (TextView) view.findViewById(R.id.currency);

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
        }
        return view;
    }

    /**
     * Adds a new item to the data set and notifies the data set observer on change.
     *
     * @param singleExpense
     */
    public void add(SingleExpense singleExpense)
    {
        if (singleExpense != null)
        {
            mExpenses.getItems().add(0, singleExpense);
        }

        notifyDataSetChanged();
    }

    /**
     *
     * @param position
     */
    public void remove(int position)
    {
        SingleExpense singleExpense = mExpenses.getItems().get(position);

        if(singleExpense != null)
        {
            mExpenses.getItems().remove(singleExpense);
        }

        notifyDataSetChanged();
    }

    /**
     *
     * Get the expense items.
     *
     */
    public List<SingleExpense> getItems()
    {
        return mExpenses.getItems();
    }

    /**
     *
     * Removes all items from list and updates the observer
     * that the list has changed.
     *
     */
    public void removeAll()
    {
        for(SingleExpense expense : mExpenses.getItems())
        {
            remove(expense);
        }

        notifyDataSetChanged();
    }
}
