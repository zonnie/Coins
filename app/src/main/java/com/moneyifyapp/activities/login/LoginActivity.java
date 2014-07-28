package com.moneyifyapp.activities.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.database.TransactionSqlHelper;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.utils.Utils;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Calendar;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements OnClickListener, TransactionHandler.onFetchingCompleteListener
{
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton;
    private Button mSignUpButton;
    private TransactionHandler mTransactionHandler;
    private TransactionSqlHelper mLocalDb;

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Utils.initializeActionBar(this);
        Utils.removeLogo(this);

        // Remove logo for this activity
        if(getActionBar() != null)
            getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // Set the content
        setContentView(R.layout.activity_login_layout);

        mTransactionHandler = TransactionHandler.getInstance(this);
        mTransactionHandler.registerToFetchComplete(this);

        storeViews();
        bindViewsToEventListeners();
    }

    /**
     */
    private void storeViews()
    {
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     */
    private void bindViewsToEventListeners()
    {
        mEmailView.setOnClickListener(this);
        mPasswordView.setOnClickListener(this);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if ((id == R.id.login) || (id == EditorInfo.IME_NULL))
                {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSignUpButton.getWindowToken(), 0);
                attemptLogin();
            }
        });

        mSignUpButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     */
    @Override
    public void onClick(View v)
    {
        ((EditText)v).setError(null);
    }

    /**
     */
    public void attemptLogin()
    {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(email))
        {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (!Utils.isEmailValid(email))
        {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        else if (TextUtils.isEmpty(password) || !Utils.isPasswordValid(password))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            LoginUser();
        }
    }

    /**
     * Logins using Parse API.
     */
    private void LoginUser()
    {
        // Use Parse API to login
        ParseUser.logInInBackground(mEmailView.getText().toString(), mPasswordView.getText().toString(),
                new LogInCallback()
                {
                    public void done(ParseUser user, ParseException e)
                    {
                        if (user != null)
                        {
                            mTransactionHandler.fetchYearTransactions(Calendar.getInstance().get(Calendar.YEAR));
                            initFromLocalDb();
                        }
                        else
                            signInFailed();

                    }
                }
        );
    }

    /**
     */
    private void initFromLocalDb()
    {
        mLocalDb = new TransactionSqlHelper(this);
    }

    /**
     */
    private void goToMainActivity()
    {
        Intent intent = new Intent(this, ExpensesActivity.class);
        startActivity(intent);
    }

    /**
     */
    private void signInFailed()
    {
        Toast.makeText(this, "We can't seem to know you, check your email and password", Toast.LENGTH_SHORT).show();
        showProgress(false);
    }

    /**
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onFetchComplete()
    {
        showProgress(false);
        goToMainActivity();
        finish();
    }
}



