package com.atoshi.modulelogin

import com.atoshi.modulebase.BaseActivity
import com.atoshi.modulebase.net.Api
import com.atoshi.modulebase.net.model.BaseObserver
import com.atoshi.modulebase.net.model.TestBean

class MainActivityLogin : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_main_login

    override fun initData() {}
    override fun initView() {
        Api.service.testGet2()
            //todo 切换
            .subscribe(object : BaseObserver<List<TestBean>>() {
            override fun onSuccess(data: List<TestBean>) {
                println("${javaClass.simpleName}.onSuccess: ${data?.size} ")
            }
        })
    }
}
