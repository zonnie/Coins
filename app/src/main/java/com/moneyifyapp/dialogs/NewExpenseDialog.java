package com.moneyifyapp.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.moneyifyapp.R;

/**
 * A dialog fragment for adding new expenses.
 */
public class NewExpenseDialog extends DialogFragment
{
    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    ImageButton mButton;
    EditText mExpenseDescription;
    EditText mExpenseValue;
    public onSubmitListener mListener;
    String text = "";

    /**
     * An interface for the containing activity/fragment to implement.
     * This is for communication between them.
     */
    public interface onSubmitListener
    {
        void onAddExpenseInDialog(String addDescription, String addSum);
    }

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    /**
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        final Dialog dialog = new Dialog(getActivity());

        // Prepare and show dialog
        dialog.setTitle("Add a new expense");
        dialog.setContentView(R.layout.fragment_add_expense);
        dialog.show();

        // Save the views
        mButton = (ImageButton) dialog.findViewById(R.id.submitButton);
        mExpenseDescription = (EditText) dialog.findViewById(R.id.addExpenseDescription);
        mExpenseValue = (EditText) dialog.findViewById(R.id.addExpenseSum);

        // Bind the submit button to the containing fragment so
        // that it will add a new item to the list view
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onAddExpenseInDialog(mExpenseDescription.getText().toString(), mExpenseValue.getText().toString());
                dismiss();
            }
        });
        return dialog;
    }
}
