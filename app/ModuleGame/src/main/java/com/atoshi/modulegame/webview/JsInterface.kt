package com.atoshi.modulegame.webview

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.webkit.JavascriptInterface
import com.atoshi.moduleads.TopOnHelper
import com.atoshi.modulebase.net.Api
import com.atoshi.modulebase.net.model.BaseObserver
import com.atoshi.modulebase.net.model.UserInfo
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulebase.utils.startPath
import com.atoshi.modulebase.wx.IWxApi
import com.atoshi.modulebase.wx.WXUtils
import com.atoshi.modulegame.GameActivity
import com.atoshi.modulegame.IGameView
import com.atoshi.modulegame.UpdateManager
import com.qq.e.ads.hybrid.HybridAD
import com.qq.e.ads.hybrid.HybridADListener
import com.qq.e.ads.hybrid.HybridADSetting
import com.qq.e.comm.managers.GDTADManager
import com.qq.e.comm.util.AdError
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class JsInterface(
    private val act: GameActivity,
    private val callback: TopOnHelper.ListenerCallback?
) {
    @JavascriptInterface
    fun showIntersAds() {
        showIntersAds(0)
    }

    @JavascriptInterface
    fun showIntersAds(index: Int) {
        TopOnHelper.intersShow(act, index, false, callback)
    }

    @JavascriptInterface
    fun showRewardAds() {
        showRewardAds(0)
    }

    @JavascriptInterface
    fun showRewardAds(index: Int) {
        TopOnHelper.rewardShow(act, index, false, callback)
    }

    @JavascriptInterface
    fun signOut() {
        SPTool.putString(WXUtils.WX_OPEN_ID, "")
        SPTool.putString(WXUtils.APP_USER_TOKEN, "")
        act.run {
            runOnUiThread {
                toast("退出登录")
                act.startPath("com.atoshi.modulelogin.MainActivityLogin")
            }
        }
    }

    @JavascriptInterface
    fun updateUserInfo() {
        SPTool.getString(WXUtils.WX_REFRESH_ACCESS_TOKEN).run {
            if (isNullOrEmpty()) {
                signOut()
            } else {
                act.runOnUiThread { (act as? IWxApi)?.getAccessToken(this) }
            }
        }
        /*val url = "http://game.atoshi.mobi/other/app?uname=7l0fmAwjrPnIOac/xXykKtRE1NVvOGudwUllWw==&unid=yzl123123&time=1600409945902&gid=20002&sign=f18690eea283e67f4005b147890b484a&nickname=151****3407&userAvatar=https://yuanzilian-image.oss-cn-hangzhou.aliyuncs.com/APP_IMAGE/5c4ea122-47ad-4273-8a94-2941e4b10b32.png"
        gamesInfo(url, "1110577168", "GameApp")*/
    }

    @JavascriptInterface
    fun updateToken() {
        useLogin()
    }

    private fun useLogin() {
        val openId = SPTool.getString(WXUtils.WX_OPEN_ID)
        var body = HashMap<String, String>()
            .apply {
                put("openId", openId)
            }.let {
                JSONObject(it as Map<*, *>).toString()
                    .toRequestBody("application/json;charset=utf-8".toMediaTypeOrNull())
            }

        Api.service.wxLogin(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<UserInfo>() {
                override fun onSuccess(data: UserInfo) {
                    println("${javaClass.simpleName}.onSuccess: $data ")
                    SPTool.putString(WXUtils.APP_USER_TOKEN, data.token)
                    act.updateToken(data.token)
                }

                override fun onError(errCode: Int, errMsg: String) {
                    super.onError(errCode, errMsg)
                    act.updateToken("error: $errCode, $errMsg")
                    act.toast(errMsg)
                }
            })
    }

    @JavascriptInterface//109
    fun getVersionCode(): Int = com.atoshi.modulebase.utils.getVersionCode(act)

    @JavascriptInterface//1.0.9
    fun getVersionName(): String = com.atoshi.modulebase.utils.getVersionName(act)

    @JavascriptInterface
    fun checkUpgradeApp() = UpdateManager(act).checkVersion()

    @JavascriptInterface
    fun shareConfig() {
        act.runOnUiThread { if (!act.isFinishing) (act as IGameView).shareConfig() }
    }


    /**
     * 0：不在
     * 1：在
     */
    @JavascriptInterface
    fun othersGames(status: Int) {
        act.runOnUiThread { if (!act.isFinishing) (act as IGameView).othersGames(status) }
    }

    /**
     * 打开其它游戏
     * url: 游戏链接
     * placementId: 媒体id
     * gameName: 游戏名称
     */
    @JavascriptInterface
    fun gamesInfo(url: String, appId: String, gameName: String) {
        act.runOnUiThread {
            GDTADManager.getInstance().initWith(act, appId)
            HybridAD(act,
                HybridADSetting().apply {
                    title(gameName)
                },
                object : HybridADListener {
                    private var TAG = "HybridADListener"
                    override fun onError(error: AdError?) {
                        println("${TAG}, onError, error code: ${error?.errorCode}, error msg: ${error?.errorMsg}")
                    }

                    override fun onPageShow() {
                        println("${TAG}, onPageShow")
                    }

                    override fun onLoadFinished() {
                        println("${TAG}, onLoadFinished")
                    }

                    override fun onClose() {
                        println("${TAG}, onClose")
                    }
                })
                .apply {
                    loadUrl(url)
                }
        }
    }
}