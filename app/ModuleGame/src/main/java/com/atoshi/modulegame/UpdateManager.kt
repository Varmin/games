package com.atoshi.modulegame

import android.content.Context
import android.content.Intent
import android.net.Uri
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
                    if(data.versionCode > getVersionCode(context)) showDialog(data.url)
                }
            })
    }

    fun showDialog(url: String){
        println("UpdateManager.showDialog: $url")
        AlertDialog.Builder(context)
            .setCancelable(false)
            .setMessage("有新的版本可以更新啦~")
            .setPositiveButton("升级App"){ _, _ ->
                var intent = Intent().apply {
                    action = "android.intent.action.VIEW"
                    data = Uri.parse(url)
                }
                if (intent.resolveActivity(context.packageManager) != null){
                    context.startActivity(Intent.createChooser(intent, "请选择浏览器下载"))
                }else{
                    Toast.makeText(context, "无浏览器可供下载", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }
}