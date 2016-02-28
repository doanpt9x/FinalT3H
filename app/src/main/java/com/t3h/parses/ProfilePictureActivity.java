package com.t3h.parses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.t3h.common.CommonMethod;
import com.t3h.common.CommonValue;
import com.t3h.common.GlobalApplication;
import com.t3h.common.ImageControl;
import com.t3h.final_t3h.MainOnlineActivity;
import com.t3h.final_t3h.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Android on 1/3/2016.
 */
public class ProfilePictureActivity extends Activity implements View.OnClickListener{
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_UPLOAD_PHOTO = 1;

    private CircleImageView imgAvatar;
    private Bitmap bitmapAvatar, bitmapDefault;
    private Uri capturedImageURI;
    private LinearLayout llUpload,llTake;
    private Button btnSkip,btnOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);
        initializeComponent();
    }

    private void initializeComponent() {
        imgAvatar = (CircleImageView) findViewById(R.id.img_avatar);
        bitmapAvatar = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
        llUpload= (LinearLayout) findViewById(R.id.ll_upload);
        llTake = (LinearLayout) findViewById(R.id.ll_take);
        llUpload.setOnClickListener(this);
        llTake.setOnClickListener(this);
        btnSkip= (Button) findViewById(R.id.btn_skip);
        btnOK= (Button) findViewById(R.id.btn_ok);
        btnSkip.setOnClickListener(this);
        btnOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_skip:
                signUpSuccess(bitmapDefault);
                break;
            case R.id.btn_ok:
                signUpSuccess(bitmapAvatar);
                break;
            case R.id.ll_upload:
                Intent intentUpload = new Intent();
                intentUpload.setClass(ProfilePictureActivity.this, UploadImageActivity.class);
                this.startActivityForResult(intentUpload, REQUEST_UPLOAD_PHOTO);
                break;
            case R.id.ll_take:
                Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intentTakePhoto.resolveActivity(ProfilePictureActivity.this.getPackageManager()) != null) {
                    @SuppressLint("SimpleDateFormat")
                    String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String fileName = "Image_" + date;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Images.Media.TITLE, fileName);
                    capturedImageURI = ProfilePictureActivity.this.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);
                    this.startActivityForResult(intentTakePhoto, REQUEST_TAKE_PHOTO);
                } else {
                    Snackbar.make(v, "Device does not support camera", Snackbar.LENGTH_LONG)
                            .setAction("ACTION", null)
                            .show();
                }
                break;
        }
    }

    private void signUpSuccess(final Bitmap bitmapAvatar) {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        CommonMethod.uploadAvatar(currentUser, bitmapAvatar);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                GlobalApplication globalApplication = (GlobalApplication) ProfilePictureActivity.this.getApplication();
                globalApplication.setAvatar(bitmapAvatar);
                globalApplication.setFullName(currentUser.get("fullName")+"");
                globalApplication.setPhoneNumber(currentUser.getUsername()+"");
                globalApplication.setEmail(currentUser.getEmail());
            }
        });

        startActivityMainOnline();
    }

    private void startActivityMainOnline() {
        Intent intent=new Intent(ProfilePictureActivity.this, MainOnlineActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_UPLOAD_PHOTO:
                    byte[] bytes = data.getByteArrayExtra(CommonValue.BYTE_AVATAR);
                    bitmapAvatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgAvatar.setImageBitmap(bitmapAvatar);
                    break;
                case REQUEST_TAKE_PHOTO:
                    Cursor cursor = getContentResolver().query(capturedImageURI,
                            new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String capturedImageFilePath = cursor.getString(index);
                    cursor.close();
                    Intent intentCropImage = new Intent();
                    intentCropImage.setClass(ProfilePictureActivity.this, ImageControl.class);
                    intentCropImage.putExtra(ImageControl.EXTRA_IMAGE, capturedImageFilePath);
                    this.startActivityForResult(intentCropImage, REQUEST_UPLOAD_PHOTO);
                    break;
            }
            btnOK.setEnabled(true);
        }
    }

}
