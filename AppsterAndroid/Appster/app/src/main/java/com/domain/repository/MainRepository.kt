package com.domain.repository

import rx.Observable

/**
 * Created by Ngoc on 5/17/2018.
 */

interface MainRepository{
    fun checkHasLiveVideo () : Observable<Boolean>
}
