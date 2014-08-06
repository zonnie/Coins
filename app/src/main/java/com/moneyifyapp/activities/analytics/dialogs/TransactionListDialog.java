package com.moneyifyapp.activities.analytics.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.adapters.ExpenseItemAdapterRead;
import com.moneyifyapp.model.MonthTransactions;

/**
 */
public class TransactionListDialog extends Dialog
{
    private Dialog mDialog;
    private String mTitle;
    private Context mContext;
    private WindowManager.LayoutParams mLayoutParams;
    private MonthTransactions mTransactions;
    private ListView mListView;

    /**
     */
    public TransactionListDialog(Context context, MonthTransactions transactions,String title)
    {
        super(context);
        mContext = context;
        mTransactions = transactions;
        mTitle = title;

        buildDialog();
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

        ExpenseItemAdapterRead incomeAdapter = new ExpenseItemAdapterRead(mContext, R.layout.adapter_expense_item_read, mTransactions);
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
}
