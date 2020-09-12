package com.atoshi.moduleads

import android.app.Activity
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

    var IS_TOP_ON_TEST = false
    val TOPON_APP_ID = if (IS_TOP_ON_TEST) "a5aa1f9deda26d" else "a5f1fcab2e2222"
    val TOPON_APP_KEY = if (IS_TOP_ON_TEST) "4f7b9ac17decb9babec83aac078742c7" else "080129c71828b0013266ca336e29abe4"

    //开屏广告
    var SPLASH_ID_PANGLE = if (IS_TOP_ON_TEST) "b5bea7c1b653ef" else "b5f1fcaca30866"
    var SPLASH_ID_GDT = if (IS_TOP_ON_TEST) "b5bea7bfd93f01" else "b5f1fcaca30866"

    //插屏广告
    var INTER_ID_PANGLE = if (IS_TOP_ON_TEST) "b5baca585a8fef" else "b5f23be649136c"
    var INTER_ID_GDT = if (IS_TOP_ON_TEST) "b5baca561bc100" else "b5f23be649136c"

    //激励视频
    var REWARD_ID_PANGLE = if (IS_TOP_ON_TEST) "b5b728e7a08cd4" else "b5f1fcb2d31a05"
    var REWARD_ID_GDT = if (IS_TOP_ON_TEST) "b5c2c880cb9d52" else "b5f1fcb2d31a05"

    //----------------------------------------fun----------------------------------------
    var placementBean: TopOnBean? = null

    /**
     * 开屏广告
     */
    private var mSplashAd: ATSplashAd? = null
    fun splashAds(container: ViewGroup, listenerCallback: ListenerCallback?) {
        getPlacementId{suc, msg ->
            if(suc){
                if(placementBean == null || placementBean!!.splash == null || placementBean!!.splash!!.isEmpty()){
                    listenerCallback?.error("0","empty")
                }else{
                    val placementId = placementBean!!.splash!![0]
                    var act = container.context as Activity
                    if(!act.isFinishing) mSplashAd = ATSplashAd(act, container, placementId, TopOnSplashListener(act, placementId, listenerCallback))
                }
            }else{
                listenerCallback?.error("-1", msg)
            }
        }
    }
    fun onDestroy() {
        mSplashAd?.onDestory()
    }

    /**
     * 插屏
     */
    private val mIntersMap = HashMap<String, TopOnIntersAd>()
    fun intersShow(act: Activity, index: Int, loading: Boolean, listenerCallback: ListenerCallback?) {
        getPlacementId { suc, msg ->
            if(suc){
                if(placementBean == null || placementBean!!.inters == null || placementBean!!.inters!!.size <= index){
                    listenerCallback?.error("0","empty")
                }else{
                    val placementId = placementBean!!.inters!![index]
                    var atrAd = mIntersMap[placementId] ?: TopOnIntersAd(act, placementId, listenerCallback).apply {
                        mIntersMap[placementId] = this
                    }
                    if(loading){
                        if(!atrAd.isAdReady) atrAd.loading()
                    }else{
                        atrAd.forceShow()
                    }
                }
            }else{
                listenerCallback?.error("-1", msg)
            }
        }
    }

    /**
     * 激励视频
     */
    private val mRewardMap = HashMap<String, TopOnRewardAd>()
    fun rewardShow(act: Activity, index: Int, loading: Boolean, listenerCallback: ListenerCallback?) {
        println("TopOnHelper.rewardShow: index=$index, loading=$loading")
        getPlacementId { suc, msg ->
            if (suc) {
                if(placementBean == null || placementBean!!.reward == null || placementBean!!.reward!!.size <= index){
                    listenerCallback?.error("0","empty")
                }else{
                    val placementId = placementBean!!.reward!![index]
                    var atrAd = mRewardMap[placementId]
                        ?: TopOnRewardAd(act, placementId, listenerCallback).apply {
                            mRewardMap[placementId] = this
                        }
                    println("TopOnHelper.rewardShow: loading = $loading, isAdReady = ${atrAd.isAdReady}")
                    if(loading){
                        if(!atrAd.isAdReady) atrAd.loading()
                    }else{
                        atrAd.forceShow()
                    }
                }
            }else{
                listenerCallback?.error("-1", msg)
            }
        }
    }


    //----------------------------------------Listener----------------------------------------
    interface ListenerCallback {
        fun success()
        fun error(placementId: String, error: String)
    }

    class TopOnSplashListener(private val act: Activity, private val placementId: String, private val callback: ListenerCallback?) : ATSplashAdListener {
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
            println("TopOnSplashListener.onAdLoaded")
        }
    }

    class TopOnIntersAd(val act: Activity, val placementId: String, private val callback: ListenerCallback?): ATInterstitial(act, placementId){
        private val mListener = TopOnInterstitialListener(placementId, callback)
        init {
            setAdListener(mListener)
        }

        fun loading(){
            mListener.isLoading(true)
            if(act != null && !act.isFinishing) load()
        }

        fun forceShow(){
            println("TopOnIntersAd.forceShow isAdReady: $isAdReady")
            mListener.isLoading(false)
            if (isAdReady) {
                if(act != null && !act.isFinishing) show()
            }else{
                mListener.forceShow{
                    if(act != null && !act.isFinishing) show()
                }
                if(act != null && !act.isFinishing) load()
            }
        }
    }
    class TopOnInterstitialListener(private val placementId: String, private val callback: ListenerCallback?) : ATInterstitialListener {
        private var mIsLoading: Boolean = false
        private var mForceCallback: (() -> Unit)? = null
        fun forceShow(forceCallback: () -> Unit){
            this.mForceCallback = forceCallback
        }
        override fun onInterstitialAdLoaded() {
            println("TopOnInterstitialListener.onInterstitialAdLoaded")
            if (mForceCallback != null) {
                mForceCallback?.invoke()
                mForceCallback = null
                println("TopOnInterstitialListener.onInterstitialAdLoaded: force set null")
            }
        }

        override fun onInterstitialAdShow(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdShow: $p0")
            if(!mIsLoading) callback?.success()
        }

        override fun onInterstitialAdLoadFail(p0: AdError?) {
            println("TopOnInterstitialListener.onInterstitialAdLoadFail: ${p0?.printStackTrace()}")
            if(!mIsLoading) callback?.error(placementId, p0?.printStackTrace() ?: "")
        }

        override fun onInterstitialAdVideoStart(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdVideoStart: $p0")
        }

        override fun onInterstitialAdVideoEnd(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdVideoEnd: $p0")
            if(!mIsLoading) callback?.success()
        }

        override fun onInterstitialAdVideoError(p0: AdError?) {
            println("TopOnInterstitialListener.onInterstitialAdVideoError: ${p0?.printStackTrace()}")
            if(!mIsLoading) callback?.error(placementId, p0?.printStackTrace() ?: "")
        }

        override fun onInterstitialAdClicked(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdClicked: $p0")
        }

        override fun onInterstitialAdClose(p0: ATAdInfo?) {
            println("TopOnInterstitialListener.onInterstitialAdClose: $p0")
            //在此回调中调用load进行广告的加载，方便下一次广告的展示
            mIntersMap[placementId]?.load()
        }

        fun isLoading(isLoading: Boolean) {
            mIsLoading = isLoading
        }
    }

    class TopOnRewardAd(val act: Activity, placementId: String, callback: ListenerCallback?):ATRewardVideoAd(act, placementId){
        private val mListener = TopOnRewardListener(placementId, callback)
        init {
            setAdListener(mListener)
        }

        fun loading(){
            mListener.isLoading(true)
            if(act != null && !act.isFinishing) load()
        }

        fun forceShow(){
            println("TopOnRewardAd.forceShow isAdReady: $isAdReady")
            mListener.isLoading(false)
            if (isAdReady) {
                if(act != null && !act.isFinishing) show()
            }else{
                mListener.forceShow{
                    if(act != null && !act.isFinishing) show()
                }
                if(act != null && !act.isFinishing) load()
            }
        }
    }
    class TopOnRewardListener(private val placementId: String, private val callback: ListenerCallback?) : ATRewardVideoListener {
        private var mIsLoading: Boolean = false
        private var mForceCallback: (() -> Unit)? = null
        fun forceShow(forceCallback: () -> Unit){
            this.mForceCallback = forceCallback
        }
        override fun onRewardedVideoAdClosed(p0: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdClosed: $p0, $placementId, ${mRewardMap[placementId]}")
            if(!mIsLoading) callback?.success()
        }

        override fun onReward(p0: ATAdInfo?) {
            println("TopOnRewardListener.onReward: $p0")
        }

        override fun onRewardedVideoAdPlayFailed(p0: AdError?, p1: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdPlayFailed: ${p0?.printStackTrace()}, $p1")
            if(!mIsLoading) callback?.error(placementId, p0?.printStackTrace() + ", " + p1)
        }

        override fun onRewardedVideoAdLoaded() {
            println("TopOnRewardListener.onRewardedVideoAdLoaded")
            if (mForceCallback != null) {
                mForceCallback?.invoke()
                mForceCallback = null
                println("TopOnRewardListener.onRewardedVideoAdLoaded: force set null")
            }
        }

        override fun onRewardedVideoAdPlayStart(p0: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdPlayStart: $p0")
            //在此回调中调用load进行广告的加载，方便下一次广告的展示
            mRewardMap[placementId]?.load()
        }

        override fun onRewardedVideoAdFailed(p0: AdError?) {
            println("TopOnRewardListener.onRewardedVideoAdFailed: ${p0?.printStackTrace()}")
            if(!mIsLoading) callback?.error(placementId, p0?.printStackTrace() ?: "")
        }

        override fun onRewardedVideoAdPlayEnd(p0: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdPlayEnd: $p0")
            //callback?.success()
        }

        override fun onRewardedVideoAdPlayClicked(p0: ATAdInfo?) {
            println("TopOnRewardListener.onRewardedVideoAdPlayClicked: $p0")
        }
        fun isLoading(isLoading: Boolean) {
            mIsLoading = isLoading
        }
    }


    private fun getPlacementId(callback: ((suc: Boolean, msg:String) -> Unit)?) {
        if (SPTool.getString(TOP_ON_AD_IDS).isNullOrEmpty()) {
            getPlacementIdApi(callback)
        }else{
            try {
                val json = SPTool.getString(TOP_ON_AD_IDS)
                placementBean = Gson().fromJson(json, TopOnBean::class.java)
                callback?.invoke(true, "")
            } catch (e: Exception) {
                callback?.invoke(false, e.toString())
                println("TopOnHelper.getPlacementId: ${e.toString()}")
            }
        }
    }

    fun getPlacementIdApi(callback: ((suc: Boolean, msg:String) -> Unit)?) {
        Api.service.getPlacementId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<TopOnBean>() {
                override fun onSuccess(data: TopOnBean) {
                    try {
                        SPTool.putString(TOP_ON_AD_IDS, Gson().toJson(data))
                        placementBean = data
                        callback?.invoke(true, "")
                    } catch (e: Exception) {
                        callback?.invoke(false, e.toString())
                        println("TopOnHelper.getPlacementIdApi.onSuccess: ${e.toString()}")
                    }
                }

                override fun onError(errCode: Int, errMsg: String) {
                    super.onError(errCode, errMsg)
                    callback?.invoke(false, "$errCode-$errMsg")
                    println("TopOnHelper.getPlacementIdApi.onError:$errCode, $errMsg ")
                }
            })
    }
}



