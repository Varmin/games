package com.atoshi.modulebase.net.model

/**
 * created by HYY on 2020/7/14
 * description:
 */
class BaseResp<T> {
    public var code:Int = -1
    public var message: String = "base resp empty"
    public var data: T? = null
}