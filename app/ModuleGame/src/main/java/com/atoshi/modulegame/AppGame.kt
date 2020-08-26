package com.atoshi.modulegame

import android.app.Application

/**
 * created by HYY on 2020/7/3
 * description:
 */
class AppGame: Application() {
    private val TAG = "GameApp"
    override fun onCreate() {
        super.onCreate()
        Thread(Runnable {
            /*QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback{
                override fun onCoreInitFinished() {
                    println("$TAG.onCoreInitFinished: ")
                }

                override fun onViewInitFinished(p0: Boolean) {
                    println("$TAG.onViewInitFinished: $p0")
                }

            })*/
        }).start()
    }
}