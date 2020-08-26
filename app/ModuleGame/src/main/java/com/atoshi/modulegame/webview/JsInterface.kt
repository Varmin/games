package com.atoshi.modulegame.webview

import android.webkit.JavascriptInterface
import com.atoshi.moduleads.TopOnHelper
import com.atoshi.modulebase.net.Api
import com.atoshi.modulebase.net.model.BaseObserver
import com.atoshi.modulebase.net.model.UserInfo
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulebase.utils.startPath
import com.atoshi.modulebase.wx.IWxLogin
import com.atoshi.modulebase.wx.WXUtils
import com.atoshi.modulegame.GameActivity
import com.atoshi.modulegame.UpdateManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class JsInterface(private val act: GameActivity, private val callback: TopOnHelper.ListenerCallback?) {
    @JavascriptInterface
    fun showIntersAds(){
        showIntersAds(0)
    }
    @JavascriptInterface
    fun showIntersAds(index: Int){
        TopOnHelper.intersShow(act, index,false, callback)
    }
    @JavascriptInterface
    fun showRewardAds(){
        showRewardAds(0)
    }
    @JavascriptInterface
    fun showRewardAds(index: Int){
        TopOnHelper.rewardShow(act, index,false,  callback)
    }

    @JavascriptInterface
    fun signOut(){
        SPTool.putString(WXUtils.WX_OPEN_ID, "")
        SPTool.putString(WXUtils.APP_USER_TOKEN, "")
        act.run {
            runOnUiThread{
                toast("退出登录")
                act.startPath("com.atoshi.modulelogin.MainActivityLogin")
            }
        }
    }

    @JavascriptInterface
    fun updateUserInfo(){
        SPTool.getString(WXUtils.WX_REFRESH_ACCESS_TOKEN).takeIf {
            println("JsInterface.updateUserInfo: $it")
            !it.isNullOrEmpty()
        }?.run {
            act.runOnUiThread {
                (act as? IWxLogin)?.getAccessToken(this)
            }
        }
    }

    @JavascriptInterface
    fun updateToken(){
        useLogin()
    }
    private fun useLogin() {
        val openId = SPTool.getString(WXUtils.WX_OPEN_ID)
        var body = HashMap<String, String>()
            .apply {
                put("openId", openId)
            }.let {
                JSONObject(it as Map<*, *>).toString().toRequestBody("application/json;charset=utf-8".toMediaTypeOrNull())
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

    @JavascriptInterface
    fun getVersionCode():String = getVersionCode()

    @JavascriptInterface
    fun checkUpgradeApp(){
        UpdateManager(act).checkVersion()
    }
}