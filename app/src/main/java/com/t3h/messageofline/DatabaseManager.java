package com.t3h.messageofline;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by NGO VAN TUAN on 25/11/2015.
 */
public class DatabaseManager {
    public static final String[] KEY_USER = new String[]{"content", "time", "number"};
    public static final String SQL_RECEIVED = "select * from SMS_RECEIVED order by time desc";
    public static final String SQL_SENT = "select * from SMS_SENT order by time desc";


    private SQLiteDatabase mSQLdata;
    private static final String DATA_BASE_PATH = Environment.getDataDirectory().getPath() + "/data/com.t3h.final_t3h/databases";
    private static final String DATA_MESSAGE = "MessageOffline.sqlite";
    public static final String TABLE_SMS_RECEIVED = "SMS_RECEIVED";
    public static final String TABLE_SMS_SENT = "SMS_SENT";
    private static final String TAG = "DatabaseManager";
    private Context context;

    public DatabaseManager(Context context) {
        this.context = context;
        copyDatabase();
    }

    private void copyDatabase() {
        new File(DATA_BASE_PATH).mkdir();
        File file = new File(DATA_BASE_PATH + "/" + DATA_MESSAGE);
        if (file.exists()) {
            Log.i(TAG, "File da ton tai");
            getSentMessage();
            getReceiveMessage();
            return;
        } else {
            try {
                file.createNewFile();
                InputStream input = context.getAssets().open(DATA_MESSAGE);
                FileOutputStream output = new FileOutputStream(file);
                int len;
                byte buff[] = new byte[1024];
                while ((len = input.read(buff)) > 0) {
                    output.write(buff, 0, len);
                }
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getAllSMS();
        }
    }

    private void openDatabase() {
        if (mSQLdata == null || mSQLdata.isOpen() == false) {
            mSQLdata = SQLiteDatabase.openDatabase(DATA_BASE_PATH + "/" + DATA_MESSAGE, null, SQLiteDatabase.OPEN_READWRITE);

        }
    }

    private void closeDatabase() {
        if (mSQLdata != null || mSQLdata.isOpen()) {
            mSQLdata.close();
        }
    }

    public boolean insertContact(String nameOfTable, String[] key, String[] values) {
        openDatabase();
        ContentValues content = new ContentValues();
        for (int i = 0; i < key.length; i++) {
            content.put(key[i], values[i]);
        }
        long valueReturn = mSQLdata.insert(nameOfTable, null, content);
        if (valueReturn == -1) {
            return false;
        }
        closeDatabase();
        return true;

    }

    public void getAllSMS() {
        File file = new File(DATA_BASE_PATH + "/" + DATA_MESSAGE);
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(message, null, null, null, null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            String body = c.getString(c.getColumnIndex("body"));
            String address = c.getString(c.getColumnIndex("address"));
            String time = c.getString(c.getColumnIndex("date"));
            String type = c.getString(c.getColumnIndex("type"));
            //Log.i(TAG, "body: " + body);
            if (type.equals("1")) {
                // Log.i(TAG, "body: " + fomatTime(time));
                insertContact(TABLE_SMS_RECEIVED, KEY_USER, new String[]{"" + body, "" + fomatTime(time), "" + address});
            } else {
                insertContact(TABLE_SMS_SENT, KEY_USER, new String[]{"" + body, "" + fomatTime(time), "" + address});
            }
            c.moveToNext();
        }
        c.close();
        getSentMessage();
        getReceiveMessage();
    }

    public void getReceiveMessage() {
        openDatabase();
        Cursor cursor = mSQLdata.rawQuery(SQL_RECEIVED, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                String body = cursor.getString(cursor.getColumnIndex("content"));
                String address = cursor.getString(cursor.getColumnIndex("number"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                // Log.i(TAG, "time: " + time);

                cursor.moveToNext();
            }
        } else {
            Toast.makeText(context, "No data select", Toast.LENGTH_SHORT).show();
        }
    }

    public void getSentMessage() {
        openDatabase();
        Cursor cursor = mSQLdata.rawQuery(SQL_SENT, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                String body = cursor.getString(cursor.getColumnIndex("content"));
                String address = cursor.getString(cursor.getColumnIndex("number"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                // Log.i(TAG, "body: " + body);

                cursor.moveToNext();
            }
        } else {
            Toast.makeText(context, "No data select", Toast.LENGTH_SHORT).show();
        }
    }

    public String fomatTime(String time) {
        long dateLong = Long.parseLong(time + "");
        Date dateModified = new Date(dateLong);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy" + " | " + "HH:mm");
        String formattedDateString = formatter.format(dateModified);

        return formattedDateString;
    }
}

