package com.moneyifyapp.activities.login.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moneyifyapp.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 */
public class ResetPasswordDialog extends Dialog
{
    private Dialog mDialog;
    private Context mContext;
    private EditText mUsername;
    private WindowManager.LayoutParams mLayoutParams;

    /**
     */
    public ResetPasswordDialog(Context context)
    {
        super(context);
        mContext = context;
        buildDialog();

        mUsername = (EditText) mDialog.findViewById(R.id.reset_user_edittext);

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
        mDialog.setContentView(R.layout.dialog_reset_password);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.copyFrom(mDialog.getWindow().getAttributes());
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    /**
     */
    public void clickedReset()
    {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mUsername.getWindowToken(), 0);
        ParseUser.requestPasswordResetInBackground(mUsername.getText().toString(), new RequestPasswordResetCallback()
        {
            @Override
            public void done(ParseException e)
            {
                Toast.makeText(mContext, "Please check your inbox for \"" +
                        mUsername.getText().toString() + "\"", Toast.LENGTH_LONG).show();
                mDialog.dismiss();
            }
        });
    }

    /**
     *
     */
    private void bindCallbacksToButtons()
    {
        Button resetButton = (Button) mDialog.findViewById(R.id.reset_password_button);
        resetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickedReset();
            }
        });

        Button cancelButton = (Button) mDialog.findViewById(R.id.reset_password_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mUsername.getWindowToken(), 0);
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
}
