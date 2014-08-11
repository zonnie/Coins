package com.moneyifyapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.activities.login.LoginActivity;
import com.moneyifyapp.database.TransactionSqlHelper;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.utils.AnimationUtils;
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
    private TransactionSqlHelper mLocalDb;

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

        ImageView imageView = (ImageView)findViewById(R.id.splashImage);
        imageView.startAnimation(AnimationUtils.getZoomInBounceAnimation(this));

        if (!firstrun)
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

        TextView slogan = (TextView) findViewById(R.id.splashSlogan);
        slogan.startAnimation(AnimationUtils.getmAlphaUpAnimation());
        TextView title = (TextView) findViewById(R.id.splashTitle);
        title.startAnimation(AnimationUtils.getmAlphaUpAnimation());
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
