package com.atoshi.modulebase.base

/**
 * created by HYY on 2020/7/23
 * description:
 */
// TODO: by HY, 2020/7/23 loading状态
interface BaseView {
    fun toast(toast: String)
    fun loading()
    fun loaded()
}