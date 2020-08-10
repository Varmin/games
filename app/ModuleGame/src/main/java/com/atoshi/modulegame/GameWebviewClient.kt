package com.atoshi.modulegame

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * todo token验证？
 * http://game.atoshi.mobi/other/android/?openid=oaMf80tEUmX9jkuXwhxKLyrTM5yQ&token=OePNgUVpQboVEF8VaN4_kbU3WB5hKpwX
 * http://game.atoshi.mobi/other/android/?openid=oaMf80tEUmX9jkuXwhxKLyrTM5yQ&token=OePNgUVpQboVEF8VaN4_keBOMum5OBa4
 * http://game.atoshi.mobi/other/android/?openid=oaMf80tEUmX9jkuXwhxKLyrTM5yQ&token=OePNgUVpQbp04K445HnBVgMxSKIX56g-
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
                    if(url?.startsWith(GAME_BASE_URL) == true) return@run "$url&reload=true"
                }
                return@run null
            }?.run {
                loadUrl(this)
            }
        }
    }
}