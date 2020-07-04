package com.atoshi.modulegame

import android.view.*
import com.atoshi.modulebase.BaseActivity
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

class GameActivity : BaseActivity() {

    private lateinit var mWebView: WebView

    override fun getLayoutId(): Int = -1
    override fun getLayoutView(): View{
        val window = window
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val flag = WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.setFlags(flag, flag)
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
    }

    override fun initData() {}
    override fun initView() {}

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()){
            mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

