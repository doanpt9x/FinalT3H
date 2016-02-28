package com.t3h.final_t3h;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.t3h.call.CallDBManager;
import com.t3h.call.CallLogActivity;
import com.t3h.common.CommonValue;
import com.t3h.common.GlobalApplication;
import com.t3h.custom_view.CircleTextView;

import java.util.ArrayList;

/**
 * Created by Android on 1/3/2016.
 * Màn hình chính sau khi đã đăng nhập thành công
 */
@SuppressWarnings("ALL")
public class MainOnlineActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{
    private ParseUser currentUser;
    private CoordinatorLayout coordinator;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigation;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tab;
    private FriendItem newFriend;
    private BroadcastMain broadcastMain;
    private CallDBManager callDBManager;
    private static final int REQUEST_ADDITION_FRIEND = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_online);
        //khởi tạo toolbar(Actionbar)
        initActionbar();
        //khởi tạo view()
        initView();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 3000);
        this.startService();
        callDBManager = new CallDBManager(this);

    }
    private void startService() {
        Intent intentStartService = new Intent();
        intentStartService.setClassName(CommonValue.PACKAGE_NAME_MAIN,
                CommonValue.PACKAGE_NAME_COMMON + ".MMCClientService");
        this.startService(intentStartService);
    }

    private void initView() {
        //open va close menu
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.open_navigation, R.string.close_navigation) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //khởi tạo và lắng nghe navigation menu
        navigation = (NavigationView) findViewById(R.id.navigation);
        navigation.setNavigationItemSelectedListener(this);
        viewPagerAdapter = new ViewPagerAdapter(this, this.getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(viewPagerAdapter);

        int[] tabBackgroundIds = new int[]{R.drawable.bg_tab_message,
                R.drawable.bg_tab_friend};
        tab = (TabLayout) findViewById(R.id.tab);
        tab.setupWithViewPager(viewPager);
        for (int i = 0; i < viewPagerAdapter.getCount(); i++) {
            tab.getTabAt(i).setText(null);
            tab.getTabAt(i).setIcon(tabBackgroundIds[i]);
        }
        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
//
//        btnAction = (FloatingActionButton) findViewById(R.id.btnAction);
//        btnAction.setOnClickListener(this);

    }

    private void initActionbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_action:
                //click actionbar new message
//                Intent intentNewMessage = new Intent(this, NewMessageActivity.class);
//                this.startActivity(intentNewMessage);
//                break;
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.nav_call_logs:
                Intent intentCallLogs = new Intent(MainOnlineActivity.this, CallLogActivity.class);
                MainOnlineActivity.this.startActivity(intentCallLogs);
                break;
            case R.id.nav_log_out:
                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Log out?");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseUser parseUser = ParseUser.getCurrentUser();
                                parseUser.put("isOnline", false);
                                parseUser.saveInBackground();
                                ParseUser.logOut();
                                callDBManager.deleteAllData();
                                callDBManager.closeDatabase();

                                ((GlobalApplication) getApplication()).setAvatar(null);
                                ((GlobalApplication) getApplication()).setFullName(null);
                                ((GlobalApplication) getApplication()).setPhoneNumber(null);
                                ((GlobalApplication) getApplication()).setEmail(null);
                                ((GlobalApplication) getApplication()).setPictureSend(null);
                                ((GlobalApplication) getApplication()).setAllFriendItems(null);

                                Intent intentLogout = new Intent(CommonValue.ACTION_LOGOUT);
                                MainOnlineActivity.this.sendBroadcast(intentLogout);
                                Intent intent = new Intent(MainOnlineActivity.this, HomeOnlineActivity.class);
                                alertDialog.dismiss();
                                MainOnlineActivity.this.startActivity(intent);
                                MainOnlineActivity.this.finish();
                            }
                        });
                alertDialog.show();
                break;
            case R.id.nav_about_us:
//                Intent intentAboutUs = new Intent(this, AboutUsActivity.class);
//                this.startActivity(intentAboutUs);
                Toast.makeText(this,"Open Deverloper",Toast.LENGTH_LONG).show();
                break;
            //click setting
            case R.id.nav_settings:
