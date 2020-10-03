package com.absinthe.anywhere_.a11y

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class A11yWorkFlow {

    private var mObservable: Observable<A11yFlowNode>? = null
    private val mDisposables = CompositeDisposable()

    fun observe(observable: Observable<A11yFlowNode>): A11yWorkFlow {
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
                .subscribeWith(object : DisposableObserver<A11yFlowNode?>() {
                    override fun onComplete() {
                        Timber.d("onComplete()")
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                    }

                    override fun onNext(a11yFlowNode: A11yFlowNode) {
                        a11yFlowNode.trigger()
                    }
                }))
    }
}