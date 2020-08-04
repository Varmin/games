package com.atoshi.moduleads

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.view.ViewGroup
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitial
import com.anythink.interstitial.api.ATInterstitialListener
import com.anythink.rewardvideo.api.ATRewardVideoAd
import com.anythink.rewardvideo.api.ATRewardVideoListener
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener
import com.atoshi.modulebase.net.Api
import com.atoshi.modulebase.net.model.BaseObserver
import com.atoshi.modulebase.net.model.TOP_ON_AD_IDS
import com.atoshi.modulebase.net.model.TopOnBean
import com.atoshi.modulebase.utils.SPTool
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.Exception

/**
 */
object TopOnHelper {

    val IS_TOP_OP = false
    val TOPON_APP_ID = if (IS_TOP_OP) "a5aa1f9deda26d" else "a5f1fcab2e2222"
    val TOPON_APP_KEY =
        if (IS_TOP_OP) "4f7b9ac17decb9babec83aac078742c7" else "080129c71828b0013266ca336e29abe4"

    //开屏广告
    var SPLASH_ID_PANGLE = if (IS_TOP_OP) "b5bea7c1b653ef" else "b5f1fcaca30866"
    var SPLASH_ID_GDT = if (IS_TOP_OP) "b5bea7bfd93f01" else "b5f1fcaca30866"

    //插屏广告
    var INTER_ID_PANGLE = if (IS_TOP_OP) "b5baca585a8fef" else "b5f23be649136c"
    var INTER_ID_GDT = if (IS_TOP_OP) "b5baca561bc100" else "b5f23be649136c"

    //激励视频
    var REWARD_ID_PANGLE = if (IS_TOP_OP) "b5b728e7a08cd4" else "b5f1fcb2d31a05"
    var REWARD_ID_GDT = if (IS_TOP_OP) "b5c2c880cb9d52" else "b5f1fcb2d31a05"

    //----------------------------------------fun----------------------------------------

    var placementBean: TopOnBean? = null

    /**
     * 开屏广告
     */
    private var mSplashAd: ATSplashAd? = null
    fun splashAds(container: ViewGroup, placementId: String, callback: Callback?) {
        var act = container.context as Activity
        mSplashAd =
            ATSplashAd(act, container, placementId, TopOnSplashListener(act, placementId, callback))
    }

    fun onDestroy() {
        mSplashAd?.onDestory()
    }

    /**
     * 插屏广告
     */
    private var mInterstitialAd: ATInterstitial? = null
    fun interstitialLoad(context: Context, placementId: String, callback: Callback?) {
        if (mInterstitialAd == null) {
            mInterstitialAd = ATInterstitial(context, INTER_ID_PANGLE).apply {
                setAdListener(TopOnInterstitialListener(placementId, callback))
                load()
            }
        } else {
            if (!mInterstitialAd!!.isAdReady) mInterstitialAd!!.load()
        }
    }

    fun interstitialShow() {
        mInterstitialAd?.run {
            if (isAdReady) show() else load()
        }
    }

    /**
     * 激励视频
     */
    private var mRewardVideoAd: ATRewardVideoAd? = null
    fun rewardLoad(act: Activity, placementId: String, callback: Callback?) {
        if (mRewardVideoAd == null) {
            mRewardVideoAd = ATRewardVideoAd(act, placementId).apply {
                setAdListener(TopOnRewardListener(placementId, callback))
                load()
            }
        } else {
            if (!mRewardVideoAd!!.isAdReady) mRewardVideoAd!!.load()
        }
    }

    fun rewardShow() {
        mRewardVideoAd?.run {
            if (isAdReady) show() else load()
        }
    }


    private val mIntersMap = HashMap<String, ATInterstitial>()
    fun intersShow(act: Activity, index: Int, loading: Boolean, callback: Callback?) {
        if (placementBean == null) {
            getPlacementId()
            callback?.error("", "未初始化")
        } else {
            val placementId = placementBean!!.inters?.get(index)
            if (placementId.isNullOrEmpty()) {
                callback?.error("", "暂无该index广告位")
            } else {
                var atrAd = mIntersMap[placementId] ?: ATInterstitial(act, placementId).apply {
                    setAdListener(TopOnInterstitialListener(placementId, callback))
                    mIntersMap[placementId] = this
                }
                if (loading || !atrAd.isAdReady) {
                    atrAd.load()
                    callback?.error("", "广告加载中...")
                }else{
                    atrAd.show()
                }
            }
        }
    }

