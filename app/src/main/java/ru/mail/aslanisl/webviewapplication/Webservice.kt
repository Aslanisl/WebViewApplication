package ru.mail.aslanisl.webviewapplication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val URL = "http://paperwork.press/"

object Webservice {
    private val webApi by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(URL)
            .build()
            .create(WebApi::class.java)
    }

    fun doWork(callback: ((List<ServerModel>) -> Unit)?) {
        callback?.invoke(initTest())
    }

    private fun initTest(): List<ServerModel> {
        val items = mutableListOf<ServerModel>()
        items.add(ServerModel(elementClass = "link menu2__item"))
        return items
    }
}
