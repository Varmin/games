package com.atoshi.games

import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import com.atoshi.moduleads.TopOnHelper
import com.atoshi.moduleads.TopOnTestActivity
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulebase.utils.startPath
import kotlinx.android.synthetic.main.activity_splash.*

// TODO: by HY, 2020/7/22 放到base模块
class SplashActivity : BaseActivity() {
    init {
        FULL_SCREEN = true
    }

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun initData() {}

    override fun initView() {
        TopOnHelper.splashAds(flAdsContainer, TopOnHelper.SPLASH_ID_GDT, object :TopOnHelper.Callback{
            override fun success() {
                checkLogin()
            }

            override fun error(placementId: String, error: String) {
                checkLogin()
            }

            fun checkLogin(){
                /*window.decorView.post{
                    // TODO: by HY, 2020/7/23 SP、数据库：存储位置、清除逻辑
                    if(TextUtils.isEmpty(SPTool.getString(SPTool.WX_OPEN_ID))){
                        startPath("com.atoshi.modulelogin.MainActivityLogin")
                    }
                }*/
                startActivity(Intent(this@SplashActivity, TopOnTestActivity::class.java))
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK) return true
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        TopOnHelper.onDestroy()
    }
}