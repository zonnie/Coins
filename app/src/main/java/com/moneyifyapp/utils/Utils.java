package com.moneyifyapp.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.preferences.PrefActivity;
import com.parse.Parse;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class Utils
{
    public static String globalCurrencyList[] = {"$", "₪", "£", "€", "¥", "¥"};
    private static final String mEmailFormat = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
    public static final String FONT_CONDENSED = "sans-serif-condensed";
    public static final String FONT_THIN = "sans-serif-thin";
    private static DecimalFormat mFormater;

    /**
     */
    public static int findIndextByString(String str)
    {
        int res = 0;

        for(int i = 0; i < globalCurrencyList.length; ++i)
        {
            if(str.equals(globalCurrencyList[i]))
                res = i;
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
     */
    public static void initializeActionBar(Activity context)
    {
        if(context.getActionBar() != null)
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
    }

    /**
     */
    public static void removeLogo(Activity context)
    {
        if(context.getActionBar() != null)
            context.getActionBar().setIcon(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
    }

    /**
     */
    public static void removeActionBar(Activity context)
    {
        if(context.getActionBar() != null)
            context.getActionBar().hide();
    }

    /**
     */
    public static void setLogo(Activity context, int resourceId)
    {
        if(context.getActionBar() != null)
        {
            context.getActionBar().setIcon(resourceId);
        }
    }

    /**
     * Get the default configured currency
     */
    public static String getDefaultCurrency(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PrefActivity.PREF_LIST_NAME,
                context.getResources().getString(R.string.pref_default_currency_value));

    }

    /**
     * Validates the email's format
     */
    public static boolean isEmailValid(String email)
    {
        boolean isValid = false;

        String expression = mEmailFormat;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches())
            isValid = true;
        return isValid;
    }

    /**
     */
    public static boolean isPasswordValid(String password)
    {
        // Nothing fancy
        return password.length() >= 4;
    }

    /**
     */
    public static String generateDayInMonthSuffix(String date)
    {
        String dateSuffix = "";

        // Build the day suffix
        if(date.endsWith("1") && !date.equals("11")){dateSuffix += "st";}
        else if(date.endsWith("2") && !date.equals("12")){dateSuffix += "nd";}
        else if(date.endsWith("3") && !date.equals("13")){dateSuffix += "rd";}
        else {dateSuffix += "th";}

        return dateSuffix;
    }

    /**
     */
    public static String formatDoubleToTextCurrency(double sum)
    {
        if(mFormater == null)
            mFormater = new DecimalFormat("#.##");

        return mFormater.format(sum);
    }

    /**
     */
    public static String getMonthNameByIndex(int monthIndex)
    {
        String month = "";

        String[] dateFormat = new DateFormatSymbols().getMonths();

        if(dateFormat != null)
        {
            if(dateFormat.length-1 >= monthIndex)
                month = dateFormat[monthIndex];
        }

        return month;
    }

    /**
     */
    public static String getMonthPrefixByIndex(int monthIndex)
    {
        String month = getMonthNameByIndex(monthIndex);
        if(month.length() > 3)
            month = month.substring(0,3);

        return  month;
    }

    /**
     */
    public static void setupBackButton(Activity context)
    {
        ActionBar bar = context.getActionBar();

        if(bar != null)
            bar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     *
     * @param log
     */
    public static void writeLog(String log)
    {
        Log.d("Yoni", "DEBUG : " + log);
    }

    /**
     */
    public static void animateForward(Activity activity)
    {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     */
    public static void animateBack(Activity activity)
    {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
