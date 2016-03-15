package com.t3h.mail;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.t3h.common.GlobalApplication;
import com.t3h.final_t3h.R;

import java.io.IOException;

public class MailActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleAccountCredential mCredential;
    private com.google.api.services.gmail.Gmail mService;

    private ImageView ivMailIcon, ivBack, ivDelete, ivSpam, ivStar;
    private TextView tvFrom, tvTime, tvSnippet;
    private Button btReply, btReplyAll, btForward;
    private WebView wvContentMail;
    private MailItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmail_detail_activity);

        final GlobalApplication global = (GlobalApplication) getApplicationContext();
        mCredential = global.getmCredential();
        mService = global.getmService();

        getViews();
        setViews();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_mail, menu);
        return true;
    }

    private void setViews() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mItem = intent.getParcelableExtra(Constant.KEY_PARCEL_MAIL_ITEM);

        tvFrom.setText(mItem.getFrom());
        tvTime.setText(mItem.getTime());
        tvSnippet.setText(mItem.getSnippet());
        ivStar.setImageResource(mItem.isStarred() ? R.drawable.ic_star : R.drawable.ic_star_outline);

        //get first letter of each String item
        String firstLetter = String.valueOf(Character.isLetterOrDigit(mItem.getFrom().charAt(0))? mItem.getFrom().charAt(0): mItem.getFrom().charAt(1)).toUpperCase();
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(mItem);
        TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, color);
        ivMailIcon.setImageDrawable(drawable);

        new GetMailTask().execute(mItem.getId());
    }

    private void getViews() {
        ivMailIcon = (ImageView) findViewById(R.id.iv_mail_icon);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivDelete = (ImageView) findViewById(R.id.iv_delete);
        ivSpam = (ImageView) findViewById(R.id.iv_spam);
        ivStar = (ImageView) findViewById(R.id.iv_star);

        tvFrom = (TextView) findViewById(R.id.tv_from);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvSnippet = (TextView) findViewById(R.id.tv_snippet);
        wvContentMail = (WebView) findViewById(R.id.wv_content);

        btReply = (Button) findViewById(R.id.bt_reply);
        btReplyAll = (Button) findViewById(R.id.bt_reply_all);
        btForward = (Button) findViewById(R.id.bt_forward);

        ivBack.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        ivSpam.setOnClickListener(this);
        ivStar.setOnClickListener(this);
        btReply.setOnClickListener(this);
        btReplyAll.setOnClickListener(this);
        btForward.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_delete:

                break;
            case R.id.iv_spam:

                break;
            case R.id.iv_star:

                break;
            case R.id.bt_reply:

                break;
            case R.id.bt_reply_all:

                break;
            case R.id.bt_forward:

                break;
            default:
                break;
        }
    }

    class GetMailTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return GmailMgr.getMessageBody(mService, mItem.getId());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            wvContentMail.loadDataWithBaseURL(null, s, "text/html", "UTF-8", null);
        }
    }
}
