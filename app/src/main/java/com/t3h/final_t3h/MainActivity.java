package com.t3h.final_t3h;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.t3h.common.GlobalApplication;
import com.t3h.message_offline.ActivityNewMessage;
import com.t3h.common.CommonValue;
import com.t3h.message_offline.DatabaseManager;
import com.t3h.message_offline.ListMessageOfANumberActivity;
import com.t3h.message_offline.MessageAdapter;

public class MainActivity extends Activity implements View.OnClickListener {
    private DrawerLayout mDrlMenu;
    private ListView mLvMessage;
    private ImageView mIvMenu;
    private ImageView mIvAvata;
    private ImageView mIvPlus;
    private TextView mTvName;
    private TextView mTvEmail;
    private TableRow mTrCallLog;
    private TableRow mTrSetting;
    private TableRow mTrAboutUs;
    private TableRow mTrMyAcount;
    private TableRow mTrLogOut;
    private TableRow mTrMessageOnline;

    private DatabaseManager mDatabaseSMS;
    private MessageAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById();
    }

    private void findViewById() {
        mDrlMenu = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLvMessage = (ListView) findViewById(R.id.lv_message);
        mIvMenu = (ImageView) findViewById(R.id.iv_menu);
        mIvPlus = (ImageView) findViewById(R.id.iv_plus);
        mIvAvata = (ImageView) findViewById(R.id.iv_avatar);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvEmail = (TextView) findViewById(R.id.tv_email);
        mTrMessageOnline = (TableRow) findViewById(R.id.tr_message_online);
        mTrCallLog = (TableRow) findViewById(R.id.tr_call_logs);
        mTrAboutUs = (TableRow) findViewById(R.id.tr_about_us);
        mTrMyAcount = (TableRow) findViewById(R.id.tr_acount);
        mTrSetting = (TableRow) findViewById(R.id.tr_setting);
        mTrLogOut = (TableRow) findViewById(R.id.tr_power);
        mMessageAdapter = new MessageAdapter(this);
        mDatabaseSMS = new DatabaseManager(this);
        mDatabaseSMS.getThreadID();
        mDatabaseSMS.showInfoAddress();
        mMessageAdapter.setmArrayMessages(mDatabaseSMS.getmArrayAddress());
        mLvMessage.setAdapter(mMessageAdapter);
        mTrMessageOnline.setOnClickListener(this);
        mTrCallLog.setOnClickListener(this);
        mTrMyAcount.setOnClickListener(this);
        mTrAboutUs.setOnClickListener(this);
        mTrSetting.setOnClickListener(this);
        mTrLogOut.setOnClickListener(this);
        mIvPlus.setOnClickListener(this);
        mIvMenu.setOnClickListener(this);
        mLvMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivityListMessage(position);
            }
        });
    }

    private void startActivityListMessage(int position) {
        Intent intent = new Intent(MainActivity.this, ListMessageOfANumberActivity.class);
        String id = mDatabaseSMS.getmArrayAddress().get(position).getmId();
        String name = mDatabaseSMS.getmArrayAddress().get(position).getmAddress();
        intent.putExtra(CommonValue.KEY_ID, id);
        intent.putExtra(CommonValue.NAME_CONTACT, name);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                mDrlMenu.openDrawer(Gravity.LEFT);
                break;
            case R.id.iv_plus:
                startActivityNewMessage();
                break;
            case R.id.tr_message_online:
                startActivityHomeOnline();
                break;
            case R.id.tr_call_logs:
                startActivityCallLogs();
                break;
            case R.id.tr_setting:
                startActivitySetting();
                break;
            case R.id.tr_about_us:
                startActivityAboutUs();
                break;
            case R.id.tr_acount:
                startActivityInformationMyAccount();
                break;
            case R.id.tr_power:
                //click menu power
                break;
        }
    }

    private void startActivityInformationMyAccount() {

    }

    private void startActivityAboutUs() {
    }

    private void startActivitySetting() {

    }

    private void startActivityCallLogs() {

    }

    private void startActivityHomeOnline() {
        if (isNetworkConnected(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null) {
                        GlobalApplication.startWaitingMMC = true;
                        GlobalApplication.startActivityMessage = false;
                        GlobalApplication.checkLoginThisId = true;
                        currentUser.put("isOnline", true);
                        currentUser.saveInBackground();
                        Intent intent = new Intent(MainActivity.this, MainOnlineActivity.class);
                        MainActivity.this.startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, HomeOnlineActivity.class);
                        MainActivity.this.startActivity(intent);
                    }
                    MainActivity.this.finish();
                }
            }, 1000);

        }else{
            Toast.makeText(this,"Please enable network!",Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo == null ? false : true;
    }

    private void startActivityNewMessage() {
        Intent intent = new Intent(this, ActivityNewMessage.class);
        startActivity(intent);
    }
}
