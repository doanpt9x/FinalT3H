package com.t3h.messageofline;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.t3h.final_t3h.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by NGO VAN TUAN on 28/12/2015.
 */
public class ActivityNewMessage extends Activity implements View.OnClickListener {
    private static final String TAG = "ActivityNewMessage";
    private ImageView mIvContact;
    private ImageView mIvSend;
    private EditText mEdNumber;
    private EditText mEdMss;

    private static final int KEY_PICK_CONTACT = 100;
    public static final String ACTION_SENT = "ac_action_sent";
    public static final String ACTION_DELTVERY = "ac_action_deltvery";
    private boolean isClickContact = false;

    private SmsManager smsMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message_offline);
        findViewById();

    }

    private void findViewById() {

        mIvSend = (ImageView) findViewById(R.id.iv_send);
        mEdMss = (EditText) findViewById(R.id.ed_mss);
        mIvContact = (ImageView) findViewById(R.id.iv_contact);

        smsMgr = SmsManager.getDefault();

        mEdNumber = (EditText) findViewById(R.id.ed_number);

        mIvSend.setOnClickListener(this);
        mIvContact.setOnClickListener(this);
        mEdNumber.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send:
                if (isClickContact == false) {
                    Log.i(TAG, isClickContact + "");
                    sendSMS(mEdNumber.getText().toString(), mEdMss.getText().toString());
                } else {
                    Log.i(TAG, isClickContact + "else");
                    sendSMS(number, mEdMss.getText().toString());
                }
                finish();
                break;
            case R.id.iv_contact:
                isClickContact = true;
                Intent intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, KEY_PICK_CONTACT);
                break;
            case R.id.ed_number:
                isClickContact = false;
                break;
        }

    }

    public void sendSMS(String address, String body) {
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_SENT), PendingIntent.FLAG_UPDATE_CURRENT);
        // Dky Broadcast de lang nghe ACTION_SENT
        IntentFilter filter = new IntentFilter(ACTION_SENT);
        filter.addAction(ACTION_DELTVERY);
        registerReceiver(myBroadcast, filter);


        smsMgr.sendTextMessage("" + address, null, "" + body, sentPI, sentPI);
    }

    private BroadcastReceiver myBroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_SENT))
                switch (getResultCode()) {
                    case RESULT_OK:
                        getTime();
                        //mDataMgr.insertContact(DatabaseManager.TABLE_SMS_SENT,
                        //      DatabaseManager.KEY_USER,
                        //      new String[]{"" + mEdMss.getText().toString(),
                        //             "" + time, "" + mEdNumber.getText().toString()});
                        Toast.makeText(ActivityNewMessage.this, "gửi thành công",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:

                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:

                        break;

                }
        }
    };
    private String number = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case KEY_PICK_CONTACT:
                if (resultCode == RESULT_OK) {
                    Uri contactData = data.getData();

                    Cursor cursor = managedQuery(contactData, null, null, null,
                            null);
                    String name = "";

                    if (cursor.moveToFirst()) {
                        String id = cursor
                                .getString(cursor
                                        .getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String numberPhone = cursor
                                .getString(cursor
                                        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (numberPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver()
                                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                            null,
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                    + "=" + id, null, null);
                            phones.moveToFirst();
                            number = phones.getString(phones
                                    .getColumnIndex("data1"));
                            name = cursor
                                    .getString(cursor
                                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                            mEdNumber.setText(name + "");

                        }


                    }

                }

                break;

            default:
                break;
        }

    }

    private String time;

    public void getTime() {
        Date dateModified = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy" + " | " + "HH:mm");
        time = formatter.format(dateModified);

    }

}
