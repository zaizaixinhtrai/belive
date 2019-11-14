package com.appster;

import android.app.Application;
import android.content.Context;

import com.appster.customview.taggableedittext.TaggableEditText;
import com.appster.services.BeLivePushManager;
import com.appster.webservice.AppsterWebserviceAPI;
import com.data.di.ApiServiceModule;
import com.data.di.RoomDbModule;
import com.data.di.SchedulerModule;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import rx.Scheduler;

import static com.data.di.ApiServiceModule.APP_AUTHEN;
import static com.data.di.ApiServiceModule.BASE_URL;
import static com.data.di.SchedulerModule.IO;
import static com.data.di.SchedulerModule.UI;

/**
 * Created by thanhbc on 3/17/18.
 */

@Singleton
@Component(modules = {AppModule.class, ApiServiceModule.class, SchedulerModule.class, RoomDbModule.class,
        AndroidInjectionModule.class,ActivityBuilder.class,})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        AppComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder serviceUrl(@Named(BASE_URL) String url);

//        @BindsInstance
//        Builder appAuthen(@Named(APP_AUTHEN) String authen);
    }

    void inject(AppsterApplication application);
    void inject(TaggableEditText taggableEditText);
    void inject(BeLivePushManager beLivePushManager);

    Context context();


    @Named(UI)
    Scheduler uiThread();

    @Named(IO)
    Scheduler executorThread();

    AppsterWebserviceAPI service();

    @Named(APP_AUTHEN)
    String authen();

//    TransactionRepositoryComponent plus(TransactionRepositoryModule transactionRepositoryModule);
}
