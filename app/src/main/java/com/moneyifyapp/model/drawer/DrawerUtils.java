package com.moneyifyapp.model.drawer;

import com.moneyifyapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class DrawerUtils
{
    public static List<DrawerGroupItem> drawerGroupItems;
    public static Map<String, List<DrawerChildItem>> drawerGroupMap;
    public static List<DrawerChildItem> sWallets;
    public static List<DrawerChildItem> sAppStuff;
    public static List<DrawerChildItem> sGeneral;

    public static final String MAIN_NAME = "Main";
    public static final String GENERAL_NAME = "General";
    public static final String WALLETS_NAME = "Wallets";

    static
    {
        drawerGroupItems = new ArrayList<DrawerGroupItem>();
        drawerGroupItems.add(new DrawerGroupItem(MAIN_NAME, "Understand your coins", R.drawable.dashboard));
        drawerGroupItems.add(new DrawerGroupItem(GENERAL_NAME, "Menage your preferences", R.drawable.controls));
        drawerGroupItems.add(new DrawerGroupItem(WALLETS_NAME, "Menage your wallets", R.drawable.fav_drawer));
    }

    static
    {
        sWallets = new ArrayList<DrawerChildItem>();
        sAppStuff = new ArrayList<DrawerChildItem>();
        sGeneral = new ArrayList<DrawerChildItem>();

        sWallets.add(new DrawerChildItem("Private", R.drawable.wallet));

        sGeneral.add(new DrawerChildItem("Account", R.drawable.account));
        sGeneral.add(new DrawerChildItem("Settings", R.drawable.controls));

        sAppStuff.add(new DrawerChildItem("Favorites", R.drawable.fav_drawer));
        sAppStuff.add(new DrawerChildItem("Analytics", R.drawable.dashboard));

        drawerGroupMap = new HashMap<String, List<DrawerChildItem>>();
        drawerGroupMap.put(MAIN_NAME, sAppStuff);
        drawerGroupMap.put(GENERAL_NAME, sGeneral);
        drawerGroupMap.put(WALLETS_NAME, sWallets);
    }
}
