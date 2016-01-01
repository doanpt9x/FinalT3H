package com.t3h.messageofline;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.provider.ContactsContract.PhoneLookup;

/**
 * Created by NGO VAN TUAN on 25/11/2015.
 */
public class DatabaseManager {


    private SQLiteDatabase mSQLdata;
    private static final String DATA_BASE_PATH = Environment.getDataDirectory().getPath() + "/data/com.t3h.final_t3h/databases";
    private static final String DATA_MESSAGE = "MessageOffline.sqlite";


    private static final String TAG = "DatabaseManager";
    private static final String COLUMN_BODY = "body";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_NAME = "x_name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_SNIPPET = "snippet";
    public static final String COLUMN_READ = "read";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_MESSAGE_COUNT = "message_count";
    private Context context;

    private ArrayList<ItemMessage> mArrayAddress = new ArrayList<>();

    public DatabaseManager(Context context) {
        this.context = context;
        copyDatabase();
    }

    public ArrayList<ItemMessage> getmArrayAddress() {
        return mArrayAddress;
    }

    private void copyDatabase() {
        new File(DATA_BASE_PATH).mkdir();
        File file = new File(DATA_BASE_PATH + "/" + DATA_MESSAGE);
        if (file.exists()) {
            Log.i(TAG, "File da ton tai");
            return;
        }
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


    }

    private void openDatabase() {
        if (mSQLdata == null || mSQLdata.isOpen() == false) {
            mSQLdata = SQLiteDatabase.openDatabase(DATA_BASE_PATH + "/" + DATA_MESSAGE, null, SQLiteDatabase.OPEN_READWRITE);

        }
    }

    public void showInfoAddress() {
        Log.i(TAG, "size:" + mArrayAddress.size());
        for (int i = 0; i < mArrayAddress.size(); i++) {
            Log.i(TAG, "id:" + mArrayAddress.get(i).getmId() + " address: " + mArrayAddress.get(i).getmAddress() + " name:" + mArrayAddress.get(i).getName()
            );
        }
    }

    private void closeDatabase() {
        if (mSQLdata != null || mSQLdata.isOpen()) {
            mSQLdata.close();
        }
    }

    public String getAddress(String id) {
        String address = "";
        Uri message = Uri.parse("content://sms");
        ContentResolver cr = context.getContentResolver();
        String[] columns = {"*"};
        String where = "thread_id=?";
        Cursor c = cr.query(message, columns, where,new String[]{id}, null, null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            address = c.getString(c.getColumnIndex(COLUMN_ADDRESS));
            break;
        }
        c.close();
        return address;
    }

    private String retrieveContactName(String phoneNumber) {
        String contactName;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor people = context.getContentResolver().query(uri, projection, null, null, null);
        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        contactName = phoneNumber;
        people.moveToFirst();
        do {
            String name = people.getString(indexName);
            String number = people.getString(indexNumber);
            if (number.equals(phoneNumber)) {
                contactName = name;
                break;
            }
        } while (people.moveToNext());
        return contactName;

    }

    public void getThreadID() {
        Uri message = Uri.parse("content://mms-sms/threads");
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(message, new String[]{"*"}, null, null, null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            String id = c.getString(c.getColumnIndex(COLUMN_ID));
            String body = c.getString(c.getColumnIndex(COLUMN_SNIPPET));
            String numberMessage = c.getString(c.getColumnIndex(COLUMN_MESSAGE_COUNT));
            String date=fomatTime(c.getString(c.getColumnIndex(COLUMN_DATE)));
            String address = getAddress(id);
            if (address != null) {
                if(address.length()>8){
                    String mavung = address.substring(0, 1);
                    if (mavung.equals("+") && address.length() > 6) {
                        address = "0" + address.substring(3);
                    }
                }
            } else {
                address="Không lấy được.";
            }
            String namecontact = retrieveContactName(address);
            mArrayAddress.add(new ItemMessage(id, namecontact, body, date, numberMessage));
            c.moveToNext();
        }
        c.close();
    }

    public String fomatTime(String time) {
        long dateLong = Long.parseLong(time + "");
        Date dateModified = new Date(dateLong);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDateString = formatter.format(dateModified);
        return formattedDateString;
    }
    public String fomatTimeListSMS(String time) {
        long dateLong = Long.parseLong(time + "");
        Date dateModified = new Date(dateLong);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:SS");
        String formattedDateString = formatter.format(dateModified);
        return formattedDateString;
    }
    public ArrayList<ItemMessage> getAllMessageANumber(String id) {
        mArrayAddress.clear();
        Uri message = Uri.parse("content://sms");
        ContentResolver cr = context.getContentResolver();
        String[] columns = {"*"};
        String where = "thread_id=?";
        Cursor c = cr.query(message, columns, where,new String[]{id}, null, null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            String type = c.getString(c.getColumnIndex(COLUMN_TYPE));
            String body = c.getString(c.getColumnIndex(COLUMN_BODY));
            String date=fomatTimeListSMS(c.getString(c.getColumnIndex(COLUMN_DATE)));
            mArrayAddress.add(new ItemMessage(type, body, date));
            c.moveToNext();
        }
        c.close();
        return mArrayAddress;
    }
}