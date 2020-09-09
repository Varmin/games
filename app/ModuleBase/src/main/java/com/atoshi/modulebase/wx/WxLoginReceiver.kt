package com.atoshi.modulebase.wx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * author：yang
 * created on：2020/8/10 14:22
 * description:
 */
const val ACTION_WX_LOGIN = "action_wx_login"
const val ACTION_WX_REFRESH = "action_wx_refresh"
class WxLoginReceiver(private val login: IWxApi, private val action: String): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.takeIf {
            it.action == action && !it.getStringExtra("code").isNullOrEmpty()
        }?.run {
            login.getAccessToken(getStringExtra("code")!!)
        }
    }
}