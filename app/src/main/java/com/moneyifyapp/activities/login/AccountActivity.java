package com.moneyifyapp.activities.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
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
import android.widget.Toast;

import com.moneyifyapp.R;
import com.moneyifyapp.utils.Utils;
import com.parse.ParseUser;

public class AccountActivity extends Activity implements View.OnClickListener
{
    private EditText mUserEditText;
    private EditText mPassEditText;
    private EditText mPassRepeatEditText;
    private View mProgressView;
    private View mSignupForm;

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

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_user_account);
        getActionBar().setHomeButtonEnabled(true);
        storeViews();
        bindEventListenersToViews();
    }

    /**
     */
    private void storeViews()
    {
        mUserEditText = (EditText) findViewById(R.id.update_username_edittext);
        mPassEditText = (EditText) findViewById(R.id.update_pass_edittext);
        mSignupForm = findViewById(R.id.account_form);
        mProgressView = findViewById(R.id.account_progress);
        mPassRepeatEditText = (EditText) findViewById(R.id.update_pass_verify_edittext);

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
        Button signUpButton = (Button) findViewById(R.id.confirm_update);
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
            updateAccount();
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
            focusView.requestFocus();
            return false;
        }
        else
            return true;
    }

    /**
     * Sign up using Parse API.
     */
    private void updateAccount()
    {
        ParseUser user = new ParseUser();
        user.setUsername(mUserEditText.getText().toString());
        user.setPassword(mPassEditText.getText().toString());

    }

    /**
     * Sign up failed, let the user know.
     */
    private void singUpFailed()
    {
        Toast toast = Toast.makeText(this, "Sign Up failed :(", Toast.LENGTH_SHORT);
        toast.show();
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
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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