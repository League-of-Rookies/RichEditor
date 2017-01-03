package com.stateless.lib.richedit.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class RichEditorStandard extends RelativeLayout implements View.OnClickListener {
    private Activity mContext;
    private RichEditor richEditor;
    private RichEditor.EditorDelegate editorDelegate;
    private RecyclerView rvWordStyle;
    String content;

    public RichEditor getRichEditor() {
        return richEditor;
    }

    public RichEditorStandard(Context context) {
        this(context, null);
    }

    public RichEditorStandard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichEditorStandard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) return;
        mContext = (Activity) context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.rich_editor_standard, this);

        richEditor = (RichEditor) findViewById(R.id.editor);
        editorDelegate = new RichEditor.EditorDelegate() {
            @Override
            public void onGetContent(String paramString) {
                Log.d("stateless", "onGetContent: " + paramString);
                if (richEditorListener != null) {
                    richEditorListener.callbackGetContent(paramString);
                }

            }

            @Override
            public void onGetImageStatus(String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3) {

                Log.d("stateless", "onGetImageStatus: " + paramArrayOfString1 + "---" + paramArrayOfString2 + "---" + paramArrayOfString3);
            }

            @Override
            public void onGetSelectionInfo(String paramString, int paramInt1, int paramInt2, boolean paramBoolean) {

                Log.d("stateless", "onGetSelectionInfo: " + paramString + "---" + paramInt1 + "---" + paramInt2 + "---" + paramBoolean);
            }

            @Override
            public void onGetSelectionStyles(RichEditor.ContentStyle paramContentStyle) {

                Log.d("stateless", "onGetSelectionStyles: " + paramContentStyle);
            }

            @Override
            public void onGetSelectionText(String paramString) {

                Log.d("stateless", "onGetSelectionText: " + paramString);
            }

            @Override
            public void onGetTitle(String paramString) {

                Log.d("stateless", "onGetTitle: " + paramString);
            }

            @Override
            public void onGetWordage(int paramInt) {

                Log.d("stateless", "onGetWordage: " + paramInt);
            }

            @Override
            public void onInit() {
                Log.d("stateless", "onInit");
            }

            @Override
            public void onInput() {

                Log.d("stateless", "onInput: ");
            }

            @Override
            public void onPageLoaded(String paramString) {
                Log.d("stateless", "onpageloaded");
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        richEditor.focusOnContent();
                    }
                }, 300L);
            }

            @Override
            public void onTap() {

                Log.d("stateless", "onTap: ");
            }

            @Override
            public void onTapImage(String paramString1, String paramString2) {

                Log.d("stateless", "onTapImage: " + paramString1 + "----" + paramString2);
            }

            @Override
            public void onTapLink(String paramString1, String paramString2) {

                Log.d("stateless", "onTapLink: " + paramString1 + "----" + paramString2);
            }
        };
        richEditor.init(mContext, editorDelegate, false, true, true);
        rvWordStyle = (RecyclerView) findViewById(R.id.rv_word_style);
        initWordStyleList();

        TextView media = (TextView) findViewById(R.id.tv_media);

        TextView redo = (TextView) findViewById(R.id.tv_redo);
        TextView undo = (TextView) findViewById(R.id.tv_undo);
        TextView wordStyle = (TextView) findViewById(R.id.tv_word_style);
        TextView more = (TextView) findViewById(R.id.tv_more);

        redo.setOnClickListener(this);
        undo.setOnClickListener(this);
        more.setOnClickListener(this);
        wordStyle.setOnClickListener(this);
        media.setOnClickListener(this);
    }

    private void initWordStyleList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvWordStyle.setLayoutManager(layoutManager);

        final WordStyleAdapter adapter = new WordStyleAdapter(getContext());
        rvWordStyle.setAdapter(adapter);


        rvWordStyle.addOnItemTouchListener(new RecyclerViewClickListener(getContext(), rvWordStyle, new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                WordStyleModel model = adapter.getList().get(position);
                if (position > 3) {

                    for (int i = 0; i < adapter.getList().size(); i++) {
                        if (i > 3) {
                            if (i == position) {
                                continue;
                            }
                            adapter.getList().get(i).setSelect(false);

                        }
                    }
                    model.setSelect(!model.isSelect());
                } else {
                    model.setSelect(!model.isSelect());
                }
                adapter.notifyDataSetChanged();
                clickWordStyle(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    private void clickWordStyle(int position) {
        switch (position) {
            case 0:
                richEditor.callSetFontBold();
                break;
            case 1:
                richEditor.callSetFontItalic();
                break;
            case 2:
                richEditor.callSetFontStrikethrough();
                break;
            case 3:
                richEditor.callSetFontUnderline();
                break;
            case 4:
                richEditor.callSetBlockquote();
                break;
            case 5:
                richEditor.callSetHeader(1);
                break;
            case 6:

                richEditor.callSetHeader(2);
                break;
            case 7:

                richEditor.callSetHeader(3);
                break;
            case 8:

                richEditor.callSetHeader(4);
                break;


        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_redo) {
            richEditor.callRedo();
        } else if (i == R.id.tv_undo) {
            richEditor.callUndo();
        } else if (i == R.id.tv_more) {
            final MoreDialogFragment moreDialogFragment = new MoreDialogFragment();
            moreDialogFragment.setDialogListener(new MoreDialogFragment.MoreDialogFragmentListener() {
                @Override
                public void clickLine() {
                    richEditor.callInsertRuleLine();
                    moreDialogFragment.dismiss();
                }

                @Override
                public void clickLink() {
                    final InputLinkDialogFragment inputLinkDialogFragment = new InputLinkDialogFragment();
                    inputLinkDialogFragment.setDialogListener(new InputLinkDialogFragment.InputLinkDialogFragmentListener() {
                        @Override
                        public void clickCancel() {
                            inputLinkDialogFragment.dismiss();
                        }

                        @Override
                        public void clickSure(String name, String link) {
                            richEditor.callInsertLink(link, name);
                            inputLinkDialogFragment.dismiss();
                        }
                    });

                    inputLinkDialogFragment.show(mContext.getFragmentManager(), "input_link_dialog_fragment");
                    moreDialogFragment.dismiss();
                }
            });
            moreDialogFragment.show(mContext.getFragmentManager(), "more_dialog_fragment");
        } else if (i == R.id.tv_word_style) {
            if (rvWordStyle.isShown()) {
                rvWordStyle.setVisibility(GONE);
            } else {
                rvWordStyle.setVisibility(VISIBLE);
            }
        } else if (i == R.id.tv_media) {
            if (richEditorListener != null) {
                richEditorListener.clickMedia();
            }
        }

    }


    public interface RichEditorListener {
        void clickMedia();

        void callbackGetContent(String content);
    }


    RichEditorListener richEditorListener;

    public void setRichEditorListener(RichEditorListener richEditorListener) {
        this.richEditorListener = richEditorListener;
    }
}
