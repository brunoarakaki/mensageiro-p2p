package br.com.mobile2you.m2ybase.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import br.com.mobile2you.m2ybase.Constants;

/**
 * Created by mobile2you on 11/08/16.
 */
public class PreferencesHelper {

    public static final String SHARED_PREFERENCES_NAME = Constants.PACKAGE_NAME + ".SHARED_PREFERENCES";

    public static final String PREF_SESSION_COOKIE = SHARED_PREFERENCES_NAME + ".PREF_SESSION_COOKIE";
    public static final String PREF_USER_ID = SHARED_PREFERENCES_NAME + ".PREF_USER_ID";

    public static final String PREF_PRIVATE_KEY = SHARED_PREFERENCES_NAME + ".PREF_PRIVATE_KEY";
    public static final String PREF_PUBLIC_KEY = SHARED_PREFERENCES_NAME + ".PREF_PUBLIC_KEY";

    private SharedPreferences mSharedPreferences;

    private PreferencesHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    private static PreferencesHelper sInstance;

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesHelper(context);
        }
    }

    public static synchronized PreferencesHelper getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PreferencesHelper.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void putSessionCookie(String sessionCookie){
        mSharedPreferences.edit().putString(PREF_SESSION_COOKIE, sessionCookie).apply();
    }

    public String getSessionCookie(){
        return mSharedPreferences.getString(PREF_SESSION_COOKIE, "");
    }

    public void putUserId(String id){
        mSharedPreferences.edit().putString(PREF_USER_ID, id).apply();
    }

    //TO-DO: REMOVE
    public byte[] getPrivateKey(){
        return Base64.decode(mSharedPreferences.getString(PREF_PRIVATE_KEY, ""), Base64.DEFAULT);
    }
    public void putPrivateKey(PrivateKey privateKey){
        mSharedPreferences.edit().putString(PREF_PRIVATE_KEY, Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT)).apply();
    }
    public byte[] getPublicKey(){
        return Base64.decode(mSharedPreferences.getString(PREF_PUBLIC_KEY, ""), Base64.DEFAULT);
    }
    public void putPublicKey(PublicKey publicKey){
        mSharedPreferences.edit().putString(PREF_PUBLIC_KEY, Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT)).apply();
    }

    public String getUserId(){
        return mSharedPreferences.getString(PREF_USER_ID, "");
    }

    public boolean isLogged(){
        return !getSessionCookie().isEmpty();
    }

    public void clearSharedPref(){
        mSharedPreferences.edit().clear().apply();
    }
}