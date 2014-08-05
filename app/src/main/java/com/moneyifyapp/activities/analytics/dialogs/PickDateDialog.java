package com.moneyifyapp.activities.analytics.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.adapters.DropdownAdapter;
import com.moneyifyapp.model.enums.Months;

/**
 */
public class PickDateDialog extends Dialog
{
    private Dialog mDialog;
    private Context mContext;
    private WindowManager.LayoutParams mLayoutParams;
    private ListView mListView;

    /**
     */
    public PickDateDialog(Context context)
    {
        super(context);
        mContext = context;

        buildDialog();


        bindCallbacksToButtons();

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
//                R.array.month_list, R.layout.adapter_dropdown_item);
    }

    /**
     *
     */
    private void buildDialog()
    {
        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_pick_date);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.copyFrom(mDialog.getWindow().getAttributes());
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mListView = (ListView) mDialog.findViewById(R.id.dialog_listview);
        DropdownAdapter adapter = new DropdownAdapter(mContext, Months.getMonthList());
        mListView.setAdapter(adapter);
    }

    /**
     */
    public void clickedPick(View view)
    {
        TextView textView = (TextView)view.findViewById(R.id.dropdown_item_textview);
        if(textView != null)
            ((DialogClicked)mContext).onDialogClick(textView.getText().toString());
        mDialog.dismiss();
    }

    /**
     *
     */
    private void bindCallbacksToButtons()
    {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mListView.getWindowToken(), 0);
                clickedPick(view);
            }
        });
    }

    /**
     */
    public void clickedCancel()
    {
        mDialog.dismiss();
    }

    /**
     */
    @Override
    public void show()
    {
        mDialog.show();
        mDialog.getWindow().setAttributes(mLayoutParams);
    }

    public interface DialogClicked
    {
        public void onDialogClick(String selected);
    }
}
