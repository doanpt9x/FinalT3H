package com.t3h.mail;

import com.google.api.services.gmail.GmailScopes;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Shiloh Marion on 10/03/2016.
 */
public class Constant {

    public static final String KEY_DEVELOPER = "AIzaSyBPEV6hDknqpvgQIMBWyHOe7K_s3Buhhes";

    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    public static final String KEY_MAIL_ID = "accountName";

    public static final String KEY_PARCEL_MAIL_ITEM = "key_parcel_email_id";

    public static final String KEY_MAIL_FROM = "key_mail_from";
    public static final String KEY_MAIL_TIME = "key_mail_time";
    public static final String KEY_MAIL_IS_ATTACHED = "key_mail_is_attached";
    public static final String KEY_MAIL_SNIPPET= "key_mail_snippet";
    public static final String KEY_MAIL_IS_STARRED = "key_mail_is_starred";

    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final String PREF_ACCOUNT_EMAIL = "accountEmail";
    public static final String USER = "me";

    public static final String Q_HAS_ATTACHMENT = "has:attachment";

    public static final String Q_IN_INBOX = "in:inbox";
    public static final String Q_IN_TRASH = "in:trash";
    public static final String Q_IS_SPAM = "in:spam";
    public static final String Q_IN_ANY_WHERE = "in:anywhere";

    public static final String Q_IS_STARRED = "is:starred";
    public static final String Q_IS_UNREAD = "is:unread";
    public static final String Q_IS_READ = "is:read";
    public static final String Q_IS_IMPORTANT = "is:important";

    public static final String Q_IN_UPDATE = "in:updates";
    public static final String Q_CATEGORY_UPDATE = "category:updates";


    public static final List<String> SCOPES = Arrays.asList(new String[]{GmailScopes.MAIL_GOOGLE_COM, GmailScopes.GMAIL_COMPOSE});
    public static final List<String> L_INBOX = Arrays.asList(new String[]{"INBOX"});
    public static final List<String> L_INBOX_PERSONAL = Arrays.asList(new String[]{"INBOX", "CATEGORY_PERSONAL"});
    public static final List<String> L_INBOX_SOCIAL = Arrays.asList(new String[]{"INBOX", "CATEGORY_SOCIAL"});
    public static final List<String> L_INBOX_PROMOTIONS = Arrays.asList(new String[]{"INBOX", "CATEGORY_PROMOTIONS"});
    public static final List<String> L_INBOX_UPDATE_UNREAD = Arrays.asList(new String[]{"INBOX", "CATEGORY_UPDATES", "UNREAD"});
    public static final List<String> L_SENT = Arrays.asList(new String[]{"SENT"});

    public static final int TYPE_Q = 1011;
    public static final int TYPE_L = 1012;
}
