package com.t3h.mail;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//import java.nio.file.FileSystems;
//import java.nio.file.Files;


/**
 * Created by Shiloh Marion on 10/03/2016.
 */
public class GmailMgr {

    private static final String TAG = "GmailMgr";

    /**
     * List all Messages of the user's mailbox with labelIds applied.
     *
     * @param service  Authorized Gmail API instance.
     * @param userId   User's email address. The special value "me"
     *                 can be used to indicate the authenticated user.
     * @param labelIds Only return Messages with these labelIds applied.
     * @throws IOException
     */
    public static List<MailItem> listMessagesWithLabels(Gmail service, String userId, List<String> labelIds, String querry) throws IOException {

        ListMessagesResponse response = null;
        if (labelIds != null) {
            response = service.users().messages().list(userId).setLabelIds(labelIds).setMaxResults((long) 25).execute();
        } else {
            response = service.users().messages().list(userId).setQ(querry).setMaxResults((long) 25).execute();
        }


        List<Message> messages = new ArrayList<Message>();

        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            break;
//            if (response.getNextPageToken() != null) {
//                String pageToken = response.getNextPageToken();
//                response = service.users().messages().list(userId).setLabelIds(labelIds)
//                        .setPageToken(pageToken).execute();
//            } else {
//                break;
//            }
        }

        List<MailItem> list = new ArrayList<MailItem>();
        for (Message msg : messages) {
            String id = msg.getId();
            Message m = service.users().messages().get(Constant.USER, id).setFormat("full").execute();

            String from = null;
            String time = null;

            MessagePart payload = m.getPayload();
            for (MessagePartHeader partHeader : payload.getHeaders()) {
                if ("From".equals(partHeader.getName())) {
                    from = partHeader.getValue().toString();
                    if (from.indexOf("<") > 0) {
                        from = from.substring(0, from.indexOf("<"));
                    }
                } else {
                    if ("Date".equals(partHeader.getName())) {
                        time = partHeader.getValue().toString().substring(5, 11);
                    }
                }
                if (time != null && from != null) {
                    break;
                }
            }

            boolean isAttached = false;
            List<MessagePart> messagePartList = payload.getParts();
            if (messagePartList != null) {
                for (MessagePart part : payload.getParts()) {
                    if (part != null && part.getFilename() != null && part.getFilename().length() > 0) {
                        isAttached = true;
                        break;
                    }
                }
            }
            boolean isStarred = m.getLabelIds().contains("STARRED");
            list.add(new MailItem(id, from, time, m.getSnippet(), isAttached, isStarred));
        }

