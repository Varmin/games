package com.atoshi.modulegame

import android.app.Application
import com.tencent.smtt.sdk.QbSdk

/**
 * created by HYY on 2020/7/3
 * description:
 */
class GameApp: Application() {
    private val TAG = "GameApp"
    override fun onCreate() {

        super.onCreate()
        QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback{
            override fun onCoreInitFinished() {
                println("$TAG.onCoreInitFinished: ")
            }

            override fun onViewInitFinished(p0: Boolean) {
                println("$TAG.onViewInitFinished: $p0")
            }

        })
    }
}