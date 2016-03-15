package com.t3h.mail;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.t3h.common.GlobalApplication;
import com.t3h.final_t3h.R;

import javax.mail.MessagingException;

public class CreateMailActivity extends AppCompatActivity implements View.OnClickListener {

    private com.google.api.services.gmail.Gmail mService;

    private ImageView ivBack, ivAttach, ivSend;
    private EditText edtFrom, edtTo, edtSubject, edtContent;
    private MailItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmail_activity_create);

        getViews();
        setViews();
    }

    private void setViews() {
//        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
//        edtFrom.setText(getPreferences(Context.MODE_PRIVATE).getString(Constant.PREF_ACCOUNT_NAME, "ngovanvinh1994@gmail.com"));
        edtFrom.setText("ngovanvinh1994@gmail.com");
        edtTo.setText("ngovanvinh19941@gmail.com");
        edtSubject.setText("Subject");
        edtContent.setText("Content");
    }

    private void getViews() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivAttach = (ImageView) findViewById(R.id.iv_attach_file);
        ivSend = (ImageView) findViewById(R.id.iv_send);

        edtFrom = (EditText) findViewById(R.id.edt_from);
        edtTo = (EditText) findViewById(R.id.edt_to);
        edtSubject = (EditText) findViewById(R.id.edt_subject);
        edtContent = (EditText) findViewById(R.id.edt_content);

        ivSend.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivAttach.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send:
                String from = edtFrom.getText().toString();
                String to = edtTo.getText().toString();
                String subject = edtSubject.getText().toString();
                String content = edtContent.getText().toString();
                new SendMailTask(to, from, subject, content).execute();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_attach_file:
                Toast.makeText(CreateMailActivity.this, "Attaching file...", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(CreateMailActivity.this, "Save to Trash", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    class SendMailTask extends AsyncTask<Void, Void, Void> {

        String[] paras;

        public SendMailTask(String... params) {
            this.paras = params;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                GlobalApplication global = (GlobalApplication) getApplicationContext();
                mService = global.getmService();
                GmailMgr.sendMessage(mService, Constant.USER, GmailMgr.createEmail(paras[0], paras[1], paras[2], paras[3]));
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
