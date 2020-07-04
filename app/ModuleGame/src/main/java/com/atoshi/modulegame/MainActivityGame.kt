package com.atoshi.modulegame

import android.content.Intent
import com.atoshi.modulebase.BaseActivity
import com.atoshi.modulebase.TestActivity

class MainActivityGame : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_main_game

    override fun initData() {}

    override fun initView() {
        startActivity(Intent(this, GameActivity::class.java))
        finish()
    }

}
