package com.stateless.lib.richedit.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stateless on 2016/12/16.
 * 基础富文本编辑器，定义事件，供外部及网页调用
 */
public class RichEditor extends WebView {

    private static final String SETUP_HTML = "file:///android_asset/editor_v19/editorv19.html";
    ;

    private static final boolean DEBUG = true;
    public static final long SHOW_EDITOR_DELAY = 300L;
    private final String TAG = getClass().getSimpleName();
    private String articleContent = "";
    private String articleTitle = "";
    private int articleWordage = 0;
    private Context context;
    private String currentElementID = "";
    private String[] failedImages;
    private EditorDelegate delegate;
    private Runnable fireOnEditorReady = new Runnable() {
        public void run() {
            onEditorReady();
        }
    };
    private Runnable fireOnGetContent = new Runnable() {
        public void run() {
            if (delegate != null) {
                delegate.onGetContent(articleContent);
            }
        }
    };
    private Runnable fireOnGetImageStatus = new Runnable()
    {
        public void run()
        {
            if (delegate != null) {
                delegate.onGetImageStatus(loadedImages, loadingImages, failedImages);
            }
        }
    };

    private Runnable fireOnGetSelectedText = new Runnable()
    {
        public void run()
        {
            if (delegate != null) {
                delegate.onGetSelectionText(selectedText);
            }
        }
    };

    private Runnable fireOnGetSelectionInfo = new Runnable()
    {
        public void run()
        {
            onEditorGetSelectionInfo();
        }
    };
    private Runnable fireOnGetSelectionStyles = new Runnable()
    {
        public void run()
        {
            onEditorGetSelectionStyles();
        }
    };
    private Runnable fireOnGetTitle = new Runnable()
    {
        public void run()
        {
            if (delegate != null) {
                delegate.onGetTitle(articleTitle);
            }
        }
    };
    private Runnable fireOnGetWordage = new Runnable()
    {
        public void run()
        {
            if (delegate != null) {
                delegate.onGetWordage(articleWordage);
            }
        }
    };
    private Runnable fireOnInput = new Runnable()
    {
        public void run()
        {
            onInput();
        }
    };
    private Runnable fireOnPaste = new Runnable()
    {
        public void run()
        {
            onPaste();
        }
    };
    private Runnable fireOnTap = new Runnable()
    {
        public void run()
        {
            onTap();
        }
    };
    private Runnable fireOnTapImage = new Runnable()
    {
        public void run()
        {
            if (delegate != null) {
                delegate.onTapImage(mLastTapImageId, mLastTapImageUrl);
            }
        }
    };
    private Runnable fireOnTapLink = new Runnable()
    {
        public void run()
        {
            if (delegate != null) {
                delegate.onTapLink(mLastTapLinkUrl, mLastTapLinkName);
            }
        }
    };

    private boolean hasSelection = false;
    private boolean isEditorReady = false;
    private boolean isMarkdown = false;
    private boolean isNightMode = false;
    private boolean isPreview = false;
    private boolean isUsingActiveMonitor = false;
    private int lineHeight = 0;
    private String[] loadedImages;
    private String[] loadingImages;
    private String mLastTapImageId;
    private String mLastTapImageUrl;
    private String mLastTapLinkName;
    private String mLastTapLinkUrl;
    private String selectedText;
    private int yOffset = 0;


    public RichEditor(Context context) {
        this(context, null);
    }

    public RichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public RichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

//        setVerticalScrollBarEnabled(false);
//        setHorizontalScrollBarEnabled(false);
//        getSettings().setJavaScriptEnabled(true);
//
//        setWebChromeClient(new WebChromeClient());
//        setWebViewClient(createWebViewClient());
//        addJavascriptInterface(new AndroidDelegate(), "AndroidDelegate");
//        loadUrl(SETUP_HTML);


    }


    protected EditorWebViewClient createWebViewClient() {
        return new EditorWebViewClient();
    }

    protected class EditorWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
