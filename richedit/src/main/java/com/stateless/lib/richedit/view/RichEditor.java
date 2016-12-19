package com.stateless.lib.richedit.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by stateless on 2016/12/16.
 * 基础富文本编辑器，定义事件，供外部及网页调用
 */
public class RichEditor extends WebView {



    public interface AfterInitialLoadListener {
        void onAfterInitialLoad(boolean isReady);
    }

    private AfterInitialLoadListener mLoadListener;

    public void setOnInitialLoadListener(AfterInitialLoadListener mLoadListener) {
        this.mLoadListener = mLoadListener;
    }

    private static final String SETUP_HTML= "file:///android_asset/editor_v19/editorv19.html";;
    private boolean isReady =false;


    public RichEditor(Context context) {
        this(context, null);
    }

    public RichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public RichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);

        setWebChromeClient(new WebChromeClient());
        setWebViewClient(createWebViewClient());
        addJavascriptInterface(new AndroidDelegate(),"AndroidDelegate");
        loadUrl(SETUP_HTML);


    }

    protected EditorWebViewClient createWebViewClient() {
        return new EditorWebViewClient();
    }

    protected class EditorWebViewClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            isReady = url.equalsIgnoreCase(SETUP_HTML);
            if (mLoadListener != null) {
                mLoadListener.onAfterInitialLoad(isReady);
            }
        }
    }


    /*
    * 触发 指定的事件
    * */
    protected void exec(String trigger){
        if (isReady){
            load(trigger);
        }
    }

    private void load(String trigger) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, null);
        } else {
            loadUrl(trigger);
        }
    }


    /*
    * 常用放出的方法
    * */

    public void redo() {
        exec("javascript:ZSSEditor.redo();");
    }

    public void undo() {
        exec("javascript:ZSSEditor.undo();");
    }
}
