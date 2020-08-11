package com.atoshi.modulebase.net

import com.atoshi.modulebase.net.model.*
import com.atoshi.modulebase.wx.WXUtils
import io.reactivex.rxjava3.core.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
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
        @Query("code") respCode: String,
        @Query("appid") appid: String = WXUtils.WX_APP_ID,
        @Query("secret") secret: String = WXUtils.WX_SECRET
    ): Observable<WxAccessToken>

    @GET("https://api.weixin.qq.com/sns/oauth2/refresh_token?grant_type=refresh_token")
    fun refreshAccessToken(
        @Query("refresh_token") respCode: String,
        @Query("appid") appid: String = WXUtils.WX_APP_ID
    ): Observable<WxAccessToken>


    @GET("https://api.weixin.qq.com/sns/userinfo")
    fun getUserInfo(
        @Query("access_token") accessToken: String,
        @Query("openid") openId: String
    ): Observable<WxUserInfo>


    @POST("userLogin/wxLogin")
    fun wxLogin(@Body body: RequestBody): Observable<BaseResp<UserInfo>>
    @POST("userLogin/register")
    fun wxRegister(@Body body: RequestBody): Observable<BaseResp<UserInfo>>
    @GET("info/getAllAds")
    fun getPlacementId(): Observable<BaseResp<TopOnBean>>
    @GET("info/version?os=1")
    fun checkVersion():Observable<BaseResp<VersionInfo>>
    @POST("userLogin/update")
    fun update(@Header("token") token:String, @Body body: RequestBody): Observable<BaseResp<UserInfo>>
}