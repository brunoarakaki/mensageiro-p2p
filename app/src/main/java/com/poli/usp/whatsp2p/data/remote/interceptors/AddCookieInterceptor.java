package com.poli.usp.whatsp2p.data.remote.interceptors;

import com.poli.usp.whatsp2p.data.local.PreferencesHelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mobile2you on 11/08/16.
 */
public class AddCookieInterceptor implements Interceptor {

    private static final String REQUEST_HEADER_COOKIE = "Cookie";

    @Override
    public Response intercept(Chain chain) throws IOException {
        String id = PreferencesHelper.getInstance().getSessionCookie();
        Request original = chain.request();
        Request request = original;

        //Only adds if there's a cookie
        if(!id.isEmpty()) {
            request = original.newBuilder()
                    .addHeader(REQUEST_HEADER_COOKIE, id)
                    .method(original.method(), original.body())
                    .build();
        }

        return chain.proceed(request);
    }
}
