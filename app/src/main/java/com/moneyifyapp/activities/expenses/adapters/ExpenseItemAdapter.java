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
import com.moneyifyapp.utils.Utils;

import java.util.List;

/**
 * An expense item adapter.
 */
public class ExpenseItemAdapter extends ArrayAdapter<Transaction>
{
    private ListItemHandler mListener;
    private int mLayoutResourceId;
    private MonthTransactions mTransactions;
    public static int PICK_IMAGE_DIMENSIONS = 130;
    private Animation mItemsLoadAnimation;
    public View mMyView;
    private ViewHolder mViewHolder;


    /**
     */
    public ExpenseItemAdapter(Context context, int resource, MonthTransactions expenses, ListItemHandler listener)
    {
        super(context, resource, expenses.getItems());
        mListener = listener;
        mTransactions = expenses;
        mLayoutResourceId = resource;
    }

    /**
     * Generates the fragments view for display for each list view item.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        mMyView = convertView;

        if (mMyView == null)
        {
            mViewHolder = new ViewHolder();
            LayoutInflater viewInflator = LayoutInflater.from(getContext());
            mMyView = viewInflator.inflate(mLayoutResourceId, null);
            storeViews();

            // Load animation lazy
            if(mItemsLoadAnimation == null)
                mItemsLoadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

            //TODO animation
            //mMyView.startAnimation(mItemsLoadAnimation);
            mMyView.setTag(mViewHolder);
        }
        else
            mViewHolder = (ViewHolder)mMyView.getTag();

        return buildItemView(position);
    }

    /**
     */
    private View buildItemView(int position)
    {
        Transaction currentTransaction = mTransactions.getItems().get(position);

        // Populate the current view according to collection item.
        if (currentTransaction != null)
        {
            bindItemToEventListeners(position);
            handleViewDate(currentTransaction);
            handleViewImage(Images.getImageByPosition(currentTransaction.mImageResourceIndex));
            handleViewDescription(currentTransaction);
            handleViewValue(currentTransaction);
            handleViewCurrency(currentTransaction);
            handleViewNote(currentTransaction);
        }
        return mMyView;
    }

