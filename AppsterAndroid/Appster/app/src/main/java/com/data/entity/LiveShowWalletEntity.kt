package com.data.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Ngoc on 6/25/2018.
 */
data class LiveShowWalletEntity(@field:SerializedName("CashoutUrl")
                                val cashoutUrl: String,
                                @field:SerializedName("Amount")
                                val amount: Double,
                                @field:SerializedName("Message")
                                val message: String,
                                @field:SerializedName("Withdrawable")
                                val withDrawable: Boolean,
                                @field:SerializedName("Currency")
                                val currency: String,
                                @field:SerializedName("WalletGroup")
                                val walletGroup: Int)