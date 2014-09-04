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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.moneyifyapp.activities.login.dialogs.DeleteDialog;
import com.moneyifyapp.activities.preferences.PrefActivity;
import com.moneyifyapp.activities.wallet.WalletActivity;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.model.YearTransactions;
import com.moneyifyapp.model.drawer.DrawerChildItem;
import com.moneyifyapp.model.drawer.DrawerUtils;
import com.moneyifyapp.model.enums.Months;
import com.moneyifyapp.utils.Utils;
import com.moneyifyapp.views.PrettyToast;
import com.parse.ParseUser;

import java.util.Calendar;

/**
 */
public class ExpensesActivity extends Activity
        implements ExpenseListFragment.OnFragmentInteractionListener,
        ViewPager.OnPageChangeListener,
        TransactionHandler.onFetchingCompleteListener,
        DeleteDialog.OnDeleteClicked
{
    private ViewPager mViewPager;
    private Calendar mCalender;
    private TransactionHandler mTransactionHandler;

    public static final int IMAGE_PICK_REQ = 90;
    public static final int IMAGE_PICK_OK = 423;
    public static final int IMAGE_PICK_CANCEL = 563;
    public static final int EXPENSE_RESULT_OK = 222;
    public static final int ACCOUNT_HANDLE = 2345;
    public static final int WALLET_HANDLE = 5345;
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
    private String mWalletToDeleteId;

    /**
     * Called once every life cycle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mActivity = this;
        mTransactionHandler = TransactionHandler.getInstance(mActivity);
        mCalender = Calendar.getInstance();
        mYearTransactions = mTransactionHandler.getYearTransactions(String.valueOf(mCalender.get(Calendar.YEAR)));

        setContentView(R.layout.activity_expenses);

        // Init Parse for data storing
        Utils.initializeParse(this);
        Utils.initializeActionBar(this, DrawerUtils.getWalletTitleById(TransactionHandler.getInstance(this).getCurrentWalletId()));
        Utils.setupBackButton(this);
        Utils.removeLogo(this);

        initViewPager();
        initDrawer();
        initActionBarDisplay();
    }

    /**
     */
    private void swapWallet(String id)
    {
        TransactionHandler.getInstance(this).setCurrentWalletId(id);
        mYearTransactions = TransactionHandler.getInstance(this).getYearTransactions(String.valueOf(mCalender.get(Calendar.YEAR)));
        updateAllFragmentsOnWalletChange();
        Utils.setCurrentWalletId(this, id);

        String walletId = TransactionHandler.getInstance(this).getCurrentWalletId();
        Utils.initializeActionBar(this, DrawerUtils.getWalletTitleById(walletId));

    }

    /**
     */
    @Override
    public void onFetchComplete()
    {

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

        // Expand all but the wallets
        int groupCount = mDrawerGroupList.getExpandableListAdapter().getGroupCount();

        for(int i = 0; i < groupCount; i++)
        {
            if(i != groupCount-1)
                mDrawerGroupList.expandGroup(i);
        }

        expandDesiredGroups();
        registerWalletsToContextMenu();
    }

    /**
     */
    private void registerWalletsToContextMenu()
    {
        registerForContextMenu(mDrawerGroupList);
    }

    /**
     */
    private void expandDesiredGroups()
    {
        // Expand all but the wallets
        int groupCount = mDrawerGroupList.getExpandableListAdapter().getGroupCount();

        for(int i = 0; i < groupCount; i++)
        {
            if (i != groupCount - 1)
                mDrawerGroupList.expandGroup(i);
        }
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

                collapseWallets();

            }

            public void onDrawerOpened(View drawerView)
            {
                if (getActionBar() != null)
                    getActionBar().setTitle(DRAWER_TITLE);

                expandAllButWallets();
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
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

        // Only for newly created wallets
        if(groupPosition == DrawerUtils.getWalletsGroupIndex())
        {
            if(childPosition > 1)
            {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.wallet_context_menu, menu);
            }
        }
    }

    /**
     */
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();

        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

        switch (item.getItemId())
        {
            case R.id.wallet_context_edit:
                editWallet(childPosition);
                return true;
            case R.id.wallet_context_delete:
                deleteWallet(childPosition);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     */
    private void editWallet(int walletPosition)
    {
        Intent intent = new Intent(this, WalletActivity.class);

        DrawerChildItem item = DrawerUtils.sWallets.get(walletPosition);

        intent.putExtra(WalletActivity.WALLET_NAME_KEY, item.getItemTitle());
        intent.putExtra(WalletActivity.WALLET_NOTE_KEY, item.getNote());
        intent.putExtra(WalletActivity.WALLET_SHARED_KEY, false);
        intent.putExtra(WalletActivity.WALLET_EDIT_KEY, true);
        intent.putExtra(WalletActivity.WALLET_ICON_KEY, item.getResourceId());
        intent.putExtra(WalletActivity.WALLET_ID_KEY, item.getId());
        startActivityForResult(intent, WalletActivity.WALLET_EDIT);
    }

    /**
     */
    private void deleteWallet(int walletPosition)
    {
        mWalletToDeleteId = DrawerUtils.sWallets.get(walletPosition).getId();

        if(!Utils.getCurrentWalletId(this).equals(mWalletToDeleteId))
        {
            DeleteDialog deleteDialog = new DeleteDialog(this, this,
                    "Are you sure you want to delete this wallet ?",
                    "This will delete all wallet related data.");
            deleteDialog.show();
        }
        else
            Utils.showPrettyToast(this, "You cannot delete the currently used wallet", PrettyToast.VERY_LONG);
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

    @Override
    public void deleteClicked()
    {
        DrawerUtils.removeWallet(mWalletToDeleteId);
        TransactionHandler.getInstance(this).removeWallet(mWalletToDeleteId);
        collapseWallets();
    }


    /**
     */
    private class DrawerItemClickListener
            implements ExpandableListView.OnChildClickListener
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
            else if(groupPosition == 2)
            {
                if(childPosition == 0)
                    startWalletDetailActivityWithResult();
                else if(childPosition == 1)
                    swapWallet(TransactionHandler.DEFAULT_WALLET_ID);
                else
                {
                    DrawerChildItem item = DrawerUtils.sWallets.get(childPosition);
                    swapWallet(item.getId());
                }
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
    private void startWalletDetailActivityWithResult()
    {
        Intent intent = new Intent(mActivity, WalletActivity.class);
        startActivityForResult(intent, WALLET_HANDLE);
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

        TransactionHandler.getInstance(this).clearAllWallets();
        DrawerUtils.resetWallets();

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
        else if((requestCode == WALLET_HANDLE || requestCode == WalletActivity.WALLET_EDIT)
                && resultCode == WalletActivity.WALLET_OK)
        {
            // Save to Parse
            String title = data.getExtras().getString(TransactionHandler.WALLET_TITLE);
            int icon = data.getExtras().getInt(TransactionHandler.WALLET_ICON_INDEX);
            String notes = data.getExtras().getString(TransactionHandler.WALLET_NOTES);

            if(requestCode == WALLET_HANDLE)
                startNewWallet(title, icon, notes);
            else
                editWallet(title, icon, notes, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        int position = mViewPager.getCurrentItem();
        updateFragmentOnCurrencyPrefChange(position);
    }

    /**
     */
    private void startNewWallet(String title, int icon, String notes)
    {
        String id = DrawerUtils.generateId(title);
        if(icon == WalletActivity.WALLET_ICON_EMPTY)
            icon = DrawerUtils.getDefaultWalletImageIndex();
        TransactionHandler.getInstance(this).addWallet(id, title, icon, notes);
        DrawerUtils.addNewWalletItem(title, icon, id, notes);
    }

    /**
     */
    private void editWallet(String title, int icon, String notes, Intent data)
    {
        String id = data.getExtras().getString(WalletActivity.WALLET_ID_KEY);
        if(icon == WalletActivity.WALLET_ICON_EMPTY)
            icon = Images.getImageIndexByResource(Images.getWalletUnsorted(),
                    DrawerUtils.getWalletById(id).getResourceId());
        TransactionHandler.getInstance(this).updateWallet(id, title, icon, notes);
        DrawerUtils.updateWalletItem(title, icon, id, notes);
        collapseWallets();
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

    /**
     * Updates the 'id' fragment in the viewpager.
     */
    private void updateAllFragmentsOnWalletChange()
    {
        for(int i = 0; i < mViewPager.getAdapter().getCount(); i++)
        {
            ExpenseListFragment frag = (ExpenseListFragment) getFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + i);
            if(frag != null)
                frag.updateOnWalletChange();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        resumeApp();
    }

    /**
     *
     */
    private void resumeApp()
    {
        String year = "" + mCalender.get(Calendar.YEAR);
        TransactionHandler handler = TransactionHandler.getInstance(this);
        if(handler.getYearTransactions(year) == null)
            logOutUser();
        else
            Utils.initializeActionBar(this, DrawerUtils.getWalletTitleById(handler.getCurrentWalletId()));
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

    /**
    */
    private void expandAllButWallets()
    {
        int groupCount = mDrawerGroupList.getExpandableListAdapter().getGroupCount();

        for(int i = 0; i < groupCount; i++)
        {
            if(i != groupCount-1)
                mDrawerGroupList.expandGroup(i);
        }

    }

    /**
     */
    private void collapseWallets()
    {
        // Close wallets list
        int groupCount = mDrawerGroupList.getExpandableListAdapter().getGroupCount();
        mDrawerGroupList.collapseGroup(groupCount-1);
    }
}
