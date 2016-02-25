package com.bulreed.webviewforvideo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();
    protected CustomViewCallback mCallBack;
    protected View mCustomView;

    private FrameLayout mFrameLayout;

    private WebView mWebview;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFrameLayout = (FrameLayout) findViewById(R.id.framelayout);
        mWebview = (WebView) findViewById(R.id.webview);

        WebChromeClient webChrome = new WebChromeClient() {

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                Log.d(DEBUG_TAG, "onShowCustomView");
                if(mCustomView != null){
                    callback.onCustomViewHidden();
                    return;
                }
                mWebview.setVisibility(View.GONE);
                mFrameLayout.addView(view);
//                setFullScreen();
                mCustomView = view;
                mCallBack = callback;
            }

            @Override
            public void onHideCustomView() {
                Log.d(DEBUG_TAG,"onHideCustomView");
                if(mCustomView == null){
                    return;
                }
                mCallBack.onCustomViewHidden();
                mFrameLayout.removeView(mCustomView);
//                mCustomView = null;
//                quitFullScreen();
                mWebview.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.v(DEBUG_TAG, consoleMessage.lineNumber() + ": " + consoleMessage.message());
                return true;
            }

            @Override
            public View getVideoLoadingProgressView() {
                return super.getVideoLoadingProgressView();
            }
        };

        // have to set _any_ web chrome client for console and alerts to work
        mWebview.setWebChromeClient(webChrome);
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebview.getSettings().setSupportZoom(false);
        mWebview.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        String url = "file:///android_asset/index.html";
        mWebview.loadUrl(url);
    }

    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void quitFullScreen() {
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    @Override
    protected void onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebview.onPause();
        } else {
            mWebview.pauseTimers();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebview.onResume();
        } else {
            mWebview.resumeTimers();
        }
    }
}
