package com.stateless.lib.richedit.view;

import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by Administrator on 2016/12/16.
 */
public class AndroidDelegate {

    public AndroidDelegate(){}


    @JavascriptInterface
    public void init(){
        Log.d("stateless","init  run");
    }

    @JavascriptInterface
    public void showKeyboard(){
        Log.d("stateless", "showKeyboard  run");
    }

}
