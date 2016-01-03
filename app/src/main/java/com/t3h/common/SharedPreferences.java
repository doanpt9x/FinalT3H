package com.t3h.common;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Android on 1/3/2016.
 */
public class SharedPreferences {
    private static final String FILE_NAME = "SharedPreferences";
    private static final String SIZE_LIST = "SIZE_LIST";
    private static final String USER_ID = "USER_ID";

    private Context context;

    public SharedPreferences(Context context) {
        this.context = context;
        this.createSharedPreferences();
    }

    private void createSharedPreferences() {
        android.content.SharedPreferences sharedPreferences =
                context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        int size = sharedPreferences.getInt(SIZE_LIST, -1);
        if (size == -1) {
            android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SIZE_LIST, 0);
            editor.apply();
        }
    }

    public ArrayList<String> readListID() {
        int size = getSizeListID();
        ArrayList<String> listID = new ArrayList<>();
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        for (int i = 0; i < size; i++) {
            String idUser = sharedPreferences.getString(USER_ID + i, "");
            listID.add(idUser);
        }
        return listID;
    }

    public void writeUserID(String userID) {
        int size = getSizeListID();
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(USER_ID + size, userID);
        edit.putInt(SIZE_LIST, size + 1);
        edit.apply();
    }

    private int getSizeListID() {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getInt(SIZE_LIST, 0);
    }
}
