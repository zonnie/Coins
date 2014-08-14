package com.moneyifyapp.activities.expenseDetail.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.moneyifyapp.activities.analytics.dialogs.TransactionListDialog;
import com.moneyifyapp.activities.expenses.adapters.ExpenseItemAdapterRead;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.Transaction;

/**
 */
public class SavedTransactionsDialog extends TransactionListDialog implements ExpenseItemAdapterRead.OnExpenseItemClicked
{
    OnTransactionItemClicked mListener;

    /**
     */
    public SavedTransactionsDialog(Context context, MonthTransactions transactions, String title, OnTransactionItemClicked listener)
    {
        super(context, transactions, title);
        mListener = listener;
    }

    /**
     */
    public interface OnTransactionItemClicked
    {
        public void transactionItemClicked(Transaction selected);
    }

    /**
     */
    @Override
    public void expenseItemClicked(View view)
    {
        final int position = mListView.getPositionForView((LinearLayout) view.getParent());
        if (position >= 0)
        {
            Transaction selected = mTransactions.getItems().get(position);
            mListener.transactionItemClicked(selected);
            mDialog.dismiss();
        }
    }

}
