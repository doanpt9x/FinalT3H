package com.t3h.parses;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.t3h.common.CommonValue;
import com.t3h.final_t3h.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Android on 1/3/2016.
 */
public class ProfileInformationActivity extends Activity implements View.OnClickListener{
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private EditText edtBirthday, edtFirstName, edtLastName, edtEmail;
    private Button btnOK;
    private RadioButton radioMale, radioFemale;
    private boolean isFillFirstName, isFillLastName, isFillEmail;
    private Pattern pattern;
    private Matcher matcher;
    private boolean gender = true;
    private String phone,password;
    public ProfileInformationActivity() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_information);
        initializeComponent();
    }

    private void initializeComponent() {
        btnOK = (Button) findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(this);
        edtBirthday = (EditText) findViewById(R.id.edt_birthday);
        edtBirthday.setOnClickListener(this);
        edtFirstName = (EditText) findViewById(R.id.edt_first_name);
        edtLastName = (EditText) findViewById(R.id.edt_last_name);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillFirstName = true;
                    enabledButtonOK();
                } else {
                    isFillFirstName = false;
                    btnOK.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillLastName = true;
                    enabledButtonOK();
                } else {
                    isFillLastName = false;
                    btnOK.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 1) {
                    isFillEmail = true;
                    enabledButtonOK();
                } else {
                    isFillEmail = false;
                    btnOK.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!validate(s.toString())) {
                    edtEmail.setError("Email is not valid");
                } else {
                    edtEmail.setError(null);
                }
            }
        });
        radioMale = (RadioButton) findViewById(R.id.rd_male);
        radioFemale = (RadioButton) findViewById(R.id.rd_female);
        radioMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = true;
                    radioMale.setTextColor(Color.parseColor("#4caf50"));
                } else {
                    gender = false;
                    radioMale.setTextColor(Color.parseColor("#666666"));
                }
            }
        });
        radioFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = false;
                    radioFemale.setTextColor(Color.parseColor("#4caf50"));
                } else {
                    gender = true;
                    radioFemale.setTextColor(Color.parseColor("#666666"));
                }
            }
        });
    }

    private void enabledButtonOK() {
        if (isFillFirstName && isFillLastName && isFillEmail) {
            btnOK.setEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edt_birthday:
                showDatePickerDialog();
                break;
            case R.id.btn_ok:
                showProfilePicture();
                break;
        }
    }

    private void showProfilePicture() {
        ParseUser currentUser=ParseUser.getCurrentUser();
        currentUser.put(CommonValue.KEY_BIRTH,edtBirthday.getText().toString());
        currentUser.put(CommonValue.KEY_EMAIL,edtEmail.getText().toString());
        currentUser.put(CommonValue.KEY_FULL_NAME, edtFirstName.getText().toString().trim() + " " + edtLastName.getText().toString().trim());
        currentUser.put(CommonValue.KEY_SEX, gender);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    e.printStackTrace();
                }else{
                    Toast.makeText(ProfileInformationActivity.this,"Next step!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Intent intent=new Intent(ProfileInformationActivity.this,ProfilePictureActivity.class);
        startActivity(intent);
    }

    public void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edtBirthday.setText((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "/" +
                        ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "/" +
                        year);
            }
        };
        String date = edtBirthday.getText().toString();
        String dates[] = date.split("/");
        int day = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]) - 1;
        int year = Integer.parseInt(dates[2]);
        DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileInformationActivity.this,
                onDateSetListener, year, month, day);
        datePickerDialog.show();
    }
    public boolean validate(final String hex) {
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }
}
