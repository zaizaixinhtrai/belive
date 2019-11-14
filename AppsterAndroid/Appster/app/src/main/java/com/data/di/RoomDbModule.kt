package com.data.di

import android.content.Context
import androidx.room.Room
import com.data.room.AppDatabase
import com.data.room.FollowItemDao
import com.data.room.FollowingLocalDbRxHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomDbModule {

    @Provides @Singleton
    fun provideBeliveRoomInstance(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME).build()
    }

    @Provides
    fun provideFollowItemDao(db: AppDatabase) = db.followItemDao()

    @Provides
    fun provideFollowerLocalDbRxHelper(followItemDao: FollowItemDao) = FollowingLocalDbRxHelper(followItemDao)
}