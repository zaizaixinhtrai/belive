package com.apster.common

import rx.Scheduler
import rx.schedulers.Schedulers
import java.util.concurrent.Executors

object SingleThreadScheduler {

    private val single = Schedulers.from(Executors.newFixedThreadPool(1))
    fun get(): Scheduler = single

}