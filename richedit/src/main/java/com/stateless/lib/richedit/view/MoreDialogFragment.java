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
import android.widget.TextView;

import com.stateless.lib.richedit.R;

/**
 * Created by wangjie on 16/12/28.
 */

public class MoreDialogFragment extends DialogFragment {

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
        View rootView = inflater.inflate(R.layout.dialog_more, null);
        TextView line=(TextView) rootView.findViewById(R.id.tv_fen_ge_line);
        TextView link=(TextView) rootView.findViewById(R.id.tv_link);


        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener!=null){
                    dialogListener.clickLine();
                }
            }
        });

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener!=null){
                    dialogListener.clickLink();
                }
            }
        });
        return rootView;
    }


    public  interface  MoreDialogFragmentListener{
        void clickLine();
        void clickLink();
    }


    MoreDialogFragmentListener dialogListener;

    public void setDialogListener(MoreDialogFragmentListener dialogListener) {
        this.dialogListener = dialogListener;
    }
}
