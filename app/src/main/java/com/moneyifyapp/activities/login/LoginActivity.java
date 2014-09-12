package com.moneyifyapp.activities.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.LoadingActivity;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.login.dialogs.ResetPasswordDialog;
import com.moneyifyapp.database.TransactionSqlHelper;
import com.moneyifyapp.guide.WelcomeActivity;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.model.tutorial.TutorialData;
import com.moneyifyapp.utils.Utils;
import com.moneyifyapp.views.PrettyToast;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Calendar;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends LoadingActivity
        implements OnClickListener, TransactionHandler.onFetchingCompleteListener
{
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private Button mSignUpButton;
    private Button mResetPasswordButton;
    private TransactionSqlHelper mLocalDb;
    private boolean mFirstRun;
    private ImageView mArt;
    public static final String EMAIL_VERIFY_KEY = "emailVerified";

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Utils.initializeParse(this);
        Utils.initializeActionBar(this);
        Utils.removeLogo(this);
        Utils.removeActionBar(this);
        Utils.animateForward(this);

        // Remove logo for this activity
        if(getActionBar() != null)
            getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // Set the content
        setContentView(R.layout.activity_login_layout);

        mFirstRun = Utils.isFirstRunLogin(this);
        Utils.setFirstRunLogin(this, false);

        storeViews();
        bindViewsToEventListeners();
    }

    /**
     */
    @Override
    protected void storeViews()
    {
        super.storeViews();
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mResetPasswordButton = (Button) findViewById(R.id.forgot_button);
        mArt = (ImageView)findViewById(R.id.login_art);
    }

    /**
     */
    public void onWindowFocusChanged (boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            mArt.startAnimation(Utils.createAnimation(this,R.anim.appear_in_bounce_from_bottom));
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

        mResetPasswordButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSignUpButton.getWindowToken(), 0);
                ResetPasswordDialog dialog = new ResetPasswordDialog(LoginActivity.this);
                dialog.show();
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
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    /**
     */
    public void attemptLogin()
    {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

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
        else if (TextUtils.isEmpty(password) || !Utils.isPasswordValid(password))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel)
            focusView.requestFocus();
        else
        {
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
                            boolean isVerified = user.getBoolean(EMAIL_VERIFY_KEY);

                            // Check email was verified
                            if(isVerified)
                            {
                                // Reset the current wallet to default wallet
                                TransactionHandler handler = TransactionHandler.getInstance(LoginActivity.this);
                                handler.setCurrentWalletId(Utils.getCurrentWalletId(LoginActivity.this));
                                TransactionHandler.getInstance(LoginActivity.this).registerToFetchComplete(LoginActivity.this);
                                handler.fetchTransactionsForAllWalletsByYear(Calendar.getInstance().get(Calendar.YEAR));
                                initFromLocalDb();
                            }
                            else
                            {
                                Utils.showPrettyToast(LoginActivity.this, "Please check your inbox in \"" +
                                        mEmailView.getText() + "\"\nand validate your email", PrettyToast.VERY_LONG);
                                ParseUser.logOut();
                                showProgress(false);
                            }
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     */
    private void signInFailed()
    {
        Utils.showPrettyToast(this, "We can't seem to know you, check your email and password", PrettyToast.VERY_LONG);
        showProgress(false);
    }

    /**
     */
    @Override
    public void onFetchComplete()
    {
        showProgress(false);
        if(mFirstRun)
            startTutorial();
        else
            goToMainActivity();
        finish();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }

    /**
     */
    public void startTutorial()
    {
        Intent intent = new Intent(LoginActivity.this,
                WelcomeActivity.class).putExtra(WelcomeActivity.TUTORIAL_TYPE_KEY, TutorialData.TutorialType.MAIN_LIST.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}



