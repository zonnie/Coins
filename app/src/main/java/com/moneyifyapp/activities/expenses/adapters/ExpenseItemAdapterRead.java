package com.moneyifyapp.activities.expenses.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moneyifyapp.R;
import com.moneyifyapp.model.Images;
import com.moneyifyapp.model.MonthTransactions;
import com.moneyifyapp.model.Transaction;

/**
 * An expense item adapter.
 */
public class ExpenseItemAdapterRead extends ArrayAdapter<Transaction>
{

    /********************************************************************/
    /**                          Members                               **/
    /**
     * ****************************************************************
     */

    private TextView mExpenseDescription;
    private LinearLayout mItemLayout;
    private TextView mExpenseValue;
    private TextView mExpenseCurrency;
    private TextView mExpenseNote;
    private TextView mExpenseDayOfMonth;
    private int mLayoutResourceId;
    private MonthTransactions mTransactions;
    private Button mRemoveItemButton;
    private ImageView mImage;
    private int mItemsLoaded;
    private View mMyView;
    public static int PICK_IMAGE_DIMENSIONS = 80;
    private final String EMPTY_NOTE_HINT = "Please enter a note...";
    private Animation mItemsLoadAnimation;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/


    /**
     * @param context
     * @param resource
     */
    public ExpenseItemAdapterRead(Context context, int resource, MonthTransactions expenses)
    {
        super(context, resource, expenses.getItems());
        mTransactions = expenses;
        mLayoutResourceId = resource;
        mItemsLoaded = 0;
    }

    /**
     * Generates the fragments view for display for each list view item.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View view = convertView;
        mMyView = view;

        if (view == null)
        {


            LayoutInflater viewInflator;
            viewInflator = LayoutInflater.from(getContext());

            view = viewInflator.inflate(mLayoutResourceId, null);

            // Load animation lazy
            if(mItemsLoadAnimation == null)
            {
                mItemsLoadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            }

            view.startAnimation(mItemsLoadAnimation);
        }

        getRegularView(position, view);

        return view;
    }

    /**
     *
     * @param position
     * @param view
     * @return
     */
    private View getRegularView(int position, View view)
    {
        Transaction currentTransactionView = mTransactions.getItems().get(position);

        // Populate the current view according to collection item.
        if (currentTransactionView != null)
        {
            // Take care of click to layout to be able to edit item in view
            mItemLayout = (LinearLayout) view.findViewById(R.id.transaction_card_layout);

            // Update ht look of the the view accordingly
            if (mTransactions.getItems().get(position).mIsExpense == false)
            {
                updateViewToExpense(view, R.color.income_color);
            }
            else
            {
                updateViewToExpense(view, R.color.expense_color);
            }

            // Handle click view for edit
            mExpenseDescription = (TextView) view.findViewById(R.id.expenseDesc);
            mImage = (ImageView)view.findViewById(R.id.image_container);
            // Handle click view for edit
            mExpenseValue = (TextView) view.findViewById(R.id.expenseValue);
            mExpenseCurrency = (TextView) view.findViewById(R.id.currency);
            // Handle click view for edit
            mExpenseNote = (TextView) view.findViewById(R.id.expenseItemNote);

            // Update image from object
            updateImage(Images.getImageByPosition(currentTransactionView.mImageResourceIndex));
            // Handle view's description
            handleViewDescription(currentTransactionView);
            // Handle view's value amount
            handleViewValue(currentTransactionView);
            // Handle view's currency
            handleViewCurrency(currentTransactionView);
            // Handle view's note
            handleViewNote(currentTransactionView);
        }
        return view;
    }

    /**
     *
     * @param currentTransactionView
     */
    private void handleViewDescription(Transaction currentTransactionView)
    {
        if (mExpenseDescription != null)
        {
            mExpenseDescription.setText(currentTransactionView.mDescription);
        }

    }

    /**
     *
     * @param currentTransactionView
     */
    private void handleViewValue(Transaction currentTransactionView)
    {
        if (mExpenseValue != null)
        {

            mExpenseValue.setText(currentTransactionView.mValue);
        }
    }

    /**
     *
     * @param currentTransactionView
     */
    private void handleViewCurrency(Transaction currentTransactionView)
    {
        if (mExpenseCurrency != null)
        {
            mExpenseCurrency.setText(currentTransactionView.mCurrency);
        }
    }

    /**
     *
     * @param currentTransactionView
     */
    private void handleViewNote(Transaction currentTransactionView)
    {
        if (mExpenseNote != null)
        {
            if (!currentTransactionView.mNotes.isEmpty())
            {
                mExpenseNote.setText(currentTransactionView.mNotes);
            }
            else
            {
                mExpenseNote.setText(EMPTY_NOTE_HINT);
            }
        }
    }

    /**
     *
     * Updates the item to be expense/income item.
     *
     * @param view
     */
    private void updateViewToExpense(View view, int colorId)
    {
        TextView amount = (TextView)view.findViewById(R.id.expenseValue);
        amount.setTextColor(view.getResources().getColor(colorId));

        TextView currency = (TextView)view.findViewById(R.id.currency);
        currency.setTextColor(view.getResources().getColor(colorId));

    }

    /**
     * Update the description's left drawable
     *
     * @param resourceIndex
     */
    private void updateImage(int resourceIndex)
    {
        Drawable img = getContext().getResources().getDrawable(resourceIndex);
        img.setBounds( 0, 0, PICK_IMAGE_DIMENSIONS, PICK_IMAGE_DIMENSIONS);
        mExpenseDescription.setCompoundDrawables(img, null, null, null);
    }
}