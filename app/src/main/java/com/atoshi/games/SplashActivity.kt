package com.atoshi.games

import android.text.TextUtils
import android.view.KeyEvent
import com.atoshi.modulebase.net.model.TOP_ON_AD_IDS
import com.atoshi.modulebase.net.model.TopOnBean
import com.atoshi.moduleads.TopOnHelper
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulebase.utils.startPath
import com.atoshi.modulebase.wx.WXUtils
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
        val json = SPTool.getString(TOP_ON_AD_IDS)
        if (json.isNullOrEmpty()) {
            checkLogin()
        } else {
            try {
                val splash = Gson().fromJson(json, TopOnBean::class.java)?.splash
                if(splash != null && !splash[0].isNullOrEmpty()) splashAds(splash[0]) else checkLogin()
            } catch (e: Exception) {
                checkLogin()
            }
        }
        TopOnHelper.getPlacementId()
    }

    private fun splashAds(placementId: String) {
        TopOnHelper.splashAds(flAdsContainer, placementId, object : TopOnHelper.Callback {
            override fun success() {
                checkLogin()
            }
            override fun error(placementId: String, error: String) {
                checkLogin()
            }
        })
    }


    fun checkLogin() {
        // TODO: by HY, 2020/7/23 SP、数据库：存储位置、清除逻辑
        if (TextUtils.isEmpty(SPTool.getString(WXUtils.WX_OPEN_ID))) {
            startPath("com.atoshi.modulelogin.MainActivityLogin")
        }
        finish()
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