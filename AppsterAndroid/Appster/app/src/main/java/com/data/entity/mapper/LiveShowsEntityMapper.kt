package com.data.entity.mapper

import com.appster.core.adapter.DisplayableItem
import com.data.entity.LiveShowEntity
import com.data.entity.LiveShowStatusEntity
import com.data.entity.requests.LiveShowFriendNumberEntity
import com.domain.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


/**
 * Created by thanhbc on 5/18/18.
 */
class LiveShowsEntityMapper {
    val triviaCountryCode: String? = null
    fun transform(shows: List<LiveShowEntity>?): List<DisplayableItem> {
        val liveShows = ArrayList<DisplayableItem>()
        if (shows == null || shows.isEmpty()) return liveShows
        liveShows.addAll(shows.map {
            val triviaCountryCode = it.countryCode
            LiveShowModel(showId = it.id,
                    userId = it.userId,
                    userName = it.username,
                    showType = it.showTypeId,
                    showDesc = it.description,
                    showTitle = it.title,
                    countryCode = it.countryCode,
                    showStatus = it.status,
                    showDateTime = it.beginTime,
                    showImage = it.image,
                    isFollow = it.isFollow,
                    slug = it.slug,
                    isTrivia = it.isTrivia,
                    isOgx = it.isOgx,
                    streamId = it.streamId,
                    waitingTime = (it.beginTime - (System.currentTimeMillis() / 1000L)).toInt(),
                    options = it.options?.map {
                        if (it.optionType == 3 && it.params.isNotEmpty()) {
                            it.params.apply {
                                val type = object : TypeToken<Map<String, String>>() {}.type
                                val myMap = Gson().fromJson(this, type) as Map<String, String>
                                it.actionValue = it.actionValue + "?revive=${myMap["ReviveCount"]}"
                            }
                        }
                        LiveShowOption(it.actionType, it.optionType, it.actionValue, it.icon, it.params, triviaCountryCode)
                    },
                    balance = it.balanceEntity?.run { Balance(it.showTypeId, amount, cashoutUrl, message, walletGroup) },
                    stampBalance = it.stampBalanceEntity?.run { StampBalance(cashoutUrl, amount) })
        })
        val lastItem = liveShows.last() as LiveShowModel
        val modifiedLastItem: DisplayableItem = LiveShowLastModel(showId = lastItem.showId,
                userId = lastItem.userId,
                userName = lastItem.userName,
                showType = lastItem.showType,
                showDesc = lastItem.showDesc,
                showTitle = lastItem.showTitle,
                showStatus = lastItem.showStatus,
                showDateTime = lastItem.showDateTime,
                showImage = lastItem.showImage,
                isFollow = lastItem.isFollow,
                slug = lastItem.slug,
                waitingTime = lastItem.waitingTime,
                options = lastItem.options,
                isTrivia = lastItem.isTrivia,
                isOgx = lastItem.isOgx,
                streamId = lastItem.streamId,
                balance = lastItem.balance,
                countryCode = lastItem.countryCode,
                stampBalance = lastItem.stampBalance)
        liveShows.apply { this[lastIndex] = modifiedLastItem }
        return liveShows
    }

    fun transform(shows: LiveShowStatusEntity?): LiveShowStatus? {
        return shows?.let {
            LiveShowStatus(it.status, it.streamId, it.slug, it.waitingTime)
        }

    }

    fun transform(entity: LiveShowFriendNumberEntity?): LiveShowFriendNumberModel? {
        return entity?.let { LiveShowFriendNumberModel(it.message, it.waitingTimeSec) }
    }
}