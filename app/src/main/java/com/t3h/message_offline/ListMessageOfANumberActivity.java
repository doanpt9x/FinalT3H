package com.t3h.message_offline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.t3h.common.CommonValue;
import com.t3h.final_t3h.R;

import java.util.ArrayList;

/**
 * Created by Android on 1/1/2016.
 */
public class ListMessageOfANumberActivity  extends Activity{
    private ListView lvMessage;
    private ListMessageANumberAdapter adapter;
    private ArrayList<ItemMessage> arrMessage;
    private DatabaseManager database;
    private String id="";
    private String name="";
    private TextView tvName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_message_a_number);
        Intent intent=getIntent();
        id=intent.getStringExtra(CommonValue.KEY_ID);
        name=intent.getStringExtra(CommonValue.NAME_CONTACT);
        initView();
    }

    private void initView() {
        database=new DatabaseManager(ListMessageOfANumberActivity.this);
        lvMessage= (ListView) findViewById(R.id.lv_list_message);
        arrMessage=database.getAllMessageANumber(id);
        adapter=new ListMessageANumberAdapter(ListMessageOfANumberActivity.this,arrMessage);
        lvMessage.setAdapter(adapter);
        tvName= (TextView) findViewById(R.id.tv_name_contact);
        tvName.setText(name);
    }
}
