package com.moneyifyapp.activities.expenses;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
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
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.FullAnalyticsActivity;
import com.moneyifyapp.activities.expenses.drawer.DrawerItemAdapter;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.activities.graphs.DashboardActivity;
import com.moneyifyapp.activities.login.LoginActivity;
import com.moneyifyapp.activities.preferences.PrefActivity;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.model.enums.Months;
import com.moneyifyapp.utils.Utils;
import com.parse.ParseUser;

import java.util.Calendar;

/**
 */
public class ExpensesActivity extends Activity
        implements ExpenseListFragment.OnFragmentInteractionListener, ViewPager.OnPageChangeListener
{
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Calendar mCalender;
    private TransactionHandler mTransactionHadler;

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
    private final String DRAWER_TITLE = "Stuff to Do";

    // Drawer
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerTopListLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private YearTransactions mYearTransactions;
    private Activity mActivity;
    private Typeface mHeadLineTypeFace;

    /**
     * Called once every life cycle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mActivity = this;
        mTransactionHadler = TransactionHandler.getInstance(mActivity);
        mCalender = Calendar.getInstance();
        mYearTransactions = mTransactionHadler.getYearTransactions(String.valueOf(mCalender.get(Calendar.YEAR)));

        setContentView(R.layout.activity_expenses);

        // Init Parse for data storing
        Utils.initializeParse(this);
        Utils.initializeActionBar(this);
        Utils.setupBackButton(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(mCalender.get(Calendar.MONTH));
        mViewPager.setOnPageChangeListener(this);

        /** Drawer **/

        mDrawerTopListLayout = (LinearLayout) findViewById(R.id.top_list_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new DrawerItemAdapter(this, R.layout.drawer_list_item));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = createDrawerToggle();
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);


        mHeadLineTypeFace = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
        TextView title = (TextView)findViewById(R.id.top_drawer_title);
        title.setTypeface(mHeadLineTypeFace);
    }

    /**
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
                getActionBar().setTitle(DRAWER_TITLE);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView)
            {
                getActionBar().setTitle(DRAWER_TITLE);
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
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}

    /**
     */
    @Override
    public void onPageSelected(int position)
    {
        updateFragmentOnCurrencyPrefChange(position);
    }

    /**
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
                Intent intent = new Intent(mActivity, FullAnalyticsActivity.class);
                startActivity(intent);
            }
            else if (position == 1)
            {
                return;
            }
            else if (position == 2)
            {
                Intent intent = new Intent(mActivity, PrefActivity.class);
                startActivityForResult(intent, REQ_PREFS);
            }
            else if(position == 3)
            {
                Intent intent = new Intent(mActivity, DashboardActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
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
            return true;
        }
        else if (id == R.id.logout)
        {
            logOutUser();
            return true;
        }
        else if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    /**
     */
    private void logOutUser()
    {
        // Logout user - this clears the disk from any user remains
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.logOut();
        mTransactionHadler.clearUserTransactions();

        // Got to login now
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Called by the contained fragment after an item within it was clicked.
     */
    @Override
    public void expenseItemClickedInFragment(Transaction transaction)
    {
        // Call back called from inside the fragment
    }

    /**
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        int position = mViewPager.getCurrentItem();
        updateFragmentOnCurrencyPrefChange(position);
    }

    /**
     * Updates the 'id' fragment in the viewpager.
     */
    private void updateFragmentOnCurrencyPrefChange(int id)
    {
        //TODO : This is will not be future-proof
        ExpenseListFragment frag = (ExpenseListFragment)getFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+id);
        frag.updateFragmentCurrency();
        frag.updateTotalCurrencyToPrefDefault();
    }

    /**
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        /**
         */
        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            ExpenseListFragment fragment = ExpenseListFragment.newInstance(position, mCalender.get(Calendar.YEAR));
            return fragment;

        }




        /**
         * Determines the number of pages the view pages holds.
         */
        @Override
        public int getCount()
        {
            // Show 12 pages
            return Months.values().length;
        }

        /**
         */
        @Override
        public CharSequence getPageTitle(int position)
        {
            return Months.getMonthNameByNumber(position).toUpperCase() + " " + mYearTransactions.mYear;
        }
    }
}