        return list;
    }

    public static String getMessageBody(Gmail service, String id) throws IOException {

        Message m = service.users().messages().get(Constant.USER, id).setFormat("full").execute();
        StringBuffer mailBody = new StringBuffer("");
        String body = "";
        String mimeType = m.getPayload().getMimeType();
        List<MessagePart> parts = m.getPayload().getParts();
        if (mimeType.contains("alternative")) {
            for (MessagePart part : parts) {
//                mailBody.append(new String(Base64.decodeBase64(part.getBody().getData().getBytes())));
                body = new String(Base64.decodeBase64(part.getBody().getData().getBytes()));
            }
        }
        return body;
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to       Email address of the receiver.
     * @param from     Email address of the sender, the mailbox account.
     * @param subject  Subject of the email.
     * @param bodyText Body text of the email.
     * @return MimeMessage to be used to send email.
     * @throws MessagingException
     */
    public static MimeMessage createEmail(String to, String from, String subject,
                                          String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        InternetAddress tAddress = new InternetAddress(to);
        InternetAddress fAddress = new InternetAddress(from);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

//    /**
//     * Create a MimeMessage using the parameters provided.
//     *
//     * @param to       Email address of the receiver.
//     * @param from     Email address of the sender, the mailbox account.
//     * @param subject  Subject of the email.
//     * @param bodyText Body text of the email.
//     * @param fileDir  Path to the directory containing attachment.
//     * @param filename Name of file to be attached.
//     * @return MimeMessage to be used to send email.
//     * @throws MessagingException
//     */
//    public static MimeMessage createEmailWithAttachment(String to, String from, String subject,
//                                                        String bodyText, String fileDir, String filename) throws MessagingException, IOException {
//        Properties props = new Properties();
//        Session session = Session.getDefaultInstance(props, null);
//
//        MimeMessage email = new MimeMessage(session);
//        InternetAddress tAddress = new InternetAddress(to);
//        InternetAddress fAddress = new InternetAddress(from);
//
//        email.setFrom(fAddress);
//        email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
//        email.setSubject(subject);
//
//        MimeBodyPart mimeBodyPart = new MimeBodyPart();
//        mimeBodyPart.setContent(bodyText, "text/plain");
//        mimeBodyPart.setHeader("Content-Type", "text/plain; charset=\"UTF-8\"");
//
//        Multipart multipart = new MimeMultipart();
//        multipart.addBodyPart(mimeBodyPart);
//
//        mimeBodyPart = new MimeBodyPart();
//        DataSource source = new FileDataSource(fileDir + filename);
//
//        mimeBodyPart.setDataHandler(new DataHandler(source));
//        mimeBodyPart.setFileName(filename);
//        String contentType = Files.probeContentType(FileSystems.getDefault()
//                .getPath(fileDir, filename));
//        mimeBodyPart.setHeader("Content-Type", contentType + "; name=\"" + filename + "\"");
//        mimeBodyPart.setHeader("Content-Transfer-Encoding", "base64");
//
//        multipart.addBodyPart(mimeBodyPart);
//
//        email.setContent(multipart);
//
//        return email;
//    }

    /**
     * Create a Message from an email
     *
     * @param email Email to be set to raw of message
     * @return Message containing base64url encoded email.
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage email) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            email.writeTo(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId  User's email address. The special value "me"
     *                can be used to indicate the authenticated user.
     * @param email   Email to be sent.
     * @throws MessagingException
     * @throws IOException
     */
    public static void sendMessage(Gmail service, String userId, MimeMessage email) {
        Message message = createMessageWithEmail(email);
        try {
            message = service.users().messages().send(userId, message).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//
//    /**
//     * Fetch a list of Gmail labels attached to the specified account.
//     *
//     * @return List of Strings labels.
//     * @throws IOException
//     */
//    public static List<String> listLabels(Gmail mService) throws IOException {
//        ListLabelsResponse listResponse = null;
//        listResponse = mService.users().labels().list(Constant.USER).execute();
//        List<String> list = new ArrayList<>();
//        list.add("ID : NAME");
//        Log.i(TAG, "ID : NAME");
//        for (Label label : listResponse.getLabels()) {
//            list.add(label.getId() + " : " + label.getName());
//            Log.i(TAG, label.getId() + " : " + label.getName());
//        }
//        return list;
//    }
//
//    /**
//     * List all Messages of the user's mailbox matching the query.
//     *
//     * @param service Authorized Gmail API instance.
//     * @param userId  User's email address. The special value "me"
//     *                can be used to indicate the authenticated user.
//     * @param query   String used to filter the Messages listed.
//     * @throws IOException
//     */
//    public static List<MailItem> listMessagesMatchingQuery(Gmail service, String userId,
//                                                           String query) throws IOException {
//        ListMessagesResponse response = service.users().messages().list(userId).setQ(query)
//                .setMaxResults((long) 25).execute();
//        List<Message> messages = new ArrayList<Message>();
//        while (response.getMessages() != null) {
//            messages.addAll(response.getMessages());
//            if (response.getNextPageToken() != null) {
//                String pageToken = response.getNextPageToken();
//                response = service.users().messages().list(userId).setQ(query)
//                        .setPageToken(pageToken).execute();
//            } else {
//                break;
//            }
//            break;
//        }
//
//        List<MailItem> list = new ArrayList<MailItem>();
//        for (Message msg : messages) {
//            String id = msg.getId();
//            Message m = service.users().messages().get(Constant.USER, id).setFormat("full").execute();
//
//            String from = null;
//            String time = null;
//
//            MessagePart payload = m.getPayload();
//            for (MessagePartHeader partHeader : payload.getHeaders()) {
//                if ("From".equals(partHeader.getName())) {
//                    from = partHeader.getValue().toString();
//                    if (from.indexOf("<") > 0) {
//                        from = from.substring(0, from.indexOf("<"));
//                    }
//                } else {
//                    if ("Date".equals(partHeader.getName())) {
//                        time = partHeader.getValue().toString().substring(5, 11);
//                    }
//                }
//                if (time != null && from != null) {
//                    break;
//                }
//            }
//
//            boolean isAttached = false;
//            List<MessagePart> messagePartList = payload.getParts();
//            if (messagePartList != null) {
//                for (MessagePart part : payload.getParts()) {
//                    if (part!=null && part.getFilename() != null && part.getFilename().length() > 0) {
//                        isAttached = true;
//                        break;
//                    }
//                }
//            }
//
//            boolean isStarred = m.getLabelIds().contains("STARRED");
//
//            list.add(new MailItem(id, from, time, m.getSnippet(), isAttached, isStarred));
//        }
//
//        return list;
//    }
//
//
//    /**
//     * List all Threads of the user's mailbox matching the query.
//     *
//     * @param service Authorized Gmail API instance.
//     * @param userId  User's email address. The special value "me"
//     *                can be used to indicate the authenticated user.
//     * @param query   String used to filter the Threads listed.
//     * @throws IOException
//     */
//    public static List<String> listThreadsMatchingQuery(Gmail service, String userId,
//                                                        String query) throws IOException {
//        List<String> list = new ArrayList<>();
//        ListThreadsResponse response = service.users().threads().list(userId).setQ(query).execute();
//        List<Thread> threads = new ArrayList<>();
//        while (response.getThreads() != null) {
//            threads.addAll(response.getThreads());
//            if (response.getNextPageToken() != null) {
//                String pageToken = response.getNextPageToken();
//                response = service.users().threads().list(userId).setQ(query).setPageToken(pageToken).execute();
//            } else {
//                break;
//            }
//        }
//
//        for (Thread thread : threads) {
//            Log.i(TAG, thread.toPrettyString());
//            list.add(thread.toPrettyString());
//        }
//        return list;
//    }
//
//    /**
//     * List all Threads of the user's mailbox with labelIds applied.
//     *
//     * @param service  Authorized Gmail API instance.
//     * @param userId   User's email address. The special value "me"
//     *                 can be used to indicate the authenticated user.
//     * @param labelIds String used to filter the Threads listed.
//     * @throws IOException
//     */
//    public static List<MailItem> listThreadsWithLabels(Gmail service, String userId, List<String> labelIds) throws IOException {
//        ListThreadsResponse response = service.users().threads().list(userId).setLabelIds(labelIds).execute();
//        List<Thread> threads = new ArrayList<>();
//        while (response.getThreads() != null) {
//            threads.addAll(response.getThreads());
//            if (response.getNextPageToken() != null) {
//                String pageToken = response.getNextPageToken();
//                response = service.users().threads().list(userId).setLabelIds(labelIds)
//                        .setPageToken(pageToken).execute();
//            } else {
//                break;
//            }
//        }
//
//        List<MailItem> list = new ArrayList<>();
//        for (Thread thread : threads) {
//            String id = thread.getId();
//            Message m = service.users().messages().get(Constant.USER, id).setFormat("full").execute();
//            String from = null;
//            String time = null;
//            List<MessagePartHeader> listHeader = m.getPayload().getHeaders();
//            for (int i = 0; i < listHeader.size(); i++) {
//                if ("From".equals(listHeader.get(i).getName())) {
//                    from = m.getPayload().getHeaders().get(i).getValue().toString();
//                    if (from.indexOf("<") > 0) {
//                        from = from.substring(0, from.indexOf("<"));
//                    }
//                } else {
//                    if ("Date".equals(listHeader.get(i).getName())) {
//                        time = m.getPayload().getHeaders().get(i).getValue().toString().substring(5, 11);
//                    }
//                }
//                if (time != null && from != null) {
//                    break;
//                }
//            }
//            list.add(new MailItem(id, from, time, m.getSnippet(), true, true));
//        }
//        return list;
//    }
//
//    /**
//     * Get the attachments in a given email.
//     *
//     * @param service   Authorized Gmail API instance.
//     * @param userId    User's email address. The special value "me"
//     *                  can be used to indicate the authenticated user.
//     * @param messageId ID of Message containing attachment..
//     * @throws IOException
//     */
//    public static boolean isAttached(Gmail service, String userId, String messageId)
//            throws IOException {
//        Message message = service.users().messages().get(userId, messageId).execute();
//        List<MessagePart> parts = message.getPayload().getParts();
//        for (MessagePart part : parts) {
//            if (part.getFilename() != null && part.getFilename().length() > 0) {
//                return true;
//            }
//        }
//        return false;
//    }
}
