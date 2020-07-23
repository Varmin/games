package com.atoshi.modulegame

import android.view.*
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.base.startPath
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

class GameActivity : BaseActivity() {
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

    // TODO: by HY, 2020/7/23 界面初始化：卡在哪些时间了？如何检测？如果有初始化放在哪里合适？
    /*override fun getLayoutView(): View{
        *//*requestWindowFeature(Window.FEATURE_NO_TITLE)
        val flag = WindowManager.LayoutParams.FLAG_FULLSCREEN
        val window = window
        window.setFlags(flag, flag)*//*
        mWebView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            settings.javaScriptEnabled = true
            webViewClient = object :WebViewClient(){
                override fun shouldOverrideUrlLoading(webView: WebView?, url: String?): Boolean {
                    println("$TAG.shouldOverrideUrlLoading: $url")
                    webView?.loadUrl(url)
                    return true
                }
            }
          loadUrl("http://game.atoshi.mobi/shenhe?uname=123456")
        }
        return mWebView
    }*/

    override fun initData() {

    }

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
                    loadUrl("http://game.atoshi.mobi/shenhe?uname=123456")
                }
                setContentView(mWebView)
            }, 10)
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

