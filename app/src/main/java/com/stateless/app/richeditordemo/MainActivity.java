package com.stateless.app.richeditordemo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.stateless.lib.richedit.view.RichEditorStandard;

import org.json.JSONObject;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private RichEditorStandard richEditorStandard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        richEditorStandard = (RichEditorStandard)findViewById(R.id.res_editor);
        richEditorStandard.setRichEditorListener(new RichEditorStandard.RichEditorListener() {
            @Override
            public void clickMedia() {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            if (resultCode==RESULT_OK && data != null){
                Uri selectedImage = data.getData();
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                String imagePath = c.getString(columnIndex);
//                richEditorStandard.getRichEditor().callInsertImage(imagePath,"图片发自于 stateless",imagePath,"s",200,200);
                richEditorStandard.getRichEditor().callInsertUploadingImagePlaceholder("1",imagePath);
//                richEditorStandard.getRichEditor().callMarkImageUploadFailed("1","上传失败");
                richEditorStandard.getRichEditor().callSetUploadingProgressOfImage("1",40);
                c.close();
            }
        }

    }

    private volatile boolean isCancelled = false;
    void uploadPic(File file){
        Configuration config = new Configuration.Builder().zone(Zone.httpAutoZone).build();
        UploadManager uploadManager = new UploadManager(config);
//        data = <File对象、或 文件路径、或 字节数组>
//        String key = <指定七牛服务上的文件名，或 null>;
//        String token = <从服务端SDK获取>;

        // 初始化、执行上传


        uploadManager.put(file, file.getName(), "",
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if(info.isOK())
                        {
                            Log.i("qiniu", "Upload Success");
                        }
                        else{
                            Log.i("qiniu", "Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                        Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, new UploadOptions(
                        null, null, false, new UpProgressHandler(){
                    public void progress(String key, double percent){
                        Log.i("qiniu", key + ": " + percent);
                    }
                },
                        new UpCancellationSignal(){
                            public boolean isCancelled(){
                                return isCancelled;
                            }
                        })
                );
    }
}
