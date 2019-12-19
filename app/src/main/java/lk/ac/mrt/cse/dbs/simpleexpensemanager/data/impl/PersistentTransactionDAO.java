package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private DBHelper dbHelper;


    public PersistentTransactionDAO(Context context){
        dbHelper = new DBHelper(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(Transaction.COLUMN_DATE, date.toString());
        values.put(Transaction.COLUMN_ACCOUNTNO, accountNo);
        values.put(Transaction.COLUMN_EXPENSETYPE, expenseType.toString());
        values.put(Transaction.COLUMN_AMOUNT, amount);
        db.insert(Transaction.TABLE_NAME, null, values);
    }


    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        List<Transaction> TransactionList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * from " + Transaction.TABLE_NAME , null);

        if (cursor.moveToFirst()) {
            do {
                TransactionList.add(new Transaction(
                        new Date(cursor.getLong(0)),
                        cursor.getString(1),
                        Enum.valueOf(ExpenseType.class, cursor.getString(2)),
                        cursor.getDouble(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return TransactionList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();



        List<Transaction> TransactionList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * from " + Transaction.TABLE_NAME +" LIMIT "+limit, null);

        if (cursor.moveToFirst()) {
            do {
                TransactionList.add(new Transaction(
                        new Date(cursor.getLong(0)),
                        cursor.getString(1),
                        Enum.valueOf(ExpenseType.class, cursor.getString(2)),
                        cursor.getDouble(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return TransactionList;
    }
}
