package com.poli.usp.whatsp2p.data.remote;

import com.poli.usp.whatsp2p.NetworkConstants;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by mobile2you on 29/11/16.
 */

public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
    private final RxJavaCallAdapterFactory original;


    public final String TIMEOUT_ERROR = "Erro inesperado, por favor tente novamente.";
    public final String UNKNOWN_ERROR = "Erro desconhecido, não foi possível completar sua requisição.";

    private RxErrorHandlingCallAdapterFactory() {
        original = RxJavaCallAdapterFactory.create();
    }

    public static CallAdapter.Factory create() {
        return new RxErrorHandlingCallAdapterFactory();
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return new RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit));
    }

    private static class RxCallAdapterWrapper implements CallAdapter<Observable<?>> {
        private final Retrofit retrofit;
        private final CallAdapter<?> wrapped;

        public RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<?> wrapped) {
            this.retrofit = retrofit;
            this.wrapped = wrapped;
        }

        @Override
        public Type responseType() {
            return wrapped.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R> Observable<?> adapt(Call<R> call) {
            return ((Observable) wrapped.adapt(call)).onErrorResumeNext(new Func1<Throwable, Observable>() {
                @Override
                public Observable call(Throwable throwable) {
                    return Observable.error(asRetrofitException(throwable));
                }
            });
        }

        private RetrofitException asRetrofitException(Throwable throwable) {
            if(throwable instanceof HttpException) {
                // We had non-2XX http error
                switch (((HttpException) throwable).code()) {
                    case NetworkConstants.CODE_FORBIDDEN:
//                    Clear shared preferences (logout)
//                    Intent loginIntent = new Intent(BaseApplication.getContext(), LoginActivity.class);
//                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
//                    mContext.startActivity(loginIntent);
                        break;
                }
                HttpException httpException = (HttpException) throwable;
                Response response = httpException.response();
                return RetrofitException.httpError(response.raw().request().url().toString(), response, retrofit);

            }

            if (throwable instanceof IOException) {
                // A network or conversion error happened
                if (throwable instanceof UnknownHostException) {
                    // Network error
                    throwable = new IOException("Rede Indisponível");
                    return RetrofitException.networkError((IOException) throwable);
                } else {
                    // Conversion error
                    throwable = new IOException("Erro de versão, pode ser necessário atualizar seu aplicativo.");
                    return RetrofitException.networkError((IOException) throwable);
                }
            }

            // We don't know what happened. We need to simply convert to an unknown error
            return RetrofitException.unexpectedError(throwable);
        }
    }
}