    private val mRewardMap = HashMap<String, ATRewardVideoAd>()
    fun rewardShow(act: Activity, index: Int, loading: Boolean, callback: Callback?) {
        if (placementBean == null) {
            getPlacementId()
            callback?.error("index: $index", "未初始化")
        } else {
            val placementId = placementBean!!.reward?.get(index)
            if (placementId.isNullOrEmpty()) {
                callback?.error("index: $index", "暂无该index广告位")
            } else {
                var atrAd = mRewardMap[placementId]
                    ?: ATRewardVideoAd(act, placementId).apply {
                        setAdListener(TopOnRewardListener(placementId, callback))
                        mRewardMap[placementId] = this
                    }
                if (loading || !atrAd.isAdReady) {
                    atrAd.load()
                    callback?.error("", "广告加载中...")
                }else{
                    atrAd.show()
                }
            }
        }
    }


    //----------------------------------------Listener----------------------------------------
    interface Callback {
        fun success()
        fun error(placementId: String, error: String)
    }

    class TopOnSplashListener(
        private val act: Activity,
        private val placementId: String,
        private val callback: Callback?
    ) : ATSplashAdListener {
        private var mSplashLoadStartTime: Long = SystemClock.currentThreadTimeMillis()

        override fun onAdDismiss(p0: ATAdInfo?) {
            println("TopOnSplashListener.onAdDismiss: $p0")
            callback?.success()
            act.finish()
        }

        override fun onNoAdError(p0: AdError?) {
            println("TopOnSplashListener.onNoAdError: ${p0?.printStackTrace()}")
            callback?.error(placementId, p0?.printStackTrace() ?: "")
            act.finish()
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
            println("TopOnSplashListener.onAdLoaded: time=${splashLoadEnd - mSplashLoadStartTime}")
        }
    }

    class TopOnInterstitialListener(
        private val placementId: String,
        private val callback: Callback?
    ) : ATInterstitialListener {
        override fun onInterstitialAdLoaded() {
            println("TopOnInterstitialListener.onInterstitialAdLoaded")
        }

        override fun onInterstitialAdShow(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdShow: $p0")
            callback?.success()
        }

        override fun onInterstitialAdLoadFail(p0: AdError?) {
            println("TopOnInterstitialListener.onInterstitialAdLoadFail: ${p0?.printStackTrace()}")
            callback?.error(placementId, p0?.printStackTrace() ?: "")
        }

        override fun onInterstitialAdVideoStart(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdVideoStart: $p0")
        }

        override fun onInterstitialAdVideoEnd(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdVideoEnd: $p0")
            callback?.success()
        }

        override fun onInterstitialAdVideoError(p0: AdError?) {
            println("TopOnInterstitialListener.onInterstitialAdVideoError: ${p0?.printStackTrace()}")
            callback?.error(placementId, p0?.printStackTrace() ?: "")
        }

        override fun onInterstitialAdClicked(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdClicked: $p0")
        }

        override fun onInterstitialAdClose(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdClose: $p0")
            //在此回调中调用load进行广告的加载，方便下一次广告的展示
            mInterstitialAd?.load()
            mIntersMap[placementId]?.load()
        }
    }

    class TopOnRewardListener(private val placementId: String, private val callback: Callback?) :
        ATRewardVideoListener {
        //建议在此回调中调用load进行广告的加载，方便下一次广告的展示
        override fun onRewardedVideoAdClosed(p0: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdClosed: $p0")
            mRewardVideoAd?.load()
            mRewardMap[placementId]?.load()
        }

        override fun onReward(p0: ATAdInfo?) {
            println("TopOnRewardListener.onReward: $p0")
        }

        override fun onRewardedVideoAdPlayFailed(p0: AdError?, p1: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdPlayFailed: ${p0?.printStackTrace()}, $p1")
            callback?.error(placementId, p0?.printStackTrace() + ", " + p1)
        }

        override fun onRewardedVideoAdLoaded() {
            println("TopOnRewardListener.onRewardedVideoAdLoaded")
        }

        override fun onRewardedVideoAdPlayStart(p0: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdPlayStart: $p0")
        }

        override fun onRewardedVideoAdFailed(p0: AdError?) {
            println("TopOnRewardListener.onRewardedVideoAdFailed: ${p0?.printStackTrace()}")
            callback?.error(placementId, p0?.printStackTrace() ?: "")
        }

        override fun onRewardedVideoAdPlayEnd(p0: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdPlayEnd: $p0")
            callback?.success()
        }

        override fun onRewardedVideoAdPlayClicked(p0: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdPlayClicked: $p0")
        }
    }


    fun getPlacementId() {
        getPlacementId(null)
    }
    fun getPlacementId(callback: (() -> Unit)?) {
        Api.service.getPlacementId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<TopOnBean>() {
                override fun onSuccess(data: TopOnBean) {
                    try {
                        callback?.invoke()
                        SPTool.putString(TOP_ON_AD_IDS, Gson().toJson(data))
                        placementBean = data
                    } catch (e: Exception) {
                    }
                }
            })
    }

}



