package com.domain.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by DatTN on 11/1/2018.
 */
class PrizeCollectModel : TreatCollectModel {

    constructor(id: Int,
                title: String,
                description: String,
                image: String,
                userPoint: Int,
                amount:Int) : super(id, title, description, image, amount, 0, false) {
        this.userPoint = userPoint
    }

    var userPoint = 0

    constructor(parcel: Parcel) : super(parcel) {
        userPoint = parcel.readInt()
        amount = parcel.readInt()
    }

    companion object CREATOR : Parcelable.Creator<PrizeCollectModel> {
        override fun createFromParcel(parcel: Parcel): PrizeCollectModel {
            return PrizeCollectModel(parcel)
        }

        override fun newArray(size: Int): Array<PrizeCollectModel?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(userPoint)
        parcel.writeInt(amount)
    }

    override fun describeContents(): Int {
        return 0
    }
}