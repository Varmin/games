package com.atoshi.modulebase.base

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.Window.*
import android.view.WindowManager
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
        setFullScreen()
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


    private fun setFullScreen() {
        requestWindowFeature(FEATURE_NO_TITLE)

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//清除flag
//        window.setStatusBarColor(resources.getColor(android.R.color.transparent));
//        window.setTitleColor(resources.getColor(android.R.color.black))
    }
}