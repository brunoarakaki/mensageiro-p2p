package com.poli.usp.whatsp2p.data.remote.interceptors;

import java.io.IOException;

import com.poli.usp.whatsp2p.data.local.PreferencesHelper;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by mobile2you on 11/08/16.
 */
public class ReceivedCookieInterceptor implements Interceptor {

    private static final String RESPONSE_HEADER_COOKIE = "Set-Cookie";

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        String cookie = originalResponse.headers().get(RESPONSE_HEADER_COOKIE);
        try {
            if (!cookie.isEmpty() && !PreferencesHelper.getInstance().getSessionCookie().equals(cookie)) {
                PreferencesHelper.getInstance().putSessionCookie(cookie);
            }
        } catch (NullPointerException e) {
           //Do Nothing
        }

        return originalResponse;
    }
}
