package com.t3h.final_t3h;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.t3h.call.OutgoingCallActivity;
import com.t3h.common.CommonMethod;
import com.t3h.common.CommonValue;
import com.t3h.common.GlobalApplication;
import com.t3h.common.OnShowPopupMenu;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Android on 1/24/2016.
 */
@SuppressLint("ValidFragment")
public class FriendFragment extends Fragment implements View.OnClickListener, OnShowPopupMenu,
        SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {
    private ListView listViewFriend;
    private FriendAdapter friendAdapter;
    private ContactAdapter contactAdapter;
    private TextView btnTabInvite, btnTabAllFriends;
    private ArrayList<ContactItem> contactItems;
    private BroadcastUpdateListFriend broadcastUpdateListFriend = new BroadcastUpdateListFriend();
    private Context context;
    private CircleImageView imgAvatar;
    private TextView txtFullName, txtStatus;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;

    public FriendFragment(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.tab_friend, null);
        this.initializeComponent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerUpdateListFriend();
        context = this.getActivity();
        this.onRefresh();
        friendAdapter = new FriendAdapter(this.getActivity(), this.getActivity());
        friendAdapter.setOnShowPopupMenu(this);
        listViewFriend.setAdapter(friendAdapter);
    }

    public void registerUpdateListFriend() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonValue.ACTION_ADD_FRIEND);
        this.getActivity().registerReceiver(broadcastUpdateListFriend, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        this.initializeProfile();
        return view;
    }
//    private void initializeProfile() {
//        imgAvatar = (CircleImageView) view.findViewById(R.id.imgAvatar);
////        imgAvatar.setImageBitmap(((GlobalApplication) this.getActivity().getApplication()).getAvatar());
////        txtFullName = (TextView) view.findViewById(R.id.txtFullName);
////        txtFullName.setText(((GlobalApplication) this.getActivity().getApplication()).getFullName());
//    }

    private void initializeComponent() {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#f44336"),
                Color.parseColor("#2196f3"), Color.parseColor("#4caf50"));
        swipeRefreshLayout.setOnRefreshListener(this);

        listViewFriend = (ListView) view.findViewById(R.id.listViewFriend);
        listViewFriend.setOnScrollListener(this);

        btnTabInvite = (TextView) view.findViewById(R.id.btnTabInvite);
        btnTabInvite.setOnClickListener(this);
        btnTabAllFriends = (TextView) view.findViewById(R.id.btnTabAllFriends);
        btnTabAllFriends.setOnClickListener(this);

        listViewFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String inComingId, inComingFullName;
                inComingId = friendAdapter.getItem(position).getId();
                inComingFullName = friendAdapter.getItem(position).getFullName();
                Intent intentChat = new Intent(context, MessageActivity.class);
                intentChat.putExtra(CommonValue.INCOMING_CALL_ID, inComingId);
                intentChat.putExtra(CommonValue.INCOMING_MESSAGE_FULL_NAME, inComingFullName);
                context.startActivity(intentChat);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTabInvite:
                this.changeStateShow(btnTabInvite);
                this.changeStateHide(btnTabAllFriends);
                contactAdapter = new ContactAdapter(this.getActivity());
                listViewFriend.setAdapter(contactAdapter);
                getAllContact();
                break;
            case R.id.btnTabAllFriends:
                friendAdapter = new FriendAdapter(this.getActivity(), this.getActivity());
                friendAdapter.setOnShowPopupMenu(this);
                this.changeStateShow(btnTabAllFriends);
                this.changeStateHide(btnTabInvite);
                listViewFriend.setAdapter(friendAdapter);
                listViewFriend.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void changeStateShow(TextView textView) {
        textView.setBackgroundResource(R.color.green_500);
        textView.setTextColor(Color.WHITE);
    }

    private void changeStateHide(TextView textView) {
        textView.setBackgroundResource(R.drawable.bg_button_friend_green);
        textView.setTextColor(Color.parseColor("#4caf50"));
    }

    @Override
    public void onRefresh() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        GlobalApplication globalApplication=(GlobalApplication)this.getActivity().getApplication();
        globalApplication.setAllFriendItems(CommonMethod.getInstance().loadListFriend(currentUser, this.getActivity()));
        final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        swipeRefreshLayout.setRefreshing(true);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                progressDialog.dismiss();
            }
        }, 1000);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onShowPopupMenuListener(final int position, View view) {

        PopupMenu popup = new PopupMenu(FriendFragment.this.getActivity(), view);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String inComingId = null, inComingFullName = null;
                inComingId=friendAdapter.getItem(position).getId();
                inComingFullName=friendAdapter.getItem(position).getFullName();
                switch (item.getItemId()) {
                    case R.id.action_open_chat:
                        Toast.makeText(context, "Open Chat", Toast.LENGTH_LONG).show();
                        Intent intentChat = new Intent(getActivity(), MessageActivity.class);
                        intentChat.putExtra(CommonValue.INCOMING_CALL_ID, inComingId);
                        intentChat.putExtra(CommonValue.INCOMING_MESSAGE_FULL_NAME, inComingFullName);
                        FriendFragment.this.getActivity().startActivity(intentChat);
                        break;
                    case R.id.action_voice_call:
                        //open activity call
                        Toast.makeText(context, "Open Call", Toast.LENGTH_LONG).show();
                        Intent intentCall = new Intent(getActivity(), OutgoingCallActivity.class);
                        intentCall.putExtra(CommonValue.INCOMING_CALL_ID, inComingId);
                        FriendFragment.this.getActivity().startActivity(intentCall);
                        break;
                    case R.id.action_view_profile:
                        //open activity profile
                        Toast.makeText(context, "Open Profile", Toast.LENGTH_LONG).show();
                        Intent intentProfile = new Intent(getActivity(), DetailActivity.class);
                        intentProfile.putExtra(CommonValue.USER_ID, inComingId);
                        FriendFragment.this.getActivity().startActivity(intentProfile);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    //get contact
    public void getAllContact() {
        contactItems = new ArrayList<ContactItem>();
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_ID
                }, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        if (cursor != null) {
            int indexNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int indexName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int indexPhoto = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String uri = null;
                try {
                    uri = cursor.getString(indexPhoto);
                    Uri uriPhoto = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI,
                            Long.parseLong(uri));
                    contactItems.add(new ContactItem(cursor.getString(indexNumber),
                            cursor.getString(indexName), uriPhoto));
                } catch (Exception e) {
                    Uri uriPhoto = Uri.parse("android.resource://com.t3h.final_t3h/"
                            + R.drawable.ic_avatar_default);
                    contactItems.add(new ContactItem(cursor.getString(indexNumber),
                            cursor.getString(indexName), uriPhoto));
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    private class BroadcastUpdateListFriend extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CommonValue.ACTION_ADD_FRIEND)) {
                FriendItem newFriend = ((MainOnlineActivity) FriendFragment.this.getActivity()).getNewFriend();
                AllFriendItem allFriendItem = new AllFriendItem(newFriend.getId(),
                        newFriend.getAvatar(), newFriend.getPhoneNumber(), newFriend.getFullName());
                if (friendAdapter == null) {
                    friendAdapter = new FriendAdapter(FriendFragment.this.getActivity(),
                            FriendFragment.this.getActivity());
                }
                friendAdapter.getAllFriendItems().add(allFriendItem);
                Collections.sort(friendAdapter.getAllFriendItems());
                friendAdapter.notifyDataSetChanged();
            }
        }
    }
}
