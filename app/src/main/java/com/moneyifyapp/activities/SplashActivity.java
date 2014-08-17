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
import com.moneyifyapp.database.TransactionSqlHelper;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.utils.Utils;
import com.parse.ParseUser;

import java.util.Calendar;

/**
 *
 */
public class SplashActivity extends Activity
        implements TransactionHandler.onFetchingCompleteListener
{
    private int SPLASH_DISPLAY_LENGTH = 1000;
    private TransactionSqlHelper mLocalDb;
    private boolean mIsFristRun;

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_layout);

        mIsFristRun = Utils.isFirstRunSplash(this);
        Utils.setFirstRunSplash(this, false);

        TransactionHandler mTransactionHandler = TransactionHandler.getInstance(this);

        if (!mIsFristRun)
        {
            mTransactionHandler.registerListenerAndFetchTransactions(this, Calendar.getInstance().get(Calendar.YEAR));
            initFromLocalDb();
        }
        else
        {
            LinearLayout firstTimeLayout = (LinearLayout) findViewById(R.id.first_time_layout);
            firstTimeLayout.setVisibility(View.VISIBLE);
            startWithNoQuery();
        }
    }

    /**
     */
    private void initFromLocalDb()
    {
        mLocalDb = new TransactionSqlHelper(this);
    }

    /**
     */
    @Override
    public void onFetchComplete()
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

                if (currentUser != null)
                    mainIntent = new Intent(SplashActivity.this, ExpensesActivity.class);
                else
                    mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
}
