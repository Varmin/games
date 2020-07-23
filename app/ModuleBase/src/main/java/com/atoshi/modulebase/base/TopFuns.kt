package com.atoshi.modulebase.base

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

/*fun Activity.start(clazz: Class<out Activity>){
    startActivity(Intent(this, clazz))
}*/

inline fun <reified T: Activity> Activity.start(){
    startActivity(Intent(this, T::class.java))
}
inline fun <reified T: Activity> Activity.startFinish(){
    startActivity(Intent(this, T::class.java))
    finish()
}

