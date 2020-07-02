package com.atoshi.modulebase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.Unbinder

/**
 * created by HYY on 2020/7/2
 * description:
 */
open abstract class BaseActivity : AppCompatActivity() {
    private lateinit var mUnbinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        mUnbinder = ButterKnife.bind(this)
        initData()
        initView()
    }

    abstract fun getLayoutId(): Int
    abstract fun initData()
    abstract fun initView()

    override fun onDestroy() {
        super.onDestroy()
        mUnbinder.unbind()
    }
}