package com.atoshi.modulebase.net.model

data class VersionInfo(
    val id: Int,
    val info: String,
    val os: Int,
    val updateType: Int,
    var url: String,
    var versionCode: Int,
    val versionName: String
)