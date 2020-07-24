package com.atoshi.modulebase.utils

import android.app.Activity
import android.content.Intent
import android.view.View

/**
 * created by HYY on 2020/7/16
 * description:
 */

fun View.click(click: (View)-> Unit){
    setOnClickListener(click)
}

fun Activity.startPath(path: String){
    startActivity(Intent(this, Class.forName(path)))
}

inline fun <reified T: Activity> Activity.startPath(){
    startActivity(Intent(this, T::class.java))
}
inline fun <reified T: Activity> Activity.startFinish(){
    startActivity(Intent(this, T::class.java))
    finish()
}

private var mLastClickTime: Long = 0
fun isFastClick(): Boolean {
    val time = System.currentTimeMillis()
    val timeD: Long = time - mLastClickTime
    if (timeD in 1..200) return true
    mLastClickTime = time
    return false
}

fun isExitClickFirst(): Boolean {
    val time = System.currentTimeMillis()
    val timeD: Long = time - mLastClickTime
    if (timeD > 2000){
        mLastClickTime = time
        return true
    }
    return false
}
