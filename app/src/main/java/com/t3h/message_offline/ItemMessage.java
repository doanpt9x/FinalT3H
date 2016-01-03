package com.t3h.message_offline;

/**
 * Created by NGO VAN TUAN on 28/12/2015.
 */
public class ItemMessage {
    private String mAddress;
    private String mBody;
    private String mTime;
    private String mType;
    private String mId;
    private String read;
    private String name;
    private String snippet;

    public ItemMessage(String type, String body, String date) {
        this.mType = type;
        this.mBody = body;
        this.mTime = date;
    }

    public ItemMessage(String mID,String mAddress,String body,String date,String numberMesage){
        this.mId = mID;
        this.mBody = body;
        this.mAddress = mAddress;
        this.mTime=date;
        this.read=numberMesage;
    }
//    public ItemMessage(String mId, String mTime, String snippet, String read, String mType) {
//        this.mId = mId;
//        this.mTime = mTime;
//        this.snippet = snippet;
//        this.read = read;
//        this.mType = mType;
//    }

    public ItemMessage(String mBody, String mType) {
        this.mBody = mBody;
        this.mType = mType;
    }

    public String getRead() {
        return read;
    }

    public String getName() {
        return name;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getmType() {
        return mType;
    }

    public String getmId() {
        return mId;
    }

    public String getmAddress() {
        return mAddress;
    }

    public String getmBody() {
        return mBody;
    }

    public String getmTime() {
        return mTime;
    }
}
