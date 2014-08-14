package com.moneyifyapp.model;

import com.moneyifyapp.R;

import java.util.Calendar;

/**
 * This is the main modeling for a transaction
 */
public class Transaction implements Comparable<Transaction>
{
    public String mId;
    public String mDescription;
    public String mValue;
    public String mCurrency;
    public int mImageResourceIndex;
    public String mNotes;
    public boolean mIsExpense;
    public String mTransactionDay;
    public boolean mSaved;
    public REPEAT_TYPE mRepeatType;
    public boolean mOriginal;
    public static final String TRANS_JSON = "transactionJson";
    public static final String CLASS_NAME = "expense";
    public static final String CLASS_USER = "User";
    public static final String KEY_ID = "code";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_VALUE = "value";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_IMAGE_NAME = "image";
    public static final String KEY_NOTES = "note";
    public static final String KEY_TYPE = "type";
    public static final String CURRENCY_DEFAULT = "₪";
    public static final String DEFUALT_TRANSCATION_ID = "0";

    /**
     */
    private Transaction(TransactionBuilder builder)
    {
        this.mId = builder.mId;
        this.mDescription = builder.mDescription;
        this.mValue = builder.mValue;
        this.mCurrency = builder.mCurrency;
        this.mImageResourceIndex = builder.mImageResourceIndex;
        this.mNotes = builder.mNotes;
        this.mIsExpense = builder.mIsExpense;
        this.mTransactionDay = builder.mTransactionDay;
        this.mSaved = builder.mSaved;
        this.mRepeatType = builder.mRepeatType;
        this.mOriginal = builder.mOriginal;
    }

    /**
     */
    public Transaction repeatTransaction(Transaction original)
    {
        TransactionBuilder builder = new TransactionBuilder();
        builder.setId(original.mId).setDescription(original.mDescription).setValue(original.mValue)
                .setCurrency(original.mCurrency).setImageResourceId(original.mImageResourceIndex)
                .setNotes(original.mNotes).setIsExpense(original.mIsExpense).setDay(original.mTransactionDay)
                .setIsSaved(original.mSaved).setRepeatType(original.mRepeatType).setOriginal(false);

        return builder.build();
    }

    /**
     *
     */
    public Transaction(String id, String description, String value,
                       String currency, String note, int imageName, boolean isExpense)
    {
        this.mId = id;
        this.mDescription = description;
        this.mValue = value;
        this.mCurrency = currency;
        this.mImageResourceIndex = imageName;
        this.mNotes = note;
        this.mIsExpense = isExpense;
        this.mTransactionDay = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        this.mSaved = false;
        this.mRepeatType = REPEAT_TYPE.NONE;
        this.mOriginal = true;
    }

    /**
     *
     */
    public Transaction(String id, String description, String value,
                       String currency, String note, int imageName, boolean isExpense,
                       String day)
    {
        this(id, description, value, currency, note, imageName, isExpense);
        this.mTransactionDay = day;
    }

    /**
     *
     */
    public Transaction(String id)
    {
        this(id, "", "", "", "", Images.getDefaultImage(), true);
        this.mTransactionDay = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        this.mSaved = false;
        this.mRepeatType = REPEAT_TYPE.NONE;
    }

    /**
     *
     */
    @Override
    public String toString()
    {
        return "Blaa";
    }

    /**
     *
     */
    @Override
    public int compareTo(Transaction another)
    {
        double myValue = Double.valueOf(this.mValue);
        double anotherValue = Double.valueOf(another.mValue);

        if(myValue < anotherValue)
            return -1;
        else if(myValue > anotherValue)
            return 1;
        else
            return 0;
    }

    /**
     *
     */
    public enum REPEAT_TYPE
    {
        NONE, DAILY, WEEKLY, MONTHLY, YEARLY;

        public static REPEAT_TYPE getTypeById(int resourceId)
        {
            switch (resourceId)
            {
                case R.id.detail_reoccur_monthly_radio:return MONTHLY;
                case R.id.detail_reoccur_yearly_radio:return YEARLY;
                default:return NONE;
            }
        }
    };

    /**
     * A builder for the transaction class
     */
    public static class TransactionBuilder
    {
        public String mId;
        public String mDescription;
        public String mValue;
        public String mCurrency;
        public int mImageResourceIndex;
        public String mNotes;
        public boolean mIsExpense;
        public String mTransactionDay;
        public boolean mSaved;
        public REPEAT_TYPE mRepeatType;
        public boolean mOriginal;

        /**
         * @return a new transaction object
         */
        public Transaction build()
        {
            return new Transaction(this);
        }

        public TransactionBuilder setId(String id){mId = id; return this;}
        public TransactionBuilder setDescription(String desc){mDescription = desc; return this;}
        public TransactionBuilder setValue(String value){mValue = value; return this;}
        public TransactionBuilder setCurrency(String currency){mCurrency = currency; return this;}
        public TransactionBuilder setImageResourceId(int imageResrouceId){mImageResourceIndex = imageResrouceId; return this;}
        public TransactionBuilder setNotes(String notes){mNotes = notes; return this;}
        public TransactionBuilder setIsExpense(boolean isExpense){mIsExpense = isExpense; return this;}
        public TransactionBuilder setDay(String day){mTransactionDay = day; return this;}
        public TransactionBuilder setIsSaved(boolean isSaved){mSaved = isSaved; return this;}
        public TransactionBuilder setRepeatType(REPEAT_TYPE repeatType){mRepeatType = repeatType; return this;}
        public TransactionBuilder setOriginal(boolean original){mOriginal = original; return this;}

    }
}
