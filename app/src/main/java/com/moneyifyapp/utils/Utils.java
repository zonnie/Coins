package com.moneyifyapp.utils;

import android.content.Context;

import com.parse.Parse;

/**
 * Created by Zonnie_Work on 01/07/2014.
 */
public class Utils
{

    /**
     * Initialized the Parse API.
     */
    public static void initializeParse(Context context)
    {
        // Init Parse for data storing
        Parse.initialize(context, "7BjKxmwKAG3nVfaDHWxWusowkJJ4kGNyMlwjrbT8", "c6uhzWLV5SPmCx259cPjHhW8qvw5VUCvDwpVVjFD");
    }
}
