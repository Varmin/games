package com.atoshi.modulebase.wx

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.atoshi.modulebase.utils.SPTool
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.io.ByteArrayOutputStream


/**
 * created by HYY on 2020/7/9
 * description:
 */


object WXUtils {
    // TODO: by HY, 2020/7/22 动态赋值：gradle配置到BuildConfig？方案？
    const val WX_APP_ID = "wxee3b1c56e518eeac"
    const val WX_SECRET = "a4858d99ce5a4c533b7247e545a13b2a"
    private const val WX_REQ_SCOPE = "snsapi_userinfo"
    private const val WX_REQ_STATE = "wechat_sdk_demo_test"
    //保存sp值的key
    const val WX_OPEN_ID = "wx_open_id"
    const val WX_ACCESS_TOKEN = "wx_access_token"
    const val WX_REFRESH_ACCESS_TOKEN = "wx_refresh_access_token"
    const val APP_USER_TOKEN = "app_user_token"

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
                println("WxUtils.login, sendReq: $it")
            }
        } else {
            Toast.makeText(mContext, "您还未安装微信客户端！", Toast.LENGTH_LONG).show()
        }
    }

    fun getWXApi(): IWXAPI = mWxApi

    fun share(bmp: Bitmap, isFriend: Boolean){
        if (mWxApi.isWXAppInstalled) {
            //初始化 WXImageObject 和 WXMediaMessage 对象
            val msg = WXMediaMessage().apply {
                mediaObject =  WXImageObject(bmp)
                //设置缩略图
                val thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 200, true)
                bmp.recycle()
                thumbData = bitmap2Bytes(thumbBmp)
            }

            //构造一个Req
            val req = SendMessageToWX.Req().apply {
                transaction = "img" + System.currentTimeMillis()
                message = msg
                scene = if(isFriend) SendMessageToWX.Req.WXSceneSession else SendMessageToWX.Req.WXSceneTimeline
                userOpenId = SPTool.getString(WXUtils.WX_OPEN_ID)
            }
            mWxApi.sendReq(req)
        } else {
            Toast.makeText(mContext, "您还未安装微信客户端！", Toast.LENGTH_LONG).show()
        }
    }

    private fun bitmap2Bytes(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        bitmap.recycle()
        println("WXUtils.bitmap2Bytes: ${baos.size() / (1024)}")
        return baos.toByteArray()
    }

}

