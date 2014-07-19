package com.moneyifyapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_layout);

        // Init Parse API
        mTransactionHandler = TransactionHandler.getInstance(this);
        mTransactionHandler.registerToFetchComplete(this);
        mTransactionHandler.featchYearTransactions(Calendar.getInstance().get(Calendar.YEAR));
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
}
