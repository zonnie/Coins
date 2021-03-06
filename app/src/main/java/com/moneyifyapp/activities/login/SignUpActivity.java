package com.moneyifyapp.activities.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.LoadingActivity;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.utils.Utils;
import com.moneyifyapp.views.PrettyToast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class SignUpActivity extends LoadingActivity implements View.OnClickListener
{
    private EditText mUserEditText;
    private EditText mPassEditText;
    private EditText mPassRepeatEditText;

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

        setContentView(R.layout.activity_sign_up);

        storeViews();
        bindEventListenersToViews();
    }

    /**
     */
    @Override
    protected void storeViews()
    {
        super.storeViews();
        super.setAnimationText("Writing it down...");
        mUserEditText = (EditText) findViewById(R.id.usernameEditText);
        mPassEditText = (EditText) findViewById(R.id.passEditText);
        mPassRepeatEditText = (EditText) findViewById(R.id.passVerifyEditText);
    }

    /**
     */
    private void bindEventListenersToViews()
    {
        mPassRepeatEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
                {
                    signUpHandle();
                    return true;
                }
                return false;
            }
        });

        mUserEditText.setOnClickListener(this);
        mPassEditText.setOnClickListener(this);
        mPassRepeatEditText.setOnClickListener(this);
        Button signUpButton = (Button) findViewById(R.id.confirm_signup);
        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signUpHandle();
            }
        });

    }

    /**
     */
    private void signUpHandle()
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPassRepeatEditText.getWindowToken(), 0);

        if (isSignUpValid())
        {
            showProgress(true);
            verifyUserNotExist(mUserEditText.getText().toString());
        }
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
    public boolean isSignUpValid()
    {
        // Reset errors.
        mUserEditText.setError(null);
        mPassEditText.setError(null);
        mPassRepeatEditText.setError(null);

        // Store values at the time of the login attempt.
        String email = mUserEditText.getText().toString();
        String password = mPassEditText.getText().toString();
        String verifyPass = mPassRepeatEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email))
        {
            mUserEditText.setError(getString(R.string.error_field_required));
            focusView = mUserEditText;
            cancel = true;
        }
        else if (!Utils.isEmailValid(email))
        {
            mUserEditText.setError(getString(R.string.error_invalid_email));
            focusView = mUserEditText;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        else if (TextUtils.isEmpty(password) || !Utils.isPasswordValid(password))
        {
            mPassEditText.setError(getString(R.string.error_invalid_password));
            focusView = mPassEditText;
            cancel = true;
        }
        // Check for a verified paassword matching
        else if (TextUtils.isEmpty(password) || !(password.equals(verifyPass)))
        {
            mPassRepeatEditText.setError(getString(R.string.error_password_not_match));
            focusView = mPassRepeatEditText;
            cancel = true;
        }

        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        }
        else
        {
            return true;
        }
    }

    private void verifyUserNotExist(String username)
    {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(ExpensesActivity.PARSE_USERNAME_KEY, username);
        query.findInBackground(new FindCallback<ParseUser>()
        {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e)
            {
                if(parseUsers == null)
                    singUpFailed();
                else if(parseUsers.isEmpty())
                {
                    signUp();
                }
                else
                    userExists();
            }
        });
    }

    /**
     * Sign up using Parse API.
     */
    private void signUp()
    {
        ParseUser user = new ParseUser();
        user.setUsername(mUserEditText.getText().toString());
        user.setEmail(mUserEditText.getText().toString());
        user.setPassword(mPassEditText.getText().toString());

        user.signUpInBackground(new SignUpCallback()
        {
            public void done(ParseException e)
            {
                if (e == null)
                {
                    Utils.showPrettyToast(SignUpActivity.this, "An email verifcation was sent to \"" +
                            mUserEditText.getText().toString() + "\"\nPlease verify your account.", PrettyToast.VERY_LONG);
                    goToLogin();
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else
                    singUpFailed();

                showProgress(false);
            }
        });
    }

    /**
     * Go to sign in activity, this occurs when sign up was successful.
     */
    private void goToLogin()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Sign up failed, let the user know.
     */
    private void singUpFailed()
    {
        Utils.showPrettyToast(this, "Sign Up failed :(", PrettyToast.VERY_LONG);
    }
    /**
     * Sign up failed, let the user know.
     */
    private void userExists()
    {
        Utils.showPrettyToast(this, "Username is taken, try another one", PrettyToast.VERY_LONG);
    }


    /**
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
            {
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        Utils.animateBack(this);
    }
}
