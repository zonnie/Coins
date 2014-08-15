package com.moneyifyapp.activities.login.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.moneyifyapp.R;

/**
 */
public class DeleteDialog extends Dialog
{
    private Dialog mDialog;
    private Context mContext;
    private WindowManager.LayoutParams mLayoutParams;
    private OnDeleteClicked mListener;
    private ImageView mDialogImage;

    /**
     */
    public DeleteDialog(Context context, OnDeleteClicked listener, String firstLine, String secondLine)
    {
        super(context);
        mContext = context;
        buildDialog();

        ((TextView) mDialog.findViewById(R.id.delete_account_first_line)).setText(firstLine);
        ((TextView) mDialog.findViewById(R.id.delete_account_second_line)).setText(secondLine);
        mDialogImage = (ImageView)mDialog.findViewById(R.id.delete_dialog_imageview);
        mListener = listener;

        bindCallbacksToButtons();
    }

    /**
     *
     */
    private void buildDialog()
    {
        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_delete);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.copyFrom(mDialog.getWindow().getAttributes());
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    /**
     */
    public void clickedDelete()
    {
        mListener.deleteClicked();
        mDialog.dismiss();
    }

    /**
     */
    public void setDialogImage(int resource)
    {
        mDialogImage.setImageResource(resource);
    }

    /**
     *
     */
    private void bindCallbacksToButtons()
    {
        mDialog.findViewById(R.id.cancel_delete_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickedCancel();
            }
        });

        mDialog.findViewById(R.id.delete_ok_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickedDelete();
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

    public interface OnDeleteClicked
    {
        public void deleteClicked();
    }
}
