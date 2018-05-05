package com.yun.phonemanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yun.phonemanager.R;

public class News extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        WebView webview =(WebView)findViewById(R.id.webview);
        webview.loadUrl("https://m.leiphone.com/tag/%E6%89%8B%E6%9C%BA%E5%AE%89%E5%85%A8\n");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,String url){
                view.loadUrl(url);
                return true;
            }
        } );
    }
    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && WebView.canGoBack()) {
            WebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    */
}
