package com.t3h.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MMCBroadcastComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentStartService = new Intent();
        intentStartService.setClassName(CommonValue.PACKAGE_NAME_MAIN,
                CommonValue.PACKAGE_NAME_COMMON + ".MMCClientService");
        context.startService(intentStartService);
    }

}