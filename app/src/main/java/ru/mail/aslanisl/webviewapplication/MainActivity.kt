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

private const val URL_LOADING_IMAGE = "https://www.dropbox.com/s/aow99vf0wpr5uks/preloader.jpg?dl=0"

class MainActivity : AppCompatActivity() {
    private var jSCommandsInvoked = false
    private var callback: ((List<ServerModel>) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initWebView()
        Glide.with(this).load(URL_LOADING_IMAGE).into(loadingImage)

        callback = { involveCommands(JSCommanFactory.generateCommands(it)) }

        webView.loadUrl("https://yandex.ru")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (jSCommandsInvoked.not()) Webservice.doWork(callback)
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
    }

    private fun involveCommands(commands: List<JSCommand>) {
        jSCommandsInvoked = true
        commands.forEach {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("javascript:${it.command}", null)
            } else {
                webView.loadUrl("javascript:${it.command}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
    }
}
