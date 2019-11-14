package com.data.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by thanhbc on 5/18/18.
 */
data class LiveShowEntity(@field:SerializedName("Status")
                          val status: Int,

                          @field:SerializedName("Options")
                          val options: List<OptionsItem>? = null,

                          @field:SerializedName("StreamId")
                          val streamId: Int? = null,

                          @field:SerializedName("Description")
                          val description: String? = null,

                          @field:SerializedName("EndTime")
                          val endTime: String? = null,

                          @field:SerializedName("Title")
                          val title: String? = null,

                          @field:SerializedName("BeginTimestamp")
                          val beginTime: Long,

                          @field:SerializedName("ShowTypeId")
                          val showTypeId: Int,

                          @field:SerializedName("Image")
                          val image: String? = null,

                          @field:SerializedName("Slug")
                          val slug: String? = null,

                          @field:SerializedName("ShowTypeName")
                          val showTypeName: String? = null,

                          @field:SerializedName("Username")
                          val username: String,

                          @field:SerializedName("UserId")
                          val userId: Int,

                          @field:SerializedName("Id")
                          val id: Int,

                          @field:SerializedName("IsFollow")
                          val isFollow: Boolean = false,
                          @field:SerializedName("Balance")
                          val balanceEntity: BalanceEntity?,

                          @field:SerializedName("IsTrivia")
                          val isTrivia: Boolean,

                          @field:SerializedName("CountryCode")
                          val countryCode: String?,
                          @field:SerializedName("StampBalance")
                          val stampBalanceEntity: StampBalanceEntity?,
                          @field:SerializedName("isOgx")
                          val isOgx: Boolean = false)

data class OptionsItem(@field:SerializedName("ActionType")
                       val actionType: Int,

                       @field:SerializedName("ActionValue")
                       var actionValue: String? = null,

                       @field:SerializedName("Params")
                       val params: String,

                       @field:SerializedName("Icon")
                       val icon: String? = null,

                       @field:SerializedName("OptionType")
                       val optionType: Int,

                       @field:SerializedName("Name")
                       val name: String? = null)

data class LiveShowStatusEntity(@field:SerializedName("Status")
                                val status: Int,
                                @field:SerializedName("StreamId")
                                val streamId: Int? = null,
                                @field:SerializedName("Slug")
                                val slug: String? = null,
                                @field:SerializedName("WaitingTimeSec")
                                val waitingTime: Int)

data class BalanceEntity(@field:SerializedName("CashoutUrl")
                         val cashoutUrl: String?,
                         @field:SerializedName("Amount")
                         val amount: Double = 0.0,
                         @field:SerializedName("Message")
                         val message: String,
                         @field:SerializedName("Currency")
                         val currency: String,
                         @field:SerializedName("WalletGroup")
                         val walletGroup: Int)

data class StampBalanceEntity(@field:SerializedName("CashoutUrl")
                              val cashoutUrl: String?,
                              @field:SerializedName("Amount")
                              val amount: Double = 0.0)