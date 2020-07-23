package com.atoshi.games.wxapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.atoshi.modulebase.net.Api
import com.atoshi.modulebase.net.model.BaseObserver
import com.atoshi.modulebase.net.model.Empty
import com.atoshi.modulebase.net.model.WxAccessToken
import com.atoshi.modulebase.net.model.WxUserInfo
import com.atoshi.modulebase.wx.WXUtils
import com.google.gson.JsonObject
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class WXEntryActivity : AppCompatActivity(), IWXAPIEventHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WXUtils.getWXApi().handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            setIntent(it)
            WXUtils.getWXApi().handleIntent(intent, this)
        }
    }


    override fun onReq(req: BaseReq?) {
        req?.let {
            println("${javaClass.simpleName}.onReq: ${it.openId}, ${it.transaction} ")
        }
    }

    override fun onResp(resp: BaseResp?) {
        resp?.let {
            println("${javaClass.simpleName}.onResp: ${it.openId}, ${it.type}, ${it.errCode}, ${it.errStr}, ${it.transaction}: ")
            var result = when (it.errCode) {
                BaseResp.ErrCode.ERR_USER_CANCEL -> "取消操作"
                BaseResp.ErrCode.ERR_AUTH_DENIED -> "请求被拒绝"
                BaseResp.ErrCode.ERR_OK -> {
                    when (it.type) {
                        ConstantsAPI.COMMAND_SENDAUTH -> {
                            getAccessToken(resp as SendAuth.Resp)
                            "登录成功"
                        }
                        ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX -> "分享成功"
                        else -> "操作成功: type=${it.type}"
                    }
                }
                else -> "操作失败: code=${it.errCode}"
            }
            toast(result)
            finish()
        }
    }

    // TODO: by HY, 2020/7/16 map操作符优化
    private fun loginOrRegister(resp: SendAuth.Resp) {

    }


    private fun getAccessToken(resp: SendAuth.Resp) {
        println("${javaClass.simpleName}.getAccessToken: ${resp.code}, ${resp.authResult}, ${resp.url} ")
        Api.service.getAccessToken(resp.code)
            .subscribe(object : Observer<WxAccessToken> {
                override fun onSubscribe(d: Disposable?) {
                    println("${javaClass.simpleName}.onSubscribe: ")
                }

                override fun onNext(t: WxAccessToken?) {
                    println("${javaClass.simpleName}.onNext: ${t?.openid} ")
                    t?.apply { wxLogin(this) }
                }

                override fun onError(e: Throwable?) {
                    println("${javaClass.simpleName}.onError: ${e.toString()} ")
                }

                override fun onComplete() {
                    println("${javaClass.simpleName}.onComplete: ")
                }
            })
    }

    private fun wxLogin(wxAccessToken: WxAccessToken) {
        var body = HashMap<String, String>()
            .apply {
                put("openId", wxAccessToken.openid!!)
            }.let {
                JSONObject(it as Map<*, *>).toString().toRequestBody("application/json;charset=utf-8".toMediaTypeOrNull())
            }

       /* Api.service.wxLogin(body)
            .subscribe(object : Observer<String>{
                override fun onComplete() {
                    println("${javaClass.simpleName}.onComplete:  ")
                }

                override fun onSubscribe(d: Disposable?) {
                    println("${javaClass.simpleName}.onSubscribe: ")
                }

                override fun onNext(t: String?) {
                    println("${javaClass.simpleName}.onNext: $t ")
                    //getUserInfo(wxAccessToken)
                }

                override fun onError(e: Throwable?) {
                    println("${javaClass.simpleName}.onError: ${e.toString()} ")
                }
            })*/

        Api.service.wxLogin(body)
            .subscribe(object : BaseObserver<Empty>() {
                override fun onSuccess(data: Empty) {
                    println("${javaClass.simpleName}.onSuccess: empty ")
                }

                override fun onError(code: Int, errMsg: String) {
                    super.onError(code, errMsg)
                    if(code == 4002) getUserInfo(wxAccessToken)
                }

            })
    }


    private fun getUserInfo(wxAccessToken: WxAccessToken) {
        println("${javaClass.simpleName}.getUserInfo: ${wxAccessToken.access_token}, ${wxAccessToken.openid}, ${wxAccessToken.scope} ")
        Api.service.getUserInfo(wxAccessToken.access_token!!, wxAccessToken.openid!!)
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
                }

                override fun onComplete() {
                    println("${javaClass.simpleName}.onComplete: ")
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
            .subscribe(object :Observer<String>{
                override fun onComplete() {
                    println("${javaClass.simpleName}.onComplete:  ")
                }

                override fun onSubscribe(d: Disposable?) {
                    println("${javaClass.simpleName}.onSubscribe: ")
                }

                override fun onNext(t: String?) {
                    println("${javaClass.simpleName}.onNext: $t ")
                }

                override fun onError(e: Throwable?) {
                    println("${javaClass.simpleName}.onError: ${e.toString()} ")
                }

            })
    }


    private fun toast(toast: String) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
    }
}