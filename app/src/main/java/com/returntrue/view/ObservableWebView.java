package com.returntrue.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by josephwang on 15/9/13.
 */
public class ObservableWebView extends WebView
{
    private OnScrollChangedListener listener;

    public ObservableWebView(final Context context)
    {
        super(context);
    }

    public ObservableWebView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ObservableWebView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        if (getOnScrollChangedListener() != null)
        {
            getOnScrollChangedListener().onScroll(l, t);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (computeVerticalScrollRange() > getMeasuredHeight())
        {
            requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(event);
    }

    public OnScrollChangedListener getOnScrollChangedListener()
    {
        return listener;
    }

    public void setOnScrollChangedListener(final OnScrollChangedListener listener)
    {
        this.listener = listener;
    }

    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    public interface OnScrollChangedListener
    {
        void onScroll(int l, int t);
    }
}