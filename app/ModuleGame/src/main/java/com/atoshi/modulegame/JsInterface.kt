package com.atoshi.modulegame

import android.webkit.JavascriptInterface
import com.atoshi.moduleads.TopOnHelper
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.net.Api
import com.atoshi.modulebase.net.model.BaseObserver
import com.atoshi.modulebase.net.model.UserInfo
import com.atoshi.modulebase.net.model.WxAccessToken
import com.atoshi.modulebase.net.model.WxUserInfo
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulebase.utils.startPath
import com.atoshi.modulebase.wx.WXUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class JsInterface(private val act: BaseActivity, private val callback: TopOnHelper.Callback?) {
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
        //loading()
        WXUtils.login()
    }



   /* private fun getAccessToken(code: String) {
        println("${javaClass.simpleName}.getAccessToken: $code")
        Api.service.getAccessToken(code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<WxAccessToken> {
                override fun onSubscribe(d: Disposable?) {
                    loading()
                    println("${javaClass.simpleName}.onSubscribe: ")
                }

                override fun onNext(t: WxAccessToken?) {
                    println("${javaClass.simpleName}.onNext: ${t?.openid} ")
                    t?.apply { wxLogin(this) }
                }

                override fun onComplete() {loaded()}
                override fun onError(e: Throwable?) {
                    println("${javaClass.simpleName}.onError: ${e.toString()} ")
                    loaded()
                }
            })
    }


    // TODO: by HY, 2020/7/23 openId限制太死，还是用token登录，改接口
    private fun wxLogin(wxAccessToken: WxAccessToken) {
        var body = HashMap<String, String>()
            .apply {
                put("openId", wxAccessToken.openid!!)
            }.let {
                JSONObject(it as Map<*, *>).toString().toRequestBody("application/json;charset=utf-8".toMediaTypeOrNull())
            }

        Api.service.wxLogin(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<UserInfo>() {
                override fun onSuccess(data: UserInfo) {
                    println("${javaClass.simpleName}.onSuccess: $data ")
                    SPTool.putString(WXUtils.WX_OPEN_ID, wxAccessToken.openid)
                    SPTool.putString(WXUtils.APP_USER_TOKEN, data.token)
                    sendReloadUrl()
                    finish()
                }

                override fun onError(code: Int, errMsg: String) {
                    super.onError(code, errMsg)
                    if(code == 4002){
                        getUserInfo(wxAccessToken)
                    }else{
                        loaded()
                    }
                }
            })
    }


    private fun getUserInfo(wxAccessToken: WxAccessToken) {
        println("${javaClass.simpleName}.getUserInfo: ${wxAccessToken.access_token}, ${wxAccessToken.openid}, ${wxAccessToken.scope} ")
        Api.service.getUserInfo(wxAccessToken.access_token!!, wxAccessToken.openid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<WxUserInfo> {
                override fun onSubscribe(d: Disposable?) {
                    println("${javaClass.simpleName}.onSubscribe: ")
                }

                override fun onNext(info: WxUserInfo?) {
                    info?.apply {
                        println("${javaClass.simpleName}.onNext: $nickname, $headimgurl ")
                        wxRegister(info)
                    }
                }

                override fun onError(e: Throwable?) {
                    println("${javaClass.simpleName}.onError: ${e.toString()} ")
                    loaded()
                }

                override fun onComplete() {
                    println("${javaClass.simpleName}.onComplete: ")
                    loaded()
                }
            })
    }*/
}