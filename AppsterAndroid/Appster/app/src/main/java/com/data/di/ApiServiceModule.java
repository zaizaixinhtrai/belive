package com.data.di;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.utility.AppsterUtility;
import com.appster.utility.LocaleUtil;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.AppsterWebserviceAPI;
import com.appster.webservice.RxJavaObserveOnMainThread;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import timber.log.Timber;

/**
 * Created by thanhbc on 3/17/18.
 */

@Module
public class ApiServiceModule {
    public static final String BASE_URL = "base_url";
    private static final String OBSERVE_MAIN = "observer_main";
    private static final String SUBSCRIBE_IO = "subscribe_io";
    public static final String APP_AUTHEN = "authen";
    String mBaseUrl = "";
    String mAuthen = "";
//    public ApiServiceModule(String baseUrl,String authen) {
//        mBaseUrl = baseUrl;
//        mAuthen = authen;
//    }

    private static final TypeAdapter<Boolean> sBooleanAsIntAdapter = new TypeAdapter<Boolean>() {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            switch (peek) {
                case BOOLEAN:
                    return in.nextBoolean();
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                    return in.nextInt() != 0;
                case STRING:
                    return Boolean.parseBoolean(in.nextString());
                default:
                    throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
            }
        }
    };

//    @Provides
//    @Named(BASE_URL)
//    String provideBaseUrl() {
//        return mBaseUrl;
//    }
//
    @Provides
    @Named(APP_AUTHEN)
    String provideAuthen() {
        return AppsterUtility.getAuth();
    }


    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC);
    }

    @Provides
    @Singleton
    OkHttpClient provideHttpClient(HttpLoggingInterceptor httpInterceptor) {
        final OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
        if (BuildConfig.DEBUG) {
            httpClient.networkInterceptors().add(new StethoInterceptor());
        }
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        try {
            if (AppsterApplication.mAppPreferences.isUserLogin()) {
                httpClient.addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("user_id", AppsterApplication.mAppPreferences.getUserModel() != null ? AppsterApplication.mAppPreferences.getUserModel().getUserId() : "")
                            .build();
                    return chain.proceed(request);
                });
            }
        } catch (Exception ex) {
            Timber.d(ex.getMessage());
        }
        httpClient.addInterceptor(chain -> {
            Request request = chain.request().newBuilder().header("language", String.valueOf(LocaleUtil.getLocaleNumber())).build();
            return chain.proceed(request);
        });
        return httpClient.build();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Boolean.class, sBooleanAsIntAdapter)
                .registerTypeAdapter(boolean.class, sBooleanAsIntAdapter)
                .create();
    }

    @Provides
    @Singleton
    Converter.Factory provideGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    @Named(SUBSCRIBE_IO)
    CallAdapter.Factory provideRxJavaIOAdapterFactory(@Named(SchedulerModule.IO) Scheduler scheduler) {
        return RxJavaCallAdapterFactory.createWithScheduler(scheduler);
    }


    @Provides
    @Singleton
    @Named(OBSERVE_MAIN)
    CallAdapter.Factory provideRxJavaMainAdapterFactory(@Named(SchedulerModule.UI) Scheduler scheduler) {
        return new RxJavaObserveOnMainThread.ObserveOnMainCallAdapterFactory(scheduler);
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(@Named(BASE_URL) String baseUrl, Converter.Factory converterFactory,
                             @Named(SUBSCRIBE_IO) CallAdapter.Factory ioCallAdapterFactory,
                             @Named(OBSERVE_MAIN) CallAdapter.Factory mainCallAdapterFactory,
                             OkHttpClient client) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(mainCallAdapterFactory)
                .addCallAdapterFactory(ioCallAdapterFactory)
                .build();
    }

    @Provides
    @Singleton
    AppsterWebserviceAPI provideApiService(Retrofit retrofit) {
        return AppsterWebServices.get(retrofit);
    }
}
