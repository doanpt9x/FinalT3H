package com.t3h.final_t3h;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<ContactItem> contactItems;
    private Context context;

    public ContactAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.initializeArrayListContactItem();
        Collections.sort(contactItems);
    }

    private void initializeArrayListContactItem() {
        contactItems = new ArrayList<ContactItem>();
        final Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_ID
                }, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        if (cursor != null) {
            final int indexNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            final int indexName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            final int indexPhoto = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                final String number = cursor.getString(indexNumber);
                String uri = null;
                try {
                    uri = cursor.getString(indexPhoto);
                    Uri uriPhoto = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI,
                            Long.parseLong(uri));
                    contactItems.add(new ContactItem(number,
                            cursor.getString(indexName), uriPhoto));
                } catch (Exception e1) {
                    Uri uriPhoto = Uri.parse("android.resource://com.t3h.final_t3h/"
                            + R.drawable.ic_avatar_default);
                    contactItems.add(new ContactItem(number,
                            cursor.getString(indexName), uriPhoto));
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    @Override
    public int getCount() {
        return contactItems.size();
    }

    @Override
    public ContactItem getItem(int position) {
        return contactItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_contact, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txtContactName = (TextView) convertView.findViewById(R.id.txtContactName);
            viewHolder.txtContactDescription = (TextView) convertView
                    .findViewById(R.id.txtContactDescription);
            viewHolder.imgContactIcon = (CircleImageView) convertView
                    .findViewById(R.id.imgContactIcon);
            viewHolder.invite = (TextView) convertView.findViewById(R.id.invite);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtContactName.setText(contactItems.get(position).getName());
        viewHolder.txtContactDescription.setText(contactItems.get(position).getPhoneNumber());
        viewHolder.imgContactIcon.setImageURI(contactItems.get(position).getPhoto());
        viewHolder.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inviteViaMessengerDefault(contactItems.get(position).getPhoneNumber());
            }
        });
        return convertView;
    }

    private class ViewHolder {
        CircleImageView imgContactIcon;
        TextView txtContactName;
        TextView txtContactDescription;
        TextView invite;
    }

    public void inviteViaMessengerDefault(String phoneNumber) {
        String sender = (String) ParseUser.getCurrentUser().get("fullName");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("sms_body", sender
                + " invite you to use MMC OK to free call and message: "
                + " <link>");
        intent.setData(Uri.parse("sms: " + phoneNumber));
        context.startActivity(intent);
    }

}