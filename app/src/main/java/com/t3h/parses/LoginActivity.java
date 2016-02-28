package com.t3h.parses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.t3h.common.GlobalApplication;
import com.t3h.final_t3h.AllFriendItem;
import com.t3h.final_t3h.MainOnlineActivity;
import com.t3h.final_t3h.R;

import java.util.ArrayList;

/**
 * Created by Android on 1/3/2016.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText edtPhoneNumber, edtPassword;
    private Button btnLogin;
    private TextView forgotPassword;
    private CheckBox rememberMe;
    private boolean isFillPhoneNumber, isFillPassword;
    private ProgressDialog progressDialog;
    private Bitmap userAvatar;

    private FloatingActionButton btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeComponent();
    }

    private void initializeComponent() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        btnSignUp = (FloatingActionButton) findViewById(R.id.btn_sign_up_login);
        btnSignUp.setOnClickListener(this);
        rememberMe = (CheckBox) findViewById(R.id.ck_remember);
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rememberMe.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.green_500));
                } else {
                    rememberMe.setTextColor(Color.parseColor("#666666"));
                }
            }
        });
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(this);
        edtPhoneNumber = (EditText) findViewById(R.id.edt_phone_number);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillPhoneNumber = true;
                    enabledButtonLogin();
                } else {
                    isFillPhoneNumber = false;
                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillPassword = true;
                    enabledButtonLogin();
                } else {
                    isFillPassword = false;
                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void enabledButtonLogin() {
        if (isFillPhoneNumber && isFillPassword) {
            btnLogin.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login(v);
                break;
            case R.id.btn_sign_up_login:
                startActivitySignUp();
                break;
        }
    }

    private void startActivitySignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void login(final View v) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        ParseUser.logInInBackground(phoneNumber, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    parseUser.put("isOnline", true);
                    parseUser.saveInBackground();
                    String objectId = parseUser.getObjectId();
                    GlobalApplication globalApplication = (GlobalApplication) LoginActivity.this.getApplication();

//                    if (!globalApplication.getIdUers().contains(objectId)) {
//                        globalApplication.addIdUser(objectId);
//                        GlobalApplication.startWaitingMMC = false;
//                        GlobalApplication.checkLoginThisId = false;
//                        GlobalApplication.startActivityMessage = false;
//                        globalApplication.setFullName((String) parseUser.get("fullName"));
//                        globalApplication.setPhoneNumber(parseUser.getUsername());
//                        globalApplication.setEmail(parseUser.getEmail());
//                        ParseFile parseFile = (ParseFile) parseUser.get("avatar");
//                        if (parseFile != null) {
//                            parseFile.getDataInBackground(new GetDataCallback() {
//                                @Override
//                                public void done(byte[] bytes, ParseException e) {
//                                    if (e == null) {
//                                        userAvatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                                        ((GlobalApplication) getApplication()).setAvatar(userAvatar);
//                                    }
//                                }
//                            });
//                        }
//                    } else {
                    GlobalApplication.startWaitingMMC = false;
                    GlobalApplication.checkLoginThisId = true;
                    GlobalApplication.startActivityMessage = false;
//                    }
                    progressDialog.dismiss();
                    btnSignUp.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00838F")));
                    btnSignUp.setImageResource(R.drawable.ic_checkmark);
                    Snackbar snackbar = Snackbar.make(v, "Logged successfully", Snackbar.LENGTH_LONG)
                            .setAction("ACTION", null)
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    super.onDismissed(snackbar, event);
                                    Intent intent = new Intent(LoginActivity.this, MainOnlineActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#80CBC4"));
                    snackbar.show();
                } else {
                    progressDialog.dismiss();
                    Snackbar.make(v, "Login error. Please try again!", Snackbar.LENGTH_LONG)
                            .setAction("ACTION", null)
                            .show();
                }
            }
        });
    }
}
