package com.atoshi.modulebase.wx

/**
 * author：yang
 * created on：2020/8/10 14:24
 * description: 调用api接口
 */
interface IWxApi {
    fun getAccessToken(code: String)
}