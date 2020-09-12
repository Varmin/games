package com.atoshi.modulegame

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.atoshi.moduleads.TopOnHelper
import com.atoshi.moduleads.TopOnTestActivity
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.net.Api
import com.atoshi.modulebase.net.model.*
import com.atoshi.modulebase.utils.*
import com.atoshi.modulebase.wx.*
import com.atoshi.modulegame.webview.GameWebviewClient
import com.atoshi.modulegame.webview.JsInterface
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

const val ACTION_LOAD_URL = "action_load_url"
const val ACTION_PRELOAD_ADS = "action_preload_ads"

class GameActivity : BaseActivity(), IWxApi, IApiForGame {
    companion object{
        var BASE_URL_GAME = if(!BuildConfig.IS_DEBUG) "http://game.lbtb.org.cn" else "http://game.atoshi.mobi/other/android"
    }
    init {
        FULL_SCREEN = true
    }

    private var mWebView: WebView? = null
    private var mUpdateReceiver: WxLoginReceiver? = null

    // TODO: by HY, 2020/7/23 EventBus
    private var mReceiverReload: ReceiverReload? = null

    private val permissions = arrayListOf(Manifest.permission.READ_PHONE_STATE)
    private val CODE_PERMISSION_REQUEST = 1101

    inner class ReceiverReload : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.run {
                if (action == ACTION_LOAD_URL) loadUrl(true)
                if (action == ACTION_PRELOAD_ADS) {
                    TopOnHelper.intersShow(this@GameActivity, 0, true, topOnCallback)
                    TopOnHelper.rewardShow(this@GameActivity, 0, true, topOnCallback)
                }
            }
        }
    }

    var topOnCallback = object : TopOnHelper.ListenerCallback {
        override fun success() {
            println("topOnCallback.success")
            runOnUiThread { adsShowSuccess() }
        }

        override fun error(placementId: String, error: String) {
            println("topOnCallback.error, $placementId, $error")
            runOnUiThread { adsShowError("$placementId, $error") }
        }
    }

    // TODO: by HY, 2020/7/23 声明周期， 在Activity生成但未显示的时候跳转：有没有更早的？
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        startPath("com.atoshi.games.SplashActivity")
    }

    override fun getLayoutId(): Int = -1

    override fun initData() {
        mReceiverReload = ReceiverReload().apply {
            registerReceiver(this, IntentFilter().apply {
                addAction(ACTION_LOAD_URL)
                addAction(ACTION_PRELOAD_ADS)
            })
        }
        mUpdateReceiver = WxLoginReceiver(this, ACTION_WX_REFRESH).apply {
            registerReceiver(this, IntentFilter(ACTION_WX_LOGIN))
        }
    }

    override fun onResume() {
        super.onResume()
        UpdateManager(this).checkVersion()
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO: yang 2020/8/11 android.permission.READ_PHONE_STATE
        mWebView?.destroy()
        mReceiverReload?.apply { unregisterReceiver(this) }
        mUpdateReceiver?.apply { unregisterReceiver(this) }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView!!.canGoBack()) {
                mWebView!!.goBack()
                return true
            }
            if (isExitClickFirst()) {
                toast("再按一次退出应用")
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun initView() {
        println("---------------------------------initView")

        window.decorView.postDelayed({
            if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
            var context = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                this@GameActivity.createConfigurationContext(Configuration())
            } else {
                this@GameActivity
            }
            mWebView = WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                webViewClient = GameWebviewClient()
                addJavascriptInterface(JsInterface(this@GameActivity, topOnCallback), "AtoshiGame")
                loadUrl()
            }
            setContentView(mWebView)
            loadUrl()
        }, 500)

        window.decorView.postDelayed({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permissions[0]
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        permissions.toTypedArray(),
                        CODE_PERMISSION_REQUEST
                    )
                }
            }
        }, 10000)
    }

    // TODO: by HY, 2020/7/24 WebView优化：缓存、预加载...
    private fun loadUrl(reload: Boolean = false) {
        val openId = SPTool.getString(WXUtils.WX_OPEN_ID)
        val token = SPTool.getString(WXUtils.APP_USER_TOKEN)
        val tag = if (reload) "&reload=true" else ""
        mWebView?.loadUrl("$BASE_URL_GAME?openid=$openId&token=$token$tag")
//        mWebView?.loadUrl("https://www.baidu.com")
    }

    private fun adsShowSuccess() {
        mWebView?.loadUrl("javascript:adsShowSuccess()")
    }

    private fun adsShowError(errMsg: String) {
        mWebView?.loadUrl("javascript:adsShowError()")
        mWebView?.loadUrl("javascript:adsShowError('$errMsg')")
    }

    /**
     * 更新信息
     */
    private fun loadUpdate(nickname: String, headimgurl: String) {
        println("GameActivity.loadUpdate：javascript:updateInfo('$nickname', '$headimgurl')")
        mWebView?.loadUrl("javascript:updateInfo('$nickname', '$headimgurl')")
    }

    /**
     * 更新token
     */
    fun updateToken(token: String){
        mWebView?.loadUrl("javascript:updateToken('$token')")
    }

    // TODO: yang 2020/8/10 整理
    //--------------------------wx更新信息--------------------------
    override fun getAccessToken(code: String) {
        println("${javaClass.simpleName}.getAccessToken: $code, ${Thread.currentThread().name}")
        Api.service.refreshAccessToken(code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<WxAccessToken> {
                override fun onSubscribe(d: Disposable?) {
                    loading()
                    println("${javaClass.simpleName}.onSubscribe: ")
                }

                override fun onNext(t: WxAccessToken?) {
                    println("${javaClass.simpleName}.onNext: refreshAccessToken: $t ")
                    t?.apply {
                        getUserInfo(this)
                        SPTool.putString(WXUtils.WX_ACCESS_TOKEN, t.access_token)
                        SPTool.putString(WXUtils.WX_REFRESH_ACCESS_TOKEN, t.refresh_token)
                    }
                }

                override fun onComplete() {}
                override fun onError(e: Throwable?) {
                    //todo refresh_access失效后，需要重新登陆，40030
                    println("${javaClass.simpleName}.onError: ${e.toString()} ")
                    loaded()
                }
            })
    }

    private fun getUserInfo(wxAccessToken: WxAccessToken) {
        println("GameActivity.getUserInfo: ${wxAccessToken.access_token}, ${wxAccessToken.openid}, ${wxAccessToken.scope} ")
        Api.service.getUserInfo(wxAccessToken.access_token, wxAccessToken.openid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<WxUserInfo> {
                override fun onSubscribe(d: Disposable?) {
                    println("${javaClass.simpleName}.onSubscribe: ")
                }

                override fun onNext(info: WxUserInfo?) {
                    info?.apply {
                        println("${javaClass.simpleName}.onNext: $nickname, $headimgurl ")
                        updateInfo(info)
                    }
                }

                override fun onError(e: Throwable?) {
                    println("${javaClass.simpleName}.onError: ${e.toString()} ")
                    loaded()
                }

                override fun onComplete() {
                    println("${javaClass.simpleName}.onComplete: ")
                }
            })
    }

    private fun updateInfo(info: WxUserInfo) {
        var body = HashMap<String, String>().apply {
            put("city", info.city)
            put("country", info.country)
            put("headimgurl", info.headimgurl)
            put("nickname", info.nickname)
            put("openid", info.openid)
            put("province", info.province)
            put("sex", info.sex.toString())
            put("unionid", info.unionid)
        }.let {
            JSONObject(it as Map<*, *>).toString()
                .toRequestBody("application/json;charset=utf-8".toMediaTypeOrNull())
        }

        println("GameActivity.updateInfo: token: " + SPTool.getString(WXUtils.APP_USER_TOKEN))
        Api.service.update(SPTool.getString(WXUtils.APP_USER_TOKEN), body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<UserInfo>() {
                override fun onSuccess(data: UserInfo) {
                    println("${javaClass.simpleName}.onSuccess: $data ")
                    loaded()
                    SPTool.putString(WXUtils.APP_USER_TOKEN, data.token)
                    loadUpdate(info.nickname, info.headimgurl)
                }

                override fun onError(errCode: Int, errMsg: String) {
                    super.onError(errCode, errMsg)
                    loaded()
                }
            })
    }

    override fun shareConfig() {
        Api.service.shareConfig(SPTool.getString(WXUtils.APP_USER_TOKEN))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :BaseObserver<ShareConfig>(){
                override fun onSubscribe(d: Disposable?) {
                    super.onSubscribe(d)
                    loading()
                }
                override fun onSuccess(data: ShareConfig) {
                    share(data)
                }

                override fun onError(errCode: Int, errMsg: String) {
                    super.onError(errCode, errMsg)
                    loaded()
                }
            })
    }

    private fun share(shareConfig: ShareConfig){
        if(shareConfig.shareList.isNotEmpty()){
            shareConfig.shareList[0].run {
                PosterHelper.getInstance()
                    .setBgUrl(bgUrl)
                    .setAvatarUrl(shareConfig.avatar)
                    .setCodeUrl(qrCodeUrl)
                    .setCodeStr("我的邀请码：${shareConfig.inviteCode}")
                    .setIntroStr("我是${shareConfig.nickName}\n${description}")
                    .getPosterBitmap(object :PosterHelper.Callback{
                        override fun success(bitmap: Bitmap) {
                            loaded()
                            showPosterDialog(bitmap)
                        }
                        override fun failed(errMsg: String) {
                            loaded()
                            Toast.makeText(this@GameActivity, errMsg, Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }

    private fun showPosterDialog(bitmap: Bitmap) {
        PosterDialog.getInstance(this@GameActivity)
            .setPosterBitmap(bitmap)
            .setShareClickListener(object : PosterDialog.ShareClickListener{
                override fun shareFriends() {
                    WXUtils.share(bitmap, true)
                }
                override fun shareFriendsCircle() {
                    WXUtils.share(bitmap, false)
                }
            })
            .init().show()
    }
}

