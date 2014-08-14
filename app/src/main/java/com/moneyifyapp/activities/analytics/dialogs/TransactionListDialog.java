package com.moneyifyapp.activities.analytics.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.adapters.ExpenseItemAdapterRead;
import com.moneyifyapp.model.MonthTransactions;

/**
 */
public class TransactionListDialog extends Dialog implements ExpenseItemAdapterRead.OnExpenseItemClicked
{
    protected Dialog mDialog;
    protected String mTitle;
    protected Context mContext;
    protected WindowManager.LayoutParams mLayoutParams;
    protected MonthTransactions mTransactions;
    protected ListView mListView;

    /**
     */
    public TransactionListDialog(Context context, MonthTransactions transactions,String title)
    {
        super(context);
        mContext = context;
        mTransactions = transactions;
        mTitle = title;

        buildDialog();
        if(transactions.getItems().size() > 0)
            mDialog.findViewById(R.id.templates_empty_textivew).setVisibility(View.GONE);
    }

    /**
     *
     */
    private void buildDialog()
    {
        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_expense_list);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.copyFrom(mDialog.getWindow().getAttributes());
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ExpenseItemAdapterRead incomeAdapter = new ExpenseItemAdapterRead(mContext, R.layout.adapter_expense_item_read, mTransactions, this);
        mListView = (ListView) mDialog.findViewById(R.id.dialog_transaction_listview);
        TextView textView = (TextView)mDialog.findViewById(R.id.dialog_transaction_title_label);
        textView.setText(mTitle);

        mListView.setAdapter(incomeAdapter);
    }

    /**
     */
    @Override
    public void show()
    {
        mDialog.show();
        mDialog.getWindow().setAttributes(mLayoutParams);
    }


    @Override
    public void expenseItemClicked(View view)
    {
    }
}
