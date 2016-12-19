package com.stateless.lib.richedit.view;

import android.content.Context;
import android.util.AttributeSet;
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
        TextView redo = (TextView) findViewById(R.id.tv_redo);
        TextView undo = (TextView) findViewById(R.id.tv_undo);

        redo.setOnClickListener(this);
        undo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_redo) {
            richEditor.redo();
        } else if (i == R.id.tv_undo) {
            richEditor.undo();
        } else {

        }

    }
}
