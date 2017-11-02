package br.com.mobile2you.m2ybase;

/**
 * Created by mobile2you on 18/08/16.
 */
public class Constants {
    // TODO: 11/04/17 fix package name
    public static final String PACKAGE_NAME = "br.com.mobile2you.m2ybase";

    public static final  String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME;
    // TODO: 11/04/17 fix email
    public static final String CONTACT_EMAIL = "atendimento@mobile2you.com.br";

    //GENERAL
    public static final String LANGUAGE_PT = "pt";
    public static final String COUNTRY_BR = "BR";

    // PERMISSION REQUEST CALLBACK ID
    public static final int PERMISSION_REQUEST_PHONE_STATE = 1;

    //API URLs
    public static final String BASE_URL = "http://polls.apiblueprint.org/";
    public static final String LOGIN_URL = BASE_URL + "polls/";
    public static final String ACCOUNT_URL = BASE_URL + "account/" ;

    public static final String EXTRA_ACTIVITY_TITLE = PACKAGE_NAME + "EXTRA_ACTIVITY_TITLE";
    public static final String EXTRA_URL = PACKAGE_NAME + "EXTRA_URL";
    public static final String EXTRA_MYSELF = PACKAGE_NAME + "EXTRA_MYSELF";
    public static final String EXTRA_CONTACT = PACKAGE_NAME + "EXTRA_CONTACT";
    public static final String EXTRA_DIRECT_CONNECTION = PACKAGE_NAME + "EXTRA_DIRECT_CONNECTION";


//    public static final String PARSE_APP_ID = "Tu3tFfazCKY9KrQkw3CwkXzkpE8RjEHhqCjvAYkB";
//    public static final String PARSE_APPCLIENT = "cpfPBghgaHvTgjdPwsgxdmVLaCmGz58dBFjWYfKK";
//    public static final String FIREBASe_SENDER_KEY = "354006123142";
//    public static final int REQUEST_EXIT = 0;

//    DATABSE CONSTANTS
    public static final String DB_CONTACTS_TABLE = "contacts";
    public static final String DB_CONTACT_FIELD_ID = "_id";
    public static final String DB_CONTACT_FIELD_NAME = "name";
    public static final String DB_CONTACT_FIELD_IP = "ip";
    public static final String DB_CONTACT_FIELD_PORT = "port";
    public static final String DB_CONTACT_FIELD_SIGN_ENCODED_KEY = "sign_encoded_key";
    public static final String DB_CONTACT_FIELD_CHAT_ENCODED_KEY = "chat_encoded_key";
    public static final String DB_MESSAGES_TABLE = "messages";
    public static final String DB_MESSAGES_FIELD_MESSAGE_ID = "_id";
    public static final String DB_MESSAGES_FIELD_SENDER_ID = "sender_id";
    public static final String DB_MESSAGES_FIELD_RECEIVER_ID = "receiver_id";
    public static final String DB_MESSAGES_FIELD_SENDER_NAME = "sender_name";
    public static final String DB_MESSAGES_FIELD_RECEIVER_NAME = "receiver_name";
    public static final String DB_MESSAGES_FIELD_TEXT = "text";
    public static final String DB_MESSAGES_FIELD_SENT_AT = "sent_at";
    public static final String FILTER_CHAT_RECEIVER = "FILTER_CHAT_RECEIVER";
    public static final String FILTER_DHT_CONNECTION = "FILTER_DHT_CONNECTION";
    public static final String FILTER_SIGNATURE_UPDATE = "FILTER_SIGNATURE_UPDATE";

    public static final String NSD_SERVICE_NAME = "whatsp2p";
    public static final String NSD_SERVICE_TYPE = "_whatsp2p._tcp";

    public static final String PGP_KEY_ALIAS = "whatsp2p";

}

