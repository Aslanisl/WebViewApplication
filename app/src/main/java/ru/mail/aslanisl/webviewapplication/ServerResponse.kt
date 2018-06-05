package ru.mail.aslanisl.webviewapplication

data class ServerResponse (
    val url: String,
    val elements: List<ServerModel>?
)