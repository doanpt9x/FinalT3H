package com.t3h.mail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Shiloh Marion on 11/03/2016.
 */
public class MailItem implements Parcelable{

    private String id, from, time, snippet;
    private boolean isAttacked, isStarred;

    public MailItem(String id, String from, String time, String snippet, boolean isAttacked, boolean isStarred) {
        this.id = id;
        this.from = from;
        this.time = time;
        this.snippet = snippet;
        this.isAttacked = isAttacked;
        this.isStarred = isStarred;
    }

    protected MailItem(Parcel in) {
        id = in.readString();
        from = in.readString();
        time = in.readString();
        snippet = in.readString();
        isAttacked = in.readByte() != 0;
        isStarred = in.readByte() != 0;
    }

    public static final Creator<MailItem> CREATOR = new Creator<MailItem>() {
        @Override
        public MailItem createFromParcel(Parcel in) {
            return new MailItem(in);
        }

        @Override
        public MailItem[] newArray(int size) {
            return new MailItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTime() {
        return time;
    }

    public String getSnippet() {
        return snippet;
    }

    public boolean isAttacked() {
        return isAttacked;
    }

    public boolean isStarred() {
        return isStarred;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(from);
        dest.writeString(time);
        dest.writeString(snippet);
        dest.writeByte((byte) (isAttacked?1:0));
        dest.writeByte((byte) (isStarred?1:0));
    }
}
