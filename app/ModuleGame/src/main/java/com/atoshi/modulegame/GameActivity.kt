package com.atoshi.modulegame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.SystemClock
import android.view.*
import com.atoshi.moduleads.TopOnHelper
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.net.model.TOP_ON_AD_IDS
import com.atoshi.modulebase.utils.startPath
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulebase.utils.isExitClickFirst
import com.atoshi.modulebase.utils.isFastClick
import com.atoshi.modulebase.wx.WXUtils
import com.tencent.smtt.sdk.WebView

const val ACTION_LOAD_URL = "action_load_url"
const val GAME_BASE_URL = "http://game.atoshi.mobi/other/android"
class GameActivity : BaseActivity() {
    // TODO: by HY, 2020/7/23 EventBus
    private var mReceiverReload: ReceiverReload? = null
    inner class ReceiverReload: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.run {
                if(action == ACTION_LOAD_URL) loadUrl()
            }
        }
    }
    var topOnCallback = object : TopOnHelper.Callback{
        override fun success() {
            println("topOnCallback.success")
            runOnUiThread{ adsShowSuccess()}
        }

        override fun error(placementId: String, error: String) {
            println("topOnCallback.error, $placementId, $error")
            runOnUiThread { adsShowError("$placementId, $error") }
        }
    }

    init {
        FULL_SCREEN = true
    }

    private var mWebView: WebView? = null

    // TODO: by HY, 2020/7/23 声明周期， 在Activity生成但未显示的时候跳转：有没有更早的？
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // TODO: by HY, 2020/7/23 过渡动画
        startPath("com.atoshi.games.SplashActivity")
    }

    override fun getLayoutId(): Int = -1

    override fun initData() {
        if(SPTool.getString(WXUtils.WX_OPEN_ID).isNullOrEmpty()){
            mReceiverReload = ReceiverReload().apply {
                registerReceiver(this, IntentFilter(ACTION_LOAD_URL))
            }
        }

        UpdateManager(this).checkVersion()
    }
    override fun onDestroy() {
        super.onDestroy()
        mReceiverReload?.apply { unregisterReceiver(this) }
        mWebView?.destroy()
    }

    // TODO: by HY, 2020/7/24 WebView优化：缓存、预加载...
    private fun loadUrl() {
        val openId = SPTool.getString(WXUtils.WX_OPEN_ID)
        val token = SPTool.getString(WXUtils.APP_USER_TOKEN)
        mWebView?.loadUrl("$GAME_BASE_URL?openid=$openId&token=$token")
//        mWebView?.loadUrl("https://www.baidu.com")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mWebView != null && mWebView!!.canGoBack()){
                mWebView!!.goBack()
                return true
            }
            if(isExitClickFirst()){
                toast("再按一次退出应用")
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
    // TODO: by HY, 2020/7/23 界面初始化：卡在哪些时间了？如何检测？如果有初始化放在哪里合适？
    override fun initView() {
        var start = SystemClock.currentThreadTimeMillis()
        println("---------------------------------initView")
       window.decorView.postDelayed({
           println("GameActivity.initView: ${SystemClock.currentThreadTimeMillis() - start}")
           if (SPTool.getString(TOP_ON_AD_IDS).isNullOrEmpty()) {
               TopOnHelper.getPlacementId {
                   TopOnHelper.intersShow(this@GameActivity, 0,true,  topOnCallback)
                   TopOnHelper.rewardShow(this@GameActivity, 0,true,  topOnCallback)
               }
           }else{
               TopOnHelper.intersShow(this@GameActivity, 0,true,  topOnCallback)
               TopOnHelper.rewardShow(this@GameActivity, 0,true,  topOnCallback)
           }
       }, 3000)

        if (BuildConfig.DEBUG) WebView.setWebContentsDebuggingEnabled(true)
        mWebView = WebView(this@GameActivity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            webViewClient = GameWebviewClient()
            addJavascriptInterface(JsInterface(this@GameActivity, topOnCallback), "AtoshiGame")
        }
        loadUrl()
        setContentView(mWebView)
    }



    private fun adsShowSuccess(){
        mWebView?.loadUrl( "javascript:adsShowSuccess()")
    }
    private fun adsShowError(errMsg: String){
        mWebView?.loadUrl("javascript:adsShowError()")
        mWebView?.loadUrl("javascript:adsShowError('$errMsg')")
    }
}

