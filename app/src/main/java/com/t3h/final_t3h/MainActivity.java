package com.t3h.final_t3h;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.t3h.messageofline.ActivityNewMessage;
import com.t3h.messageofline.DatabaseManager;
import com.t3h.messageofline.MessageAdapter;

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                mDrlMenu.openDrawer(Gravity.LEFT);

                break;
            case R.id.iv_plus:
                Intent intent = new Intent(this, ActivityNewMessage.class);
                startActivity(intent);

                break;
            case R.id.tr_message_online:
                //event click Message Online into Menu

                break;
            case R.id.tr_call_logs:
                //event click Call logs into Menu

                break;
            case R.id.tr_setting:
                //event click settings into Menu

                break;
            case R.id.tr_about_us:
                //event click about us into Menu

                break;
            case R.id.tr_acount:
                //event click my acount into Menu

                break;
            case R.id.tr_power:
                //event click log out into Menu
                break;
        }
    }


}
