package com.t3h.final_t3h;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.t3h.common.GlobalApplication;
import com.t3h.common.OnShowPopupMenu;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Android on 2/7/2016.
 */
public class FriendAdapter extends BaseAdapter {
    private static final String TAG = "FriendAdapter";

    private ArrayList<AllFriendItem> allFriendItems;
    private LayoutInflater layoutInflater;
    private OnShowPopupMenu onShowPopupMenu;

    public FriendAdapter(Context context, Activity activity) {
        layoutInflater = LayoutInflater.from(context);

        if (((GlobalApplication) activity.getApplication()).getAllFriendItems() != null) {
            allFriendItems = ((GlobalApplication) activity.getApplication()).getAllFriendItems();
        } else {
            allFriendItems = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return allFriendItems.size();
    }

    @Override
    public AllFriendItem getItem(int position) {
        return allFriendItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_all_friend, parent, false);
            viewHolder.imgAvatar = (CircleImageView) convertView.findViewById(R.id.imgAvatar);
            viewHolder.menu = (ImageView) convertView.findViewById(R.id.menu);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imgAvatar.setImageBitmap(allFriendItems.get(position).getAvatar());
        viewHolder.txtName.setText(allFriendItems.get(position).getFullName());
        viewHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowPopupMenu.onShowPopupMenuListener(position, viewHolder.menu);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        CircleImageView imgAvatar;
        TextView txtName;
        ImageView menu;
    }

    public void setOnShowPopupMenu(OnShowPopupMenu onShowPopupMenu) {
        this.onShowPopupMenu = onShowPopupMenu;
    }

    public ArrayList<AllFriendItem> getAllFriendItems() {
        return allFriendItems;
    }

}
