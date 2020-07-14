package com.atoshi.modulebase.net.model

import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

/**
 * created by HYY on 2020/7/14
 * description:
 */
abstract class BaseObserver<T> : Observer<BaseResp<T>>{
    override fun onComplete() {
        println("${javaClass.simpleName}.onComplete: ")
    }

    override fun onSubscribe(d: Disposable?) {
        println("${javaClass.simpleName}.onSubscribe: ")
    }

    override fun onNext(t: BaseResp<T>?) {
        t?.apply {
            if (errorCode == 0) {
                t.data?.apply {
                    onSuccess(t.data!!)
                } ?: onError("data：返回错误")
            }else{
                onError(t.errorMsg)
            }
        } ?: onError("服务器暂无数据")
    }

    override fun onError(e: Throwable?) {
        onError(e.toString())
    }


    abstract fun onSuccess(result: T)
    fun onError(errMsg: String){
        // TODO: by HY, 2020/7/14 获取Application
        println("${javaClass.simpleName}.onError: $errMsg ")
    }
}