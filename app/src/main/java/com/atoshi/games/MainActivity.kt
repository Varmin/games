package com.atoshi.games

import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.base.start
import com.atoshi.modulelogin.MainActivityLogin

class MainActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initData() {
    }

    override fun initView() {
        //start(MainActivityLogin::class.java)
    }
}