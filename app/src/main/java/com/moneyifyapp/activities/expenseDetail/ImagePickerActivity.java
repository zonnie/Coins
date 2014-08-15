package com.moneyifyapp.activities.expenseDetail;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.utils.Utils;

public class ImagePickerActivity extends ListActivity implements  ImageAdapter.OnItemClick
{
    private Animation mItemsLoadAnimation;
    private ImageAdapter mImageAdapter;
    private EditText mFilterField;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        Utils.initializeActionBar(this);
        Utils.removeActionBar(this);
        Utils.animateForward(this);

        // Instance of ImageAdapter Class
        mImageAdapter = new ImageAdapter(this, Images.getImageWithCaptions(), this);
        getListView().setAdapter(mImageAdapter);
        getListView().setTextFilterEnabled(true);
        initFilterField();

        // Load animation lazy
        if (mItemsLoadAnimation == null)
            mItemsLoadAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        getListView().startAnimation(mItemsLoadAnimation);

    }

    /**
     */
    private void initFilterField()
    {
        mFilterField = (EditText) findViewById(R.id.image_picker_filter);
        mFilterField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
                mImageAdapter.getFilter().filter(arg0);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3){}

            @Override
            public void afterTextChanged(Editable arg0){}
        });
    }

    /**
     * Called by the adapter.
     */
    @Override
    public void onItemClick(int position)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mFilterField.getWindowToken(), 0);

        Intent data = getIntent();
        data.putExtra(Transaction.KEY_IMAGE_NAME, position);
        setResult(ExpensesActivity.IMAGE_PICK_OK, data);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        Utils.animateBack(this);
    }
}
