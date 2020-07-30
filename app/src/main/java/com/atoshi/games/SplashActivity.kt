package com.atoshi.games

import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener
import com.atoshi.modulebase.base.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*

// TODO: by HY, 2020/7/22 放到base模块
class SplashActivity : BaseActivity() {
    private lateinit var mSplashAd: ATSplashAd

    init {
        FULL_SCREEN = true
    }

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun initData() {}

    override fun initView() {
        /*window.decorView.postDelayed({
            // TODO: by HY, 2020/7/23 SP、数据库：存储位置、清除逻辑
            if(TextUtils.isEmpty(SPTool.getString(SPTool.WX_OPEN_ID))){
                startPath("com.atoshi.modulelogin.MainActivityLogin")
            }
            finish()
        }, 1000)*/


        mSplashAd = ATSplashAd(this, flAdsContainer, "placementId", ATListener())
    }

    override fun onDestroy() {
        super.onDestroy()
        mSplashAd.onDestory()
    }

    inner class ATListener: ATSplashAdListener{
        override fun onAdDismiss(p0: ATAdInfo?) {
            println("ATListener.onAdDismiss: $p0")
        }

        override fun onNoAdError(p0: AdError?) {
            println("ATListener.onNoAdError: $p0")
        }

        override fun onAdShow(p0: ATAdInfo?) {
            println("ATListener.onAdShow: $p0")
        }

        override fun onAdClick(p0: ATAdInfo?) {
            println("ATListener.onAdClick: $p0")
        }

        override fun onAdTick(p0: Long) {
            println("ATListener.onAdTick: $p0")
        }

        override fun onAdLoaded() {
            println("ATListener.onAdLoaded")
        }

    }


}