package com.absinthe.anywhere_.workflow

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class WorkFlow {

    private var mObservable: Observable<FlowNode>? = null
    private val mDisposables = CompositeDisposable()

    fun observe(observable: Observable<FlowNode>): WorkFlow {
        mObservable = observable
        return this
    }

    fun start() {
        if (mObservable == null) {
            return
        }
        mDisposables.add(mObservable!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<FlowNode?>() {
                    override fun onComplete() {
                        Timber.d("onComplete()")
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                    }

                    override fun onNext(flowNode: FlowNode) {
                        flowNode.trigger()
                    }
                }))
    }
}