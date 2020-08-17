package com.atoshi.games

import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import com.atoshi.modulebase.net.model.TOP_ON_AD_IDS
import com.atoshi.modulebase.net.model.TopOnBean
import com.atoshi.moduleads.TopOnHelper
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulebase.utils.startPath
import com.atoshi.modulebase.wx.WXUtils
import com.atoshi.modulegame.ACTION_LOAD_ADS
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_splash.*
import java.lang.Exception

// TODO: by HY, 2020/7/22 放到base模块
class SplashActivity : BaseActivity() {
    init {
        FULL_SCREEN = true
    }

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun initData() {}

    override fun initView() {
        //上面有可能拿的是sp中的值，这里更新一下
        TopOnHelper.getPlacementIdApi(null)

        //先把Activity显示出来，再加载，否则卡顿
        //todo 确切显示回调？而不是延迟；  如何预加载
        flAdsContainer.postDelayed({
            println("SplashActivity.initView--delay")
            TopOnHelper.splashAds(flAdsContainer, object : TopOnHelper.ListenerCallback {
                override fun success() {
                    println("SplashActivity.success")
                    checkLogin()
                }
                override fun error(placementId: String, error: String) {
                    println("SplashActivity.error: $error")
                    checkLogin()
                }
            })
        }, 10)
    }



    fun checkLogin() {
        sendBroadcast(Intent(ACTION_LOAD_ADS))
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
    }
}