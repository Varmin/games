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
class WxLoginReceiver(private val login: IWxLogin): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.takeIf {
            it.action == ACTION_WX_LOGIN && !it.getStringExtra("code").isNullOrEmpty()
        }?.run {
            login?.getAccessToken(getStringExtra("code")!!)
        }
    }
}