package com.appster.features.prizeBag

import android.os.Parcel
import android.os.Parcelable

class PrizeBagViewModel(val bagItemId: Int, val name: String?, val email: String?, val image: String?, val prizeName: String?, val prizeTitle: String?) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())


    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(bagItemId)
        writeString(name)
        writeString(email)
        writeString(email)
        writeString(prizeName)
        writeString(prizeTitle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PrizeBagViewModel> {
        override fun createFromParcel(parcel: Parcel): PrizeBagViewModel {
            return PrizeBagViewModel(parcel)
        }

        override fun newArray(size: Int): Array<PrizeBagViewModel?> {
            return arrayOfNulls(size)
        }
    }

}