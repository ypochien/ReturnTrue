package com.returntrue.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.returntrue.R;
import com.returntrue.framework.JActivity;
import com.returntrue.util.Const;
import com.returntrue.util.JLog;
import com.returntrue.util.JUtil;
import com.returntrue.util.ui.JDialog;


/**
 * Created by josephwang on 2017/4/15.
 */
public class WebViewManager
{
    public static final String TAG = WebViewManager.class.getSimpleName();
    private JActivity activity;
    private WebView web;
    private Listener listener;
    private boolean isClearHistory = false;

    public interface Listener
    {
        void onShowWebLoading();

        void onLoadUrl(String url);

        void onProgressChanged(WebView view, int progress);

        void onDismissWebLoading();
    }

    public WebViewManager(JActivity activity, WebView innerWeb, Listener listener)
    {
        this.activity = activity;
        this.web = innerWeb;
        this.listener = listener;
        initWeb();
    }

    private void initWeb()
    {
        web.setVerticalScrollBarEnabled(true);
        web.setHorizontalScrollBarEnabled(false);
        web.setScrollContainer(true);
        web.getSettings().setAppCacheEnabled(false);
        web.getSettings().setRenderPriority(WebSettings.RenderPriority.NORMAL);
        web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        web.getSettings().setAllowContentAccess(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setPluginState(WebSettings.PluginState.ON);
        web.getSettings().setSupportZoom(false);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        web.getSettings().setSaveFormData(true);
        web.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        web.setBackgroundColor(Color.TRANSPARENT);
        web.setLayerType(View.LAYER_TYPE_NONE, null);

        web.setInitialScale(0);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);

        web.setWebChromeClient(chromeClient);
//        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(webViewClient);
    }

    public void loadUrl(String url)
    {
        JLog.d("WebViewManager url " + url);
        web.loadUrl("about:blank");
        web.loadUrl(url);
    }

    public void loadData(final String htmlData)
    {
        showWebLoading();
        web.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
//                int width = UIAdjuster.getScreenWidth(BaseApplication.getContext());
                String header = "<html>";
                String meta = "<header> <meta id=\"viewport\" " +
                        "name=\"viewport\" content=\"target-densitydpi=device-dpi, " +
                        "width=device-width, initial-scale=1, " +
                        "maximum-scale=1.0, minimum-scale=1.0, " +
                        "user-scalable=no\">";
                String fontSize = "<style> .production_content{\n" +
                        " display: block;\n" +
                        " overflow: hidden;\n" +
                        " width:100%; " +
                        "font-family: 'sans-serif',font-size:14px; line-height: 24px;\n" +
                        "}\n img {" +
                        " width:100%;" +
                        " max-width:320px; " +
                        " }</style> " +
                        "</header>" +
                        "<body>" +
                        "<div id=\"product_content\">";

                StringBuffer content = new StringBuffer();
                content.append(header);
                content.append(meta);
                content.append(fontSize);
                content.append(htmlData);
                content.append("</div>");
                content.append("</body>");
                content.append("</html>");

//                web.loadData(content.toString(), "text/html; charset=UTF-8", null);
                web.loadDataWithBaseURL(Const.PHOTO_PREFIX, content.toString(), null, "UTF-8", null);
                JLog.d("WebViewManager content " +  content.toString());

                web.removeCallbacks(this);
            }
        }, 200L);
    }

    private final Runnable cancelLoadingTask = new Runnable()
    {
        @Override
        public void run()
        {
            dismissWebLoading();
            getHandler().removeCallbacks(this);
        }
    };

    public void clearHistory()
    {
        web.clearFormData();
        web.clearHistory();  // REMOVE THIS LINE
        web.clearCache(true); // REMOVE THIS LINE
        isClearHistory = true;
    }

    private void dismissWebLoading()
    {
        listener.onDismissWebLoading();
    }

    private void showWebLoading()
    {
        listener.onShowWebLoading();
    }

    private Handler getHandler()
    {
        if (activity != null)
        {
            return activity.getHandler();
        }
        return null;
    }

    private Activity getActivity()
    {
        if (activity != null)
        {
            return activity;
        }
        return null;
    }

    private final WebChromeClient chromeClient = new WebChromeClient()
    {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result)
        {
            getHandler().removeCallbacks(cancelLoadingTask);
            getHandler().post(cancelLoadingTask);
            JDialog.showMessage(getActivity(), JUtil.getString(R.string.message_title), message);
            result.confirm();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result)
        {
            getHandler().removeCallbacks(cancelLoadingTask);
            getHandler().post(cancelLoadingTask);
//    		result.confirm();
            JDialog.showMessage(getActivity(),
                    JUtil.getString(R.string.message_title),
                    message,
                    JUtil.getString(R.string.button_confirm),
                    JUtil.getString(R.string.button_cancel),
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            result.confirm();
                        }
                    }, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            result.cancel();
                        }
                    });
            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title)
        {
            super.onReceivedTitle(view, title);
        }

        public void onProgressChanged(WebView view, int progress)
        {
            listener.onProgressChanged(view, progress);
            if (progress == 100)
            {
                getHandler().removeCallbacks(cancelLoadingTask);
                getHandler().post(cancelLoadingTask);
            }
        }
    };

    private final WebViewClient webViewClient = new WebViewClient()
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            JLog.d(JLog.JosephWang, TAG + " shouldOverrideUrlLoading url " + url);
            showWebLoading();
            if (listener != null)
            {
                listener.onLoadUrl(url);
            }

            if (url.startsWith("vnd.youtube:"))
            {
                int n = url.indexOf("?");
                if (n > 0)
                {
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(String.format("http://www.youtube.com/v/%s",
                                    url.substring("vnd.youtube:".length(), n)))));
                }
                return true;
            }
            else if (url.endsWith(".3gp"))
            {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                getActivity().startActivity(intent);
                return true;
            }
            else if (url.startsWith("market://"))
            {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(i);
                return true;
            }
            else if (url.contains("//play.google.com/store/apps"))
            {
                String[] s = url.substring(url.indexOf("?") + 1).split("&");
                for (int i = 0; i < s.length; i++)
                {
                    if (s[i].startsWith("id="))
                    {
                        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?" + s[i])));
                        return true;
                    }
                }
            }
            else if (url.startsWith("http://line.naver.jp/"))
            {
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            showWebLoading();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            super.onReceivedError(view, errorCode, description, failingUrl);
            getHandler().removeCallbacks(cancelLoadingTask);
            getHandler().post(cancelLoadingTask);

            JLog.d(JLog.JosephWang, "onReceivedError errorCode " + errorCode);
            JLog.d(JLog.JosephWang, "onReceivedError description " + description);
            JLog.d(JLog.JosephWang, "onReceivedError failingUrl " + failingUrl);

//            Toast.makeText(getActivity(), "錯誤 onReceivedError " + description + " !!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler sslErrorHandler, SslError error)
        {
            sslErrorHandler.proceed(); // Ignore SSL certificate errors
            getHandler().removeCallbacks(cancelLoadingTask);
            getHandler().post(cancelLoadingTask);
            if (error != null)
            {
                JLog.d(JLog.JosephWang, "onReceivedSslError error " + error.getPrimaryError());
//                Toast.makeText(getActivity(), "錯誤 onReceivedSslError " + error.getPrimaryError() + " !!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            getHandler().removeCallbacks(cancelLoadingTask);
            getHandler().post(cancelLoadingTask);
            if (isClearHistory)
            {
                view.clearHistory();
                isClearHistory = false;
            }
            super.onPageFinished(view, url);
        }
    };
}
