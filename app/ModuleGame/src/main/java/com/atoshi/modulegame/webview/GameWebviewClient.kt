package com.atoshi.modulegame.webview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.atoshi.modulegame.GameActivity

/**
 * todo token验证？
 * http://game.atoshi.mobi/other/android/?openid=oaMf80tEUmX9jkuXwhxKLyrTM5yQ&token=qyMoRbcw_-d04K445HnBVihnTq4F5ppb
 */
class GameWebviewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(webView: WebView?, url: String?): Boolean {
        println("GameWebviewClient.shouldOverrideUrlLoading:  ${webView?.copyBackForwardList()?.size}, $url")
        url?.takeUnless {
            url.startsWith("http")
        }?.run {
            launchApp(webView, this)
            webGoback(webView)
            return true
        }
        return super.shouldOverrideUrlLoading(webView, url)
    }

    override fun doUpdateVisitedHistory(webView: WebView?, url: String?, reReload: Boolean) {
        super.doUpdateVisitedHistory(webView, url, reReload)
        url?.takeIf {
            it.contains("reload=true")
        }?.run {
            webView?.clearHistory()
        }
    }

    private fun launchApp(webView: WebView?, url: String?) {
        webView?.context?.run {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "未安装相应客户端", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun webGoback(webView: WebView?) {
        webView?.apply {
            copyBackForwardList().run {
                for (i in (size - 1) downTo 0) {
                    val url = getItemAtIndex(i)?.url
                    if(url?.startsWith(GameActivity.BASE_URL_GAME) == true) return@run "$url&reload=true"
                }
                return@run null
            }?.run {
                loadUrl(this)
            }
        }
    }

    override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
        super.onPageStarted(p0, p1, p2)
        println("GameWebviewClient.onPageStarted: $p1")
    }

    override fun onPageFinished(p0: WebView?, p1: String?) {
        super.onPageFinished(p0, p1)
        println("GameWebviewClient.onPageFinished: $p1")
    }
}