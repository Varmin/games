package com.atoshi.games

import android.text.TextUtils
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.base.startPath
import com.atoshi.modulebase.utils.SPTool

// TODO: by HY, 2020/7/22 放到base模块
class SplashActivity : BaseActivity() {
    init {
        FULL_SCREEN = true
    }

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun initData() {}

    override fun initView() {
        window.decorView.postDelayed({
            // TODO: by HY, 2020/7/23 SP、数据库：存储位置、清除逻辑
            if(TextUtils.isEmpty(SPTool.getString(SPTool.WX_OPEN_ID))){
                startPath("com.atoshi.modulelogin.MainActivityLogin")
            }
            finish()
        }, 2000)
    }
}