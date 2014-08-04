package com.moneyifyapp.activities.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.login.dialogs.ChangePasswordDialog;
import com.moneyifyapp.activities.login.dialogs.ChangeUserDialog;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.utils.Utils;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class AccountActivity extends Activity implements View.OnClickListener
{
    private View mProgressView;
    private View mSignupForm;
    public static final int ACCOUNT_DELETED = 323;
    public static final int ACCOUNT_SAME = 32323;
    public int mItemsCounter;
    private final String DELETE_MSG = "Are you sure you want to delete your account ?" +
            "                       \nThis will also delete all your transactions";

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Utils.initializeActionBar(this);
        Utils.initializeParse(this);
        Utils.setupBackButton(this);
        Utils.removeLogo(this);
        Utils.removeActionBar(this);
        Utils.animateForward(this);

        setContentView(R.layout.activity_user_account);
        getActionBar().setHomeButtonEnabled(true);

        storeViews();
    }

    /**
     */
    private void storeViews()
    {
        mSignupForm = findViewById(R.id.account_form);
        mProgressView = findViewById(R.id.account_progress);
    }

    /**
     */
    public void changeUserClicked(View view)
    {
        promptChangeUserDialog();
    }

    /**
     */
    private void promptChangeUserDialog()
    {
        ChangeUserDialog dialog = new ChangeUserDialog(this);
        dialog.show();
    }

    /**
     */
    public void changePasswordClicked(View view)
    {
        promptChangePasswordDialog();
    }

    /**
    */
    private void promptChangePasswordDialog()
    {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this);
        dialog.show();
    }

    /**
     */
    @Override
    public void onClick(View v)
    {
        ((EditText) v).setError(null);
    }

    /**
     * Sign up using Parse API.
     */
    private void updateAccount()
    {
        ParseUser user = new ParseUser();
        //user.setUsername(mUserEditText.getText().toString());
        //user.setPassword(mPassEditText.getText().toString());

    }

    /**
     * Sign up failed, let the user know.
     */
    private void singUpFailed()
    {
        Toast toast = Toast.makeText(this, "Sign Up failed :(", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user_account, menu);
        return true;
    }

    /**
     */
    public void promptAccountDelete(View view)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteAccount();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(DELETE_MSG).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    /**
     */
    private void deleteAccount()
    {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Transaction.CLASS_NAME);
        query.whereEqualTo(ExpensesActivity.PARSE_USER_KEY, user);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e)
            {
                mItemsCounter = parseObjects.size();
                deleteAccountTransactions(parseObjects);
            }
        });
    }

    /**
     */
    private void deleteAccountTransactions(List<ParseObject> parseObjects)
    {
        for (ParseObject object : parseObjects)
        {
            object.deleteInBackground(new DeleteCallback()
            {
                @Override
                public void done(ParseException e)
                {
                    mItemsCounter--;
                    if (mItemsCounter == 0)
                    {
                        setResult(ACCOUNT_DELETED);
                        finish();
                    }
                }
            });
        }
    }

    /**
     */
    public void OnBackClicked(View view)
    {
        onBackPressed();
    }

    /**
     *
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Utils.animateBack(this);
    }

    /**
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignupForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignupForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mSignupForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else
        {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignupForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
