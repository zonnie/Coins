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

    static
    {
        drawerGroupItems = new ArrayList<DrawerGroupItem>();
        drawerGroupItems.add(new DrawerGroupItem("Cool Stuff", "Understand your coins", R.drawable.dashboard));
        drawerGroupItems.add(new DrawerGroupItem("General", "Menage your preferences", R.drawable.controls));
        drawerGroupItems.add(new DrawerGroupItem("Wallets", "Menage your wallets", R.drawable.fav_drawer));
    }

    static
    {
        sWallets = new ArrayList<DrawerChildItem>();
        sAppStuff = new ArrayList<DrawerChildItem>();
        sGeneral = new ArrayList<DrawerChildItem>();

        sWallets.add(new DrawerChildItem("Private", R.drawable.wallet));

        sGeneral.add(new DrawerChildItem("Account", R.drawable.account));
        sGeneral.add(new DrawerChildItem("Preferences", R.drawable.controls));

        sAppStuff.add(new DrawerChildItem("Favorites", R.drawable.fav_drawer));
        sAppStuff.add(new DrawerChildItem("Analytics", R.drawable.dashboard));

        drawerGroupMap = new HashMap<String, List<DrawerChildItem>>();
        drawerGroupMap.put("Cool Stuff", sAppStuff);
        drawerGroupMap.put("General", sGeneral);
        drawerGroupMap.put("Wallets", sWallets);
    }
}
