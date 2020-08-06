package com.atoshi.modulegame

import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

class GameWebviewClient: WebViewClient() {
    override fun shouldOverrideUrlLoading(webView: WebView?, url: String?): Boolean {
        println("GameWebviewClient.shouldOverrideUrlLoading: $url")
        webView?.loadUrl(url)
        return true
    }
}