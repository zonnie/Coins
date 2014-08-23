package com.moneyifyapp.activities.wallet;

import com.moneyifyapp.activities.expenseDetail.ImageAdapter;
import com.moneyifyapp.activities.expenseDetail.ImagePickerActivity;
import com.moneyifyapp.activities.wallet.adapters.WalletImageAdapter;
import com.moneyifyapp.model.Images;

public class WalletImagePicker extends ImagePickerActivity
{
    /**
     */
    @Override
    protected ImageAdapter createAdapter()
    {
        return new WalletImageAdapter(this, Images.getWalletImageWithCaptions(), this);
    }

    /**
     */
    @Override
    protected int getImageIndexBySortedPosition(int position)
    {
        return Images.getWalletImageIndexBySortedPosition(position);
    }
}
