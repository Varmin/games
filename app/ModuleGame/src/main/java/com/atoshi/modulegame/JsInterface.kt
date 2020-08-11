package com.atoshi.modulegame

import android.webkit.JavascriptInterface
import com.atoshi.moduleads.TopOnHelper
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulebase.utils.startPath
import com.atoshi.modulebase.wx.IWxLogin
import com.atoshi.modulebase.wx.WXUtils

class JsInterface(private val act: GameActivity, private val callback: TopOnHelper.Callback?) {
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
}