package com.atoshi.games

import android.app.Application
import android.os.Build
import android.webkit.WebView
import androidx.multidex.MultiDexApplication
import com.anythink.core.api.ATSDK
import com.anythink.core.api.ATSDKInitListener
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

        //Webview多进程兼容（聚合快手时必须添加）
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
            && packageName != Application.getProcessName()){
            WebView.setDataDirectorySuffix(Application.getProcessName())
        }

        var TOPON_APP_ID = "a5f1fcab2e2222"
        var TOPON_APP_KEY = "080129c71828b0013266ca336e29abe4"
        ATSDK.init(this, TOPON_APP_ID, TOPON_APP_KEY, object : ATSDKInitListener{
            override fun onSuccess() {
                println("App.onSuccess")
            }
            override fun onFail(fail: String?) {
                println("App.onFail: $fail")
            }
        })
        ATSDK.integrationChecking(this)
    }
}