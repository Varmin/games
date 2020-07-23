package com.atoshi.games

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.atoshi.modulebase.base.startFinish
import com.atoshi.modulebase.utils.SPTool
import com.atoshi.modulegame.MainActivityGame
import com.atoshi.modulelogin.MainActivityLogin

// TODO: by HY, 2020/7/22 放到base模块
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if(TextUtils.isEmpty(SPTool.getString(SPTool.WX_OPEN_ID))){
            startFinish<MainActivityLogin>()
        }else{
            startFinish<MainActivityGame>()
        }
    }
}