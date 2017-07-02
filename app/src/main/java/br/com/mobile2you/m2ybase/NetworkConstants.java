package br.com.mobile2you.m2ybase;

/**
 * Created by mobile2you on 29/11/16.
 */

public class NetworkConstants {
    //1xx Informational
    public static final int CODE_WITHOUT_NETWORK = 0;

    //2xx Success
    public static final int CODE_RESPONSE_SUCCESS = 200;

    //3xx Redirection
    public static final int CODE_NOT_FOUND = 340;

    //4xx Client Error
    public static final int CODE_TIMEOUT = 408;
    public static final int CODE_RESPONSE_UNAUTHORIZED = 401;
    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_FORBIDDEN = 403;

    //5xx Server Error
    public static final int CODE_UNKNOWN = 500;

    //FACEBOOK PERMISSIONS
    public static final String[] FACEBOOK_PERMISSIONS = {"public_profile", "email"};
    public static final String FACEBOOK_REQUEST_KEY = "fields";
    public static final String FACEBOOK_REQUEST_VALUE = "id, first_name, last_name, email";

    //API URLs
    public static final String BASE_URL = "http://polls.apiblueprint.org/";
    public static final String LOGIN_URL =  BASE_URL + "polls/";
    public static final String ACCOUNT_URL = BASE_URL + "account/";
}
