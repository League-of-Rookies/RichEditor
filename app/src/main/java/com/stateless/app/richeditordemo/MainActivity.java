package com.stateless.app.richeditordemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stateless.lib.richedit.view.RichEditorStandard;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RichEditorStandard richEditorStandard=(RichEditorStandard)findViewById(R.id.res_editor);
    }
}
