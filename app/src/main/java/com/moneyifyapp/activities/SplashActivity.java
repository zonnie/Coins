package com.moneyifyapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.login.LoginActivity;
import com.moneyifyapp.model.TransactionHandler;
import com.parse.ParseUser;

import java.util.Calendar;

/**
 *
 */
public class SplashActivity extends Activity
        implements TransactionHandler.onFetchingCompleteListener
{
    private TransactionHandler mTransactionHandler;
    private String SHARED_PREF_NAME = "com.moneyifyapp";
    private String FIRST_RUN_FLAG = "firstrun";
    private int SPLASH_DISPLAY_LENGTH = 1200;

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_layout);

        boolean firstrun = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE).getBoolean(FIRST_RUN_FLAG, true);

        // Save the state with shared preferences
        getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE).edit().putBoolean(FIRST_RUN_FLAG, false).commit();
        mTransactionHandler = TransactionHandler.getInstance(this);

        if (!firstrun)
            mTransactionHandler.registerListenerAndFetchTransactions(this, Calendar.getInstance().get(Calendar.YEAR));
        else
        {
            LinearLayout firstTimeLayout = (LinearLayout) findViewById(R.id.first_time_layout);
            firstTimeLayout.setVisibility(View.VISIBLE);
            startWithNoQuery();
        }

    }

    /**
     */
    @Override
    public void onFetchComplete()
    {
        ParseUser currentUser = ParseUser.getCurrentUser();
        Intent mainIntent = null;

        // Login user automatically if it can be done
        if (currentUser != null)
            mainIntent = new Intent(SplashActivity.this, ExpensesActivity.class);
        else
            mainIntent = new Intent(SplashActivity.this, LoginActivity.class);

        startActivity(mainIntent);
        finish();
    }

    /**
     *
     */
    private void startWithNoQuery()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent mainIntent;

                // Login user automatically if it can be done
                if (currentUser != null)
                    mainIntent = new Intent(SplashActivity.this, ExpensesActivity.class);
                else
                    mainIntent = new Intent(SplashActivity.this, LoginActivity.class);

                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
