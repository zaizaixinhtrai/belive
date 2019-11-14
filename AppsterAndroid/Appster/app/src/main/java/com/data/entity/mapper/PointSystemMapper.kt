package com.data.entity.mapper

import com.appster.AppsterApplication
import com.appster.R
import com.appster.core.adapter.DisplayableItem
import com.appster.features.points.DailyBonus
import com.appster.features.points.MysteryBox
import com.appster.features.points.MysteryBoxViewType
import com.appster.features.points.Prize
import com.data.entity.*
import com.domain.models.EarnPointsModel
import com.domain.models.PrizeBagModel
import com.domain.models.PrizeItem
import java.util.ArrayList
import com.domain.models.TreatCollectModel
import javax.inject.Inject

/**
 *  Created by DatTN on 10/24/2018
 */
class PointSystemMapper @Inject constructor() {

    fun map(entity: EarnPointsEntity): EarnPointsModel {
        return entity.let { EarnPointsModel(it.userPoints, it.PointInfoURL, it.message) }
    }

    fun map(src: List<MysteryBoxEntity>?): List<DisplayableItem> {
//        val newSrc = src?.filter { item -> !item.prizeEntities.isEmpty() }
        return src?.map { mysteryBoxEntity ->
            MysteryBox(mysteryBoxEntity.id, (AppsterApplication.getApplication().mContext).getString(R.string.premium_mystery_box), mysteryBoxEntity.thumbUrl, mysteryBoxEntity.bgColorCode, mysteryBoxEntity.pointUse,
                    mapPrizes(mysteryBoxEntity.prizeEntities), mysteryBoxEntity.coverImage)
        } ?: mutableListOf()
    }

    fun map(src: DailyBonusEntity?): DailyBonus {
        return src?.run {
            DailyBonus(id, title, thumbUrl, bgColorCode, countDown, mapPrizes(prizeEntities), coverImage)
        } ?: DailyBonus(-1, "", "", "", -1, mutableListOf(), "")
    }

    fun mapPrizes(src: List<PrizeEntity>?): List<Prize> {
        return src?.map {
            mapPrize(it)
        } ?: mutableListOf()
    }

    fun map(src: List<TreatEntity>?): DailyBonus {
        val dailyBonus = fakeDailyBonus()
        if (src != null) {
            val newSrc = src.take(5)
            dailyBonus.prizes = newSrc.map {
                mapPrize(it)
            }
        }
        return dailyBonus
    }

    private fun fakeDailyBonus(): DailyBonus {
        return DailyBonus(15, (AppsterApplication.getApplication().mContext).getString(R.string.free_mystery_box), "", "#ff0000", 10, mutableListOf(), "")
    }

    fun mapPrize(prizeEntity: TreatEntity): Prize {
        return Prize(prizeEntity.id, if (prizeEntity.title.isNullOrEmpty()) "" else prizeEntity.title!!, if (prizeEntity.description.isNullOrEmpty()) "" else prizeEntity.description!!, if (prizeEntity.image.isNullOrEmpty()) "" else prizeEntity.image!!,
                1, false, 0,
                prizeEntity.amount, 0, "", "",
                "", "", 0, 0)
    }

    fun mapPrize(prizeEntity: PrizeEntity): Prize {
        return Prize(prizeEntity.id, prizeEntity.title, prizeEntity.desc, prizeEntity.thumbUrl,
                prizeEntity.type, prizeEntity.limited, prizeEntity.quantity,
                prizeEntity.amount, prizeEntity.giftId, prizeEntity.storeBrief, prizeEntity.termConditions,
                prizeEntity.contactInfo, prizeEntity.urlInfo, prizeEntity.expireDate, prizeEntity.status)
    }

    fun mapDailyPrizes(src: List<DailyPrizeEntity>?): List<Prize> {
        return src?.map {
            Prize(it.id, "Daily + ${it.title}", it.desc, it.thumbUrl,
                    it.type, it.limited, it.quantity,
                    it.amount, it.giftId, it.storeBrief, it.termConditions,
                    it.contactInfo, it.urlInfo, it.expireDate, it.status)
        } ?: mutableListOf()
    }

    fun mapDailyBonusPrizes(src: List<TreatEntity>?): List<Prize> {
        return src?.map {
            Prize(it.id, if (it.title.isNullOrEmpty()) "" else it.title!!, if (it.description.isNullOrEmpty()) "" else it.description!!, if (it.image.isNullOrEmpty()) "" else it.image!!,
                    0, false, 0,
                    it.amount, 0, "", "",
                    "", "", 0, 0)
        } ?: mutableListOf()
    }

    fun transform(entity: List<PrizeBagEntity>?): List<DisplayableItem> {
        val listItems = ArrayList<PrizeBagModel>()
        if (entity == null || entity.isEmpty()) return listItems

        listItems.addAll(entity.map {
            PrizeBagModel(id = it.id,
                    prizeItem = it.prizeItemEntity?.run { PrizeItem(id, name, title, image, type, limited, quantity, amount, giftId, storeBrief, termConditions, contactInfo, infoUrl, expireDate) },
                    name = it.name,
                    email = it.email,
                    created = it.created,
                    redeemDate = it.redeemDate,
                    sentDate = it.sentDate,
                    status = it.status)
        }
        )
        return ArrayList(listItems).sortedWith(Comparator { p1, p2 ->
            when {
                p1.status < p2.status -> 1
                p1.status == p2.status -> 0
                else -> -1
            }
        })
    }

    fun mapPickedPrize(prizeEntity: PrizeEntity): TreatCollectModel {
        return TreatCollectModel(prizeEntity.id, prizeEntity.title, prizeEntity.desc, prizeEntity.thumbUrl,
                prizeEntity.amount, 0, false)
    }

}