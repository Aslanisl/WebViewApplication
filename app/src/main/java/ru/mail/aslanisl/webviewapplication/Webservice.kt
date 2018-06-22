package ru.mail.aslanisl.webviewapplication

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Random

// Just base url for retrofit
private const val URL = "http://paperwork.press/"

object Webservice {
    val webApi by lazy {
//        val client = OkHttpClient()
//            .newBuilder()
//            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
//            .build()
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(URL)
//            .client(client)
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

    fun getRandom(from: Int, to: Int): Int {
        return from + Random().nextInt(to)
    }
}
