package com.appster;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by thanhbc on 3/17/18.
 */

@Module
public class AppModule {
    @Provides
    @Singleton
    public Context provideContext(Application application) {
        return application;
    }
}
