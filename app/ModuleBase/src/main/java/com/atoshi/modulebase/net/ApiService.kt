package com.atoshi.modulebase.net

import com.atoshi.modulebase.net.model.BaseResp
import com.atoshi.modulebase.net.model.TestBean
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url
import java.util.*

/**
 * created by HYY on 2020/7/13
 * description:
 */
interface ApiService {
    @GET
    fun testGet(@Url url: String = "https://wanandroid.com/wxarticle/chapters/json"): Call<String>
    @GET("https://wanandroid.com/wxarticle/chapters/json")
    fun testGet2(): Observable<BaseResp<List<TestBean>>>

    @GET("https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code={respCode}&grant_type=authorization_code")
    fun getAccessToken(@Path("respCode") respCode: String): Observable<BaseResp<TestBean>>


}