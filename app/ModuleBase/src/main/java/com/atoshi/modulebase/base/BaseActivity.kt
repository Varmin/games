package com.atoshi.modulebase.base

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.Window.*
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * created by HYY on 2020/7/2
 * description:
 */
abstract class BaseActivity : AppCompatActivity(), BaseView {
    protected var TAG = "BaseActivity"

    //是否全屏
    protected var FULL_SCREEN = false
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(FULL_SCREEN){
            window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TAG = localClassName
        super.onCreate(savedInstanceState)
        if (getLayoutView() != null) {
            setContentView(getLayoutView())
        }else if(getLayoutId() != -1){
            setContentView(getLayoutId())
        }
        initData()
        initView()
    }

    open fun getLayoutView(): View? = null
    abstract fun getLayoutId(): Int
    abstract fun initData()
    abstract fun initView()


    override fun toast(toast: String) = Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
    override fun loading() {
        toast("loading")
    }

    override fun loaded() {
        toast("loaded")
    }

}