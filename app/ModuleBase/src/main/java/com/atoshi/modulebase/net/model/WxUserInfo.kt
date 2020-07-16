package com.atoshi.modulebase.net.model

/**
 * created by HYY on 2020/7/16
 * description:
 */
data class WxUserInfo (
    var city: String = "",
    var country: String = "",
    var headimgurl: String = "",
    var nickname: String = "",
    var openid: String = "",
    var privilege:  MutableList<String> = ArrayList(),
    var province: String = "",
    var sex: Int = 0,
    var unionid: String = ""
)