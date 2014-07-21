package com.moneyifyapp.activities.analytics;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.analytics.fragments.MonthAnalyticsFragment;
import com.moneyifyapp.utils.Utils;

import java.util.Calendar;
import java.util.Locale;

/**
 * This is a swipable activity that displays all
 * The major analytics avialable.
 */
public class FullAnalyticsActivity extends Activity
{
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Calendar mCalender;

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_layout);
        // Init Parse for data storing
        Utils.initializeParse(this);
        Utils.initializeActionBar(this);
        Utils.setupBackButton(this);
        Utils.setLogo(this, R.drawable.chart);

        mCalender = Calendar.getInstance();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    /**
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
            {
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        /**
         */
        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        /**
         */
        @Override
        public Fragment getItem(int position)
        {
            return MonthAnalyticsFragment.newInstance(mCalender.get(Calendar.MONTH), mCalender.get(Calendar.YEAR), null);
        }

        /**
         */
        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        /**
         */
        @Override
        public CharSequence getPageTitle(int position)
        {
            Locale l = Locale.getDefault();
            switch (position)
            {
                case 0:
                    return getString(R.string.title_analytics_by_date).toUpperCase(l);
                case 1:
                    return getString(R.string.title_analytics_by_category).toUpperCase(l);
                case 2:
                    return getString(R.string.title_analytics_general).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
