package com.t3h.final_t3h;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.parse.ParseUser;
import com.t3h.common.GlobalApplication;

/**
 * Created by Android on 3/12/2016.
 */
public class HelloActivity extends Activity {
    private ImageView mIvicon;
    private Animation mAnimation;
    private final int HELLO_DISPLAY_TIME = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        initView();
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
                    Intent intent = new Intent(HelloActivity.this, MainOnlineActivity.class);
                    HelloActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(HelloActivity.this, HomeOnlineActivity.class);
                    HelloActivity.this.startActivity(intent);
                }
                HelloActivity.this.finish();
            }
        }, HELLO_DISPLAY_TIME);
    }
    private void initView(){
        mIvicon= (ImageView) findViewById(R.id.iv_icon);
        mAnimation= AnimationUtils.loadAnimation(this,R.anim.anim_alpha);
        mIvicon.setAnimation(mAnimation);
        mAnimation.start();
    }
}
