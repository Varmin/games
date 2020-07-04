package com.atoshi.modulebase

import android.widget.TextView

class TestActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_test

    override fun initData() {
    }

    override fun initView() {
        var tvTest = findViewById<TextView>(R.id.tv_test)
        tvTest.text = "Kotlin"
    }
}