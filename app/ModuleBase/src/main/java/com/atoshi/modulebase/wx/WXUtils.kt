package com.atoshi.modulebase.wx

import android.content.Context
import android.widget.Toast
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * created by HYY on 2020/7/9
 * description:
 */


object WXUtils {
    private const val WX_APP_ID = "wx02027c5ed55b1219"
    private const val WX_SECRET = ""
    private const val WX_REQ_SCOPE = "snsapi_userinfo"
    private const val WX_REQ_STATE = "wechat_sdk_demo_test"

    private lateinit var mWxApi: IWXAPI
    private lateinit var mContext: Context

    /**
     * Application 初始化
     */
    fun registerApp(context: Context): Boolean {
        mContext = context
        mWxApi = WXAPIFactory.createWXAPI(context, WX_APP_ID)
        return mWxApi.registerApp(WX_APP_ID)
    }

    fun login() {
        if (mWxApi.isWXAppInstalled) {
            //Toast.makeText(mContext, "请稍后...", Toast.LENGTH_SHORT).show()
            mWxApi.sendReq(SendAuth.Req().apply {
                scope = WX_REQ_SCOPE
                state = WX_REQ_STATE
            }).let {
                println("WxUtils.login: $it")
            }
        } else {
            Toast.makeText(mContext, "您还未安装微信客户端！", Toast.LENGTH_LONG).show()
        }
    }

    fun getWXApi(): IWXAPI = mWxApi
}

