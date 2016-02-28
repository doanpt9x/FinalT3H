package com.t3h.parses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.t3h.final_t3h.R;

/**
 * Created by Android on 1/3/2016.
 */
public class SignUpActivity extends Activity implements View.OnClickListener{
    private EditText edtPhoneNumber, edtPassword, edtConfirmPassword;
    private FloatingActionButton btnLogIn;
    private Button btnSignUp;
    private CheckBox checkBoxAgree;
    private boolean isFillPhoneNumber, isFillPassword, isFillConfirmPassword, isCheckBoxChecked;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initializeComponent();
    }
    private void initializeComponent() {
        btnSignUp = (Button) findViewById(R.id.btn_sign_up_sign);
        btnSignUp.setOnClickListener(this);
        btnLogIn = (FloatingActionButton) findViewById(R.id.btn_login_sign_up);
        btnLogIn.setOnClickListener(this);
        edtPhoneNumber = (EditText) findViewById(R.id.edt_phone_number);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtConfirmPassword = (EditText) findViewById(R.id.edt_confirm);
        checkBoxAgree = (CheckBox) findViewById(R.id.ck_term_condition);
        checkBoxAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBoxAgree.isChecked()) {
                    isCheckBoxChecked = true;
                    enabledButtonSignUp();
                    checkBoxAgree.setTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.green_500));
                } else {
                    isCheckBoxChecked = false;
                    btnSignUp.setEnabled(false);
                    checkBoxAgree.setTextColor(Color.parseColor("#666666"));
                }
            }
        });
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillPhoneNumber = true;
                    enabledButtonSignUp();
                } else {
                    isFillPhoneNumber = false;
                    btnSignUp.setEnabled(false);
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
                    enabledButtonSignUp();
                } else {
                    isFillPassword = false;
                    enabledButtonSignUp();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtPassword.getText().toString().length() > 0
                        && edtConfirmPassword.getText().toString().length() > 0) {
                    if (!edtPassword.getText().toString()
                            .equals(edtConfirmPassword.getText().toString())) {
                        edtPassword.setError("'Password' and 'Confirm Password' do not match");
                    } else {
                        edtPassword.setError(null);
                        edtConfirmPassword.setError(null);
                    }
                } else {
                    edtPassword.setError(null);
                    edtConfirmPassword.setError(null);
                }
            }
        });
        edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillConfirmPassword = true;
                    enabledButtonSignUp();
                } else {
                    isFillConfirmPassword = false;
                    enabledButtonSignUp();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtPassword.getText().toString().length() > 0
                        && edtConfirmPassword.getText().toString().length() > 0) {
                    if (!edtConfirmPassword.getText().toString()
                            .equals(edtPassword.getText().toString())) {
                        edtConfirmPassword.setError("'Confirm Password' and 'Password' do not match");
                    } else {
                        edtConfirmPassword.setError(null);
                        edtPassword.setError(null);
                    }
                } else {
                    edtConfirmPassword.setError(null);
                    edtPassword.setError(null);
                }
            }
        });
    }

    private void enabledButtonSignUp() {
        if (isFillPhoneNumber && isFillPassword && isFillConfirmPassword && isCheckBoxChecked
                && edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
            btnSignUp.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up_sign:
                signUp(v);
                break;
            case R.id.btn_login_sign_up:
                showLogin();
                break;
        }
    }

    private void showLogin() {
        Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void signUp(final View v) {
        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Signing up");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final ParseUser newUser = new ParseUser();
        phoneNumber = edtPhoneNumber.getText().toString().trim();
        newUser.setUsername(phoneNumber);
        newUser.setPassword(edtPassword.getText().toString().trim());
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    newUser.put("isOnline", true);
                    newUser.saveInBackground();
                    progressDialog.dismiss();
                    btnLogIn.setBackgroundTintList(ColorStateList
                            .valueOf(Color.parseColor("#e91e63")));
                    btnLogIn.setImageResource(R.drawable.ic_checkmark);
                    Snackbar snackbar = Snackbar.make(v, "Registered successfully",
                            Snackbar.LENGTH_LONG)
                            .setAction("ACTION", null)
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    super.onDismissed(snackbar, event);
                                    showProfileInformation();
                                }
                            });
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#4caf50"));
                    snackbar.show();
                } else {
                    progressDialog.dismiss();
                    Snackbar.make(v, "There was an error signing up", Snackbar.LENGTH_LONG)
                            .setAction("ACTION", null)
                            .show();
                }
            }
        });
    }

    private void showProfileInformation() {
        Intent intent=new Intent(SignUpActivity.this,ProfileInformationActivity.class);
        startActivity(intent);
    }
}
