package com.moneyifyapp.activities.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.Transaction;
import com.moneyifyapp.model.TransactionHandler;
import com.moneyifyapp.utils.AnimationUtils;
import com.moneyifyapp.utils.Utils;

/**
 *
 */
public class WalletActivity extends Activity
{
    EditText mWalletDesc;
    EditText mWalletNotes;
    ImageButton mWalletIcon;
    int mImageIconIndex;
    private AlphaAnimation mAlphaDown;
    private AlphaAnimation mAlphaUp;
    public static final int WALLET_OK = 9111;
    public static final int WALLET_CANCEL = 135;

    /**
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        Utils.initializeActionBar(this);
        Utils.setupBackButton(this);
        Utils.removeActionBar(this);

        mAlphaDown = AnimationUtils.getAlphaDownAnimation();
        mAlphaUp = AnimationUtils.getAlphaUpAnimation();

        storeViews();

        Animation fadeInAnimation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(R.id.wallet_root).startAnimation(fadeInAnimation);

    }

    public void storeViews()
    {
        mWalletDesc = (EditText)findViewById(R.id.walletDescription);
        mWalletNotes = (EditText)findViewById(R.id.walletNotes);
        mWalletIcon = (ImageButton)findViewById(R.id.walletIcon);

        mWalletNotes.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if ((actionId == EditorInfo.IME_ACTION_NEXT))
                {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mWalletNotes.getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        });

    }

    /**
     */
    public void OnBackClicked(View view)
    {
        onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        setResult(WALLET_CANCEL, getIntent());
        Utils.animateBack(this);
    }

    /**
     */
    public void onAcceptClicked(View view)
    {
        Intent intent = getIntent();
        intent.putExtra(TransactionHandler.WALLET_TITLE, mWalletDesc.getText().toString());
        intent.putExtra(TransactionHandler.WALLET_ICON_INDEX, mImageIconIndex);
        intent.putExtra(TransactionHandler.WALLET_NOTES, mWalletNotes.getText().toString());
        setResult(WALLET_OK, getIntent());
        finish();
        Utils.animateBack(this);
    }

    /**
     */
    public void iconClicked(View view)
    {
        mWalletIcon.startAnimation(mAlphaDown);
        startImagePicker();
    }

    /**
     */
    public void startImagePicker()
    {
        Intent intent = new Intent(this, WalletImagePicker.class);
        startActivityForResult(intent, ExpensesActivity.IMAGE_PICK_REQ);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        mWalletIcon.startAnimation(mAlphaUp);

        if (resultCode == ExpensesActivity.IMAGE_PICK_OK)
        {
            mImageIconIndex = data.getExtras().getInt(Transaction.KEY_IMAGE_NAME);
            mWalletIcon.setImageResource(Images.getSmallImageByPosition(mImageIconIndex, Images.getWalletUnsorted()));

            if(mWalletDesc.getText().toString().isEmpty())
                mWalletDesc.setText(Images.getCaptioneByPosition(Images.getWalletUnsorted(), mImageIconIndex));
        }
    }
}
