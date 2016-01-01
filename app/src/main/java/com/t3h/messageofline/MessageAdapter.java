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
 * Created by NGO VAN TUAN on 28/12/2015.
 */
public class MessageAdapter extends BaseAdapter {
    private ArrayList<ItemMessage> mArrayMessages = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;

    public MessageAdapter(Context mContext) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
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

    public void setmArrayMessages(ArrayList<ItemMessage> mArrayMessages) {
        this.mArrayMessages = mArrayMessages;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_message_offline, null);
            viewHolder.mAddress = (TextView) view.findViewById(R.id.tv_address);
            viewHolder.mBody = (TextView) view.findViewById(R.id.tv_body);
            viewHolder.mTime = (TextView) view.findViewById(R.id.tv_time);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mAddress.setText(mArrayMessages.get(position).getmAddress());
        viewHolder.mBody.setText(mArrayMessages.get(position).getmBody());
        viewHolder.mTime.setText(mArrayMessages.get(position).getmTime());
        return view;
    }

    class ViewHolder {
        TextView mAddress;
        TextView mBody;
        TextView mTime;
    }
}
