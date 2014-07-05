package com.moneyifyapp.activities.expenseDetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.moneyifyapp.R;
import com.moneyifyapp.activities.expenses.ExpensesActivity;
import com.moneyifyapp.model.Transaction;

public class ImagePickerActivity extends Activity
{
    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        GridView gridView = (GridView) findViewById(R.id.image_grid);

        // Instance of ImageAdapter Class
        gridView.setAdapter(new ImageAdapter(this));
    }

    /**
     * Called by the contained adapter when an image is clicked.
     * This method returns the result to the calling activity with the image's resource id.
     */
    public void onItemClick(int position)
    {
        Intent data = getIntent();
        data.putExtra(Transaction.KEY_IMAGE_NAME, position);
        setResult(ExpensesActivity.IMAGE_PICK_OK, data);
        finish();
    }

    /**
     *
     */
    @Override
    public void onBackPressed()
    {
        Intent mIntent = getIntent();
        setResult(ExpensesActivity.IMAGE_PICK_CANCEL, mIntent);
        super.onBackPressed();
    }
}
