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
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.GraphActivity;
import com.moneyifyapp.activities.expenses.drawer.DrawerExpandableAdapter;
import com.moneyifyapp.activities.expenses.fragments.ExpenseListFragment;
import com.moneyifyapp.activities.favorites.FaviorteActivity;
import com.moneyifyapp.activities.login.AccountActivity;
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
        implements ExpenseListFragment.OnFragmentInteractionListener,
        ViewPager.OnPageChangeListener
{
    private ViewPager mViewPager;
    private Calendar mCalender;
    private TransactionHandler mTransactionHadler;

    public static final int IMAGE_PICK_REQ = 90;
    public static final int IMAGE_PICK_OK = 423;
    public static final int IMAGE_PICK_CANCEL = 563;
    public static final int EXPENSE_RESULT_OK = 222;
    public static final int ACCOUNT_HANDLE = 2345;
    public static final int EXPENSE_RESULT_CANCELED = 333;
    public static final int REQ_NEW_ITEM = 42;
    public static final int REQ_PREFS = 532;
    public static final int RESP_CHANGED = 533;
    public static final int REQ_EDIT_ITEM = 92;
    public static String PARSE_USER_KEY = "user";
    public static String PARSE_USERNAME_KEY = "username";
    private final String DRAWER_TITLE = "Stuff to Do";

    // Drawer
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerTopListLayout;
    private ExpandableListView mDrawerGroupList;
    private ActionBarDrawerToggle mDrawerToggle;
    private YearTransactions mYearTransactions;
    private Activity mActivity;

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

        initViewPager();
        initDrawer();
        initActionBarDisplay();
    }

    /**
     */
    private void initActionBarDisplay()
    {
        if (getActionBar() != null)
            getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
    }

    /**
     */
    private void initViewPager()
    {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(sectionsPagerAdapter);
        mViewPager.setCurrentItem(mCalender.get(Calendar.MONTH));
        mViewPager.setOnPageChangeListener(this);
    }

    /**
     */
    private void initDrawer()
    {
        mDrawerTopListLayout = (LinearLayout) findViewById(R.id.top_list_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerGroupList = (ExpandableListView) findViewById(R.id.left_drawer);
        mDrawerGroupList.setAdapter(new DrawerExpandableAdapter(this, R.layout.drawer_list_group));
        mDrawerGroupList.setOnChildClickListener(new DrawerItemClickListener());
        mDrawerToggle = createDrawerToggle();
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerGroupList.expandGroup(0);

        /*mDrawerGroupList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
        {
            @Override
            public void onGroupExpand(int groupPosition)
            {
                for(int i = 0; i < mDrawerGroupList.getExpandableListAdapter().getGroupCount(); i++)
                    if(i != groupPosition)
                        mDrawerGroupList.collapseGroup(i);
            }
        });*/
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

            public void onDrawerClosed(View view)
            {
                if (getActionBar() != null)
                    getActionBar().setTitle(DRAWER_TITLE);
            }

            public void onDrawerOpened(View drawerView)
            {
                if (getActionBar() != null)
                    getActionBar().setTitle(DRAWER_TITLE);
            }
        };

        return result;
    }

    /**
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /**
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
     */
    private class DrawerItemClickListener implements ExpandableListView.OnChildClickListener
    {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
        {
            mDrawerGroupList.setItemChecked(childPosition, true);
            mDrawerLayout.closeDrawer(mDrawerTopListLayout);

            // App stuff
            if (groupPosition == 0)
            {
                if (childPosition == 0)
                    startFavoritesActivity();
                else if (childPosition == 1)
                    startAnalyticsActivity();
            }
            // General
            else if (groupPosition == 1)
            {
                if (childPosition == 0)
                    startAccountActivity();
                else if (childPosition == 1)
                    startPrefActivity();
            }

            return true;
        }
    }

    /**
     */
    private void startAnalyticsActivity()
    {
        Intent intent = new Intent(mActivity, GraphActivity.class);
        startActivity(intent);
    }

    /**
     */
    private void startPrefActivity()
    {
        Intent intent = new Intent(mActivity, PrefActivity.class);
        startActivityForResult(intent, REQ_PREFS);
    }

    /**
     */
    private void startAccountActivity()
    {
        Intent intent = new Intent(mActivity, AccountActivity.class);
        startActivityForResult(intent, ACCOUNT_HANDLE);
    }

    /**
     */
    private void startFavoritesActivity()
    {
        Intent intent = new Intent(mActivity, FaviorteActivity.class);
        startActivity(intent);
    }

    /**
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.expenses, menu);
        return true;
    }

    /**
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.jump_today)
        {
            mViewPager.setCurrentItem(mCalender.get(Calendar.MONTH));
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
        ParseUser.logOut();

        mTransactionHadler.clearUserTransactions();
        startLoginActivity();
    }

    /**
     */
    private void startLoginActivity()
    {
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
        if(requestCode == ACCOUNT_HANDLE && resultCode == AccountActivity.ACCOUNT_DELETED)
            handleAccountDelete();
        super.onActivityResult(requestCode, resultCode, data);
        int position = mViewPager.getCurrentItem();
        updateFragmentOnCurrencyPrefChange(position);
    }

    /**
     */
    private void handleAccountDelete()
    {
        startLoginActivity();
        ParseUser.getCurrentUser().deleteInBackground();
        logOutUser();
        finish();
    }

    /**
     * Updates the 'id' fragment in the viewpager.
     */
    private void updateFragmentOnCurrencyPrefChange(int id)
    {
        //TODO : This is will not be future-proof
        ExpenseListFragment frag = (ExpenseListFragment) getFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + id);
        frag.updateFragmentCurrency();
        frag.updateTotalCurrencyToPrefDefault();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
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
            return ExpenseListFragment.newInstance(position, mCalender.get(Calendar.YEAR));
        }

        /**
         * Determines the number of pages the view pages holds.
         */
        @Override
        public int getCount()
        {
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

    /**
     */
    @Override
    public void onBackPressed()
    {
        finish();
    }

    /**
     * Called from XML
     */
    public void logoutClicked(View view)
    {
        logOutUser();
    }
}
