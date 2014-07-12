package com.moneyifyapp.activities.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moneyifyapp.R;
import com.moneyifyapp.utils.Utils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity implements View.OnClickListener
{

    /********************************************************************/
    /**                          Members                               **/
    /**
     * ****************************************************************
     */

    EditText mUserEditText;
    EditText mPassEditText;
    EditText mPassRepeatEditText;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Utils.initializeActionBar(this);
        Utils.initializeParse(this);
        Utils.removeLogo(this);

        setContentView(R.layout.activity_sign_up);

        mUserEditText = (EditText) findViewById(R.id.usernameEditText);
        mPassEditText = (EditText) findViewById(R.id.passEditText);
        mPassRepeatEditText = (EditText) findViewById(R.id.passVerifyEditText);
        Button signUpButton = (Button) findViewById(R.id.confirm_signup);

        mUserEditText.setOnClickListener(this);
        mPassEditText.setOnClickListener(this);
        mPassRepeatEditText.setOnClickListener(this);

        // Bind sign up button.
        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isSignUpValid())
                {
                    signUp();
                }
            }
        });

    }

    /**
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        ((EditText) v).setError(null);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
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

    /**
     * Sign up using Parse API.
     */
    private void signUp()
    {
        ParseUser user = new ParseUser();
        user.setUsername(mUserEditText.getText().toString());
        user.setPassword(mPassEditText.getText().toString());

        user.signUpInBackground(new SignUpCallback()
        {
            public void done(ParseException e)
            {
                if (e == null)
                {
                    goToLogin();
                    finish();
                } else
                {
                    singUpFailed();
                }
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
        Toast toast = Toast.makeText(this, "Sign Up failed :(", Toast.LENGTH_SHORT);
        toast.show();
    }
}
