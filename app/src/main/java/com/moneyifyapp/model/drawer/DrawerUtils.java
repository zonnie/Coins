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
        drawerItems.add(new DrawerItem("Analytics", "Understand your coins", R.drawable.dashboard));
        drawerItems.add(new DrawerItem("Preferences", "Menage your preferences", R.drawable.contorls));
        drawerItems.add(new DrawerItem("Account", "Menage your account", R.drawable.account));
        //drawerItems.add(new DrawerItem("Dashboard", "Look at stuff", R.drawable.dashboard));
        //drawerItems.add(new DrawerItem("Share Wallet", "Share your wallet with friends", R.drawable.wallet));
    }
}
