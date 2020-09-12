package com.atoshi.modulebase.net.model

data class ShareConfig(
    val avatar: String = "",
    val inviteCode: String = "",
    val nickName: String = "",
    val shareList: List<Share>
){
    data class Share(
        val bgUrl: String = "",
        val description: String = "",
        val id: Int,
        val qrCodeUrl: String = ""
    )
}