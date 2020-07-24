package com.atoshi.modulebase.base

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children

/**
 * created by HYY on 2020/7/2
 * description:
 */
abstract class BaseActivity : AppCompatActivity(), BaseView {
    protected var TAG = "BaseActivity"

    //是否全屏
    protected var FULL_SCREEN = false
    protected var TRANSLUCENT_STATUS = false
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (FULL_SCREEN) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        if (TRANSLUCENT_STATUS){
            window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TAG = localClassName
        super.onCreate(savedInstanceState)
        if (getLayoutView() != null) {
            setContentView(getLayoutView())
        } else if (getLayoutId() != -1) {
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
        // TODO: by HY, 2020/7/23 动态加载xml自定义

        var content = findViewById<ViewGroup>(android.R.id.content)
        for (view in content.children.iterator()) {
            (view.tag as? String)?.run {
                if(this == "loading") return
            }
        }

        var bar = ProgressBar(this).apply {
            layoutParams = FrameLayout.LayoutParams(100, 100).apply {
                gravity = Gravity.CENTER or Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
            }
            tag = "loading"

        }
        findViewById<ViewGroup>(android.R.id.content).addView(bar)
    }

    override fun loaded() {
        var content = findViewById<ViewGroup>(android.R.id.content)
        for (view in content.children.iterator()) {
            (view.tag as? String)?.run {
                if(this == "loading"){
                    content.removeView(view)
                    return
                }
            }
        }
    }

}