package com.moneyifyapp.activities.expenses;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.drawer.DrawerItemAdapter;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.activities.login.LoginActivity;
import com.moneyifyapp.activities.preferences.PrefActivity;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.model.enums.Months;
import com.moneyifyapp.utils.Utils;
import com.parse.ParseUser;

import java.util.Calendar;

/**
 *
 */
public class ExpensesActivity extends Activity
        implements ExpenseListFragment.OnFragmentInteractionListener, ViewPager.OnPageChangeListener
{
    /********************************************************************/
    /**                            Members                             **/
    /********************************************************************/

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

    public static final int IMAGE_PICK_REQ = 90;
    public static final int IMAGE_PICK_OK = 423;
    public static final int IMAGE_PICK_CANCEL = 563;
    public static final int EXPENSE_RESULT_OK = 222;
    public static final int EXPENSE_RESULT_CANCELED = 333;
    public static final int REQ_NEW_ITEM = 42;
    public static final int REQ_PREFS = 532;
    public static final int RESP_CHANGED = 533;
    public static final int REQ_EDIT_ITEM = 92;
    public static String PARSE_USER_KEY = "user";

    // Drawer
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerTopListLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private YearTransactions mYearTransactions;
    private Activity mActivity;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     * Called once every life cycle.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mActivity = this;

        setContentView(R.layout.activity_expenses);
        mCalender = Calendar.getInstance();

        // Init Parse for data storing
        Utils.initializeParse(this);
        Utils.initializeActionBar(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(mCalender.get(Calendar.MONTH));
        mViewPager.setOnPageChangeListener(this);

        /** Drawer **/

        mTitle = "Stuff to Do";

        mDrawerTopListLayout = (LinearLayout) findViewById(R.id.top_list_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setAdapter(new DrawerItemAdapter(this, R.layout.drawer_list_item));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = createDrawerToggle();

        // Add shadow
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);

        mYearTransactions = new YearTransactions(mCalender.get(Calendar.YEAR));
    }

    /**
     * @return
     */
    private ActionBarDrawerToggle createDrawerToggle()
    {
        ActionBarDrawerToggle result = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        )
        {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view)
            {
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView)
            {
                getActionBar().setTitle(mTitle);
            }
        };

        return result;
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /**
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}

    /**
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position)
    {
        updateFragmentOnCurrencyPrefChange(position);
    }

    /**
     *
     * @param state
     */
    @Override
    public void onPageScrollStateChanged(int state){}

    /**
     *
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mDrawerTopListLayout);

            //TODO This is temp, should be done for every drawer item
            if (position == 0)
            {
                /** Do drawer actions here **/
                mYearTransactions = new YearTransactions(mCalender.get(Calendar.YEAR) + 1);
            }
            if (position == 1)
            {
                return;
            }
            if (position == 2)
            {
                Intent intent = new Intent(mActivity, PrefActivity.class);
                startActivityForResult(intent, REQ_PREFS);
            }
        }
    }

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.expenses, menu);
        getMenuInflater().inflate(R.menu.logout_action, menu);
        return true;
    }

    /**
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

        if (id == R.id.jump_today)
        {
            mViewPager.setCurrentItem(mCalender.get(Calendar.MONTH));
        } else if (id == R.id.logout)
        {
            // Logout user - this clears the disk from any user remains
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.logOut();

            // Got to login now
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called by the contained fragment after an item within it was clicked.
     *
     * @param transaction the id of the item that was clicked.
     */
    @Override
    public void expenseItemClickedInFragment(Transaction transaction)
    {
        // Call back called from inside the fragment
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        int position = mViewPager.getCurrentItem();
        updateFragmentOnCurrencyPrefChange(position);
    }

    /**
     *
     * Updates the 'id' fragment in the viewpager.
     *
     * @param id
     */
    private void updateFragmentOnCurrencyPrefChange(int id)
    {
        //TODO : This is will not be future-proof
        ExpenseListFragment frag = (ExpenseListFragment)getFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+id);
        frag.updateFragmentCurrency();
        frag.updateTotalCurrencyToPrefDefault();
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
            ExpenseListFragment fragment = ExpenseListFragment.newInstance(position, mYearTransactions);
            return fragment;

        }

        /**
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
         * Responsible for the naming of the pages.
         *
         * @param position the position for which to get the title.
         * @return string - the name of the page.
         */
        @Override
        public CharSequence getPageTitle(int position)
        {
            //return Months.getMonthNameByNumber(position + 1) + " " + mCalender.get(Calendar.YEAR);
            return Months.getMonthNameByNumber(position + 1) + " " + mYearTransactions.mYear;
        }
    }
}
