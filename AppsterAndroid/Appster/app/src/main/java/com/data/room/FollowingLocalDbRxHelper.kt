package com.data.room

import com.appster.customview.taggableedittext.FollowUserView
import com.appster.models.FollowItemModel
import com.apster.common.SingleThreadScheduler
import rx.Observable
import rx.Observer
import rx.schedulers.Schedulers

class FollowingLocalDbRxHelper(private val dao: FollowItemDao) {

    fun insertOne(followItem: FollowItemModel): Observable<String>  {
        val observable = Observable.create<String> { sub ->
            dao.insert(followItem)
            sub.onNext("")
            sub.onCompleted()
        }
        return observable.subscribeOn(SingleThreadScheduler.get())
    }

    fun bulkInsert(payload: List<FollowItemModel>): Observable<String> {
        val observable = Observable.create<String> { sub ->
            dao.insert(payload)
            sub.onNext("")
            sub.onCompleted()
        }
        return observable.subscribeOn(SingleThreadScheduler.get())
    }

    fun filter(keyword: String, excludedIds: Array<String>): Observable<List<FollowItemModel>> {
        val observable = Observable.create<List<FollowItemModel>> { sub ->
            val filteringList = dao.search(keyword)
            sub.onNext(filteringList.filter { !excludedIds.contains(it.userId) })
            sub.onCompleted()
        }
        return observable.subscribeOn(Schedulers.io())
    }

    fun findByNameList(names: Array<String>): Observable<List<FollowItemModel>> {
        val observable = Observable.create<List<FollowItemModel>> { sub ->
            sub.onNext(dao.findByNameList(names))
            sub.onCompleted()
        }
        return observable.subscribeOn(Schedulers.io())
    }

    fun erase(): Observable<String> {
        val observable = Observable.create<String> { sub ->
            dao.erase()
            sub.onNext("")
            sub.onCompleted()
        }
        return observable.subscribeOn(SingleThreadScheduler.get())
    }

    fun map(original: List<FollowItemModel>): List<FollowUserView> {
        return original.map { FollowUserView(it.userId, it.userName, it.displayName, it.profilePic) }
    }

}

abstract class  OnlyContentObserver<T>: Observer<T> {
    override fun onError(e: Throwable?) {}
    override fun onCompleted() {}
}