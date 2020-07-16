package com.atoshi.modulelogin

import com.atoshi.modulebase.BaseActivity
import com.atoshi.modulebase.net.Api
import com.atoshi.modulebase.net.model.BaseObserver
import com.atoshi.modulebase.net.model.TestBean
import com.atoshi.modulebase.wx.WXUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main_login.*

class MainActivityLogin : BaseActivity() {


    override fun getLayoutId(): Int = R.layout.activity_main_login

    override fun initData() {
        WXUtils.registerApp(this)
    }
    override fun initView() {
        // TODO: by HY, 2020/7/15 顶层函数
        ivLoginWx.setOnClickListener {
            WXUtils.login()
        }

        tvLoginTest.text = "Kotlin"

        Api.service.testGet2()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<List<TestBean>>() {
            override fun onSuccess(data: List<TestBean>) {
                println("${javaClass.simpleName}.onSuccess: ${data.size} ")
            }
        })
    }
}
