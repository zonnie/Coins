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

    public static final String ADD_WALLET_ID = "new";

    public static final String MAIN_NAME = "Main";
    public static final String GENERAL_NAME = "General";
    public static final String WALLETS_NAME = "Wallets";

    public static final String WALLET_CREATE = "Create a Wallet";
    public static final String DEFAULT_WALLET_NAME = "My Wallet";

    public static final String GROUP_ACCOUNT_TITLE = "Account";
    public static final String GROUP_SETTINGS_TITLE = "Settings";
    public static final String GROUP_FAVORITES_TITLE = "Favorites";
    public static final String GROUP_ANALYTICS_TITLE = "Analytics";


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
        sWallets.add(new DrawerChildItem(WALLET_CREATE, R.drawable.add_wallet,
                R.drawable.add_wallet, ADD_WALLET_ID, "Add a new Wallet"));
        sWallets.add(new DrawerChildItem(DEFAULT_WALLET_NAME, R.drawable.wallet,
                R.drawable.wallet_small, TransactionHandler.DEFAULT_WALLET_ID, "Default Wallet"));

        sGeneral = new ArrayList<DrawerChildItem>();
        sGeneral.add(new DrawerChildItem(GROUP_ACCOUNT_TITLE, R.drawable.account,
                R.drawable.account, DrawerUtils.generateId(GROUP_ACCOUNT_TITLE) , "Manage your account"));
        sGeneral.add(new DrawerChildItem(GROUP_SETTINGS_TITLE, R.drawable.controls,
                R.drawable.controls,DrawerUtils.generateId(GROUP_SETTINGS_TITLE), "App Settings"));

        sAppStuff = new ArrayList<DrawerChildItem>();
        sAppStuff.add(new DrawerChildItem(GROUP_FAVORITES_TITLE, R.drawable.fav_drawer,
                R.drawable.fav_drawer, DrawerUtils.generateId(GROUP_FAVORITES_TITLE), "Manage your Favorites"));
        sAppStuff.add(new DrawerChildItem(GROUP_ANALYTICS_TITLE, R.drawable.dashboard,
                R.drawable.dashboard_small, DrawerUtils.generateId(GROUP_ANALYTICS_TITLE), "Insights on your wallet"));

        // Add to map
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
    public static void addNewWalletItem(String title, int drawableIndex, String id, String notes)
    {
        int icon = Images.getImageByPosition(drawableIndex,  Images.getWalletUnsorted());
        int iconSmall = Images.getSmallImageByPosition(drawableIndex,  Images.getWalletUnsorted());
        sWallets.add(new DrawerChildItem(title, icon, iconSmall, id, notes));
    }

    /**
     */
    public static void updateWalletItem(String title, int drawableIndex, String id, String notes)
    {
        DrawerChildItem item = getWalletById(id);
        item.setItemTitle(title);
        item.setResourceIdLarge(Images.getImageByPosition(drawableIndex,  Images.getWalletUnsorted()));
        item.setResourceIdSmall(Images.getSmallImageByPosition(drawableIndex,  Images.getWalletUnsorted()));
        item.setNote(notes);
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

        for (DrawerChildItem sWallet : sWallets)
        {
            if (sWallet.getId().equals(id))
                walletTitle = sWallet.getItemTitle();
        }

        return walletTitle;
    }

    /**
     */
    public static DrawerChildItem getWalletById(String id)
    {
        DrawerChildItem wallet = null;

        for (DrawerChildItem curWallet : sWallets)
        {
            if (curWallet.getId().equals(id))
                wallet = curWallet;
        }

        return wallet;
    }

    /**
     */
    public static void resetWallets()
    {
        sWallets.clear();
        sWallets.add(new DrawerChildItem(WALLET_CREATE, R.drawable.add_wallet,
                R.drawable.add_wallet, ADD_WALLET_ID,  "Add a new Wallet"));
        sWallets.add(new DrawerChildItem(DEFAULT_WALLET_NAME, R.drawable.wallet,
                R.drawable.wallet_small, TransactionHandler.DEFAULT_WALLET_ID, "Default Wallet"));
    }

    /**
     */
    public static int getDefaultWalletImageIndex()
    {
        int walletIconIndex = 0;

        for (int i = 0 ; i < Images.getWalletUnsorted().size(); i++)
            if (Images.getWalletUnsorted().get(i).getImage() == R.drawable.wallet_small)
                walletIconIndex = i;

        return walletIconIndex;
    }

    /**
     */
    public static int getWalletsGroupIndex()
    {
        return drawerGroupItems.size()-1;
    }
}
