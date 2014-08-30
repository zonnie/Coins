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
        sWallets.add(new DrawerChildItem(WALLET_CREATE, R.drawable.add_wallet, R.drawable.add_wallet, ADD_WALLET_ID));
        sWallets.add(new DrawerChildItem(DEFAULT_WALLET_NAME, R.drawable.wallet, R.drawable.wallet_small, TransactionHandler.DEFAULT_WALLET_ID));

        sGeneral = new ArrayList<DrawerChildItem>();
        sGeneral.add(new DrawerChildItem("Account", R.drawable.account, R.drawable.account));
        sGeneral.add(new DrawerChildItem("Settings", R.drawable.controls, R.drawable.controls));

        sAppStuff = new ArrayList<DrawerChildItem>();
        sAppStuff.add(new DrawerChildItem("Favorites", R.drawable.fav_drawer, R.drawable.fav_drawer));
        sAppStuff.add(new DrawerChildItem("Analytics", R.drawable.dashboard, R.drawable.dashboard_small));

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
    public static void addNewWalletItem(String title, int drawableIndex, String id)
    {
        int icon = Images.getImageByPosition(drawableIndex,  Images.getWalletUnsorted());
        int iconSmall = Images.getSmallImageByPosition(drawableIndex,  Images.getWalletUnsorted());
        sWallets.add(new DrawerChildItem(title, icon, iconSmall, id));
    }

    /**
     */
    public static void updateWalletItem(String title, int drawableIndex, String id)
    {
        DrawerChildItem item = getWalletById(id);
        item.setItemTitle(title);
        item.setResourceIdLarge(Images.getImageByPosition(drawableIndex,  Images.getWalletUnsorted()));
        item.setResourceIdSmall(Images.getSmallImageByPosition(drawableIndex,  Images.getWalletUnsorted()));
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
        sWallets.add(new DrawerChildItem(WALLET_CREATE, R.drawable.add_wallet, R.drawable.add_wallet, ADD_WALLET_ID));
        sWallets.add(new DrawerChildItem(DEFAULT_WALLET_NAME, R.drawable.wallet,  R.drawable.wallet_small, TransactionHandler.DEFAULT_WALLET_ID));
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
