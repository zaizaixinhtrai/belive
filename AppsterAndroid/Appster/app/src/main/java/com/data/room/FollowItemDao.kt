package com.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.appster.models.FollowItemModel

@Dao
interface FollowItemDao {

    @Query("select * from FollowItemModel where UserName in (:names)")
    fun findByNameList(names: Array<String>): List<FollowItemModel>

    @Query("select * from FollowItemModel where UserName like '%'||:keyword||'%' or DisplayName like '%'||:keyword||'%'")
    fun search(keyword: String): List<FollowItemModel>

    @Query("delete from FollowItemModel")
    fun erase()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(followItems: List<FollowItemModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: FollowItemModel)
}