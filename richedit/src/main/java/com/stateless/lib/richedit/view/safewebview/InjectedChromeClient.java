package com.stateless.lib.richedit.view.safewebview;

import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.Iterator;

/**
 * Created by Administrator on 2016/12/20.
 */
public class InjectedChromeClient extends WebChromeClient {

    public static final int INJECT_LEVEL_HEIGHT = 99;
    public static final int INJECT_LEVEL_NORMAL = 25;
    private static final String TAG=InjectedChromeClient.class.getSimpleName();
    private int injectLevel = 25;
    private boolean mIsInjectedJS = false;
    private JsCallJava mJsCallJava;

    public InjectedChromeClient(){}

    public InjectedChromeClient(WebView view,JsCallJava jsCallJava,int injectLevel){
        init(view,jsCallJava,injectLevel);
    }

    public InjectedChromeClient(JsCallJava jsCallJava){
        this.mJsCallJava=jsCallJava;
    }


    private void init(WebView view, JsCallJava jsCallJava, int injectLevel) {
        this.mJsCallJava = jsCallJava;
        this.injectLevel = injectLevel;
    }

    private void injectJS(WebView paramWebView) {
        Iterator localIterator = this.mJsCallJava.a().iterator();
        while (localIterator.hasNext()) {
            String str = (String)localIterator.next();
            Log.d(TAG, " inject js interface : " + str);
            paramWebView.loadUrl(str);
        }
        this.mIsInjectedJS = true;
    }

    public boolean onJsAlert(WebView paramWebView, String paramString1, String paramString2, JsResult paramJsResult) {
        paramJsResult.confirm();
        return true;
    }


    public boolean onJsPrompt(WebView paramWebView, String paramString1, String paramString2, String paramString3, JsPromptResult paramJsPromptResult) {
        Log.d(TAG, "onJsPrompt message " + paramString2);
//        paramJsPromptResult.confirm(this.mJsCallJava.a(paramWebView, paramString2));
        return true;
    }


    public void setJsCallJava(WebView view,JsCallJava jsCallJava,int injectLevel) {
        init(view,jsCallJava,injectLevel);
    }
}
