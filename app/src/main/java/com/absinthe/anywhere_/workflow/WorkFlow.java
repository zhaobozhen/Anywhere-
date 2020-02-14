package com.absinthe.anywhere_.workflow;

import com.absinthe.anywhere_.utils.manager.Logger;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class WorkFlow {

    private Observable<FlowNode> mObservable;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    public WorkFlow observe(Observable<FlowNode> observable) {
        mObservable = observable;
        return this;
    }

    public void start() {
        if (mObservable == null) {
            return;
        }

        mDisposables.add(mObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<FlowNode>() {
                    @Override public void onComplete() {
                        Logger.d("onComplete()");
                    }

                    @Override public void onError(Throwable e) {
                        Logger.e("onError()", e);
                    }

                    @Override public void onNext(FlowNode flowNode) {
                        flowNode.trigger();
                    }
                }));
    }
}
