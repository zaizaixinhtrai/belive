package com.appster.features.points

import com.domain.models.TreatCollectModel

/**
 *  Created by DatTN on 10/26/2018
 */
class DailyBonus(id: Int, title: String, thumbUrl: String, bgColorCode: String?, var countDown: Int, prizes: List<Prize>, coverImage: String?) :
        MysteryBox(id, title, thumbUrl, bgColorCode, 0, prizes, coverImage) {

    override var viewType: Int = MysteryBoxViewType.VIEW_DAILY
        get() = MysteryBoxViewType.VIEW_DAILY

    fun getTreatCollectionModel(): TreatCollectModel {
        return TreatCollectModel(123, "Ahaha", "Ahihi", "", 200, 1, true)
    }
}