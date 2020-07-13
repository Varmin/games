package com.atoshi.games.wxapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.atoshi.games.R
import com.atoshi.modulebase.wx.WXUtils
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

class WXEntryActivity : AppCompatActivity(), IWXAPIEventHandler{
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
                        ConstantsAPI.COMMAND_SENDAUTH -> "登录成功"
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


    private fun toast(toast: String){
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
    }
}