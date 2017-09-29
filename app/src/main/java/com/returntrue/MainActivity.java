package com.returntrue;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.returntrue.framework.JActivity;
import com.returntrue.manager.WebViewManager;
import com.returntrue.util.Const;
import com.returntrue.view.ObservableWebView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends JActivity
{
    @BindView(R.id.web)
    ObservableWebView web;

    @BindView(R.id.webview_loading)
    ViewGroup webviewLoading;

    private WebViewManager webViewManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_fragment);
        ButterKnife.bind(this);
        initView();
        loadUrl();
    }

    private void initView()
    {
        webViewManager = new WebViewManager(this, web, new WebViewManager.Listener()
        {
            @Override
            public void onShowWebLoading()
            {
                showWebLoading();
            }

            @Override
            public void onLoadUrl(String url)
            {
            }

            @Override
            public void onProgressChanged(WebView view, int progress)
            {
//                progressBarTitle.setText("載入 " + String.valueOf(progress) + " %");
            }

            @Override
            public void onDismissWebLoading()
            {
                dismissWebLoading();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        web.onResume();
    }

    public void loadUrl()
    {
        showWebLoading();
        webViewManager.loadUrl(Const.URL);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        dismissWebLoading();
        handler.removeCallbacks(null);
        if (web != null)
        {
            web.destroy();
        }
    }

    private void dismissWebLoading()
    {
        if (webviewLoading != null)
        {
            webviewLoading.setVisibility(View.GONE);
        }
    }

    private void showWebLoading()
    {
        if (webviewLoading != null)
        {
            webviewLoading.setVisibility(View.VISIBLE);
        }
    }
}
