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

import java.util.List;

/**
 * An expense item adapter.
 */
public class ExpenseItemAdapter extends ArrayAdapter<Transaction>
{

    /********************************************************************/
    /**                          Members                               **/
    /**
     * ****************************************************************
     */

    private ListItemHandler mListener;
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
    public ExpenseItemAdapter(Context context, int resource, MonthTransactions expenses, ListItemHandler listener)
    {
        super(context, resource, expenses.getItems());
        mListener = listener;
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

        return getRegularView(position, view);
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
            mItemLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.showItem(v);
                }
            });

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
            mExpenseDayOfMonth = (TextView)view.findViewById(R.id.expense_item_date_text);

            // Handle click view for edit
            mRemoveItemButton = (Button) view.findViewById(R.id.expenseRemove);
            mRemoveItemButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.removeFromFragmentList(v);
                }
            });

            /**     Build the view **/

            // Handle the view's creation date
            handleViewDate(currentTransactionView);
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
    private void handleViewDate(Transaction currentTransactionView)
    {
        if (mExpenseDescription != null)
        {
            String date = currentTransactionView.mTransactionDay;

            if(date.endsWith("1") && !date.equals("11")){date += "st";}
            else if(date.endsWith("2") && !date.equals("12")){date += "nd";}
            else if(date.endsWith("3") && !date.equals("13")){date += "rd";}
            else {date += "th";}

            mExpenseDayOfMonth.setText(date);
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
     * @param position
     */
    public void remove(int position)
    {
        Transaction transaction = mTransactions.getItems().get(position);



        super.remove(transaction);
    }

    /**
     * Get the expense items.
     */
    public List<Transaction> getItems()
    {
        return mTransactions.getItems();
    }

    /**
     * Removes all items from list and updates the observer
     * that the list has changed.
     */
    public void removeAll()
    {
        for (Transaction expense : mTransactions.getItems())
        {
            remove(expense);
        }

        notifyDataSetChanged();
    }

    /**
     * @param position
     * @param expense
     */
    public void update(int position, Transaction expense)
    {
        // Update a single transaction
        updateTransaction(position, expense);

        // Reflect on display
        notifyDataSetChanged();
    }

    /**
     *
     * @param position
     * @param expense
     */
    private void updateTransaction(int position, Transaction expense)
    {
        Transaction updatedExpense = mTransactions.getItems().get(position);

        if (expense != null)
        {
            updatedExpense.mDescription = expense.mDescription;
            updatedExpense.mValue = expense.mValue;
            updatedExpense.mCurrency = expense.mCurrency;
            updatedExpense.mNotes = expense.mNotes;
            updatedExpense.mImageResourceIndex = expense.mImageResourceIndex;
            // Update image
            updateImage(Images.getImageByPosition(updatedExpense.mImageResourceIndex));

            // Update the transaction look according to type
            updateViewType(updatedExpense, expense);
        }
    }

    /**
     *
     * Update the view look according to it's type.
     *
     * @param updatedExpense
     * @param expense
     */
    private void updateViewType(Transaction updatedExpense, Transaction expense)
    {
        boolean changeTextColor = false;

        if(updatedExpense.mIsExpense != expense.mIsExpense)
        {
            changeTextColor = true;
        }

        updatedExpense.mIsExpense = expense.mIsExpense;

        // Update the amount color according to transaction type
        if(expense.mIsExpense && changeTextColor)
        {
            if(mMyView != null)
            {
                updateViewToExpense(mMyView, R.color.expense_color);
            }
        }
        else if(!expense.mIsExpense && changeTextColor)
        {
            if(mMyView != null)
            {
                updateViewToExpense(mMyView, R.color.income_color);
            }
        }
    }

    /**
     *
     * @param transaction
     * @param position
     */
    @Override
    public void insert(Transaction transaction, int position)
    {
        super.insert(transaction, position);
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

    /**
     * Interface to communicate with the containing fragment
     */
    public interface ListItemHandler
    {
        public void removeFromFragmentList(View view);
        public void showItem(View view);
    }
}