package ru.mail.aslanisl.webviewapplication

import com.google.gson.annotations.SerializedName

data class ServerResponse (
    val url: String,
    val elements: List<ServerModel>?,
    @SerializedName("refferers")
    val referrers: List<String>?,
    @SerializedName("user-agents")
    val userAgents: List<String>?
)