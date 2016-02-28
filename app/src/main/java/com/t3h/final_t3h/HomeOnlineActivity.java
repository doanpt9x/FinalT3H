package com.t3h.final_t3h;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.t3h.parses.LoginActivity;
import com.t3h.parses.SignUpActivity;

/**
 * Created by Android on 1/3/2016.
 * Màn hình cho lựa chọn đăng ký hoặc đăng nhập
 */
public class HomeOnlineActivity extends Activity implements View.OnClickListener{
    private Button btnLogin, btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_online);
        initializeComponent();
    }
    private void initializeComponent() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                startActivityLogin();
                break;
            case R.id.btn_sign_up:
                startActivitySignUp();
                break;
        }
    }

    private void startActivitySignUp() {
        Intent intent=new Intent(HomeOnlineActivity.this,SignUpActivity.class);
        startActivity(intent);
    }

    private void startActivityLogin() {
        Intent intent=new Intent(HomeOnlineActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
