package com.moneyifyapp.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.moneyifyapp.R;
import com.moneyifyapp.fragments.ExpenseListFragment;
import com.moneyifyapp.model.SingleExpense;
import com.moneyifyapp.model.enums.Months;
import com.parse.Parse;

import java.util.Calendar;
import java.util.Locale;

/**
 *
 */
public class ExpensesActivity extends Activity
        implements ExpenseListFragment.OnFragmentInteractionListener
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /**
     * A calender instance for all date purposes.
     */
    Calendar mCalender;

    /**
     * Called once every life cycle.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        mCalender = Calendar.getInstance();

        // Init Parse for data storing
        Parse.initialize(this, "7BjKxmwKAG3nVfaDHWxWusowkJJ4kGNyMlwjrbT8", "c6uhzWLV5SPmCx259cPjHhW8qvw5VUCvDwpVVjFD");


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(mCalender.get(Calendar.MONTH));
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.expenses, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * Called by the contained fragment after an item within it was clicked.
     *
     * @param singleExpense the id of the item that was clicked.
     */
    @Override
    public void expenseItemClickedInFragment(SingleExpense singleExpense)
    {
        Toast toast = Toast.makeText(this, "Clicked \"" + singleExpense.mDescription + "\"", Toast.LENGTH_SHORT);
        toast.show();
    }


    /**
     * Inner class
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        /**
         *
         * Returns an fragment according to the position the
         * user is viewing.
         *
         * @param position the position for which to fetch the item.
         * @return the currently viewed fragment.
         */
        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position != 0)
            {
                return ExpenseListFragment.newInstance("true");
            }
            else
            {
                return ExpenseListFragment.newInstance("stam");
            }
        }

        /**
         *
         * Determines the number of pages the view pages holds.
         *
         * @return int - the number of pages.
         */
        @Override
        public int getCount()
        {
            // Show 12 pages
            return Months.values().length;
        }

        /**
         *
         * Responsible for the naming of the pages.
         *
         * @param position the position for which to get the title.
         *
         * @return string - the name of the page.
         */
        @Override
        public CharSequence getPageTitle(int position)
        {
            Locale l = Locale.getDefault();
            return Months.getMonthNameByNumber(position+1) + "/" + mCalender.get(Calendar.YEAR);
        }
    }
}
