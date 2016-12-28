package com.stateless.app.richeditordemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class PreViewActivity extends AppCompatActivity {

    public static void start(Context context,String data) {
        Intent starter = new Intent(context, PreViewActivity.class);
        starter.putExtra("data",data);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pre_view);

        String data = getIntent().getStringExtra("data");
        TextView preView=(TextView)findViewById(R.id.tv_preview);
        preView.setText(Html.fromHtml(data));

    }
}
