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
import android.widget.ToggleButton;

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
    EditText mWalletDescEditText;
    EditText mWalletNotesEditText;
    ImageButton mWalletImageButton;
    ToggleButton mWalletSharedToggle;
    int mImageIconIndex = WALLET_ICON_EMPTY;
    private AlphaAnimation mAlphaDown;
    private AlphaAnimation mAlphaUp;
    public static final int WALLET_OK = 9111;
    public static final int WALLET_EDIT = 9551;
    public static final int WALLET_CANCEL = 135;
    private String mWalletName;
    private String mWalletNote;
    private boolean mWalletShared;
    private boolean mIsEdit;
    private String mWalletId;
    private int mWalletIconResource;

    public static final int WALLET_ICON_EMPTY = -1;
    public static final String WALLET_NAME_KEY = "walletName";
    public static final String WALLET_NOTE_KEY = "walletNote";
    public static final String WALLET_SHARED_KEY = "walletShared";
    public static final String WALLET_EDIT_KEY = "walletEdit";
    public static final String WALLET_ICON_KEY = "walletIcon";
    public static final String WALLET_ID_KEY = "walletId";

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

        if(getIntent().getExtras() != null)
        {
            if(getIntent().hasExtra(WALLET_NAME_KEY))
                mWalletName = getIntent().getExtras().getString(WALLET_NAME_KEY);
            if(getIntent().hasExtra(WALLET_NOTE_KEY))
                mWalletNote = getIntent().getExtras().getString(WALLET_NOTE_KEY);
            if(getIntent().hasExtra(WALLET_SHARED_KEY))
                mWalletShared = getIntent().getExtras().getBoolean(WALLET_SHARED_KEY);
            if(getIntent().hasExtra(WALLET_EDIT_KEY))
                mIsEdit = getIntent().getExtras().getBoolean(WALLET_EDIT_KEY);
            if(getIntent().hasExtra(WALLET_ICON_KEY))
                mWalletIconResource = getIntent().getExtras().getInt(WALLET_ICON_KEY);
            if(getIntent().hasExtra(WALLET_ID_KEY))
                mWalletId = getIntent().getExtras().getString(WALLET_ID_KEY);
        }


        mAlphaDown = AnimationUtils.getAlphaDownAnimation();
        mAlphaUp = AnimationUtils.getAlphaUpAnimation();

        storeViews();

        Animation fadeInAnimation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(R.id.wallet_root).startAnimation(fadeInAnimation);

    }

    /**
     */
    public void storeViews()
    {
        mWalletDescEditText = (EditText)findViewById(R.id.walletDescription);
        mWalletNotesEditText = (EditText)findViewById(R.id.walletNotes);
        mWalletImageButton = (ImageButton)findViewById(R.id.walletIcon);
        mWalletSharedToggle = (ToggleButton)findViewById(R.id.toggle_wallet_share);

        mWalletNotesEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if ((actionId == EditorInfo.IME_ACTION_NEXT))
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mWalletNotesEditText.getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        });

        if(mIsEdit)
        {
            mWalletDescEditText.setText(mWalletName);
            mWalletNotesEditText.setText(mWalletNote);
            mWalletImageButton.setImageResource(mWalletIconResource);
            mWalletSharedToggle.setChecked(mWalletShared);
        }

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
        intent.putExtra(TransactionHandler.WALLET_TITLE, mWalletDescEditText.getText().toString());
        intent.putExtra(TransactionHandler.WALLET_ICON_INDEX, mImageIconIndex);
        intent.putExtra(TransactionHandler.WALLET_NOTES, mWalletNotesEditText.getText().toString());
        if(mIsEdit)
            intent.putExtra(WALLET_ID_KEY, mWalletId);
        setResult(WALLET_OK, getIntent());
        finish();
        Utils.animateBack(this);
    }

    /**
     */
    public void iconClicked(View view)
    {
        mWalletImageButton.startAnimation(mAlphaDown);
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

        mWalletImageButton.startAnimation(mAlphaUp);

        if (resultCode == ExpensesActivity.IMAGE_PICK_OK)
        {
            mImageIconIndex = data.getExtras().getInt(Transaction.KEY_IMAGE_NAME);
            mWalletImageButton.setImageResource(Images.getImageByPosition(mImageIconIndex, Images.getWalletUnsorted()));

            if(mWalletDescEditText.getText().toString().isEmpty())
                mWalletDescEditText.setText(Images.getCaptioneByPosition(Images.getWalletUnsorted(), mImageIconIndex));
        }
    }
}
