package com.t3h.final_t3h;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.t3h.common.GlobalApplication;

/**
 * Created by Android on 1/24/2016.
 */
public class AdditionFriend extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_ADDITION_FRIEND = 0;

    private EditText edtPhoneNumber;
    private Button btnAddFriend;
    private String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_add_friend);
        this.initView();
    }

    private void initView() {
        btnAddFriend = (Button) findViewById(R.id.btnAddFriend);
        btnAddFriend.setOnClickListener(this);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    btnAddFriend.setEnabled(true);
                } else {
                    btnAddFriend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddFriend:
                phoneNumber = edtPhoneNumber.getText().toString().trim();
                ParseUser user=ParseUser.getCurrentUser();
                if (phoneNumber.equals(user.getUsername().toString())) {
                    Toast.makeText(this, "You can not make friends with yourself", Toast.LENGTH_SHORT).show();
                    return;
                }
                this.finish();
                break;
        }
    }
    @Override
    public void finish() {
        if (phoneNumber == null) {
            this.setResult(Activity.RESULT_OK);
        } else {
            Intent intent = new Intent();
            intent.putExtra("PHONE_NUMBER", phoneNumber);
            this.setResult(Activity.RESULT_OK, intent);
        }
        super.finish();
    }

}
