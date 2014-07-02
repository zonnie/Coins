package com.moneyifyapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.moneyifyapp.R;
import com.moneyifyapp.utils.Utils;
import com.parse.ParseUser;

/**
 *
 */
public class SplashActivity extends Activity
{
    /********************************************************************/
    /**                          Members                               **/
    /**
     * ****************************************************************
     */

    private final int SPLASH_DISPLAY_LENGTH = 1200;

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
        setContentView(R.layout.activity_splash_layout);

        // Init Parse API
        Utils.initializeParse(this);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent mainIntent = null;

                // Login user automatically if it can be done
                if (currentUser != null)
                {
                    mainIntent = new Intent(SplashActivity.this, ExpensesActivity.class);
                } else
                {
                    mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
