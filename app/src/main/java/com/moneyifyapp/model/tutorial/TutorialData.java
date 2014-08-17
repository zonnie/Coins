package com.moneyifyapp.model.tutorial;

import com.moneyifyapp.R;

/**
 */
public class TutorialData
{
    public enum TutorialType {EXPENSE, MAIN_LIST, ANALYTICS}

    /** Expense Tutorial **/

    public static int[] mExpenseTutorialImages =
            {R.drawable.basic
                    , R.drawable.spend_earn
                    , R.drawable.category_pick
                    , R.drawable.bookmark
                    , R.drawable.expense_done};

    public static String[] mExpenseTutorialHeadlines =
            {"Let's start with the Basics"
                    ,"Expense & Income"
                    ,"Categories"
                    ,"Favorites"
                    ,"That's it"};

    public static String[] mExpenseTutorialTopText =
            {"Basically, a transaction must have a description and it\'s amount"
                    ,"Each transaction is either an income or an expense"
                    ,"You can categorize each transaction from our wide variaty of categories"
                    ,"Use your \"Favorites\" to create new transactions in a pinch"
                    ,"When you are done, simply click the \"Done\" button"};

    public static String[] mExpenseTutorialBottomText =
            {"Optionally you can provide a note for further detail"
                    ,"Tap the \"Spent\"/\"Earned\" button to toggle between the two"
                    ,"This will help us share some insights on where you spend your money"
                    ,"Just tap the \"Favorite\" to save this transaction for future use"
                    ,""};


    /** Main List Tutorial **/

    public static int[] mListTutorialImages =
            {R.drawable.add_new_item
                    , R.drawable.view_item
                    , R.drawable.totals};

    public static String[] mListTutorialHeadlines =
            {"Adding new stuff"
            ,"Update existing stuff"
            ,"Monthly Overview"};

    public static String[] mListTutorialTopText =
            {"Tap the \"+\" to add new income or expenses"
            ,"Take a look at existing transactions and update them"
            ,"Tap to get a complete monthly overview"};

    public static String[] mListTutorialBottomText =
            {"Here you'll be able to 'reuse' favorite transactions"
            ,""
            ,"Tip : Add some stuff before heading to the monthly overview"};
}
