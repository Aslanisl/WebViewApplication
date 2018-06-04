package ru.mail.aslanisl.webviewapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.graphics.Bitmap
import android.os.Handler

private const val URL_LOADING_IMAGE = "https://www.dropbox.com/s/aow99vf0wpr5uks/preloader.jpg?dl=0"
private const val URL_TO_INVOLVE = "https://yandex.ru"

class MainActivity : AppCompatActivity() {
    private var jSCommandsInvoked = false
    private var callback: ((List<ServerModel>) -> Unit)? = null
    private var callbackHrefs: ((String) -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callbackHrefs = { initHrefs(it) }

        initWebView()
        Glide.with(this).load(URL_LOADING_IMAGE).into(loadingImage)

        callback = { involveCommands(JSCommanFactory.generateCommands(it)) }

        webView.loadUrl(URL_TO_INVOLVE)
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
            Webservice.webApi.loadHref(it).enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>?, t: Throwable?) {}

                override fun onResponse(call: Call<String>?, response: Response<String>?) {}
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
        callbackHrefs = null
    }
}
