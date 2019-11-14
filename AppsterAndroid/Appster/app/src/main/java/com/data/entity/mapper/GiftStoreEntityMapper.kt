package com.data.entity.mapper

import com.appster.sendgift.GiftItemModel
import com.appster.webservice.response.BaseResponse
import com.data.entity.GiftStoreEntity
import com.domain.models.GiftStoreModel

/**
 * Created by thanhbc on 3/27/18.
 */
class GiftStoreEntityMapper {
    fun transform(entity: BaseResponse<GiftStoreEntity>?): GiftStoreModel? {
       return entity?.data?.let {
            GiftStoreModel(it.totalGem, it.totalGold, it.giftItems.map {
                GiftItemModel().apply {
                    giftId = it.giftId
                    giftName = it.giftName
                    categoryId = it.categoryId
                    amount = it.amount
                    giftImage = it.giftImage
                    costBean = it.costBean
                    giftType = it.type
                }
            })
        }
    }

}