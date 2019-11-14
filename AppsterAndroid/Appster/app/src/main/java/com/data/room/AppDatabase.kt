package com.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.appster.models.FollowItemModel

@Database(entities = [FollowItemModel::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    companion object {
        const val NAME = "belivedb"
    }

    abstract fun followItemDao(): FollowItemDao
}