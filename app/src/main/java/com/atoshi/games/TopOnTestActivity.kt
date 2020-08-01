package com.atoshi.games

import android.os.SystemClock
import android.view.KeyEvent
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitial
import com.anythink.interstitial.api.ATInterstitialListener
import com.anythink.rewardvideo.api.ATRewardVideoAd
import com.anythink.rewardvideo.api.ATRewardVideoListener
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.utils.click
import kotlinx.android.synthetic.main.activity_top_on_test.*

class TopOnTestActivity : BaseActivity() {
    private var mSplashLoadStart: Long = 0
    private lateinit var mRewardVideoAd: ATRewardVideoAd
    private lateinit var mInterstitialAd: ATInterstitial
    private var mSplashAd: ATSplashAd? = null
    override fun getLayoutId(): Int = R.layout.activity_top_on_test

    override fun initData() {}

    override fun initView() {
        btnSplash.click {
            mSplashLoadStart = SystemClock.currentThreadTimeMillis().apply {
                println("TopOnTestActivity.initView")
            }
            mSplashAd = ATSplashAd(this, flAdsContainer, TopOnHelper.SPLASH_ID_GDT, TopOnSplashListener())
        }

        mInterstitialAd = ATInterstitial(this, TopOnHelper.INTER_ID_PANGLE).apply {
            setAdListener(TopOnInterstitialListener())
            load()
            toast("isReady: $isAdReady")
        }

        btnLoad.click {
            println("TopOnTestActivity.initView: ${mInterstitialAd.isAdReady}")

            if(!mInterstitialAd.isAdReady) mInterstitialAd.load()
        }
        btnShow.click {
            println("TopOnTestActivity.initView: ${mInterstitialAd.isAdReady}")

            if(mInterstitialAd.isAdReady){
                mInterstitialAd.show(this)
            }else{
                mInterstitialAd.load()
                //todo 加载、展示
            }
        }

        mRewardVideoAd = ATRewardVideoAd(this, TopOnHelper.REWARD_ID_PANGLE).apply {
            setAdListener(TopOnRewardListener())
            load()
            toast("激励：$isAdReady")
        }
        btnLoadReward.click {
            println("TopOnTestActivity.initView: ${mRewardVideoAd.isAdReady}")
            if(!mRewardVideoAd.isAdReady) mRewardVideoAd.load()
        }
        btnShowReward.click {
            println("TopOnTestActivity.initView: ${mRewardVideoAd.isAdReady}")
            if(mRewardVideoAd.isAdReady){
                mRewardVideoAd.show(this)
            }else{
                mRewardVideoAd.load()
                //todo 展示
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK) return true
        return super.onKeyDown(keyCode, event)
    }
    override fun onDestroy() {
        super.onDestroy()
        mSplashAd?.onDestory()
    }
    fun finishSplash() {
        //finish()
    }

    inner class TopOnSplashListener: ATSplashAdListener {
        override fun onAdDismiss(p0: ATAdInfo?) {
            println("TopOnSplashListener.onAdDismiss: $p0")
            finishSplash()
        }

        override fun onNoAdError(p0: AdError?) {
            println("TopOnSplashListener.onNoAdError: ${p0?.printStackTrace()}")
            finishSplash()
        }

        override fun onAdShow(p0: ATAdInfo?) {
            println("TopOnSplashListener.onAdShow: $p0")
        }

        override fun onAdClick(p0: ATAdInfo?) {
            println("TopOnSplashListener.onAdClick: $p0")
        }

        override fun onAdTick(p0: Long) {
            println("TopOnSplashListener.onAdTick: $p0")
        }

        override fun onAdLoaded() {
            val splashLoadEnd = SystemClock.currentThreadTimeMillis()
            println("TopOnSplashListener.onAdLoaded: ${splashLoadEnd - mSplashLoadStart}")
        }
    }

    inner class TopOnInterstitialListener : ATInterstitialListener {
        override fun onInterstitialAdLoadFail(p0: AdError?) {
            println("TopOnInterstitialListener.onInterstitialAdLoadFail: ${p0?.printStackTrace()}")
        }

        override fun onInterstitialAdLoaded() {
            println("TopOnInterstitialListener.onInterstitialAdLoaded")
        }

        override fun onInterstitialAdVideoEnd(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdVideoEnd: $p0")
        }

        override fun onInterstitialAdShow(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdShow: $p0")
        }

        override fun onInterstitialAdVideoError(p0: AdError?) {
            println("TopOnInterstitialListener.onInterstitialAdVideoError: ${p0?.printStackTrace()}")
        }

        override fun onInterstitialAdClicked(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdClicked: $p0")
        }

        override fun onInterstitialAdVideoStart(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdVideoStart: $p0")
        }

        override fun onInterstitialAdClose(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdClose: $p0")
            //建议在此回调中调用load进行广告的加载，方便下一次广告的展示
            mInterstitialAd?.load()
        }
    }

    inner class TopOnRewardListener: ATRewardVideoListener{
        //建议在此回调中调用load进行广告的加载，方便下一次广告的展示
        override fun onRewardedVideoAdClosed(p0: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdClosed: $p0")
            mRewardVideoAd?.load()
        }

        override fun onReward(p0: ATAdInfo?) {
            println("TopOnRewardListener.onReward: $p0")
        }

        override fun onRewardedVideoAdPlayFailed(p0: AdError?, p1: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdPlayFailed: ${p0?.printStackTrace()}, $p1")
        }

        override fun onRewardedVideoAdLoaded() {
            println("TopOnRewardListener.onRewardedVideoAdLoaded")
        }

        override fun onRewardedVideoAdPlayStart(p0: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdPlayStart: $p0")
        }

        override fun onRewardedVideoAdFailed(p0: AdError?) {
            println("TopOnRewardListener.onRewardedVideoAdFailed: ${p0?.printStackTrace()}")
        }

        override fun onRewardedVideoAdPlayEnd(p0: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdPlayEnd: $p0")
        }

        override fun onRewardedVideoAdPlayClicked(p0: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdPlayClicked: $p0")
        }

    }
}