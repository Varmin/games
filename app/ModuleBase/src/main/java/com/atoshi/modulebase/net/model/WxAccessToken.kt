package com.atoshi.modulebase.net.model

/**
 * created by HYY on 2020/7/16
 * description:
 */
data class WxAccessToken(
    val access_token: String,
    val expires_in: String,
    val refresh_token: String,
    val openid: String,
    val scope: String,
    val unionid: String
)