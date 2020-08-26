package com.atoshi.modulegame

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.atoshi.modulebase.net.Api
import com.atoshi.modulebase.net.model.BaseObserver
import com.atoshi.modulebase.net.model.VersionInfo
import com.atoshi.modulebase.utils.getVersionCode
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


class UpdateManager(val context: Context) {
    fun checkVersion(){
        Api.service.checkVersion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :BaseObserver<VersionInfo>(){
                override fun onSuccess(data: VersionInfo) {
                    println("UpdateManager.onSuccess: $data")
                    if(data.versionCode > getVersionCode(context)) showDialog(data)
                }
            })
    }

    fun showDialog(info: VersionInfo){
        println("UpdateManager.showDialog: ${info.url}")
        AlertDialog.Builder(context)
            .setCancelable(false)
            .setMessage(Html.fromHtml(info.info))
            .setPositiveButton("升级App"){ _, _ ->
                try {
                    context.startActivity(Intent().apply {
                        action = "android.intent.action.VIEW"
                        data = Uri.parse(info.url)
                        setClassName("com.android.browser","com.android.browser.BrowserActivity")
                    })
                }catch (e: Exception){
                    var intent = Intent().apply {
                        action = "android.intent.action.VIEW"
                        data = Uri.parse(info.url)
                    }
                    if (intent.resolveActivity(context.packageManager) != null){
                        context.startActivity(Intent.createChooser(intent, "请选择浏览器下载"))
                    }else{
                        Toast.makeText(context, "无浏览器可供下载", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }
}