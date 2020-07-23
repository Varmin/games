package com.atoshi.modulelogin

import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.base.click
import com.atoshi.modulebase.net.Api
import com.atoshi.modulebase.net.model.WxAccessToken
import com.atoshi.modulebase.net.model.WxUserInfo
import com.atoshi.modulebase.wx.WXUtils
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main_login.*

class MainActivityLogin : BaseActivity() {
    init {
        FULL_SCREEN = true
    }
    override fun getLayoutId(): Int = R.layout.activity_main_login

    override fun initData() {}

    override fun initView() {
        tvLogin.click {
            WXUtils.login()
        }
    }
}
