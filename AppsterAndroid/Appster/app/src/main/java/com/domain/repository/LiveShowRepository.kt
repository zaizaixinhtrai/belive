package com.domain.repository

import com.appster.core.adapter.DisplayableItem
import com.domain.models.LiveShowFriendNumberModel
import com.domain.models.LiveShowStatus
import rx.Observable

/**
 * Created by thanhbc on 5/18/18.
 */
interface LiveShowRepository{
    fun getLiveShows() : Observable<List<DisplayableItem>>
    fun checkShows(showId: Int) : Observable<LiveShowStatus>
    fun getFriendNumber(showId:Int):Observable<LiveShowFriendNumberModel>
}