package com.atoshi.games

import androidx.multidex.MultiDexApplication
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulebase.wx.WXUtils

/**
 * created by HYY on 2020/7/9
 * description:
 */
class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        SPTool.init(this)
        WXUtils.registerApp(this)
    }
}