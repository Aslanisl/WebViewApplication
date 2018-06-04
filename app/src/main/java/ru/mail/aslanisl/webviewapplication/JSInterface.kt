package ru.mail.aslanisl.webviewapplication

import android.webkit.JavascriptInterface

class JSInterface(var callback: ((String) -> Unit)?) {
    @JavascriptInterface
    fun hrefsResponse(urls: String) {
        callback?.invoke(urls)
    }
}