    /**
     */
    private void bindItemToEventListeners(int position)
    {
        // Take care of click to layout to be able to edit item in view
        mViewHolder.mItemLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.showItem(v);
            }
        });

        initTransactionLookType(position);
        mViewHolder.mRemoveItemButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.removeFromFragmentList(v);
            }
        });
    }

    /**
     */
    private void initTransactionLookType(int position)
    {
        // Update ht look of the the view accordingly
        if (!(mTransactions.getItems().get(position).mIsExpense))
            updateViewToExpense(mMyView, R.color.income_color);
        else
            updateViewToExpense(mMyView, R.color.expense_color);
    }

    /**
     */
    private void storeViews()
    {
        mViewHolder.mItemLayout = (LinearLayout) mMyView.findViewById(R.id.transaction_card_layout);
        mViewHolder.mExpenseDescription = (TextView) mMyView.findViewById(R.id.expenseDesc);
        mViewHolder.mExpenseValue = (TextView) mMyView.findViewById(R.id.expenseValue);
        mViewHolder.mExpenseCurrency = (TextView) mMyView.findViewById(R.id.currency);
        mViewHolder.mExpenseNote = (TextView) mMyView.findViewById(R.id.expenseItemNote);
        mViewHolder.mExpenseDayOfMonth = (TextView)mMyView.findViewById(R.id.expense_item_date_text);
        mViewHolder.mExpenseMonth = (TextView)mMyView.findViewById(R.id.expense_item_date_month);
        mViewHolder.mRemoveItemButton = (Button) mMyView.findViewById(R.id.expenseRemove);
        mViewHolder.mRemoveItemLayout = (LinearLayout) mMyView.findViewById(R.id.remove_item_button_layout);
        mViewHolder.mExpenseImage = (ImageView) mMyView.findViewById(R.id.expense_item_image);
    }

    /**
     */
    private void handleViewDescription(Transaction currentTransactionView)
    {
        if (mViewHolder.mExpenseDescription != null)
        {
            mViewHolder.mExpenseDescription.setText(currentTransactionView.mDescription);
        }
    }

    /**
     */
    private void handleViewDate(Transaction currentTransactionView)
    {
        if (mViewHolder.mExpenseDescription != null)
        {
            String date = currentTransactionView.mTransactionDay;
            String dateSuffix = Utils.getMonthPrefixByIndex(mTransactions.mMonthNumber).toUpperCase();
            mViewHolder.mExpenseDayOfMonth.setText(date);
            mViewHolder.mExpenseMonth.setText(dateSuffix);
        }

    }

    /**
     */
    private void handleViewValue(Transaction currentTransactionView)
    {
        if (mViewHolder.mExpenseValue != null)
            mViewHolder.mExpenseValue.setText(currentTransactionView.mValue);
    }

    /**
     */
    private void handleViewCurrency(Transaction currentTransactionView)
    {
        if (mViewHolder.mExpenseCurrency != null)
            mViewHolder.mExpenseCurrency.setText(currentTransactionView.mCurrency);
    }

    /**
     */
    private void handleViewNote(Transaction currentTransactionView)
    {
        if (mViewHolder.mExpenseNote != null)
            mViewHolder.mExpenseNote.setText(currentTransactionView.mNotes);
    }

    /**
     * Updates the item to be expense/income item.
     */
    private void updateViewToExpense(View view, int colorId)
    {
        TextView amount = (TextView)view.findViewById(R.id.expenseValue);
        amount.setTextColor(view.getResources().getColor(colorId));
        TextView currency = (TextView)view.findViewById(R.id.currency);
        currency.setTextColor(view.getResources().getColor(colorId));
    }

    /**
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
            remove(expense);

        notifyDataSetChanged();
    }

    /**
     */
    public void update(int position, Transaction expense)
    {
        // Update a single transaction
        updateTransaction(position, expense);
        notifyDataSetChanged();
    }

    /**
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
            handleViewImage(Images.getImageByPosition(updatedExpense.mImageResourceIndex));
            updateViewType(updatedExpense, expense);
            updatedExpense.mSaved = expense.mSaved;
        }
    }

    /**
     * Update the view look according to it's type.
     */
    private void updateViewType(Transaction updatedExpense, Transaction expense)
    {
        boolean changeTextColor = false;

        if(updatedExpense.mIsExpense != expense.mIsExpense)
            changeTextColor = true;

        updatedExpense.mIsExpense = expense.mIsExpense;
        // Update the amount color according to transaction type
        if(expense.mIsExpense && changeTextColor)
        {
            if(mMyView != null)
                updateViewToExpense(mMyView, R.color.expense_color);
        }
        else if(!expense.mIsExpense && changeTextColor)
        {
            if(mMyView != null)
                updateViewToExpense(mMyView, R.color.income_color);
        }
    }

    /**
     */
    @Override
    public void insert(Transaction transaction, int position)
    {
        super.insert(transaction, position);
        notifyDataSetChanged();
    }

    /**
     * Update the description's left drawable
     */
    private void handleViewImage(int resourceIndex)
    {
        Drawable img = getContext().getResources().getDrawable(resourceIndex);
        img.setBounds( 0, 0, PICK_IMAGE_DIMENSIONS, PICK_IMAGE_DIMENSIONS);
        mViewHolder.mExpenseImage.setImageDrawable(img);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(PICK_IMAGE_DIMENSIONS, PICK_IMAGE_DIMENSIONS);
        mViewHolder.mExpenseImage.setLayoutParams(layoutParams);

    }

    /**
     * Interface to communicate with the containing fragment
     */
    public interface ListItemHandler
    {
        public void removeFromFragmentList(View view);
        public void showItem(View view);
    }

    /**
     * View holder for list viewing optimization
     */
    public static class ViewHolder
    {
        public TextView mExpenseDescription;
        public LinearLayout mItemLayout;
        public LinearLayout mRemoveItemLayout;
        public TextView mExpenseValue;
        public TextView mExpenseCurrency;
        public TextView mExpenseNote;
        public TextView mExpenseDayOfMonth;
        public TextView mExpenseMonth;
        public Button mRemoveItemButton;
        public ImageView mExpenseImage;
    }
}