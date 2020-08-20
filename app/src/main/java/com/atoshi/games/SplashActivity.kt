package com.atoshi.games

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import android.view.KeyEvent
import com.atoshi.moduleads.TopOnHelper
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulebase.utils.startPath
import com.atoshi.modulebase.wx.WXUtils
import com.atoshi.modulegame.ACTION_PRELOAD_ADS
import kotlinx.android.synthetic.main.activity_splash.*

// TODO: by HY, 2020/7/22 放到base模块

const val ACTION_LOAD_SPLASH = "action_load_splash"
class SplashActivity : BaseActivity() {
    private var mReceiver: ReceiverReload? = null

    init {
        FULL_SCREEN = true
    }

    inner class ReceiverReload : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.run {
                if (action == ACTION_LOAD_SPLASH && !isFinishing) loadSplashAd()
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun initData() {}

    override fun initView() {
        //上面有可能拿的是sp中的值，这里更新一下
        TopOnHelper.getPlacementIdApi(null)
        //Application中的sdk初始化是在子线程中，此处等初始化完成后再执行
        if (App.mInstance.isATsdkInit) {
            loadSplashAd()
        }else{
            mReceiver = ReceiverReload().apply {
                registerReceiver(this, IntentFilter(ACTION_LOAD_SPLASH))
            }
        }
    }

    fun loadSplashAd(){
        // todo 5s强制跳转
        TopOnHelper.splashAds(flAdsContainer, object : TopOnHelper.ListenerCallback {
            override fun success() {
                println("SplashActivity.success")
                if(!isFinishing) checkLogin()
            }
            override fun error(placementId: String, error: String) {
                println("SplashActivity.error: $error")
                if(!isFinishing) checkLogin()
            }
        })

    }


    fun checkLogin() {
        sendBroadcast(Intent(ACTION_PRELOAD_ADS))
        // TODO: by HY, 2020/7/23 SP、数据库：存储位置、清除逻辑
        if (TextUtils.isEmpty(SPTool.getString(WXUtils.WX_OPEN_ID))) {
            startPath("com.atoshi.modulelogin.MainActivityLogin")
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        println("SplashActivity.onResume")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) return true
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        TopOnHelper.onDestroy()
        mReceiver?.run { unregisterReceiver(this) }
    }
}