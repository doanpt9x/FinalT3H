package com.t3h.final_t3h;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.t3h.call.OutgoingCallActivity;
import com.t3h.common.CommonMethod;
import com.t3h.common.CommonValue;
import com.t3h.common.GlobalApplication;
import com.t3h.common.OnLoadedInformation;
import com.t3h.common.Sound;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by Android on 2/7/2016.
 */
public class MessageActivity extends AppCompatActivity implements View.OnClickListener,
        ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "MessageActivity";
    private static final int REQUEST_ATTACH = 0;
    private static final int REQUEST_PICTURE = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int NUMBER_COLLECTION_EMOTICON = 20;

    private RelativeLayout layoutMain, menu;
    private LinearLayout emoticons;
    private InputMethodManager inputMethodManager;
    private ListView listViewMessage;
    private MessageOnlineAdapter messageAdapter;
    private String outGoingMessageId, inComingMessageId;
    private ReentrantLock reentrantLock = new ReentrantLock();
    private EditText edtContent;
    private TextView txtStatus;
    private ImageView btnAttach, btnSend, imgEmoticon, btnPicture, btnCamera, btnMap;
    private BroadcastMessage broadcastMessage;
    private String inComingFullName, content;
    private CommonMethod commonMethod;
    private boolean isOpenEmoticons = false;
    private TabLayout tabs;
    private ViewPager viewPager;
    private int[] emoticonIds;
    private EmoticonAdapter[] emoticonAdapters = new EmoticonAdapter[NUMBER_COLLECTION_EMOTICON];
    private ArrayList<CollectionEmoticonItem> collectionEmoticonItems;
    private CollectionEmoticonAdapter collectionEmoticonAdapter;
    private Uri capturedImageURI;
    private Sound sound;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_message);
        GlobalApplication.startActivityMessage = true;

        sound = new Sound(this, 10);

        commonMethod = CommonMethod.getInstance();

        Intent intent = this.getIntent();
        outGoingMessageId = ParseUser.getCurrentUser().getObjectId();
        inComingMessageId = intent.getStringExtra(CommonValue.INCOMING_CALL_ID);
        inComingFullName = intent.getStringExtra(CommonValue.INCOMING_MESSAGE_FULL_NAME);

        this.initializeToolbar();
        this.initializeComponent();
        this.registerBroadcastMessage();

        messageAdapter = new MessageOnlineAdapter(this, inComingMessageId);
        messageAdapter.setOnLoadedInformation(new OnLoadedInformation() {
            @Override
            public void onLoaded(boolean result, boolean isOnline) {
                listViewMessage.setAdapter(messageAdapter);
                MessageActivity.this.getData();
                toolbar.setSubtitle(isOnline ? "Online" : "Offline");
            }
        });
    }

    private void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(inComingFullName);
    }

    private void initializeComponent() {
        inputMethodManager = (InputMethodManager) this.
                getSystemService(Context.INPUT_METHOD_SERVICE);

        layoutMain = (RelativeLayout) findViewById(R.id.layoutMain);
        layoutMain.getViewTreeObserver().addOnGlobalLayoutListener(this);
        menu = (RelativeLayout) findViewById(R.id.menu);
        emoticons = (LinearLayout) findViewById(R.id.emoticons);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        listViewMessage = (ListView) findViewById(R.id.listViewMessage);
        listViewMessage.setSelected(false);
        btnAttach = (ImageView) findViewById(R.id.btnAttach);
        btnAttach.setOnClickListener(this);
        btnPicture = (ImageView) findViewById(R.id.btnPicture);
        btnPicture.setOnClickListener(this);
        btnCamera = (ImageView) findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(this);
        btnMap = (ImageView) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(this);
        btnSend = (ImageView) findViewById(R.id.btnSend);
        btnSend.setEnabled(false);
        btnSend.setOnClickListener(this);
        edtContent = (EditText) findViewById(R.id.edtContent);
        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    btnSend.setEnabled(false);
                    btnSend.setImageResource(R.drawable.ic_sent_negative);
                } else {
                    btnSend.setEnabled(true);
                    btnSend.setImageResource(R.drawable.ic_sent_active);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        imgEmoticon = (ImageView) findViewById(R.id.imgEmoticon);
        imgEmoticon.setOnClickListener(this);

        emoticonIds = new int[]{R.drawable.chim1, R.drawable.chim2, R.drawable.chim3,
                R.drawable.chim4, R.drawable.chim5, R.drawable.chim6, R.drawable.chim7,
                R.drawable.chim8, R.drawable.chim8, R.drawable.chim9, R.drawable.chim10,
                R.drawable.chim11, R.drawable.chim12, R.drawable.chim13, R.drawable.chim14,
                R.drawable.chim15,R.drawable.chim16,R.drawable.chim17,R.drawable.chim18
                ,R.drawable.chim19,R.drawable.chim20};
        ArrayList<EmoticonItem> emoticonItems3 = new ArrayList<EmoticonItem>();
        for (int i = 0; i < emoticonIds.length; i++) {
            emoticonItems3.add(new EmoticonItem(emoticonIds[i]));
        }
        emoticonAdapters[3] = new EmoticonAdapter(this, emoticonItems3, inComingMessageId, inComingFullName);

        emoticonIds = new int[]{R.drawable.dog1, R.drawable.dog2, R.drawable.dog3,
                R.drawable.dog4, R.drawable.dog5, R.drawable.dog6, R.drawable.dog7,
                R.drawable.dog8, R.drawable.dog9, R.drawable.dog10, R.drawable.dog11,
                R.drawable.dog12, R.drawable.dog13, R.drawable.dog14, R.drawable.dog15,
                R.drawable.dog16, R.drawable.dog17, R.drawable.dog18, R.drawable.dog19,
                R.drawable.dog20, R.drawable.dog21, R.drawable.dog22, R.drawable.dog23,
                R.drawable.dog24, R.drawable.dog25, R.drawable.dog26, R.drawable.dog27,
                R.drawable.dog28, R.drawable.dog29, R.drawable.dog30, R.drawable.dog31,
                R.drawable.dog32, R.drawable.dog33, R.drawable.dog34, R.drawable.dog35,
                R.drawable.dog36, R.drawable.dog37, R.drawable.dog38, R.drawable.dog39,
                R.drawable.dog40};
        ArrayList<EmoticonItem> emoticonItems4 = new ArrayList<EmoticonItem>();
        for (int i = 0; i < emoticonIds.length; i++) {
            emoticonItems4.add(new EmoticonItem(emoticonIds[i]));
        }
        emoticonAdapters[4] = new EmoticonAdapter(this, emoticonItems4, inComingMessageId, inComingFullName);

        emoticonIds = new int[]{R.drawable.gil1, R.drawable.gil2, R.drawable.gil3,
                R.drawable.gil4, R.drawable.gil5, R.drawable.gil6, R.drawable.gil7,
                R.drawable.gil8, R.drawable.gil9, R.drawable.gil10, R.drawable.gil11,
                R.drawable.gil12, R.drawable.gil13, R.drawable.gil14, R.drawable.gil15,
                R.drawable.gil16, R.drawable.gil17, R.drawable.gil18, R.drawable.gil19,
                R.drawable.gil20, R.drawable.gil21, R.drawable.gil22, R.drawable.gil23,
                R.drawable.gil24, R.drawable.gil25, R.drawable.gil26, R.drawable.gil27,
                R.drawable.gil28, R.drawable.gil29, R.drawable.gil30, R.drawable.gil31,
                R.drawable.gil32, R.drawable.gil33, R.drawable.gil34, R.drawable.gil35,
                R.drawable.gil36, R.drawable.gil37, R.drawable.gil38, R.drawable.gil39,
                R.drawable.gil40};
        ArrayList<EmoticonItem> emoticonItems2 = new ArrayList<EmoticonItem>();
        for (int i = 0; i < emoticonIds.length; i++) {
            emoticonItems2.add(new EmoticonItem(emoticonIds[i]));
        }
        emoticonAdapters[2] = new EmoticonAdapter(this, emoticonItems2, inComingMessageId, inComingFullName);

        emoticonIds = new int[]{R.drawable.bi1, R.drawable.bi2, R.drawable.bi3,
                R.drawable.bi4, R.drawable.bi5, R.drawable.bi6, R.drawable.bi7,
                R.drawable.bi8, R.drawable.bi9, R.drawable.bi10, R.drawable.bi11,
                R.drawable.bi12, R.drawable.bi13, R.drawable.bi14, R.drawable.bi15,
                R.drawable.bi16, R.drawable.bi17, R.drawable.bi18, R.drawable.bi19,
                R.drawable.bi20, R.drawable.bi21, R.drawable.bi22, R.drawable.bi23,
                R.drawable.bi24, R.drawable.bi26, R.drawable.bi27, R.drawable.bi28,};
        ArrayList<EmoticonItem> emoticonItems1 = new ArrayList<EmoticonItem>();
        for (int i = 0; i < emoticonIds.length; i++) {
            emoticonItems1.add(new EmoticonItem(emoticonIds[i]));
        }
        emoticonAdapters[1] = new EmoticonAdapter(this, emoticonItems1, inComingMessageId, inComingFullName);

        emoticonIds = new int[]{R.drawable.rb1, R.drawable.rb2, R.drawable.rb3,
                R.drawable.rb4, R.drawable.rb5, R.drawable.rb6, R.drawable.rb7,
                R.drawable.rb8, R.drawable.rb9, R.drawable.rb10, R.drawable.rb11,
                R.drawable.rb12, R.drawable.rb13, R.drawable.rb14, R.drawable.rb15,
                R.drawable.rb16};
        ArrayList<EmoticonItem> emoticonItems0 = new ArrayList<EmoticonItem>();
        for (int i = 0; i < emoticonIds.length; i++) {
            emoticonItems0.add(new EmoticonItem(emoticonIds[i]));
        }
        emoticonAdapters[0] = new EmoticonAdapter(this, emoticonItems0, inComingMessageId, inComingFullName);

        collectionEmoticonItems = new ArrayList<>();
        collectionEmoticonItems.add(new CollectionEmoticonItem(emoticonAdapters[0]));
        collectionEmoticonItems.add(new CollectionEmoticonItem(emoticonAdapters[1]));
        collectionEmoticonItems.add(new CollectionEmoticonItem(emoticonAdapters[2]));
        collectionEmoticonItems.add(new CollectionEmoticonItem(emoticonAdapters[3]));
        collectionEmoticonItems.add(new CollectionEmoticonItem(emoticonAdapters[4]));

        collectionEmoticonAdapter = new CollectionEmoticonAdapter(this, collectionEmoticonItems);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(collectionEmoticonAdapter);

        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.getTabAt(3).setIcon(R.drawable.chim1);
        tabs.getTabAt(4).setIcon(R.drawable.dog1);
        tabs.getTabAt(2).setIcon(R.drawable.gil1);
        tabs.getTabAt(1).setIcon(R.drawable.bi1);
        tabs.getTabAt(0).setIcon(R.drawable.rb1);
    }

    private void getData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        String[] ids = new String[]{outGoingMessageId, inComingMessageId};
        query.whereContainedIn("senderId", Arrays.asList(ids));
        query.whereContainedIn("receiverId", Arrays.asList(ids));
        query.orderByDescending("createdAt");
        query.setLimit(100);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null) {
                    return;
                }
                for (ParseObject message : list) {
                    reentrantLock.lock();
                    int type = MessageOnlineAdapter.TYPE_INCOMING;
                    if (outGoingMessageId.equals(message.getString("senderId"))) {
                        type = MessageOnlineAdapter.TYPE_OUTGOING;
                    }
                    String content = message.getString("content");
                    String date = message.getString("date");
                    if (!content.contains(CommonValue.MMC_KEY)) {
                        messageAdapter.addMessage(0, new MessagesItem(type,
                                SpannableString.valueOf(content), 0, date));
                    } else {
                        String key = content.substring(0, CommonValue.KEY_LENGTH + 1);
                        content = content.substring(CommonValue.KEY_LENGTH + 1);
                        switch (key) {
                            case CommonValue.MMC_KEY_EMOTICON:
                                int emoticonId = Integer.parseInt(content);
                                SpannableString emoticon = commonMethod.toSpannableString(
                                        MessageActivity.this, emoticonId);
                                messageAdapter.addMessage(0, new MessagesItem(type, emoticon, 1, date));
                                break;
                            case CommonValue.MMC_KEY_FILE:
                                messageAdapter.addMessage(0, new MessagesItem(type,
                                        SpannableString.valueOf(content), 2, date));
                                break;
                            case CommonValue.MMC_KEY_PICTURE:
                                messageAdapter.addMessage(0, new MessagesItem(type,
                                        SpannableString.valueOf(content), 3, date));
                                break;
                        }

                    }
                    reentrantLock.unlock();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAttach:
                Intent intentAttach = new Intent();
                intentAttach.setAction(Intent.ACTION_GET_CONTENT);
                intentAttach.setType("file/*");
                this.startActivityForResult(intentAttach, REQUEST_ATTACH);
                break;
            case R.id.btnSend:
                content = edtContent.getText().toString();
                edtContent.setText("");
                txtStatus.setText("Sending...");
                Intent intentSend = new Intent();
                intentSend.setAction(CommonValue.ACTION_SEND_MESSAGE);
                intentSend.putExtra(CommonValue.INCOMING_MESSAGE_ID, inComingMessageId);
                intentSend.putExtra(CommonValue.INCOMING_MESSAGE_FULL_NAME, inComingFullName);
                intentSend.putExtra(CommonValue.MESSAGE_CONTENT, content);
                intentSend.putExtra(CommonValue.MMC_KEY_DATE, commonMethod.getMessageDate());
                MessageActivity.this.sendBroadcast(intentSend);
                break;
            case R.id.imgEmoticon:
                if (!isOpenEmoticons) {
                    isOpenEmoticons = true;
                    emoticons.setVisibility(View.VISIBLE);
                    menu.setVisibility(View.GONE);
                    listViewMessage.setSelection(messageAdapter.getCount());
                } else {
                    isOpenEmoticons = false;
                    emoticons.setVisibility(View.GONE);
                    menu.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btnPicture:
                Intent intentPicture = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                this.startActivityForResult(intentPicture, REQUEST_PICTURE);
                break;
            case R.id.btnCamera:
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intentCamera.resolveActivity(getPackageManager()) != null) {
                    @SuppressLint("SimpleDateFormat")
                    String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String fileName = "MMC_" + date;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Images.Media.TITLE, fileName);
                    capturedImageURI = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);
                    this.startActivityForResult(intentCamera, REQUEST_CAMERA);
                }
                break;
            case R.id.btnMap:
                Intent intentMap = new Intent();
                intentMap.setAction(CommonValue.ACTION_MAP);
                intentMap.putExtra(CommonValue.USER_ID, inComingMessageId);
                intentMap.putExtra(CommonValue.INCOMING_MESSAGE_FULL_NAME,
                        ((GlobalApplication) MessageActivity.this.getApplication()).getFullName());
                this.sendBroadcast(intentMap);
                Snackbar snackbar = Snackbar.make(view, "Please waiting...", Snackbar.LENGTH_LONG)
                        .setAction("ACTION", null);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(Color.parseColor("#4caf50"));
                snackbar.show();
                break;
        }
    }

    private void registerBroadcastMessage() {
        if (broadcastMessage == null) {
            broadcastMessage = new BroadcastMessage();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(CommonValue.STATE_MESSAGE_SENT);
            intentFilter.addAction(CommonValue.STATE_MESSAGE_INCOMING);
            intentFilter.addAction(CommonValue.STATE_MESSAGE_DELIVERED);
            MessageActivity.this.registerReceiver(broadcastMessage, intentFilter);
        }
    }

    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        layoutMain.getWindowVisibleDisplayFrame(rect);
        int heightDiff = layoutMain.getRootView().getHeight() - (rect.bottom + rect.top);
        if (heightDiff > 100) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    menu.setVisibility(View.GONE);
                    emoticons.setVisibility(View.GONE);
                    isOpenEmoticons = false;
                }
            });
        } else if (heightDiff <= 100 && emoticons.getVisibility() == View.GONE) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    menu.setVisibility(View.VISIBLE);
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        txtStatus.setText("Sending...");
        switch (requestCode) {
            case REQUEST_ATTACH:
                String pathFile = data.getData().getPath();
                Intent intentAttach = new Intent();
                intentAttach.setAction(CommonValue.ACTION_SEND_MESSAGE);
                intentAttach.putExtra(CommonValue.INCOMING_MESSAGE_ID, inComingMessageId);
                intentAttach.putExtra(CommonValue.INCOMING_MESSAGE_FULL_NAME, inComingFullName);
                intentAttach.putExtra(CommonValue.MESSAGE_CONTENT, pathFile);
                intentAttach.putExtra(CommonValue.MMC_KEY, CommonValue.MMC_KEY_FILE);
                intentAttach.putExtra(CommonValue.MMC_KEY_DATE, commonMethod.getMessageDate());
                MessageActivity.this.sendBroadcast(intentAttach);
                break;
            case REQUEST_PICTURE:
                String pathPicture = this.getPathFromUri(data.getData());
                Intent intentPicture = new Intent();
                intentPicture.setAction(CommonValue.ACTION_SEND_MESSAGE);
                intentPicture.putExtra(CommonValue.INCOMING_MESSAGE_ID, inComingMessageId);
                intentPicture.putExtra(CommonValue.INCOMING_MESSAGE_FULL_NAME, inComingFullName);
                intentPicture.putExtra(CommonValue.MESSAGE_CONTENT, pathPicture);
                intentPicture.putExtra(CommonValue.MMC_KEY, CommonValue.MMC_KEY_PICTURE);
                intentPicture.putExtra(CommonValue.MMC_KEY_DATE, commonMethod.getMessageDate());
                MessageActivity.this.sendBroadcast(intentPicture);
                break;
            case REQUEST_CAMERA:
                Cursor cursor = this.getContentResolver().query(capturedImageURI,
                        new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String capturedImageFilePath = cursor.getString(index);
                cursor.close();
                Intent intentCamera = new Intent();
                intentCamera.setAction(CommonValue.ACTION_SEND_MESSAGE);
                intentCamera.putExtra(CommonValue.INCOMING_MESSAGE_ID, inComingMessageId);
                intentCamera.putExtra(CommonValue.INCOMING_MESSAGE_FULL_NAME, inComingFullName);
                intentCamera.putExtra(CommonValue.MESSAGE_CONTENT, capturedImageFilePath);
                intentCamera.putExtra(CommonValue.MMC_KEY, CommonValue.MMC_KEY_PICTURE);
                intentCamera.putExtra(CommonValue.MMC_KEY_DATE, commonMethod.getMessageDate());
                MessageActivity.this.sendBroadcast(intentCamera);
                break;
        }
    }

    private String getPathFromUri(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri,
                new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }


    private class BroadcastMessage extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CommonValue.STATE_MESSAGE_SENT:
                    sound.playMessageSent();
                    String key = intent.getStringExtra(CommonValue.MMC_KEY);
                    String dateSend = intent.getStringExtra(CommonValue.MMC_KEY_DATE);
                    if (key == null) {
                        messageAdapter.addMessage(messageAdapter.getCount(), new MessagesItem(
                                MessageOnlineAdapter.TYPE_OUTGOING, SpannableString.valueOf(intent
                                .getStringExtra(CommonValue.MESSAGE_CONTENT)), 0, dateSend));
                    } else {
                        String content = intent.getStringExtra(CommonValue.MESSAGE_CONTENT);
                        switch (key) {
                            case CommonValue.MMC_KEY_EMOTICON:
                                int emoticonId = Integer.parseInt(content);
                                messageAdapter.addMessage(messageAdapter.getCount(),
                                        new MessagesItem(MessageOnlineAdapter.TYPE_OUTGOING, commonMethod
                                                .toSpannableString(MessageActivity.this, emoticonId),
                                                1, dateSend));
                                break;
                            case CommonValue.MMC_KEY_FILE:
                                messageAdapter.addMessage(messageAdapter.getCount(),
                                        new MessagesItem(MessageOnlineAdapter.TYPE_OUTGOING, SpannableString
                                                .valueOf(content), 2, dateSend));
                                break;
                            case CommonValue.MMC_KEY_PICTURE:
                                messageAdapter.addMessage(messageAdapter.getCount(),
                                        new MessagesItem(MessageOnlineAdapter.TYPE_OUTGOING,
                                                SpannableString.valueOf(content),
                                                ((GlobalApplication) getApplication()).getPictureSend(),
                                                3, dateSend));
                                break;
                        }
                    }
                    txtStatus.setText("Sent");
                    break;
                case CommonValue.STATE_MESSAGE_INCOMING:
                    String keyIncoming = intent.getStringExtra(CommonValue.MMC_KEY);
                    String dateReceive = intent.getStringExtra(CommonValue.MMC_KEY_DATE);
                    if (keyIncoming == null) {
                        messageAdapter.addMessage(messageAdapter.getCount(),
                                new MessagesItem(MessageOnlineAdapter.TYPE_INCOMING, SpannableString
                                        .valueOf(intent.getStringExtra(CommonValue.MESSAGE_CONTENT)),
                                        0, dateReceive));
                    } else {
                        String content = intent.getStringExtra(CommonValue.MESSAGE_CONTENT);
                        switch (keyIncoming) {
                            case CommonValue.MMC_KEY_EMOTICON:
                                int emoticonId = Integer.parseInt(content);
                                messageAdapter.addMessage(messageAdapter.getCount(),
                                        new MessagesItem(MessageOnlineAdapter.TYPE_INCOMING, commonMethod
                                                .toSpannableString(MessageActivity.this, emoticonId),
                                                1, dateReceive));
                                break;
                            case CommonValue.MMC_KEY_FILE:
                                messageAdapter.addMessage(messageAdapter.getCount(),
                                        new MessagesItem(MessageOnlineAdapter.TYPE_INCOMING, SpannableString
                                                .valueOf(content), 2, dateReceive));
                                break;
                            case CommonValue.MMC_KEY_PICTURE:
                                messageAdapter.addMessage(messageAdapter.getCount(), new
                                        MessagesItem(MessageOnlineAdapter.TYPE_INCOMING, SpannableString
                                        .valueOf(content), 3, dateReceive));
                                break;
                        }
                    }
                    break;
                case CommonValue.STATE_MESSAGE_DELIVERED:
                    txtStatus.setText("Delivered");
                    break;
            }
            listViewMessage.setSelection(messageAdapter.getCount());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_voice_call:
                Intent intentCall = new Intent(this, OutgoingCallActivity.class);
                intentCall.putExtra(CommonValue.INCOMING_CALL_ID, inComingMessageId);
                this.startActivity(intentCall);
                break;
            case R.id.action_view_profile:
                Intent intentProfile = new Intent(this, DetailActivity.class);
                intentProfile.putExtra(CommonValue.USER_ID, inComingMessageId);
                this.startActivity(intentProfile);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isOpenEmoticons) {
            emoticons.setVisibility(View.GONE);
            isOpenEmoticons = false;
            listViewMessage.setSelection(messageAdapter.getCount());
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        GlobalApplication.startActivityMessage = false;
        this.unregisterReceiver(broadcastMessage);
        super.onDestroy();
    }
}