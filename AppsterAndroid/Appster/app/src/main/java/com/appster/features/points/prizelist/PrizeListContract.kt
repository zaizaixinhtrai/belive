package com.appster.features.points.prizelist

import com.appster.features.mvpbase.BaseContract
import com.appster.features.points.Prize

interface PrizeListContract {

    interface View : BaseContract.View{
        fun showPrizeList(prizeList: List<Prize>)
    }

    interface UserActions : BaseContract.Presenter<PrizeListContract.View>{
        fun loadPrizeList(boxType: Int, boxId: Int)
    }
}
