package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentAccountDAO implements AccountDAO {

    private DBHelper  dbHelper;


    public PersistentAccountDAO(Context context) {
        dbHelper = new DBHelper(context);
    }


    @Override
    public List<String> getAccountNumbersList() {

        SQLiteDatabase db =  dbHelper.getWritableDatabase();

        List<String> AccountNumbers = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT " +Account.COLUMN_ACOOUNTNO + " from "
                + Account.TABLE_NAME , null);
        ArrayList<String> accountNumbers = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            AccountNumbers.add(cursor.getString(0));
        }

        cursor.close();
        return AccountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {

        SQLiteDatabase db =  dbHelper.getWritableDatabase();

        List<Account> AccountList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * from " + Account.TABLE_NAME, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Account account = new Account(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getDouble(3));
            AccountList.add(account);
        }
        cursor.close();
        return AccountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db =  dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + Account.TABLE_NAME + " WHERE " + Account.COLUMN_ACOOUNTNO + "=?;", new String[]{accountNo});
        Account account;
        if (cursor.moveToFirst()) {
            account = new Account(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getDouble(3));
        } else {
            throw new InvalidAccountException("Invalid account Number");
        }
        cursor.close();

        return account;

    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db =  dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Account.COLUMN_ACOOUNTNO, account.getAccountNo());
        values.put(Account.COLUMN_BANKNAME, account.getBankName());
        values.put(Account.COLUMN_HOLDERNAME, account.getAccountHolderName());
        values.put(Account.COLUMN_BALANCE, account.getBalance());

        db.insert(Account.TABLE_NAME, null, values);


    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db =  dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + Account.TABLE_NAME + " WHERE " + Account.COLUMN_ACOOUNTNO + "=?;", new String[]{accountNo});
        if (cursor.moveToFirst()) {
            db.delete(Account.TABLE_NAME, Account.COLUMN_ACOOUNTNO + " = ?", new String[]{accountNo});
        } else {
            throw new InvalidAccountException("Invalid account Number");
        }
        cursor.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        getAccount(accountNo);
        SQLiteDatabase db =  dbHelper.getWritableDatabase();

        String query = null;
        switch (expenseType){
            case EXPENSE:
                query = "UPDATE ACCOUNT SET "+Account.COLUMN_BALANCE+" = "+Account.COLUMN_BALANCE+" - ? WHERE "+Account.COLUMN_ACOOUNTNO+" = ?";
            case INCOME:
                query = "UPDATE ACCOUNT SET "+Account.COLUMN_BALANCE+" = "+Account.COLUMN_BALANCE+" + ? WHERE "+Account.COLUMN_ACOOUNTNO+" = ?";
        }

//        query = String.format(query,
//                Account.COLUMN_BALANCE,
//                Account.COLUMN_BALANCE,
//                Account.COLUMN_ACOOUNTNO);
        db.execSQL(query, new Object[]{amount,accountNo});
    }
}
