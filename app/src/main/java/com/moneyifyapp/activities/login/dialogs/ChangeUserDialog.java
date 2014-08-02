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
public class ChangeUserDialog extends Dialog
{
    private Dialog mDialog;
    private Context mContext;
    private EditText mNewUser;
    private WindowManager.LayoutParams mLayoutParams;

    /**
     */
    public ChangeUserDialog(Context context)
    {
        super(context);
        mContext = context;
        buildDialog();

        mNewUser = (EditText) mDialog.findViewById(R.id.change_user_edittext);

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
        mDialog.setContentView(R.layout.dialog_change_user);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.copyFrom(mDialog.getWindow().getAttributes());
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    /**
     */
    public void clickedChange()
    {
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(mNewUser.getText().toString());
        user.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                Toast.makeText(mContext, "Username Updated Successfully", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }
        });
    }

    /**
     *
     */
    private void bindCallbacksToButtons()
    {
        Button changePassButton = (Button) mDialog.findViewById(R.id.change_user_button);
        changePassButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mNewUser.getWindowToken(), 0);
                if(isSignUpValid())
                    clickedChange();
            }
        });

        Button cancelButton = (Button) mDialog.findViewById(R.id.change_user_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mNewUser.getWindowToken(), 0);
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

    public boolean isSignUpValid()
    {
        // Reset errors.
        mNewUser.setError(null);
        String email = mNewUser.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email))
        {
            mNewUser.setError(mContext.getString(R.string.error_field_required));
            focusView = mNewUser;
            cancel = true;
        }
        else if (!Utils.isEmailValid(email))
        {
            mNewUser.setError(mContext.getString(R.string.error_invalid_email));
            focusView = mNewUser;
            cancel = true;
        }

        if (cancel)
        {
            focusView.requestFocus();
            return false;
        }
        else
        {
            return true;
        }
    }

}
