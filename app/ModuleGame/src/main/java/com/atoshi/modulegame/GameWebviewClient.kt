package com.atoshi.modulegame

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * http://game.atoshi.mobi/other/android/?openid=oaMf80tEUmX9jkuXwhxKLyrTM5yQ&token=OePNgUVpQboVEF8VaN4_kbU3WB5hKpwX
 * http://game.atoshi.mobi/other/android/?openid=oaMf80tEUmX9jkuXwhxKLyrTM5yQ&token=OePNgUVpQboVEF8VaN4_keBOMum5OBa4
 */
class GameWebviewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(webView: WebView?, url: String?): Boolean {
        println("GameWebviewClient.shouldOverrideUrlLoading:  ${webView?.copyBackForwardList()?.size}, $url")

        url?.let {
            if (!url.startsWith("http")) {
                launchApp(webView, url)
                webGoback(webView)
                return true
            }
        }
        return super.shouldOverrideUrlLoading(webView, url)
    }

    override fun doUpdateVisitedHistory(webView: WebView?, url: String?, reReload: Boolean) {
        super.doUpdateVisitedHistory(webView, url, reReload)
        url?.run {
            if(contains("reload=true")) webView?.clearHistory()
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
        webView?.run {
            copyBackForwardList().apply {
                for (i in (size-1) downTo 0){
                    getItemAtIndex(i)?.url?.let {
                        if(it.startsWith(GAME_BASE_URL)){
                            loadUrl("$it&reload=true")
                        }
                    }
                }
            }
        }
    }
}