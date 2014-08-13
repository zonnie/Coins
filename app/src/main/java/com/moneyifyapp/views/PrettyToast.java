package com.moneyifyapp.views;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moneyifyapp.R;

/**
 */
public class PrettyToast extends Toast
{
    private Activity mContext;
    private ToastParams mParams;
    private View mView;
    private Animation mToastAnimation;
    private final int OFFSET = 10;

    /**
     */
    public PrettyToast(Activity context, ToastParams params)
    {
        super(context);
        mContext = context;
        mParams = params;
        init();
    }

    /**
     */
    private void init()
    {
        LayoutInflater inflater = mContext.getLayoutInflater();
        mView = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) mContext.findViewById(R.id.toast_layout_root));

        initText();
        initImage();
        setParams();

        setView(mView);
    }

    /**
     */
    private void initText()
    {
        TextView text = (TextView) mView.findViewById(R.id.toast_textview);
        text.setText(mParams.mToastText);
    }

    /**
     */
    private void initImage()
    {
        ImageView image = (ImageView) mView.findViewById(R.id.toast_imageview);
        image.setImageResource(mParams.mImageResource);
    }

    /**
     */
    private void setParams()
    {
        setGravity(mParams.mGravity, OFFSET, OFFSET);
        setDuration(mParams.mDuration);
    }

    /**
     */
    public void show()
    {
        super.show();
    }

    /**
     *
     */
    public static class ToastParams
    {
        public String mToastText;
        public int mImageResource;
        public int mDuration;
        public int mGravity;

        public ToastParams()
        {
            mImageResource = R.drawable.info;
            mDuration = Toast.LENGTH_SHORT;
            mGravity = Gravity.BOTTOM;
        }
    }
}
