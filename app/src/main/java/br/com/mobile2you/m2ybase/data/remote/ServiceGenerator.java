package br.com.mobile2you.m2ybase.data.remote;

import java.util.List;

import br.com.mobile2you.m2ybase.BuildConfig;
import br.com.mobile2you.m2ybase.NetworkConstants;
import br.com.mobile2you.m2ybase.data.remote.interceptors.AddCookieInterceptor;
import br.com.mobile2you.m2ybase.data.remote.interceptors.ReceivedCookieInterceptor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mobile2you on 29/11/16.
 */

public class ServiceGenerator {

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE));
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, List<Interceptor> interceptors) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        for (Interceptor interceptor : interceptors) {
            httpClient.addInterceptor(interceptor);
        }
        return retrofit.create(serviceClass);
    }

    public static <S> S createServiceWithCookieInterceptors(Class<S> serviceClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(new ReceivedCookieInterceptor())
                .addInterceptor(new AddCookieInterceptor());
        return retrofit.create(serviceClass);
    }

    public static <S> S createServiceWithCookieInterceptors(Class<S> serviceClass, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(new ReceivedCookieInterceptor())
                .addInterceptor(new AddCookieInterceptor());
        return retrofit.create(serviceClass);
    }

    public static <S> S createServiceWithCookieInterceptors(Class<S> serviceClass, List<Interceptor> interceptors) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new ReceivedCookieInterceptor())
                .addInterceptor(new AddCookieInterceptor());
        for (Interceptor interceptor : interceptors) {
            httpClient.addInterceptor(interceptor);
        }
        return retrofit.create(serviceClass);
    }
}
