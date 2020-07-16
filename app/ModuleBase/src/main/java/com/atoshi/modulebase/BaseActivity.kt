package com.atoshi.modulebase

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * created by HYY on 2020/7/2
 * description:
 */
abstract class BaseActivity : AppCompatActivity() {
    protected var TAG = "BaseActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        TAG = localClassName
        super.onCreate(savedInstanceState)
        if (getLayoutView() != null) {
            setContentView(getLayoutView())
        }else{
            setContentView(getLayoutId())
        }
        initData()
        initView()
    }

    open fun getLayoutView(): View? = null
    abstract fun getLayoutId(): Int
    abstract fun initData()
    abstract fun initView()

}