package com.moneyifyapp.activities.analytics.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Spinner;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.adapters.DropdownAdapter;
import com.moneyifyapp.model.enums.Months;

/**
 */
public class PickDateDialog extends Dialog
{
    private Dialog mDialog;
    private Context mContext;
    private Spinner mMonthSelect;
    private WindowManager.LayoutParams mLayoutParams;

    /**
     */
    public PickDateDialog(Context context)
    {
        super(context);
        mContext = context;

        buildDialog();

        mMonthSelect = (Spinner) mDialog.findViewById(R.id.select_month_spinner);
        DropdownAdapter adapter = new DropdownAdapter(mContext, Months.getMonthList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMonthSelect.setAdapter(adapter);

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
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    /**
     */
    public void clickedPick()
    {
        String month = mMonthSelect.getSelectedItem().toString();
        ((DialogClicked)mContext).onDialogClick(month);
        mDialog.dismiss();
    }

    /**
     *
     */
    private void bindCallbacksToButtons()
    {
        Button changePassButton = (Button) mDialog.findViewById(R.id.dialog_pick_date_button);
        changePassButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mMonthSelect.getWindowToken(), 0);
                clickedPick();
            }
        });

        Button cancelButton = (Button) mDialog.findViewById(R.id.change_user_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mMonthSelect.getWindowToken(), 0);
                clickedCancel();
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
