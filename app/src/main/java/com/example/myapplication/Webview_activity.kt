package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity



class MyWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (url.startsWith("https://accounts.google.com/o/oauth2/auth")) {
            // Handle the OAuth2 request in your app.
            return true
        }
        return false
    }
}
//
//
//class Webview_activity : AppCompatActivity() {
//     var message:String ="Console log";
//
//    @SuppressLint("MissingInflatedId")
////    @SuppressLint("SetJavaScriptEnabled")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.webview_layout)
//
//
//        val webView = findViewById<WebView>(R.id.webView)
//        webView.webViewClient = MyWebViewClient()
//        webView.settings.javaScriptEnabled = true
//        webView.settings.userAgentString = "useragentstring"
//        webView.getSettings().setSupportMultipleWindows(true);
//
//        webView.settings.javaScriptCanOpenWindowsAutomatically = true
//        webView.settings.domStorageEnabled = true
//        webView.loadUrl("https://deepvisiontech.ai/tnstartup/home.html")
//        webView.clearCache(true) // Clears the cache, including disk and memory caches.
//        webView.clearFormData()  // Clears any stored form data in the WebView.
//        webView.clearHistory()
//        webView.webViewClient = WebViewClient()
//
//        var relodButton = findViewById<Button>(R.id.reload)
//        var logsButton =findViewById<Button>(R.id.logs)
//
//        logsButton?.let { logsButton->logsButton.setOnClickListener {
//            var Logsintent = Intent(this, Logs::class.java);
//            Logsintent.putExtra("message",message);
//            startActivity(Logsintent)
//        }
//        }
//        relodButton?.let { reloadButton ->
//            reloadButton.setOnClickListener(View.OnClickListener {
//
//                println("heeeeeeeeeeeeeeeelo");
//                webView.loadUrl("https://deepvisiontech.ai/tnstartup/home.html")
//            })
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//        {
//            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
//        }
//        // loading http://www.google.com url in the WebView.
//         // Clears the browsing history.
//
//// If you want to clear all the data including cookies and sessions, use the CookieManager:
////        val cookieManager = CookieManager.getInstance()
////        cookieManager.removeAllCookies(null)
////        cookieManager.flush()
//
//        webView.webChromeClient = object : WebChromeClient() {
//            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
//                message+="\n\n"+ consoleMessage.message()
//
//                Log.d("WebViewConsole", message)
//                return true
//            }
//        }
////        fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
////            if (errorCode == WebViewClient.ERROR_AUTHENTICATION) {
////                // Handle the authentication error in your app.
////            }
////            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
////                super.onActivityResult(requestCode, resultCode, data)
////
////                if (requestCode == RC_GOOGLE_SIGN_IN) {
////                    // Handle the Google Sign In result.
////                }
////            }
//
//        // this will enable the javascript.
//
//
//        // WebViewClient allows you to handle
//        // onPageFinished and override Url loading.
//
//
//    }
//}