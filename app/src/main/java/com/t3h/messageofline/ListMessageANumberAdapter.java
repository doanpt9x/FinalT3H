package com.t3h.messageofline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.t3h.final_t3h.R;

import java.util.ArrayList;

/**
 * Created by Android on 1/1/2016.
 */
public class ListMessageANumberAdapter extends BaseAdapter {
    public static final int TYPE_OUTGOING = 1;
    public static final int TYPE_INCOMING = 2;
    private ArrayList<ItemMessage> mArrayMessages = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;

    public ListMessageANumberAdapter(Context mContext, ArrayList<ItemMessage> arrMessage) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        this.mArrayMessages = arrMessage;
    }

    @Override
    public int getCount() {
        return mArrayMessages.size();
    }

    @Override
    public ItemMessage getItem(int position) {
        return mArrayMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(mArrayMessages.get(position).getmType());
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        int type = getItemViewType(position);
        TextView mBody=null;
        switch (type) {
            case TYPE_OUTGOING:
                view = mInflater.inflate(R.layout.item_message_left, parent, false);
                mBody = (TextView) view.findViewById(R.id.item_message_left);
                break;
            case TYPE_INCOMING:
                view = mInflater.inflate(R.layout.item_message_right, parent, false);
                mBody = (TextView) view.findViewById(R.id.item_message_right);
                break;
        }
        mBody.setText(mArrayMessages.get(position).getmBody() + "\n" + mArrayMessages.get(position).getmTime());
        return view;
    }

}