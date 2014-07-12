package com.moneyifyapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.preferences.PrefActivity;
import com.parse.Parse;

/**
 * Created by Zonnie_Work on 01/07/2014.
 */
public class Utils
{

    public static String state[] = {"$", "₪"};

    /**
     *
     * @param str
     * @return
     */
    public static int findIndextByString(String str)
    {
        int res = 0;

        for(int i = 0; i < state.length; ++i)
        {
            if(str.equals(state[i]))
            {
                res = i;
            }
        }

        return  res;
    }

    /**
     * Initialized the Parse API.
     */
    public static void initializeParse(Context context)
    {
        // Init Parse for data storing
        Parse.initialize(context, "7BjKxmwKAG3nVfaDHWxWusowkJJ4kGNyMlwjrbT8", "c6uhzWLV5SPmCx259cPjHhW8qvw5VUCvDwpVVjFD");
    }

    /**
     * Initializes the action bar to be custom made.
     *
     * @param context
     */
    public static void initializeActionBar(Activity context)
    {
        context.getActionBar().setDisplayShowCustomEnabled(true);
        context.getActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(context);
        View v = inflator.inflate(R.layout.custom_actionbar, null);

        //if you need to customize anything else about the text, do it here.
        //I'm using a custom TextView with a custom font in my layout xml so all I need to do is set title
        ((TextView) v.findViewById(R.id.title)).setText(context.getTitle());

        //assign the view to the actionbar
        context.getActionBar().setCustomView(v);
    }

    /**
     *
     * @param context
     */
    public static void removeLogo(Activity context)
    {
        context.getActionBar().setIcon(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
    }

    /**
     *
     * @param context
     */
    public static void setLogo(Activity context, int resourceId)
    {
        context.getActionBar().setIcon(resourceId);
    }

    /**
     * Get the default configured currency
     *
     * @param context
     * @return
     */
    public static String getDefaultCurrency(Activity context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PrefActivity.PREF_LIST_NAME,
                context.getResources().getString(R.string.pref_default_currency_value));

    }
}
