package com.moneyifyapp.activities.login.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moneyifyapp.R;
import com.moneyifyapp.utils.Utils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 */
public class ChangePasswordDialog extends Dialog
{
    private Dialog mDialog;
    private Context mContext;
    private EditText mNewPass;
    private EditText mNewPassRepeat;
    private WindowManager.LayoutParams mLayoutParams;

    /**
     */
    public ChangePasswordDialog(Context context)
    {
        super(context);
        mContext = context;
        buildDialog();

        mNewPass = (EditText) mDialog.findViewById(R.id.change_password_edittext);
        mNewPassRepeat = (EditText) mDialog.findViewById(R.id.change_password_repeat_edittext);

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
        mDialog.setContentView(R.layout.dialog_change_password);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.copyFrom(mDialog.getWindow().getAttributes());
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    /**
     */
    public void clickedChange()
    {
        if(isSignUpValid())
        {
            ParseUser user = ParseUser.getCurrentUser();
            user.setPassword(mNewPass.getText().toString());
            user.saveInBackground(new SaveCallback()
            {
                @Override
                public void done(ParseException e)
                {
                    Toast.makeText(mContext, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            });
        }
    }

    /**
     *
     */
    private void bindCallbacksToButtons()
    {
        Button changePassButton = (Button) mDialog.findViewById(R.id.change_password_button);
        changePassButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mNewPassRepeat.getWindowToken(), 0);
                clickedChange();
            }
        });

        Button cancelButton = (Button) mDialog.findViewById(R.id.change_password_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mNewPassRepeat.getWindowToken(), 0);
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

    /**
     */
    public boolean isSignUpValid()
    {
        // Reset errors.
        mNewPass.setError(null);
        mNewPassRepeat.setError(null);

        // Store values at the time of the login attempt.
        String password = mNewPass.getText().toString();
        String verifyPass = mNewPassRepeat.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !Utils.isPasswordValid(password))
        {
            mNewPass.setError(mContext.getString(R.string.error_invalid_password));
            focusView = mNewPass;
            cancel = true;
        }
        else if (TextUtils.isEmpty(password) || !(password.equals(verifyPass)))
        {
            mNewPassRepeat.setError(mContext.getString(R.string.error_password_not_match));
            focusView = mNewPassRepeat;
            cancel = true;
        }

        if (cancel)
        {
            focusView.requestFocus();
            return false;
        }
        else
            return true;
    }

}
