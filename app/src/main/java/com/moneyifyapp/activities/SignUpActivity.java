package com.moneyifyapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moneyifyapp.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity
{

    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    EditText mUserEditText;
    EditText mPassEditText;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Parse.initialize(this, "7BjKxmwKAG3nVfaDHWxWusowkJJ4kGNyMlwjrbT8", "c6uhzWLV5SPmCx259cPjHhW8qvw5VUCvDwpVVjFD");

        setContentView(R.layout.activity_sign_up);

        mUserEditText = (EditText)findViewById(R.id.usernameEditText);
        mPassEditText = (EditText)findViewById(R.id.passEditText);
        Button signUpButton = (Button)findViewById(R.id.confirm_signup);

        // Bind sign up button.
        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(validate())
                {
                    signUp();
                }
            }
        });


    }

    /**
     *
     * Validate the sign up info.
     *
     * @return 'true' if valid, 'false' otherwise.
     */
    private boolean validate()
    {
        boolean valid = true;

        if(mUserEditText.getText().length() <= 0
                || mPassEditText.getText().length() <= 0
                || mPassEditText.getText().length() < 4)
        {
            valid = false;
        }

        return valid;
    }

    /**
     *
     * Sign up using Parse API.
     *
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
                }
                else
                {
                    singUpFailed();
                }
            }
        });
    }

    /**
     *
     * Go to sign in activity, this occurs when sign up was successful.
     *
     */
    private void goToLogin()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     *
     * Sign up failed, let the user know.
     *
     */
    private void singUpFailed()
    {
        Toast toast = Toast.makeText(this, "Sign Up failed :(", Toast.LENGTH_SHORT);
        toast.show();
    }
}
