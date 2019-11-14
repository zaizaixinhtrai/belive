package com.appster.sendgift

class GiftItemModel {


    var categoryId: String? = null

    var giftImage = ""


    var giftId: String? = null

    var amount: Int = 0

    var giftName: String? = null

    var costBean: Int = 0

    var giftColor: Int = 0

    var giftType: Int = 0

    var isChoose = false


    fun updateAmount(amount: Int) {
        //        if(this.amount >0) {
        //            this.amount--;
        this.amount = amount
        //        }
    }
}
