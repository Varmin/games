package com.atoshi.modulelogin

/**
 * author：yang
 * created on：2020/8/10 14:24
 * description:
 */
interface IWxLogin {
    fun getAccessToken(code: String)
}