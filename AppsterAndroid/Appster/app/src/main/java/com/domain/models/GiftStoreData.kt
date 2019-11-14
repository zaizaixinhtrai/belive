package com.domain.models

import com.appster.sendgift.GiftItemModel

/**
 * Created by thanhbc on 3/27/18.
 */
data class GiftStoreModel(val totalGem: Int, val totalGold: Int, val giftItems: List<GiftItemModel>)