//            isReady = url.equalsIgnoreCase(SETUP_HTML);
        }
    }


    /*
    * 触发 指定的事件
    * */
    protected void exec(String trigger) {
        load(trigger);
    }

    protected void exec(String trigger, String paras) {
        load(trigger, paras);
    }

    protected void execJS(String js) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(js, null);
        } else {
            loadUrl(js);
        }
    }

    private void load(String trigger) {
        callScript(trigger);
    }

    private void load(String trigger, String paras) {
        callScript(trigger, paras);
    }

    private void callScript(String command, String paras) {
        String script = "ZSSEditor." + command + "(" + paras + ");";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(script, null);
        } else {
            loadUrl(script);
        }
    }

    private void callScript(String command) {
        String script = "ZSSEditor." + command + "();";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(script, null);
        } else {
            loadUrl(script);
        }
    }


    private void onEditorGetSelectionInfo() {
        if (this.delegate != null) {
            this.delegate.onGetSelectionInfo(this.currentElementID, this.yOffset, this.lineHeight, this.hasSelection);
        }
    }

    private void onEditorGetSelectionStyles() {
        if (this.delegate != null) {
            this.delegate.onGetSelectionStyles(CurrentStyles.toStyle());
        }
    }

    private void onEditorReady() {
        this.isEditorReady = true;
        if (this.delegate != null) {
            this.delegate.onInit();
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(VISIBLE);
            }
        }, 300L);
    }

    private void onInput() {
        if (this.delegate != null) {
            this.delegate.onInput();
        }
    }

    private void onPaste() {
        String str = ((ClipboardManager) this.context.getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip().getItemAt(0).getText().toString();
        execJS("Maleskine.getPaste(\"" + regularizeString(str) + "\");");
    }

    private void onTap() {
        if (this.delegate != null) {
            this.delegate.onTap();
        }
    }

    private static String regularizeString(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        content = content.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\n", "\\\\n");
        Pattern pEmptys = Pattern.compile("\\s");
        Pattern pAlowEmptys = Pattern.compile("[ \u3000\\n\\t\\r]");
        Matcher mEmptys = pEmptys.matcher(content);
        while (mEmptys.find()) {
            String empty_char = mEmptys.group();
            if (!pAlowEmptys.matcher(empty_char).find()) {
                content = content.replaceAll(empty_char, "");
            }
        }

        return content;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void callCancelRemoveCurrentImage() {
        exec("cancelRemoveCurrentImage");
    }

    public void callGetArticleContent() {
        execJS("Maleskine.getContent(true);");
    }

    public void callGetArticleTitle() {
        execJS("Maleskine.getTitle();");
    }

    public void callGetCurrentImageStatus() {
        execJS("Maleskine.getImageStates();");
    }

    public void callGetSelectText() {
        exec("getSelectedText");
    }


    public void callGetWordage() {
        execJS("Maleskine.getWordage();");
    }

    public void callInsertHtml(String paramString) {
        exec("insertHTML", paramString);
    }

    public void callInsertImage(String url, String caption, String display_url, String slug, int width, int height) {
        String param = "\"" + regularizeString(url) + "\",\"" + regularizeString(caption) + "\"";
        if ((display_url != null) && (display_url.length() > 0)) {
            param = param + ",\"" + regularizeString(display_url) + "\"";
            if ((slug != null) && (slug.length() > 0)) {
                param = param + ",\"" + regularizeString(slug) + "\"";
                if ((width > 0) && (height > 0)) {
                    param = param + "," + width + "," + height;
                }
            }
        }

        exec("insertImage", param);
    }

    public void callInsertLink(String url, String title) {
        String str = title;
        if (title.length() == 0) {
            str = url;
        }
        if (url.length() == 0) {
            exec("unlink");
            return;
        }
        if (isLink()) {
            exec("updateLink", "\"" + regularizeString(url) + "\",\"" + regularizeString(str) + "\"");
            return;
        }
        exec("insertLink", "\"" + regularizeString(url) + "\",\"" + regularizeString(str) + "\"");
    }


    public void callInsertQuickLink() {
        exec("quickLink");
    }


    public void callInsertRuleLine() {
        exec("setHorizontalRule");
    }

    public void callInsertUploadingImagePlaceholder(String imageID, String localPath) {
        exec("insertLocalImage", "\"" + regularizeString(imageID) + "\",\"" + regularizeString(localPath) + "\"");
    }

    public void callMarkImageUploadFailed(String imageID, String message) {
        exec("markImageUploadFailed", "\"" + regularizeString(imageID) + "\",\"" + regularizeString(message) + "\"");
    }

    public void callRedo() {
        exec("redo");
    }

    public void callRemoveCurrentImage() {
        exec("removeCurrentImage");
    }

    public void callRemoveImageWithID(String imageID) {
        exec("removeImage", "\"" + regularizeString(imageID) + "\"");
    }

    public void callRemoveParagraphFormat() {
        exec("removeFormating");
    }

    public void callSetBackgroundColor(String color) {
        exec("setBackgroundColor", "\"" + regularizeString(color) + "\"");
    }

    public void callSetBlockquote() {
        exec("setBlockquote");
    }

    public void callSetFontBold() {
        exec("setBold");
    }

    public void callSetFontItalic() {
        exec("setItalic");
    }

    public void callSetFontStrikethrough() {
        exec("setStrikeThrough");
    }

    public void callSetFontSubscript() {
        exec("setSubscript");
    }

    public void callSetFontSuperscript() {
        exec("setSuperscript");
    }

    public void callSetFontUnderline() {
        exec("setUnderline");
    }

    public void callSetHeader(int level) {
        if (level < 1) {
            level = 1;
        } else if (level > 6) {
            level = 6;
        }
        exec("setHeading", "\"h" + level + "\"");
    }

    public void callSetIndent() {
        exec("setIndent");
    }


    public void callSetJustifyCenter() {
        exec("setJustifyCenter");
    }

    public void callSetJustifyFull() {
        exec("setJustifyFull");
    }

    public void callSetJustifyLeft() {
        exec("setJustifyLeft");
    }

    public void callSetJustifyRight() {
        exec("setJustifyRight");
    }

    public void callSetNormalParagraph() {
        exec("setParagraph");
    }

    public void callSetOrderedList() {
        exec("setOrderedList");
    }

    public void callSetOutdent() {
        exec("setOutdent");
    }

    public void callSetTextColor(String paramString) {
        exec("setTextColor", "\"" + regularizeString(paramString) + "\"");
    }

    public void callSetUnorderedList() {
        exec("setUnorderedList");
    }

    public void callSetUploadingProgressOfImage(String paramString, int paramInt) {
        exec("setProgressOnImage", "\"" + regularizeString(paramString) + "\",\"" + paramInt + "\"");
    }

    public void callUndo() {
        exec("undo");
    }

    public void callUnmarkImageUploadFailed(String paramString) {
        exec("unmarkImageUploadFailed", "\"" + regularizeString(paramString) + "\"");
    }

    public void callUpdateUploadingImagePlaceholder(String imageID, String url, String display_url, String slug) {
        String param = "\"" + regularizeString(imageID) + "\",\"" + regularizeString(url) + "\"";
        if ((display_url != null) && (display_url.length() > 0)) {
            param = param + ",\"" + regularizeString(display_url) + "\"";
            if ((slug != null) && (slug.length() > 0)) {
                param = param + ",\"" + regularizeString(slug) + "\"";
            }
        }
        Log.d(TAG, param);
        exec("replaceLocalImageWithRemoteImage", param);
    }


    public boolean didHasSelection() {
        return this.hasSelection;
    }

    public void doListenBlur(boolean isOn) {
        EditorSettings.listenBlur = isOn;
    }

    public void doListenFocus(boolean isOn) {
        EditorSettings.listenFocus = isOn;
    }

    public void doListenInput(boolean isOn) {
        EditorSettings.listenInput = isOn;
    }

    public void doListenKeyDown(boolean isOn) {
        EditorSettings.listenKeyDown = isOn;
    }

    public void doListenKeyUp(boolean isOn) {
        EditorSettings.listenKeyUp = isOn;
    }


    public void doListenLog(boolean isOn) {
        EditorSettings.listenLog = isOn;
    }


    public void doListenSelectionChanged(boolean isOn) {
        EditorSettings.listenSelectionChanged = isOn;
    }

    public void doListenTap(boolean isOn) {
        EditorSettings.listenTap = isOn;
    }

    public void focusOnContent() {
        requestFocus();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                execJS("showKeyboard();");
                showKeyboard();
            }
        }, 100L);
    }

    public void focusOnTitle() {
        requestFocus();
        execJS("showKeyboardInTitle();");
        showKeyboard();
    }

    public String getCurrentElementID() {
        return this.currentElementID;
    }

    public int getLineHeight() {
        return this.lineHeight;
    }

    public String getSelectedText() {
        return this.selectedText;
    }

    public int getYOffset() {
        return this.yOffset;
    }


    public void hideKeyboard() {
        Log.d(this.TAG, "hideKeyboard original");
        try {
            ((InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getWindowToken(), 0);
            return;
        } catch (Exception localException) {
            Log.d(this.TAG, "hideKeyboard " + localException.toString());
        }
    }

    public String imageCaption() {
        return CurrentStyles.imageCaption;
    }

    public String imageID() {
        return CurrentStyles.imageID;
    }

    public int imageStatus() {
        return CurrentStyles.imageStatus;
    }

    public String imageURL() {
        return CurrentStyles.imageURL;
    }


    public void init(Context context) {
        init(context, null);
    }

    public void init(Context context, EditorDelegate delegate) {
        init(context, delegate, Boolean.valueOf(false), Boolean.valueOf(false), Boolean.valueOf(false));
    }

    @TargetApi(17)
    public void init(Context context, final EditorDelegate delegate, final Boolean isNightMode, final Boolean isMarkdown, Boolean isPreview){
        this.context=context;
        this.delegate=delegate;
        this.isNightMode=isNightMode;
        this.isMarkdown=isMarkdown;
        this.isPreview=isPreview;

        setHorizontalScrollBarEnabled(false);
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBlockNetworkImage(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        String ua = settings.getUserAgentString();

        ua = ua + " HighLevel";
        if(isNightMode.booleanValue()) {
            ua = ua + " NightMode";
        }
        settings.setUserAgentString(ua);
        addJavascriptInterface(new JSBridge(), "Android");
        setWebChromeClient(new WebChromeClient());

        setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isMarkdown.booleanValue()) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page Load Finished: " + url);
                StringBuilder jsSetting = new StringBuilder();
                if (isEditorReady) {
                    return;
                }
                if (EditorSettings.listenLog) {
                    jsSetting.append("ZSSEditor.eventListeners.log=true;");
                } else {
                    jsSetting.append("ZSSEditor.eventListeners.log=false;");
                }
                if (EditorSettings.listenSelectionChanged) {
                    jsSetting.append("ZSSEditor.eventListeners.selectionChanged=true;");
                } else {
                    jsSetting.append("ZSSEditor.eventListeners.selectionChanged=false;");
                }
                if (EditorSettings.listenInput) {
                    jsSetting.append("ZSSEditor.eventListeners.input=true;");
                } else {
                    jsSetting.append("ZSSEditor.eventListeners.input=false;");
                }
                if (EditorSettings.listenKeyDown) {
                    jsSetting.append("ZSSEditor.eventListeners.keydown=true;");
                } else {
                    jsSetting.append("ZSSEditor.eventListeners.keydown=false;");
                }
                if (EditorSettings.listenKeyUp) {
                    jsSetting.append("ZSSEditor.eventListeners.keyup=true;");
                } else {
                    jsSetting.append("ZSSEditor.eventListeners.keyup=false;");
                }
                if (EditorSettings.listenTap) {
                    jsSetting.append("ZSSEditor.eventListeners.tap=true;");
                } else {
                    jsSetting.append("ZSSEditor.eventListeners.tap=false;");
                }
                if (EditorSettings.listenFocus) {
                    jsSetting.append("ZSSEditor.eventListeners.focus=true;");
                } else {
                    jsSetting.append("ZSSEditor.eventListeners.focus=false;");
                }
                if (EditorSettings.listenBlur) {
                    jsSetting.append("ZSSEditor.eventListeners.blur=true;");
                } else {
                    jsSetting.append("ZSSEditor.eventListeners.blur=false;");
                }
                if (isNightMode) {
                    jsSetting.append("Maleskine.setEditorAsNightMode();");
                } else {
                    jsSetting.append("Maleskine.setEditorAsDayMode();");
                }
                if (EditorSettings.listenLog) {
                    jsSetting.append("AndroidDelegate.log=function(msg){Android.log(msg);};");
                }
                jsSetting.append("AndroidDelegate.init=function(){Android.init();};");
                jsSetting.append("AndroidDelegate.getTitle=function(title){Android.getTitle(title);};");
                jsSetting.append("AndroidDelegate.getContent=function(content){Android.getContent(content);};");
                jsSetting.append("AndroidDelegate.onGetWordage=function(wordage){Android.getWordage(wordage);};");
                if (EditorSettings.listenSelectionChanged) {
                    jsSetting.append("AndroidDelegate.onSelectionChanged=function(params){Android.onSelectionChanged(params);};");
                }
                jsSetting.append("AndroidDelegate.onSelectionStyles=function(params){Android.onSelectionStyles(params);};");
                if (EditorSettings.listenInput) {
                    jsSetting.append("AndroidDelegate.onInput=function(){Android.onInput();};");
                }
                if (EditorSettings.listenTap) {
                    jsSetting.append("AndroidDelegate.onTap=function(){Android.onTap();};");
                }
                if (EditorSettings.listenSelectionChanged) {
                    jsSetting.append("AndroidDelegate.onTapImage=function(param){Android.onTapImage(param);};");
                }
                jsSetting.append("AndroidDelegate.onTapLink=function(param){Android.onTapLink(param);};");
                jsSetting.append("AndroidDelegate.onPaste=function(){Android.onPaste();};");
                jsSetting.append("AndroidDelegate.onGetImageStatus=function(loaded,loading,failed){Android.onGetImageStatus(loaded,loading,failed);};");
                jsSetting.append("AndroidDelegate.showKeyboard=function(){Android.showKeyboard();};");
                jsSetting.append("AndroidDelegate.hideKeyboard=function(){Android.hideKeyboard();};");
                jsSetting.append("Maleskine.loadStatus.interface=true;Maleskine.loadStatus.check();");
                jsSetting.append("AndroidDelegate.onGetSelectedText=function(param){Android.getSelectedText(param);};");
                if (delegate != null) {
                    delegate.onPageLoaded(jsSetting.toString());
                }
                loadUrl("javascript:"+jsSetting.toString());
            }
        });

        loadUrl(SETUP_HTML);
    }


    public boolean isBlockQuote()
    {
        return CurrentStyles.isBlockquote;
    }

    public boolean isBold()
    {
        return CurrentStyles.isBold;
    }

    public boolean isCodeQuote()
    {
        return (CurrentStyles.isPre) || (CurrentStyles.isCode);
    }

    public boolean isCurrentElementIdEqContent()
    {
        return (!TextUtils.isEmpty(this.currentElementID)) && ((this.currentElementID.equals("zss_field_content")) || (this.currentElementID.equals("zss_field_markdown")));
    }

    public boolean isCurrentElementIdEqTitle()
    {
        return (!TextUtils.isEmpty(this.currentElementID)) && ("zss_field_title".equals(this.currentElementID));
    }

    public boolean isHead1()
    {
        return CurrentStyles.isHead1;
    }

    public boolean isHead2()
    {
        return CurrentStyles.isHead2;
    }

    public boolean isHead3()
    {
        return CurrentStyles.isHead3;
    }

    public boolean isHead4()
    {
        return CurrentStyles.isHead4;
    }

    public boolean isHead5()
    {
        return CurrentStyles.isHead5;
    }

    public boolean isHead6()
    {
        return CurrentStyles.isHead6;
    }

    public boolean isImage()
    {
        return CurrentStyles.isImage;
    }

    public boolean isItalic()
    {
        return CurrentStyles.isItalic;
    }

    public boolean isLine()
    {
        return CurrentStyles.isLine;
    }

    public boolean isLink()
    {
        return CurrentStyles.isLink;
    }

    public boolean isMarkdownMode()
    {
        return this.isMarkdown;
    }

    public boolean isNightMode()
    {
        return this.isNightMode;
    }

    public boolean isNormal()
    {
        return ((CurrentStyles.isP) || (CurrentStyles.isDiv)) && (!CurrentStyles.isBlockquote) && (!CurrentStyles.isPre) && (!CurrentStyles.isCode) && (!CurrentStyles.isOrderedList) && (!CurrentStyles.isUnorderedList);
    }

    public boolean isOrderedList()
    {
        return CurrentStyles.isOrderedList;
    }

    public boolean isPreviewMode()
    {
        return this.isPreview;
    }

    public boolean isStrikethrough()
    {
        return CurrentStyles.isStrikethrough;
    }

    public boolean isSubscript()
    {
        return CurrentStyles.isSubscript;
    }

    public boolean isSupscript()
    {
        return CurrentStyles.isSupscript;
    }

    public boolean isUnderline()
    {
        return CurrentStyles.isUnderline;
    }

    public boolean isUnorderedList()
    {
        return CurrentStyles.isUnorderedList;
    }

    public boolean isUsingActiveSelectionMonitor()
    {
        return this.isUsingActiveMonitor;
    }

    public String linkTitle()
    {
        return CurrentStyles.linkTitle;
    }

    public String linkURL()
    {
        return CurrentStyles.linkURL;
    }


    public void pause()
    {
        if (this.isEditorReady) {
            setVisibility(INVISIBLE);
        }
        pauseTimers();
    }

    public void resume()
    {
        if (this.isEditorReady) {
            setVisibility(VISIBLE);
        }
        resumeTimers();
    }



    public void setArticleContent(String paramString)
    {
        execJS("Maleskine.setContent(\"" + regularizeString(paramString) + "\");");
    }

    public void setArticleTitle(String paramString)
    {
        execJS("Maleskine.setTitle(\"" + regularizeString(paramString) + "\");");
    }

    public void setMarkdownMode(boolean paramBoolean)
    {
        this.isMarkdown = paramBoolean;
        this.isPreview = false;
        if (this.isMarkdown)
        {
            execJS("Maleskine.setMarkdownMode();");
            return;
        }
        execJS("Maleskine.setRichTextMode();");
    }


    public void setNightMode(boolean paramBoolean)
    {
        this.isNightMode = paramBoolean;
        if (this.isNightMode)
        {
            execJS("Maleskine.setEditorAsNightMode();");
            return;
        }
        execJS("Maleskine.setEditorAsDayMode();");
    }

    public void setPreviewState(boolean paramBoolean)
    {
        this.isPreview = paramBoolean;
        if (this.isPreview)
        {
            execJS("Maleskine.enterPreviewMode();");
            return;
        }
        execJS("Maleskine.exitPreviewMode();");
    }

    public void showKeyboard()
    {
        Log.d(this.TAG, "showKeyboard original");
        try
        {
            requestFocus();
            ((InputMethodManager)this.context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(this, 0);
            return;
        }
        catch (Exception localException)
        {
            Log.d(this.TAG, "showKeyboard" + localException.toString());
        }
    }

    public void turnActiveSelectionMonitorOff()
    {
        this.isUsingActiveMonitor = false;
        execJS("ZSSEditor.usingActiveMonitor=false;");
    }

    public void turnActiveSelectionMonitorOn()
    {
        this.isUsingActiveMonitor = true;
        execJS("ZSSEditor.usingActiveMonitor=true;");
    }


    public static class ContentStyle{
        public String imageCaption = "";
        public String imageID = "";
        public int imageStatus = 0;
        public String imageURL = "";
        public boolean isBlockquote = false;
        public boolean isBold = false;
        public boolean isCode = false;
        public boolean isDiv = false;
        public boolean isHead1 = false;
        public boolean isHead2 = false;
        public boolean isHead3 = false;
        public boolean isHead4 = false;
        public boolean isHead5 = false;
        public boolean isHead6 = false;
        public boolean isImage = false;
        public boolean isItalic = false;
        public boolean isLine = false;
        public boolean isLink = false;
        public boolean isOrderedList = false;
        public boolean isP = false;
        public boolean isPre = false;
        public boolean isStrikethrough = false;
        public boolean isSubscript = false;
        public boolean isSupscript = false;
        public boolean isUnderline = false;
        public boolean isUnorderedList = false;
        public String linkTitle = "";
        public String linkURL = "";
    }

    public static class CurrentStyles {
        public static String imageCaption;
        public static String imageID;
        public static int imageStatus;
        public static String imageURL;
        public static boolean isBlockquote;
        public static boolean isBold = false;
        public static boolean isCode;
        public static boolean isDiv;
        public static boolean isHead1;
        public static boolean isHead2;
        public static boolean isHead3;
        public static boolean isHead4;
        public static boolean isHead5;
        public static boolean isHead6;
        public static boolean isImage;
        public static boolean isItalic = false;
        public static boolean isLine;
        public static boolean isLink;
        public static boolean isOrderedList;
        public static boolean isP;
        public static boolean isPre;
        public static boolean isStrikethrough;
        public static boolean isSubscript;
        public static boolean isSupscript;
        public static boolean isUnderline = false;
        public static boolean isUnorderedList;
        public static String linkTitle;
        public static String linkURL;
        private static final String tagBlockquote;
        private static final String tagBold;
        private static final String tagCode;
        private static final String tagDiv;
        private static final String tagHRule;
        private static final String tagHead1;
        private static final String tagHead2;
        private static final String tagHead3;
        private static final String tagHead4;
        private static final String tagHead5;
        private static final String tagHead6;
        private static final String tagImage;
        private static final String tagImageCaptionPrefix = "image-alt:".intern();
        private static final String tagImageIDPrefix = "image-id:".intern();
        private static final String tagImageStatusPrefix = "image-status:".intern();
        private static final String tagImageURLPrefix;
        private static final String tagItalic;
        private static final String tagLink;
        private static final String tagLinkTitlePrefix;
        private static final String tagLinkURLPrefix;
        private static final String tagOrderedList;
        private static final String tagOrderedListPrime;
        private static final String tagP;
        private static final String tagPre;
        private static final String tagStrikeThrough;
        private static final String tagSubscript;
        private static final String tagSupscript;
        private static final String tagUnderline;
        private static final String tagUnorderedList;
        private static final String tagUnorderedListPrime;
        static {
            isStrikethrough = false;
            isSubscript = false;
            isSupscript = false;
            isHead1 = false;
            isHead2 = false;
            isHead3 = false;
            isHead4 = false;
            isHead5 = false;
            isHead6 = false;
            isDiv = false;
            isP = false;
            isPre = false;
            isCode = false;
            isBlockquote = false;
            isOrderedList = false;
            isUnorderedList = false;
            isLine = false;
            isLink = false;
            isImage = false;
            linkURL = "";
            linkTitle = "";
            imageURL = "";
            imageCaption = "";
            imageID = "";
            imageStatus = 0;
            tagBold = "bold".intern();
            tagItalic = "italic".intern();
            tagUnderline = "underline".intern();
            tagStrikeThrough = "strikethrough".intern();
            tagSubscript = "subscript".intern();
            tagSupscript = "superscript".intern();
            tagHead1 = "h1".intern();
            tagHead2 = "h2".intern();
            tagHead3 = "h3".intern();
            tagHead4 = "h4".intern();
            tagHead5 = "h5".intern();
            tagHead6 = "h6".intern();
            tagP = "p".intern();
            tagDiv = "div".intern();
            tagBlockquote = "blockquote".intern();
            tagPre = "pre".intern();
            tagCode = "code".intern();
            tagOrderedList = "orderedlist".intern();
            tagOrderedListPrime = "ol".intern();
            tagUnorderedList = "unorderedlist".intern();
            tagUnorderedListPrime = "ul".intern();
            tagHRule = "hrule".intern();
            tagLink = "islink".intern();
            tagImage = "isimage".intern();
            tagLinkURLPrefix = "link:".intern();
            tagLinkTitlePrefix = "link-title:".intern();
            tagImageURLPrefix = "image:".intern();
        }

        private static void reset(){
            isBold = false;
            isItalic = false;
            isUnderline = false;
            isStrikethrough = false;
            isSubscript = false;
            isSupscript = false;
            isHead1 = false;
            isHead2 = false;
            isHead3 = false;
            isHead4 = false;
            isHead5 = false;
            isHead6 = false;
            isDiv = false;
            isP = false;
            isPre = false;
            isCode = false;
            isBlockquote = false;
            isOrderedList = false;
            isUnorderedList = false;
            isLine = false;
            isLink = false;
            isImage = false;
            linkURL = "";
            linkTitle = "";
            imageURL = "";
            imageCaption = "";
            imageID = "";
            imageStatus = 0;
        }

        private static void setStyles(String[] paramArrayOfString){
            for (int i=0;i<paramArrayOfString.length;i++)
            {
                String str = paramArrayOfString[i].intern();
                if (str.equals(tagBold)) {
                    isBold = true;
                }
                else if (str.equals(tagItalic)) {
                    isItalic = true;
                } else if (str.equals(tagUnderline)) {
                    isUnderline = true;
                } else if (str.equals(tagStrikeThrough)) {
                    isStrikethrough = true;
                } else if (str.equals(tagSubscript)) {
                    isSubscript = true;
                } else if (str.equals(tagSupscript)) {
                    isSupscript = true;
                } else if (str.equals(tagHead1)) {
                    isHead1 = true;
                } else if (str.equals(tagHead2)) {
                    isHead2 = true;
                } else if (str.equals(tagHead3)) {
                    isHead3 = true;
                } else if (str.equals(tagHead4)) {
                    isHead4 = true;
                } else if (str.equals(tagHead5)) {
                    isHead5 = true;
                } else if (str.equals(tagHead6)) {
                    isHead6 = true;
                } else if (str.equals(tagP)) {
                    isP = true;
                } else if (str.equals(tagDiv)) {
                    isDiv = true;
                } else if (str.equals(tagBlockquote)) {
                    isBlockquote = true;
                } else if (str.equals(tagPre)) {
                    isPre = true;
                } else if (str.equals(tagCode)) {
                    isCode = true;
                } else if (str.equals(tagOrderedList)) {
                    isOrderedList = true;
                } else if (str.equals(tagOrderedListPrime)) {
                    isOrderedList = true;
                } else if (str.equals(tagUnorderedList)) {
                    isUnorderedList = true;
                } else if (str.equals(tagUnorderedListPrime)) {
                    isUnorderedList = true;
                } else if (str.equals(tagHRule)) {
                    isLine = true;
                } else if (str.equals(tagLink)) {
                    isLink = true;
                } else if (str.equals(tagImage)) {
                    isImage = true;
                } else if (str.indexOf(tagLinkURLPrefix) == 0) {
                    linkURL = str.replaceFirst(tagLinkURLPrefix, "");
                } else if (str.indexOf(tagLinkTitlePrefix) == 0) {
                    linkTitle = str.replaceFirst(tagLinkTitlePrefix, "");
                } else if (str.indexOf(tagImageURLPrefix) == 0) {
                    imageURL = str.replaceFirst(tagImageURLPrefix, "");
                } else if (str.indexOf(tagImageCaptionPrefix) == 0) {
                    imageCaption = str.replaceFirst(tagImageCaptionPrefix, "");
                } else if (str.indexOf(tagImageIDPrefix) == 0) {
                    imageID = str.replaceFirst(tagImageIDPrefix, "");
                } else if (str.indexOf(tagImageStatusPrefix) == 0) {
                    imageStatus = Integer.parseInt(str.replaceFirst(tagImageStatusPrefix, ""));
                }
            }
        }

        public static ContentStyle toStyle(){
            ContentStyle localContentStyle=new ContentStyle();
            if (isBold){
                localContentStyle.isBold=true;
            }
            if (isItalic) {
                localContentStyle.isItalic = true;
            }
            if (isUnderline) {
                localContentStyle.isUnderline = true;
            }
            if (isStrikethrough) {
                localContentStyle.isStrikethrough = true;
            }
            if (isSubscript) {
                localContentStyle.isSubscript = true;
            }
            if (isSupscript) {
                localContentStyle.isSupscript = true;
            }
            if (isHead1) {
                localContentStyle.isHead1 = true;
            }
            if (isHead2) {
                localContentStyle.isHead2 = true;
            }
            if (isHead3) {
                localContentStyle.isHead3 = true;
            }
            if (isHead4) {
                localContentStyle.isHead4 = true;
            }
            if (isHead5) {
                localContentStyle.isHead5 = true;
            }
            if (isHead6) {
                localContentStyle.isHead6 = true;
            }
            if (isDiv) {
                localContentStyle.isDiv = true;
            }
            if (isP) {
                localContentStyle.isP = true;
            }
            if (isPre) {
                localContentStyle.isPre = true;
            }
            if (isCode) {
                localContentStyle.isCode = true;
            }
            if (isBlockquote) {
                localContentStyle.isBlockquote = true;
            }
            if (isOrderedList) {
                localContentStyle.isOrderedList = true;
            }
            if (isUnorderedList) {
                localContentStyle.isUnorderedList = true;
            }
            if (isLine) {
                localContentStyle.isLine = true;
            }
            if (isLink) {
                localContentStyle.isLink = true;
            }
            if (isImage) {
                localContentStyle.isImage = true;
            }
            localContentStyle.linkURL = linkURL;
            localContentStyle.linkTitle = linkTitle;
            localContentStyle.imageURL = imageURL;
            localContentStyle.imageCaption = imageCaption;
            localContentStyle.imageID = imageID;
            localContentStyle.imageStatus = imageStatus;
            return localContentStyle;
        }
    }

    public static abstract interface EditorDelegate {
        public abstract void onGetContent(String paramString);

        public abstract void onGetImageStatus(String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3);

        public abstract void onGetSelectionInfo(String paramString, int paramInt1, int paramInt2, boolean paramBoolean);

        public abstract void onGetSelectionStyles(RichEditor.ContentStyle paramContentStyle);

        public abstract void onGetSelectionText(String paramString);

        public abstract void onGetTitle(String paramString);

        public abstract void onGetWordage(int paramInt);

        public abstract void onInit();

        public abstract void onInput();

        public abstract void onPageLoaded(String paramString);

        public abstract void onTap();

        public abstract void onTapImage(String paramString1, String paramString2);

        public abstract void onTapLink(String paramString1, String paramString2);
    }

    private static class EditorSettings {
        private static boolean listenBlur = true;
        private static boolean listenFocus;
        private static boolean listenInput;
        private static boolean listenKeyDown;
        private static boolean listenKeyUp;
        private static boolean listenLog = true;
        private static boolean listenSelectionChanged = true;
        private static boolean listenTap;

        static
        {
            listenInput = true;
            listenKeyDown = false;
            listenKeyUp = false;
            listenTap = true;
            listenFocus = false;
        }
    }

    private class JSBridge{
        private final String strParamsEqual = "=";
        private final String strParamsSplit = "~";
        private final String tagH = "height".intern();
        private final String tagID = "id".intern();
        private final String tagS = "hasSelection".intern();
        private final String tagY = "yOffset".intern();

        public JSBridge(){}

        @JavascriptInterface
        public void init(){
            Log.d(TAG,"!!!!!!! On JavascriptInterface Init !!!!!!!");
            post(fireOnEditorReady);
        }

        @JavascriptInterface
        public void log(String msg){
            Log.d(TAG,"New Log From JS:\n"+msg);
        }

        @JavascriptInterface
        public void onSelectionChanged(String param){
            String id="";
            int y=0;
            int h=0;
            int s=0;
            String[]params=param.split("~",-1);

            int i2=params.length;
            int i=0;
            if (i<i2){
                String [] line=params[i].split("=");
                if (line.length<2){
                    return;
                }
                String tag=line[0];
                if (tag.equals(this.tagID)){
                    id=line[1];
                }else if (tag.equals(this.tagY)){
                    double d=Double.valueOf(line[1]).doubleValue();
                    y=(int)d;
                }else if (tag.equals(this.tagH)){
                    double d=Double.valueOf(line[1]).doubleValue();
                    h=(int)d;
                }else if (tag.equals(this.tagS)){
                    s= Integer.parseInt(line[1]);
                }
            }

            if (id.length()>0){
                currentElementID=id;
                yOffset=y;
                lineHeight=h;
                hasSelection=(s!=0?true:false);
            }
            Log.d(TAG,"onSelectionChanged y param "+param);
            post(fireOnGetSelectionInfo);

        }

        @JavascriptInterface
        public void onSelectionStyles(String param){
            Log.d(TAG,"onSelectionStyles "+param);
            String [] params=param.split("~",-1);
            CurrentStyles.reset();
            CurrentStyles.setStyles(params);
            post(fireOnGetSelectionStyles);
        }

        @JavascriptInterface
        public void onInput(){
            Log.d(TAG,"onInput ");
            post(fireOnInput);
        }

        @JavascriptInterface
        public  void onTap(){
            Log.d(TAG,"onTap ");
            post(fireOnTap);
        }

        @JavascriptInterface
        public void onTapImage(String param){
            try {
                String[] segments=param.split("~");
                if (segments.length>=3){
                    mLastTapImageId=segments[1].split("=")[1];
                    mLastTapImageUrl=segments[2].split("=")[1];
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.d(TAG,"Tap Image: "+param+" imageID "+mLastTapImageId+" url "+mLastTapImageUrl);
            post(fireOnTapImage);
        }

        @JavascriptInterface
        public void onTapLink(String param){
            try {
                String[] segments=param.split("~");
                if (segments.length>=3){
                    mLastTapLinkUrl=segments[1].split("=")[1];
                    mLastTapLinkName=segments[2].split("=")[1];
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.d(TAG,"onTapLink "+param+" url "+mLastTapLinkUrl+" name "+mLastTapLinkName);
            post(fireOnTapLink);
        }

        @JavascriptInterface
        public void onPaste() {
            post(fireOnPaste);
        }

        @JavascriptInterface
        public  void onGetImageStatus(String loadedImages,String loadingImages,String failedImages){
            if (loadedImages.length()>4){
                loadedImages=loadedImages.substring(2,loadedImages.length()-2);
                RichEditor.this.loadedImages=loadedImages.split("\",\"");
            }else {
                RichEditor.this.loadedImages=new String[]{""};
            }

            if (loadingImages.length()>4){
                loadingImages=loadingImages.substring(2,loadingImages.length()-2);
                RichEditor.this.loadingImages=loadingImages.split("\",\"");
            }else {
                RichEditor.this.loadingImages=new String[]{""};
            }


            if (failedImages.length()>4){
                failedImages=failedImages.substring(2,failedImages.length()-2);
                RichEditor.this.failedImages=failedImages.split("\",\"");
            }else {
                RichEditor.this.failedImages=new String[]{""};
            }

            post(fireOnGetImageStatus);
        }

        @JavascriptInterface
        public void getTitle(String title){
            RichEditor.this.articleTitle=title;
            post(fireOnGetTitle);
        }

        @JavascriptInterface
        public void getContent(String content) {
            articleContent = content;
            post(fireOnGetContent);
        }

        @JavascriptInterface
        public void getWordage( int wordage) {
            //            if(v.a()) {
//                v.b(TAG, "onGetSelectTExt " + selectText);
//            }

            try {
                articleWordage=Integer.valueOf(wordage).intValue();
            }catch (Exception e){
                articleWordage =0;
            }

            post(fireOnGetWordage);
        }

        @JavascriptInterface
        public void getSelectedText( String selectText) {
//            if(v.a()) {
//                v.b(TAG, "onGetSelectTExt " + selectText);
//            }
            selectedText = selectText;
            post(fireOnGetSelectedText);
        }

        public void showKeyboard(){
            RichEditor.this.showKeyboard();
        }


        public void hideKeyboard(){
            RichEditor.this.hideKeyboard();
        }
    }


}
