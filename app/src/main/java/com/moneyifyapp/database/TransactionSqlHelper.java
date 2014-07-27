package com.moneyifyapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.moneyifyapp.model.Transaction;

import java.util.LinkedList;
import java.util.List;

/**
 */
public class TransactionSqlHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TRANSACTION_DB";
    private static final String TRANSACTION_TABLE = "DATABASE_NAME";
    private static final String CODE_KEY = "code";
    private static final String CURRENCY_KEY = "currency";
    private static final String DESCRIPTION_KEY = "description";
    private static final String VALUE_KEY = "value";
    private static final String IMAGE_KEY = "image";
    private static final String NOTE_KEY = "note";
    private static final String DAY_KEY = "day";
    private static final String MONTH_KEY = "month";
    private static final String TYPE_KEY = "type";
    private static final String YEAR_KEY = "year";
    private static final String[] COLUMNS = {CODE_KEY, CURRENCY_KEY, DESCRIPTION_KEY, VALUE_KEY, IMAGE_KEY,
            NOTE_KEY, DAY_KEY, MONTH_KEY, TYPE_KEY, YEAR_KEY};

    /**
     */
    public TransactionSqlHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // SQL statement to create book table
        String CREATE_EXPENSE_TABLE = "CREATE TABLE " + TRANSACTION_TABLE + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT, " +
                "currency TEXT, " +
                "description TEXT, " +
                "value TEXT, " +
                "image INTEGER, " +
                "note TEXT, " +
                "day TEXT, " +
                "month INTEGER, " +
                "type BOOLEAN, " +
                "year INTEGER)";
        // create books table
        db.execSQL(CREATE_EXPENSE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS books");
        // create fresh books table
        this.onCreate(db);

    }

    /**
     */
    public void addBook(Transaction transaction, int month, int year)
    {
        // get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CODE_KEY, transaction.mId);
        values.put(CURRENCY_KEY, transaction.mCurrency);
        values.put(DESCRIPTION_KEY, transaction.mDescription);
        values.put(VALUE_KEY, transaction.mValue);
        values.put(IMAGE_KEY, transaction.mImageResourceIndex);
        values.put(NOTE_KEY, transaction.mNotes);
        values.put(DAY_KEY, transaction.mTransactionDay);
        values.put(MONTH_KEY, month);
        values.put(TYPE_KEY, transaction.mIsExpense);
        values.put(YEAR_KEY, year);


        db.insert(TRANSACTION_TABLE,
                null,
                values);

        db.close();
    }

    /**
     */
    public Transaction getBook(String id)
    {
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TRANSACTION_TABLE, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        String currency = cursor.getString(1);
        String description = cursor.getString(2);
        String values = cursor.getString(3);
        int image = Integer.valueOf(cursor.getString(4));
        String notes = cursor.getString(5);
        String day = cursor.getString(6);
        boolean type = Boolean.valueOf(cursor.getString(8));
        Transaction transaction = new Transaction(id, description, values, currency, notes, image, type, day);

        // 5. return book
        return transaction;
    }

    /**
     */
    public List<Transaction> getAllTransactions()
    {
        List<Transaction> transactions = new LinkedList<Transaction>();

        // 1. build the query
        String query = "SELECT  * FROM " + TRANSACTION_TABLE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Transaction transaction = null;
        if (cursor.moveToFirst())
        {
            do
            {
                String code = cursor.getString(0);
                String currency = cursor.getString(1);
                String description = cursor.getString(2);
                String values = cursor.getString(3);
                int image = Integer.valueOf(cursor.getString(4));
                String notes = cursor.getString(5);
                String day = cursor.getString(6);
                boolean type = Boolean.valueOf(cursor.getString(8));
                transaction = new Transaction(code, description, values, currency, notes, image, type, day);

                // Add book to books
                transactions.add(transaction);
            }
            while (cursor.moveToNext());
        }

        // return books
        return transactions;
    }

    /**
     */
    public void deleteBook(Transaction book) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TRANSACTION_TABLE, //table name
                CODE_KEY + " = ?",  // selections
                new String[] { String.valueOf(book.mId) }); //selections args

        db.close();
    }
}
