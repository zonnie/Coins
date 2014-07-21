package com.moneyifyapp.activities.expenseDetail;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.utils.Utils;

public class ImagePickerActivity extends ListActivity
{
    private Animation mItemsLoadAnimation;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        Utils.initializeActionBar(this);
        Utils.setLogo(this, R.drawable.pic);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        // Instance of ImageAdapter Class
        getListView().setAdapter(new ImageAdapter(this));

        // Load animation lazy
        if(mItemsLoadAnimation == null)
            mItemsLoadAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        getListView().startAnimation(mItemsLoadAnimation);

    }

    /**
     * Called by the adapter.
     */
    public void onItemClick(int position)
    {
        Intent data = getIntent();
        data.putExtra(Transaction.KEY_IMAGE_NAME, position);
        setResult(ExpensesActivity.IMAGE_PICK_OK, data);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    /**
     *
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent mIntent = getIntent();
        setResult(ExpensesActivity.IMAGE_PICK_CANCEL, mIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
