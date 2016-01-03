package com.t3h.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Android on 1/3/2016.
 */
public class CommonMethod {
    private static CommonMethod commonMethod;

    public static CommonMethod getInstance() {
        if (commonMethod == null) {
            commonMethod = new CommonMethod();
        }
        return commonMethod;
    }

    public CommonMethod() {
    }
    public int convertSizeIcon(float density, int sizeDp) {
        return (int) (sizeDp * (density / 160));
    }

    public static void uploadAvatar(ParseUser parseUser, Bitmap avatar) {
        if (parseUser == null) return;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        avatar.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        if (bytes != null) {
            ParseFile parseFile = new ParseFile(bytes);
            parseUser.put("avatar", parseFile);
            parseUser.saveInBackground();
        }
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Bitmap decodeSampledBitmapFromResource(String uri,int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(uri, options);
    }
    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize * 2;
    }
    public int getOrientation(String path) {
        int rotate = 0;
        try {
            File file = new File(path);
            ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
    public Bitmap getBitmap(int orientation, Bitmap bitmap) {
        if (orientation == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }
}
