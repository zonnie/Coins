package com.moneyifyapp.activities.wallet.adapters;

import android.app.Activity;

import com.moneyifyapp.activities.expenseDetail.ImageAdapter;
import com.moneyifyapp.model.Images;

import java.util.List;

/**
 */
public class WalletImageAdapter extends ImageAdapter
{
    /**
     */
    public WalletImageAdapter(Activity context, List<Images.ImageWithCaption> images, OnItemClick listener)
    {
        super(context, images, listener);
    }

    /**
     */
    public List<Integer> getSortedImages()
    {
        return Images.getSortedImages(Images.getWalletSorted());
    }
}
