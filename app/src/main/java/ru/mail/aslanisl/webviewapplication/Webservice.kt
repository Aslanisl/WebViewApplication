package ru.mail.aslanisl.webviewapplication

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Random

// Just base url for retrofit
private const val URL = "http://paperwork.press/"

object Webservice {
    val webApi by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(URL)
            .build()
            .create(WebApi::class.java)
    }

    fun loadServerResponse(callback: Callback<ServerResponse>): Call<ServerResponse> {
        val userId = getUUID()
        val call = webApi.loadServerData("http://kulonklub.ru/1AV?sub_id_1=$userId&sub_id_2=antivir1")
        call.enqueue(callback)
        return call
    }

    fun doWork(callback: (() -> Unit)?) {
        callback?.invoke()
    }

    private fun getUUID(): Int{
        val pref = App.instance.getSharedPreferences("Antivirus", Context.MODE_PRIVATE)
        var uuid = pref.getInt("UUID", 0)
        if (uuid == 0){
            uuid = createUUID()
            pref.edit().putInt("UUID", uuid).apply()
        }
        return uuid
    }
    private fun createUUID() = 10000000 + Random().nextInt(99999999)
}
