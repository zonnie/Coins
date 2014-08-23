package com.moneyifyapp.model.drawer;

import com.moneyifyapp.R;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.TransactionHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public static final String WALLET_CREATE = "Create a Wallet";
    public static final String DEFAULT_WALLET_NAME = "My Wallet";

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

        sWallets.add(new DrawerChildItem(WALLET_CREATE, R.drawable.add_wallet, "new"));
        sWallets.add(new DrawerChildItem(DEFAULT_WALLET_NAME, R.drawable.wallet_small, TransactionHandler.DEFAULT_WALLET_ID));

        sGeneral.add(new DrawerChildItem("Account", R.drawable.account));
        sGeneral.add(new DrawerChildItem("Settings", R.drawable.controls));

        sAppStuff.add(new DrawerChildItem("Favorites", R.drawable.fav_drawer));
        sAppStuff.add(new DrawerChildItem("Analytics", R.drawable.dashboard));

        drawerGroupMap = new HashMap<String, List<DrawerChildItem>>();
        drawerGroupMap.put(MAIN_NAME, sAppStuff);
        drawerGroupMap.put(GENERAL_NAME, sGeneral);
        drawerGroupMap.put(WALLETS_NAME, sWallets);
    }

    /**
     */
    public static String generateId(String name)
    {
        return (name + Calendar.getInstance().get(Calendar.MINUTE) + UUID.randomUUID());
    }

    /**
     */
    public static void addNewWalletItem(String title, int drawableIndex, String id)
    {
        int icon = Images.getSmallImageByPosition(drawableIndex,  Images.getWalletUnsorted());
        sWallets.add(new DrawerChildItem(title, icon, id));
    }

    /**
     */
    public static void removeWallet(String id)
    {
        DrawerChildItem toRemove = null;

        for(DrawerChildItem item : sWallets)
        {
            if(item.getId().equals(id))
                toRemove = item;
        }

        if(toRemove != null)
            sWallets.remove(toRemove);
    }

    /**
     */
    public static String getWalletTitleById(String id)
    {
        String walletTitle = "";

        for(int i = 0; i < sWallets.size(); i++)
        {
            if(sWallets.get(i).getId().equals(id))
                walletTitle = sWallets.get(i).getItemTitle();
        }

        return walletTitle;
    }

    /**
     *
     */
    public static void resetWallets()
    {
        sWallets.clear();
        sWallets.add(new DrawerChildItem(WALLET_CREATE, R.drawable.add_wallet, "new"));
        sWallets.add(new DrawerChildItem(DEFAULT_WALLET_NAME, R.drawable.wallet_small, TransactionHandler.DEFAULT_WALLET_ID));
    }
}
