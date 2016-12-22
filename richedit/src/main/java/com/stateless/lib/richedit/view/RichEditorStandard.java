package com.stateless.lib.richedit.view;

import android.content.Context;
import android.renderscript.Script;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stateless.lib.richedit.R;

/**
 * Created by stateless on 2016/12/16.
 * 标准的富文本编辑器 封装ui和简书app的一致
 */
public class RichEditorStandard extends RelativeLayout implements View.OnClickListener{
    private Context mContext;
    private RichEditor richEditor;
    private RichEditor.EditorDelegate editorDelegate;

    public RichEditorStandard(Context context) {
        this(context, null);
    }

    public RichEditorStandard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichEditorStandard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.rich_editor_standard, this);
        richEditor = (RichEditor)findViewById(R.id.editor);
        editorDelegate = new RichEditor.EditorDelegate() {
            @Override
            public void onGetContent(String paramString) {
                Log.d("stateless","onGetContent: "+paramString);
            }

            @Override
            public void onGetImageStatus(String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3) {

                Log.d("stateless","onGetImageStatus: "+paramArrayOfString1+"---"+paramArrayOfString2+"---"+paramArrayOfString3);
            }

            @Override
            public void onGetSelectionInfo(String paramString, int paramInt1, int paramInt2, boolean paramBoolean) {

                Log.d("stateless","onGetSelectionInfo: "+paramString+"---"+paramInt1+"---"+paramInt2+"---"+paramBoolean);
            }

            @Override
            public void onGetSelectionStyles(RichEditor.ContentStyle paramContentStyle) {

                Log.d("stateless","onGetSelectionStyles: "+paramContentStyle);
            }

            @Override
            public void onGetSelectionText(String paramString) {

                Log.d("stateless","onGetSelectionText: "+paramString);
            }

            @Override
            public void onGetTitle(String paramString) {

                Log.d("stateless","onGetTitle: "+paramString);
            }

            @Override
            public void onGetWordage(int paramInt) {

                Log.d("stateless","onGetWordage: "+paramInt);
            }

            @Override
            public void onInit() {
                Log.d("stateless","onInit");
            }

            @Override
            public void onInput() {

                Log.d("stateless","onInput: ");
            }

            @Override
            public void onPageLoaded(String paramString) {
                Log.d("stateless","onpageloaded");
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        richEditor.focusOnContent();
                    }
                },300L);
            }

            @Override
            public void onTap() {

                Log.d("stateless","onTap: ");
            }

            @Override
            public void onTapImage(String paramString1, String paramString2) {

                Log.d("stateless","onTapImage: "+paramString1+"----"+paramString2);
            }

            @Override
            public void onTapLink(String paramString1, String paramString2) {

                Log.d("stateless","onTapLink: "+paramString1+"----"+paramString2);
            }
        };
        richEditor.init(mContext, editorDelegate, false, true, true);
        TextView redo = (TextView) findViewById(R.id.tv_redo);
        TextView undo = (TextView) findViewById(R.id.tv_undo);

        TextView h1 = (TextView) findViewById(R.id.tv_h1);

        redo.setOnClickListener(this);
        undo.setOnClickListener(this);
        h1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_redo) {
            richEditor.callRedo();
        } else if (i == R.id.tv_undo) {
            richEditor.callUndo();
        } else if (i==R.id.tv_h1){
            richEditor.callInsertRuleLine();

        }

    }
}
