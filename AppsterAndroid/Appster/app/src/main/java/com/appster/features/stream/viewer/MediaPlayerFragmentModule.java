package com.appster.features.stream.viewer;

import com.appster.base.FragmentScope;
import com.data.di.TriviaRepositoryModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by thanhbc on 4/26/18.
 */

@Module
public abstract class MediaPlayerFragmentModule {
    @ContributesAndroidInjector(modules = TriviaRepositoryModule.class)
    @FragmentScope
    abstract MediaPlayerFragment provideMediaPlayerFragmentFactory();
}
