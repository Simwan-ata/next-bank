package com.banksms.manager

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var bridge: FinanceBridge

    companion object {
        private const val ASSET_HOST = "appassets.androidplatform.net"
        private const val ASSET_PREFIX = "/assets/"
        private const val HOME_URL = "https://appassets.androidplatform.net/assets/index.html"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        bridge = FinanceBridge(this, TransactionRepository(this))

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler(ASSET_PREFIX, WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowFileAccess = false
        settings.allowContentAccess = false
        settings.allowUniversalAccessFromFileURLs = false
        settings.allowFileAccessFromFileURLs = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setSupportZoom(false)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ) = assetLoader.shouldInterceptRequest(request.url)

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = !isTrustedAssetUrl(request.url)
        }

        webView.webChromeClient = WebChromeClient()
        webView.addJavascriptInterface(bridge, "AndroidFinance")
        webView.loadUrl(HOME_URL)
    }

    private fun isTrustedAssetUrl(uri: Uri): Boolean =
        uri.scheme == "https" &&
            uri.host == ASSET_HOST &&
            uri.path.orEmpty().startsWith(ASSET_PREFIX)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 4101 && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            webView.evaluateJavascript(
                "window.onSmsPermissionGranted && window.onSmsPermissionGranted()",
                null
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}