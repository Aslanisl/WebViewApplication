package ru.mail.aslanisl.webviewapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface WebApi {

    @GET
    fun loadServerData(@Url url: String): Call<ServerResponse>

    @GET
    fun loadUrl(@Url url: String): Call<String>
}
