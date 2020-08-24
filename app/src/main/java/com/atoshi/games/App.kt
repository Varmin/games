package com.atoshi.games

import android.app.Application
import android.content.Intent
import android.os.Build
import android.util.Log
import android.webkit.WebView
import androidx.multidex.MultiDexApplication
import com.anythink.core.api.ATSDK
import com.anythink.core.api.ATSDKInitListener
import com.atoshi.moduleads.TopOnHelper
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulebase.wx.WXUtils
import com.tencent.smtt.sdk.QbSdk

/**
 * created by HYY on 2020/7/9
 * description:
 */
class App : MultiDexApplication() {
    var isATsdkInit: Boolean = false

    companion object {
        lateinit var mInstance: App
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        // TODO: yang 2020/8/20 线程池
        init()
        Thread {
            initFromThread()
        }.start()
    }

    private fun init() {
        SPTool.init(this)
    }

    private fun initFromThread() {
        //微信
        WXUtils.registerApp(this)

        //Webview多进程兼容（聚合快手时必须添加）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
            && packageName != Application.getProcessName()) {
            WebView.setDataDirectorySuffix(Application.getProcessName())
        }
        QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback{
            override fun onCoreInitFinished() {}
            override fun onViewInitFinished(p0: Boolean) {
                println("App.onViewInitFinished: $p0")
            }
        })

        //TopOn
        if (BuildConfig.DEBUG) ATSDK.setNetworkLogDebug(true)
        ATSDK.init(
            this,
            TopOnHelper.TOPON_APP_ID,
            TopOnHelper.TOPON_APP_KEY,
            object : ATSDKInitListener {
                override fun onSuccess() {
                    println("App.onSuccess")
                    isATsdkInit = true
                    sendBroadcast(Intent(ACTION_LOAD_SPLASH))
                }

                override fun onFail(fail: String?) {
                    println("App.onFail: $fail")
                    isATsdkInit = true
                    sendBroadcast(Intent(ACTION_LOAD_SPLASH))
                }
            })
    }
}