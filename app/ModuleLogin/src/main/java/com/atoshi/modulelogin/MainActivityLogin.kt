package com.atoshi.modulelogin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.utils.click
import com.atoshi.modulebase.net.Api
import com.atoshi.modulebase.net.model.*
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulebase.utils.getVersionCode
import com.atoshi.modulebase.utils.getVersionName
import com.atoshi.modulebase.wx.WXUtils
import com.tencent.mm.opensdk.modelmsg.SendAuth
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main_login.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

const val ACTION_WX_LOGIN = "action_wx_login"
class MainActivityLogin : BaseActivity(), IWxLogin {
    private lateinit var mWxLoginReceiver: WxLoginReceiver

    // TODO: by HY, 2020/7/23 EventBus
/*    inner class WxLoginReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.takeIf {
                it.action == ACTION_WX_LOGIN && !it.getStringExtra("code").isNullOrEmpty()
            }?.run {
                getAccessToken(getStringExtra("code")!!)
            }
        }
    }*/
    init {
        FULL_SCREEN = true
    }
    override fun getLayoutId(): Int = R.layout.activity_main_login

    override fun initData() {
        mWxLoginReceiver = WxLoginReceiver(this)
        registerReceiver(mWxLoginReceiver, IntentFilter(ACTION_WX_LOGIN))
    }

    override fun initView() {
        tvLogin.click {
            loading()
            WXUtils.login()
        }
        tvVersion.text = "v"+getVersionName(this)
    }

    override fun onBackPressed() {}


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mWxLoginReceiver)
    }





    // TODO: by HY, 2020/7/16 map操作符优化
    private fun loginOrRegister(resp: SendAuth.Resp) {
    }



    override fun getAccessToken(code: String) {
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
    }

    private fun wxRegister(info: WxUserInfo){
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
            JSONObject(it as Map<*, *>).toString().toRequestBody("application/json;charset=utf-8".toMediaTypeOrNull())
        }
        Api.service.wxRegister(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<UserInfo>() {
                override fun onSuccess(data: UserInfo) {
                    println("${javaClass.simpleName}.onSuccess: $data ")
                    SPTool.putString(WXUtils.WX_OPEN_ID, info.openid)
                    SPTool.putString(WXUtils.APP_USER_TOKEN, data.token)
                    sendReloadUrl()
                    finish()
                }

                override fun onComplete() {
                    super.onComplete()
                    loaded()
                }
                override fun onError(code: Int, errMsg: String) {
                    super.onError(code, errMsg)
                    loaded()
                }
            })
    }

    private fun sendReloadUrl(){
        sendBroadcast(Intent("action_load_url"))
    }
}
