package com.moneyifyapp.activities.login;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.LoadingActivity;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.login.dialogs.ChangePasswordDialog;
import com.moneyifyapp.activities.login.dialogs.ChangeUserDialog;
import com.moneyifyapp.activities.login.dialogs.DeleteDialog;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.utils.AnimationUtils;
import com.moneyifyapp.utils.Utils;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class AccountActivity extends LoadingActivity
        implements View.OnClickListener, DeleteDialog.OnDeleteClicked
{
    public static final int ACCOUNT_DELETED = 323;
    public static final int ACCOUNT_SAME = 32323;
    public int mItemsCounter;

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

        storeViews();
    }

    /**
     */
    @Override
    protected void storeViews()
    {
        super.storeViews();
        super.setAnimationText("Deleting account \"" + ParseUser.getCurrentUser().getUsername().toString() + "\"");
        TextView userLayout = (TextView) findViewById(R.id.change_password_field);
        userLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(AnimationUtils.getBounceAnimtion(AccountActivity.this));
                changeUserClicked(v);

            }
        });

        TextView passLayout = (TextView) findViewById(R.id.change_username_field);
        passLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(AnimationUtils.getBounceAnimtion(AccountActivity.this));
                changePasswordClicked(v);

            }
        });
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
     */
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
        String first = getResources().getString(R.string.delete_account_first_text);
        String second = getResources().getString(R.string.delete_account_second_text);
        DeleteDialog dialog = new DeleteDialog(this, this, first, second);
        dialog.show();
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
        if(parseObjects.isEmpty())
        {
            setResult(ACCOUNT_DELETED);
            finish();
            super.showProgress(false);
            return;
        }

        for (ParseObject object : parseObjects)
        {
            object.deleteInBackground(new DeleteCallback()
            {
                @Override
                public void done(ParseException e)
                {
                    mItemsCounter--;
                    if (mItemsCounter <= 0)
                    {
                        setResult(ACCOUNT_DELETED);
                        finish();
                        showProgress(false);
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
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Utils.animateBack(this);
    }

    @Override
    public void deleteClicked()
    {
        showProgress(true);
        deleteAccount();
    }
}
