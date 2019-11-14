package com.data.di;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by thanhbc on 3/17/18.
 */

@Module
public class SchedulerModule {

    public static final String IO = "executor_thread";
    public static final String UI = "ui_thread";
    @Provides
    @Singleton
    @Named(IO)
    Scheduler provideExecutorThread() {
        return Schedulers.io();
    }

    @Provides
    @Singleton
    @Named(UI)
    Scheduler provideUiThread() {
        return AndroidSchedulers.mainThread();
    }
}
