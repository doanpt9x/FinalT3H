package com.t3h.parses;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;
import com.t3h.common.CommonMethod;
import com.t3h.common.GlobalApplication;
import com.t3h.custom_view.SquareImageView;
import com.t3h.final_t3h.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Android on 1/3/2016.
 */
public class ImageAdapter extends BaseAdapter {
    private static final String TAG = "ImageAdapter";
    private Context context;
    private ArrayList<String> imageURLs;
    private LayoutInflater layoutInflater;
    private final int SIZE_IMAGE;

    public ImageAdapter(Context context) {
        this.context = context;
        CommonMethod commonMethod=new CommonMethod();
        layoutInflater = LayoutInflater.from(context);
        initializeListImage();
        for (String i : imageURLs) {
            Log.i(TAG, "uri: " + i);
        }
        SIZE_IMAGE = (GlobalApplication.WIDTH_SCREEN - commonMethod.convertSizeIcon(GlobalApplication.DENSITY_DPI, 4) * 4) / 3;
    }

    private void initializeListImage() {
        imageURLs = new ArrayList<String>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.DATA}, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            imageURLs.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            cursor.moveToNext();
        }
        return;
    }

    @Override
    public int getCount() {
        return imageURLs.size();
    }

    @Override
    public String getItem(int position) {
        return imageURLs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquareImageView imgImage;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_image, parent, false);
            imgImage = (SquareImageView) convertView.findViewById(R.id.imgImage);
            convertView.setTag(imgImage);
        } else {
            imgImage = (SquareImageView) convertView.getTag();
        }
        Picasso.with(parent.getContext())
                .load(new File(imageURLs.get(position)))
                .resize(SIZE_IMAGE, SIZE_IMAGE)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.warning)
                .centerCrop()
                .into(imgImage);

        return convertView;
    }
}
