package com.atoshi.modulebase.net

import com.atoshi.modulebase.net.model.BaseResp
import com.atoshi.modulebase.net.model.TestBean
import com.atoshi.modulebase.net.model.WxAccessToken
import com.atoshi.modulebase.net.model.WxUserInfo
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.*

/**

Android平台
应用下载地址：未填写
应用签名：a7f323afbec5dda63ec29af9696f84ec
包名：com.atoshi.games
应用官网
http://www.bcsvz.com

 */

/**
 * created by HYY on 2020/7/13
 * description:
 */
interface ApiService {
    @GET
    fun testGet(@Url url: String = "https://wanandroid.com/wxarticle/chapters/json"): Call<String>

    @GET("https://wanandroid.com/wxarticle/{path}/{json}")
    fun testGet2(
        @Path("path") chapters: String = "chapters",
        @Path("json") json: String = "json"
    ): Observable<BaseResp<List<TestBean>>>

    @GET("https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code")
    fun getAccessToken(
        @Query("respCode") respCode: String,
        @Query("appid") appid: String = "wx02027c5ed55b1219",
        @Query("secret") secret: String = "c5ed55b1219"
    ): Observable<WxAccessToken>

    @GET("https://api.weixin.qq.com/sns/userinfo")
    fun getUserInfo(
        @Query("accessToken") accessToken: String,
        @Query("openId") openId: String
    ): Observable<WxUserInfo>


}