//                drawerLayout.closeDrawers();
//                viewPager.setCurrentItem(3);
                Toast.makeText(this,"Open Setting Sysmte",Toast.LENGTH_LONG).show();
                break;
            //click my account
            case R.id.nav_my_account:
//                Intent intentMyAccount = new Intent(this, MyAccountActivity.class);
//                this.startActivity(intentMyAccount);
                Toast.makeText(this,"Open setting Account",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_mail:
                Toast.makeText(this,"Open Mail",Toast.LENGTH_LONG).show();
                break;
        }
        return true;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //listener onlick item menu
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_add_user:
                Intent intentAdditionFriend = new Intent(this, AdditionFriend.class);
                this.startActivityForResult(intentAdditionFriend, REQUEST_ADDITION_FRIEND);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        //nếu menu đang mở thì đóng. còn k thì backpressed
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        //nếu activity hủy thì kiểm tra user để put trạng thái online
        if (currentUser != null) {
            currentUser.put("isOnline", false);
            currentUser.saveInBackground();
        }

        if (broadcastMain != null) {
            unregisterReceiver(broadcastMain);
            broadcastMain = null;
        }
        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADDITION_FRIEND:
                    if (data == null) {
                        return;
                    }
                    String phoneNumber = data.getStringExtra("PHONE_NUMBER");
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Addition Friend");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    final ParseUser currentUser = ParseUser.getCurrentUser();
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", phoneNumber);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e != null) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Snackbar.make(coordinator, "Error",
                                        Snackbar.LENGTH_LONG)
                                        .setAction("ACTION", null)
                                        .show();
                                return;
                            }
                            if (parseUser == null) {
                                progressDialog.dismiss();
                                Snackbar.make(coordinator, "That account does not exist",
                                        Snackbar.LENGTH_LONG)
                                        .setAction("ACTION", null)
                                        .show();
                                return;
                            }
                            ArrayList<String> listFriend = (ArrayList<String>) currentUser.get("listFriend");
                            String newUserId = parseUser.getObjectId();
                            if (listFriend == null) {
                                listFriend = new ArrayList<String>();
                            } else {
                                if (listFriend.contains(newUserId)) {
                                    progressDialog.dismiss();
                                    Snackbar.make(coordinator, "That account has been identical",
                                            Snackbar.LENGTH_LONG)
                                            .setAction("ACTION", null)
                                            .show();
                                    return;
                                }
                            }
                            listFriend.add(newUserId);
                            currentUser.put("listFriend", listFriend);
                            currentUser.saveInBackground();
                            MainOnlineActivity.this.createNewFriend(parseUser);
                            progressDialog.dismiss();
                            Snackbar snackbar = Snackbar.make(coordinator, "Addition friend successfully",
                                    Snackbar.LENGTH_LONG)
                                    .setAction("ACTION", null);
                            View snackbarView = snackbar.getView();
                            snackbarView.setBackgroundColor(Color.parseColor("#4caf50"));
                            snackbar.show();
                        }
                    });
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public FriendItem getNewFriend() {
        return newFriend;
    }

    private void createNewFriend(final ParseUser parseUser) {
        final ParseFile parseFile = (ParseFile) parseUser.get("avatar");
        if (parseFile != null) {
            parseFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    final Bitmap avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    final String id = parseUser.getObjectId();

                    newFriend = new FriendItem(id, avatar, parseUser.getUsername(),
                            parseUser.getString("fullName"));
                    Intent intentAddFriend = new Intent();
                    intentAddFriend.setAction(CommonValue.ACTION_ADD_FRIEND);
                    boolean isOnline = parseUser.getBoolean("isOnline");
                    intentAddFriend.putExtra("isOnline", isOnline);
                    MainOnlineActivity.this.sendBroadcast(intentAddFriend);
                }
            });
        }
    }
    private void registerBroastCastMain() {
        if (broadcastMain == null) {
            broadcastMain = new BroadcastMain();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("MAIN");
            registerReceiver(broadcastMain, intentFilter);
        }
    }
    private class BroadcastMain extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//          img.setImageBitmap(((GlobalApplication) getApplication()).getAvatar());
        }
    }

}
