package com.t3h.message_offline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by NGO VAN TUAN on 28/12/2015.
 */
public class MessageBroadCast extends BroadcastReceiver {
    private String smsBody = "";
    private String address = "";
    public static final String SMS_EXTRA_NAME = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

        }

    }

    public void getBodySMS(Intent intent, Context context) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_EXTRA_NAME);

            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                smsBody = smsMessage.getMessageBody().toString();
                address = smsMessage.getOriginatingAddress();


            }
            // Log.i(TAG, smsBody);
        }
    }
}
