package com.atoshi.modulelogin

import android.app.Application
import com.atoshi.modulebase.wx.WXUtils

/**
 * created by HYY on 2020/7/9
 * description:
 */
class AppLogin: Application() {
    override fun onCreate() {
        super.onCreate()
        WXUtils.registerApp(this)
    }
}