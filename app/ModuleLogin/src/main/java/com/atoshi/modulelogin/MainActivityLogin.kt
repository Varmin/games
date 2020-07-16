package com.atoshi.modulelogin

import androidx.recyclerview.widget.RecyclerView
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.base.click
import com.atoshi.modulebase.net.Api
import com.atoshi.modulebase.net.model.BaseObserver
import com.atoshi.modulebase.net.model.TestBean
import com.atoshi.modulebase.net.model.WxAccessToken
import com.atoshi.modulebase.net.model.WxUserInfo
import com.atoshi.modulebase.wx.WXUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main_login.*

class MainActivityLogin : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_main_login

    override fun initData() {
        // TODO: by HY, 2020/7/16 Application 初始化
        WXUtils.registerApp(this)
    }
    override fun initView() {
        tvLogin.click {
            WXUtils.login()
        }

        Api.service.testGet2()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<List<TestBean>>() {
            override fun onSuccess(data: List<TestBean>) {
                println("${javaClass.simpleName}.onSuccess: ${data.size} ")
            }
        })

        Api.service.getAccessToken("resp.code")
            .subscribe(object : Observer<WxAccessToken> {
                override fun onComplete() {
                    println("${javaClass.simpleName}.onComplete: ")
                }

                override fun onSubscribe(d: Disposable?) {
                    println("${javaClass.simpleName}.onSubscribe: ")
                }

                override fun onNext(t: WxAccessToken?) {
                    println("${javaClass.simpleName}.onNext: ${t?.openid} ")
                }

                override fun onError(e: Throwable?) {
                    println("${javaClass.simpleName}.onError: ${e.toString()} ")
                }
            })

        Api.service.getUserInfo("token", "openid")
            .subscribe(object :Observer<WxUserInfo>{
                override fun onComplete() {
                    println("${javaClass.simpleName}.onComplete: ")
                }

                override fun onSubscribe(d: Disposable?) {
                    println("${javaClass.simpleName}.onSubscribe: ")
                }

                override fun onNext(info: WxUserInfo?) {
                    info?.apply {
                        println("${javaClass.simpleName}.onNext: $nickname, $headimgurl ")
                    }
                }

                override fun onError(e: Throwable?) {
                    println("${javaClass.simpleName}.onError: ${e.toString()} ")
                }

            })
    }
}
