package com.stateless.lib.richedit.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.stateless.lib.richedit.R;

/**
 * Created by wangjie on 16/12/28.
 */

public class InputLinkDialogFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_input_link, null);
        TextView cancel = (TextView) rootView.findViewById(R.id.tv_cancel);
        TextView sure = (TextView) rootView.findViewById(R.id.tv_sure);

        final EditText linkname = (EditText) rootView.findViewById(R.id.et_link_name);
        final EditText linksrc = (EditText) rootView.findViewById(R.id.et_link_src);




        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.clickCancel();
                }
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.clickSure(linkname.getText().toString(),linksrc.getText().toString());
                }
            }
        });
        return rootView;
    }


    public interface InputLinkDialogFragmentListener {
        void clickCancel();

        void clickSure(String name,String link);
    }


    InputLinkDialogFragmentListener dialogListener;

    public void setDialogListener(InputLinkDialogFragmentListener dialogListener) {
        this.dialogListener = dialogListener;
    }
}