package com.example.twitterspammer.bindingUtils

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.BindingAdapter

object WebViewUtils {
    @BindingAdapter("setWebViewClient")
    @JvmStatic
    fun setWebViewClient(webView: WebView, client: WebViewClient){
        webView.webViewClient = client
    }

    @BindingAdapter("loadUrl")
    @JvmStatic
    fun loadUrl(webView: WebView, url: String){
        if(url.isNotEmpty()){
            webView.loadUrl(url)
        }
    }
}