package com.atoshi.games

/**
 * 错误信息统计
 * 开屏： onNoAdError: code[ 4001 ],desc[ Return Ad is empty. ],platformCode[ 6000 ],platformMSG[ 未知错误，详细码：102006 ]
 * permission.READ_PHONE_STATE
 */
object TopOnHelper {
    val IS_TOP_OP = false
    val TOPON_APP_ID = if(IS_TOP_OP) "a5aa1f9deda26d" else "a5f1fcab2e2222"
    val TOPON_APP_KEY = if(IS_TOP_OP) "4f7b9ac17decb9babec83aac078742c7" else "080129c71828b0013266ca336e29abe4"

    //开屏广告
    var SPLASH_ID_PANGLE = if(IS_TOP_OP) "b5bea7c1b653ef" else "b5f1fcaca30866"
    var SPLASH_ID_GDT = if(IS_TOP_OP) "b5bea7bfd93f01" else "b5f1fcaca30866"


    //插屏广告
    var INTER_ID_PANGLE = if(IS_TOP_OP) "b5baca585a8fef" else "b5f23be649136c"
    var INTER_ID_GDT = if(IS_TOP_OP) "b5baca561bc100" else "b5f23be649136c"

    //激励视频
    var REWARD_ID_PANGLE = if(IS_TOP_OP) "b5b728e7a08cd4" else "b5f1fcb2d31a05"
    var REWARD_ID_GDT = if(IS_TOP_OP) "b5c2c880cb9d52" else "b5f1fcb2d31a05"




}

