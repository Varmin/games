package com.atoshi.modulelogin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * author：yang
 * created on：2020/8/10 14:22
 * description:
 */
class WxLoginReceiver(val login: IWxLogin): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.takeIf {
            it.action == ACTION_WX_LOGIN && !it.getStringExtra("code").isNullOrEmpty()
        }?.run {
            login.getAccessToken(getStringExtra("code")!!)
        }
    }
}