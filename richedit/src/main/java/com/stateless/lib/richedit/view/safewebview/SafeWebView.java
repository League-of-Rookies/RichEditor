package com.stateless.lib.richedit.view.safewebview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by Administrator on 2016/12/20.
 */
public class SafeWebView extends WebView {

    private int injectLevel=25;
    private static final String TAG = SafeWebView.class.getSimpleName();

    private InjectedChromeClient chromeClient;
    private JsCallJava jsCallJava;


    public SafeWebView(Context context) {
        super(context);
        if(!isInEditMode()) {
            init();
        }
    }

    public SafeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init();
        }
    }

    public SafeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            init();
        }
    }


    /*
    * 移除webview 可能导致的一些漏洞
    * */
    private void init() {
        removeJavascriptInterface("searchBoxJavaBridge_");
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");
        getSettings().setSavePassword(false);
    }

    public void addJSInterface(Object object, String name) {
        addJSInterface(object, name, injectLevel);
    }

    public void addJSInterface(Object object, String name, int injectLevel) {
        Log.d(TAG, "addJSInterface : " + name + " " + injectLevel);
        this.injectLevel = injectLevel;
        if (this.jsCallJava==null){
            this.jsCallJava=new JsCallJava(name,object);
        }
        if (this.chromeClient==null){
            this.chromeClient=new InjectedChromeClient();
            setWebChromeClient(this.chromeClient);
        }
//        this.chromeClient.
//        if(jsCallJava == null) {
//            jsCallJava = new b(name, object);
//        } else {
//            jsCallJava.a(name, object);
//        }
//        if(chromeClient == null) {
//            chromeClient = new a();
//            setWebChromeClient(chromeClient);
//        }
//        chromeClient.setJsCallJava(this, jsCallJava, injectLevel);
    }
}
