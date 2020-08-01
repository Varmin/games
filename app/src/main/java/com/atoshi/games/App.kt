package com.atoshi.games

import android.app.Application
import android.os.Build
import android.webkit.WebView
import androidx.multidex.MultiDexApplication
import com.anythink.core.api.ATSDK
import com.anythink.core.api.ATSDKInitListener
import com.atoshi.moduleads.TopOnHelper
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
            && packageName !=


            Application.getProcessName()){
            WebView.setDataDirectorySuffix(Application.getProcessName())
        }

        if(BuildConfig.DEBUG) ATSDK.setNetworkLogDebug(true)
        ATSDK.init(this, TopOnHelper.TOPON_APP_ID, TopOnHelper.TOPON_APP_KEY, object : ATSDKInitListener{
            override fun onSuccess() {
                println("App.onSuccess")
            }
            override fun onFail(fail: String?) {
                println("App.onFail: $fail")
            }
        })
        //验证sdk集成是否正确
        ATSDK.integrationChecking(this)
    }
}