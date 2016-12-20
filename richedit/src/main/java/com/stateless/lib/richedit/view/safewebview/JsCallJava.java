package com.stateless.lib.richedit.view.safewebview;

import android.text.TextUtils;
import android.webkit.WebView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/20.
 */
public class JsCallJava {
    private Gson a;
    private Map<String,Object> b;
    private Map<String, Map<String, Method>> c;
    private List<String> d;


    public JsCallJava(String injectedName, Object injectedObj) {
        d = new ArrayList();
        b = new HashMap();
        c = new HashMap();

    }


    
    public List<String> a() {
        return this.d;
    }
}
