package com.t3h.mail;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.t3h.common.GlobalApplication;
import com.t3h.final_t3h.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class GmailMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, DialogInterface.OnDismissListener {

    private static final String TAG = "GmailMainActivity";
    private GoogleAccountCredential mCredential;
    private com.google.api.services.gmail.Gmail mService;
    private ProgressDialog mProgress;
    private ListView mLvData;

    private Exception mLastError = null;
    private ListMailAdapter mMailAdapter;
    private int mType = Constant.TYPE_Q;
    private Object mObj = Constant.Q_IN_INBOX;
    private ListMsgTask mTask;

    private NavigationView mNavigationView;
    private ImageView ivProfileIcon;
    private TextView tvProfileName;
    private TextView tvProfileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmail_activity_main);

        mLvData = (ListView) findViewById(R.id.lv_data);
        mLvData.setOnItemClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_create);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(GmailMainActivity.this, CreateMailActivity.class);
                startActivity(intent);
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading ...");
        mProgress.setOnDismissListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setCheckedItem(R.id.menu_item_inbox);
        mNavigationView.setNavigationItemSelectedListener(this);


        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Constant.SCOPES)
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(Constant.PREF_ACCOUNT_NAME, null));
        refreshResults();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gmail_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.menu_item_inbox:
                mType = Constant.TYPE_Q;
                mObj = Constant.Q_IN_INBOX;
                refreshResults();
                break;
            case R.id.menu_item_primary:
                mType = Constant.TYPE_L;
                mObj = Constant.L_INBOX_PERSONAL;
                refreshResults();
                break;
            case R.id.menu_item_social:
                mType = Constant.TYPE_L;
                mObj = Constant.L_INBOX_SOCIAL;
                refreshResults();
                break;
            case R.id.menu_item_promotions:
                mType = Constant.TYPE_L;
                mObj = Constant.L_INBOX_PROMOTIONS;
                refreshResults();
                break;
            case R.id.menu_item_starred:
                mType = Constant.TYPE_Q;
                mObj = Constant.Q_IS_STARRED;
                refreshResults();
                break;
            case R.id.menu_item_important:
                mType = Constant.TYPE_Q;
                mObj = Constant.Q_IS_IMPORTANT;
                refreshResults();
                break;
            case R.id.menu_item_sent:
                mType = Constant.TYPE_L;
                mObj = Constant.L_SENT;
                refreshResults();
                break;
            case R.id.menu_item_spam:
                mType = Constant.TYPE_Q;
                mObj = Constant.Q_IN_INBOX;
                refreshResults();
                break;
            case R.id.menu_item_drafts:
                mType = Constant.TYPE_Q;
                mObj = Constant.Q_IN_INBOX;
                refreshResults();
                break;
            case R.id.menu_item_settings:
                Toast.makeText(GmailMainActivity.this, "Not Now!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_item_call_chat:
                startActivityChatAndCall();
                break;
            case R.id.menu_item_change_account:
                chooseAccount();
                break;
            default:
                break;

        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startActivityChatAndCall() {

    }


    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
//            refreshResults();
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case Constant.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case Constant.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {

                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        SharedPreferences settings =  getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(Constant.PREF_ACCOUNT_NAME, accountName);
                        editor.apply();

                        View view = mNavigationView.inflateHeaderView(R.layout.gmail_nav_header_main);
                        tvProfileName = (TextView) view.findViewById(R.id.tv_profile_name);
                        tvProfileEmail = (TextView) view.findViewById(R.id.tv_profile_email);
                        tvProfileEmail.setText(accountName);
                        tvProfileName.setText(accountName.substring(0, accountName.indexOf("@")).toUpperCase());
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(GmailMainActivity.this, "Account unspecified.", Toast.LENGTH_LONG).show();
                }
                break;
            case Constant.REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a set of data from the Gmail API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        if (isGooglePlayServicesAvailable()) {
            if (mCredential.getSelectedAccountName() == null) {
                chooseAccount();
            } else {
                if (isDeviceOnline()) {
                    if(mTask!=null){
                        mTask.cancel(true);
                    }
                    mTask = new ListMsgTask(mCredential);
                    mTask.execute();
                } else {
                    Toast.makeText(GmailMainActivity.this, "No network connection available.", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(GmailMainActivity.this, "Google Play Services required. After installing, close and relaunch this app.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                mCredential.newChooseAccountIntent(), Constant.REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                GmailMainActivity.this,
                Constant.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final GlobalApplication global = (GlobalApplication) getApplicationContext();
        global.setmCredential(mCredential);
        global.setmService(mService);

        MailItem item = mMailAdapter.getItem(position);

        Intent intent = new Intent(GmailMainActivity.this, MailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.KEY_PARCEL_MAIL_ITEM, item);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mTask!=null){
//            mTask.cancel(true);
        }
    }

    /**
     * An asynchronous task that handles the Gmail API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class ListMsgTask extends AsyncTask<Void, Void, List<MailItem>> {

        public ListMsgTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Gmail API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Gmail API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<MailItem> doInBackground(Void... params) {
            try {
                if(mType==Constant.TYPE_L){
                    return GmailMgr.listMessagesWithLabels(mService, Constant.USER, (List<String>)mObj, null);
                }else{
                    return GmailMgr.listMessagesWithLabels(mService, Constant.USER, null , (String)mObj);
                }

//                Log.i(TAG, "=======================listThreadsMatchingQuery");
//                GmailMgr.listThreadsMatchingQuery(mService, Constant.USER, null);
//                Log.i(TAG, "=======================listThreadsWithLabels UNREAD");
//                GmailMgr.listThreadsWithLabels(mService, Constant.USER, Arrays.asList(new String[]{"UNREAD"}));
//                GmailMgr.sendMessage(mService, Constant.USER, GmailMgr.createEmail("ngovanvinh19941@gmail.com", Constant.USER, "subject", "Content"));
//                GmailMgr.listLabels(mService);
//                Log.i(TAG, "=======================listMessagesWithLabels");
//                return GmailMgr.listMessagesWithLabels(mService, Constant.USER, Constant.L_INBOX_PERSONAL, null);
//                return GmailMgr.listMessagesWithLabels(mService, Constant.USER, null , Constant.Q_IN_UPDATE);
            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<MailItem> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                Toast.makeText(GmailMainActivity.this, "No results returned.", Toast.LENGTH_LONG).show();
            } else {
                mMailAdapter = new ListMailAdapter(GmailMainActivity.this, output);
                mLvData.setAdapter(mMailAdapter);
                Toast.makeText(GmailMainActivity.this, "Size : " + output.size(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "Size : " + output.size());
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Constant.REQUEST_AUTHORIZATION);
                }/* else {
                    Toast.makeText(GmailMainActivity.this, "The following error occurred:\n" + mLastError.getMessage(), Toast.LENGTH_LONG).show();
                }*/
            } else {
                Toast.makeText(GmailMainActivity.this, "Request cancelled.", Toast.LENGTH_LONG).show();
            }
        }
    }


    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            ivProfileIcon.setImageBitmap(result);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }


}
