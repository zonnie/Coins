package com.moneyifyapp.model.drawer;

import com.moneyifyapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zonnie_Work on 06/07/2014.
 */
public class DrawerUtils
{
    public static List<DrawerItem> drawerItems;

    static
    {
        drawerItems = new ArrayList<DrawerItem>();
        //drawerItems.add(new DrawerItem("Change Year", "Change the year", R.drawable.cal));
        drawerItems.add(new DrawerItem("Analytics", "Understand your coins", R.drawable.chart));
        //drawerItems.add(new DrawerItem("My Wallets", "Here you can manage your wallets", R.drawable.wallet));
        drawerItems.add(new DrawerItem("Share Wallet", "Share your wallet with friends", R.drawable.wallet));
        drawerItems.add(new DrawerItem("Preferences", "Menage your preferences", R.drawable.contorls));

    }
}
