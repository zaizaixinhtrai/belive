package com.appster.webservice;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.utility.LocaleUtil;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by User on 9/8/2015.
 */
public class AppsterWebServices {
    private static volatile AppsterWebserviceAPI sServiceInstance;
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

    private AppsterWebServices() {
    }


    public static AppsterWebserviceAPI get(Retrofit retrofit) {
        if (sServiceInstance == null) {
            synchronized (AppsterWebServices.class) {
                if (sServiceInstance == null) {
                    sServiceInstance = provideApiService(retrofit);
                }
            }
        }
        return sServiceInstance;
    }

    public static AppsterWebserviceAPI get() {
        if (sServiceInstance == null) {
            synchronized (AppsterWebServices.class) {
                if (sServiceInstance == null) {
                    sServiceInstance = provideApiService(provideRetrofit(BuildConfig.API_ENDPOINT
                            , provideGsonConverterFactory(provideGson())
                            , provideRxJavaIOAdapterFactory(Schedulers.io())
                            , provideRxJavaMainAdapterFactory(AndroidSchedulers.mainThread())
                            , provideHttpClient(null)));
                }
            }
        }
        return sServiceInstance;
    }

    public static void resetAppsterWebserviceAPI() {
//        sServiceInstance = null;
    }


    static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC);
    }


    static OkHttpClient provideHttpClient(HttpLoggingInterceptor httpInterceptor) {
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


    static Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Boolean.class, sBooleanAsIntAdapter)
                .registerTypeAdapter(boolean.class, sBooleanAsIntAdapter)
                .create();
    }


    static Converter.Factory provideGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }


    static CallAdapter.Factory provideRxJavaIOAdapterFactory(Scheduler scheduler) {
        return RxJavaCallAdapterFactory.createWithScheduler(scheduler);
    }


    static CallAdapter.Factory provideRxJavaMainAdapterFactory(Scheduler scheduler) {
        return new RxJavaObserveOnMainThread.ObserveOnMainCallAdapterFactory(scheduler);
    }


    static Retrofit provideRetrofit(String baseUrl, Converter.Factory converterFactory,
                                    CallAdapter.Factory ioCallAdapterFactory,
                                    CallAdapter.Factory mainCallAdapterFactory,
                                    OkHttpClient client) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(mainCallAdapterFactory)
                .addCallAdapterFactory(ioCallAdapterFactory)
                .build();
    }

    static AppsterWebserviceAPI provideApiService(Retrofit retrofit) {
        return retrofit.create(AppsterWebserviceAPI.class);
    }
}
