package com.atoshi.modulegame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.*
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.base.startPath
import com.atoshi.modulebase.utils.SPTool
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

const val ACTION_LOAD_URL = "action_load_url"
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

    init {
        FULL_SCREEN = true
    }

    private lateinit var mWebView: WebView

    // TODO: by HY, 2020/7/23 声明周期， 在Activity生成但未显示的时候跳转：有没有更早的？
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // TODO: by HY, 2020/7/23 过渡动画
        startPath("com.atoshi.games.SplashActivity")
    }

    override fun getLayoutId(): Int = -1

    override fun initData() {
        if(SPTool.getString(SPTool.WX_OPEN_ID).isNullOrEmpty()){
            mReceiverReload = ReceiverReload().apply {
                registerReceiver(this, IntentFilter(ACTION_LOAD_URL))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mReceiverReload?.apply { unregisterReceiver(this) }
    }

    // TODO: by HY, 2020/7/23 界面初始化：卡在哪些时间了？如何检测？如果有初始化放在哪里合适？
    override fun initView() {
        window.decorView.apply {
            postDelayed({
                mWebView = WebView(this@GameActivity).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            webView: WebView?,
                            url: String?
                        ): Boolean {
                            println("$TAG.shouldOverrideUrlLoading: $url")
                            webView?.loadUrl(url)
                            return true
                        }
                    }
                }
                loadUrl()
                setContentView(mWebView)
            }, 10)
        }
    }

    private fun loadUrl() {
        var openId = SPTool.getString(SPTool.WX_OPEN_ID)
        mWebView.loadUrl("http://game.atoshi.mobi/shenhe?uname=123456&openid=$openId")
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

