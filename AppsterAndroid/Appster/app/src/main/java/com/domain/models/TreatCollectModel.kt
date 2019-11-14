package com.domain.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Ngoc on 6/14/2018.
 */
open class TreatCollectModel(var id: Int,
                             var title: String,
                             var description: String,
                             var image: String,
                             var amount: Int,
                             var treatRank: Int,
                             var isClaim: Boolean) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readByte().toInt() != 0)

    companion object CREATOR : Parcelable.Creator<TreatCollectModel> {
        override fun createFromParcel(parcel: Parcel): TreatCollectModel {
            return TreatCollectModel(parcel)
        }

        override fun newArray(size: Int): Array<TreatCollectModel?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeInt(amount)
        parcel.writeInt(treatRank)
        parcel.writeByte(if (isClaim) 1.toByte() else 0.toByte())
    }

    override fun describeContents(): Int {
        return 0
    }
}