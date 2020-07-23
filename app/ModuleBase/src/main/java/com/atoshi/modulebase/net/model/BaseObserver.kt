package com.atoshi.modulebase.net.model

import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

/**
 * created by HYY on 2020/7/14
 * description:
 */
const val NET_CODE_ERROR = -1000
const val NET_CODE_RESP_ERROR = -1001
const val NET_CODE_DATA_ERROR = -1002
abstract class BaseObserver<T> : Observer<BaseResp<T>>{

    override fun onComplete() {
        println("${javaClass.simpleName}.onComplete: ")
    }

    override fun onSubscribe(d: Disposable?) {
        println("${javaClass.simpleName}.onSubscribe: ")
    }

    override fun onNext(t: BaseResp<T>?) {
        t?.apply {
            if (code == 200 && message == "ok") {
                t.data?.apply {
                    onSuccess(t.data!!)
                } ?: onError(NET_CODE_DATA_ERROR, "data：返回错误")
            }else{
                onError(t.code, t.message)
            }
        } ?: onError(NET_CODE_RESP_ERROR, "服务器暂无数据")
    }

    override fun onError(e: Throwable?) {
        onError(NET_CODE_ERROR, e.toString())
    }


    abstract fun onSuccess(data: T)
    open fun onError(errCode: Int, errMsg: String){
        // TODO: by HY, 2020/7/14 获取Application
        println("${javaClass.simpleName}.onError: $errCode, $errMsg ")
    }
}