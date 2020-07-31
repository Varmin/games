package com.atoshi.games

import android.view.KeyEvent
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
        mSplashAd = ATSplashAd(this, flAdsContainer, TopTest.SPLASH_ID_GDT, ATListener())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK) return true
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSplashAd.onDestory()
    }

    inner class ATListener: ATSplashAdListener{
        override fun onAdDismiss(p0: ATAdInfo?) {
            println("ATListener.onAdDismiss: $p0")
            finishSplash()
        }

        override fun onNoAdError(p0: AdError?) {
            println("ATListener.onNoAdError: ${p0?.printStackTrace()}")
            finishSplash()
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


    fun finishSplash() {
        finish()
    }

}