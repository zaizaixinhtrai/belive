package com.data.entity

/**
 *  Created by DatTN on 10/23/2018
 */
class DailyBonusEntity(val id: Int, val title: String, val thumbUrl: String, val bgColorCode: String?, var countDown: Int, val prizeEntities: List<PrizeEntity>,val coverImage:String?)
