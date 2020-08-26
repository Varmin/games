package com.atoshi.modulebase.net

import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * created by HYY on 2020/7/13
 * description:
 */
const val IS_RELEASE = true
object Api{
    var BASE_URL_API = if(IS_RELEASE) "http://game.lbtb.org.cn/game/gamebox/" else "http://47.93.219.181:9599/game/gamebox/"
    val service: ApiService
    init {
        service = initRetrofit(initOkHttp()).create(ApiService::class.java)
    }

    private fun initOkHttp():OkHttpClient{
        var httpClient = OkHttpClient.Builder().apply {
            connectTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        // TODO: by HY, 2020/7/14 urlManager?
        return RetrofitUrlManager.getInstance().with(httpClient).build()
    }

    private fun initRetrofit(okHttpClient: OkHttpClient): Retrofit{
        return Retrofit.Builder().apply {
            client(okHttpClient)
            baseUrl(BASE_URL_API)
            addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }




}