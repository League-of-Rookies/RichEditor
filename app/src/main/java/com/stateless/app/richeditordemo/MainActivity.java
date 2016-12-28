package com.stateless.app.richeditordemo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.utils.UrlSafeBase64;
import com.stateless.lib.richedit.view.RichEditorStandard;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static javax.xml.transform.OutputKeys.ENCODING;

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

            @Override
            public void callbackGetContent(String content) {

                PreViewActivity.start(MainActivity.this,content);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_preview:
                richEditorStandard.getRichEditor().callGetArticleContent();
//                Log.d("stateless",":"+richEditorStandard.getRichEditor().getArticleContent());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

                File file=new File(imagePath);
                String key = System.currentTimeMillis() + "_" + file.getName();
                richEditorStandard.getRichEditor().callInsertUploadingImagePlaceholder(key,imagePath);


                uploadPic(key,file);
                c.close();
            }
        }

    }

    private volatile boolean isCancelled = false;
    void uploadPic(final String key,final File file){
        Configuration config = new Configuration.Builder().zone(Zone.httpAutoZone).build();
        UploadManager uploadManager = new UploadManager(config);
//        data = <File对象、或 文件路径、或 字节数组>
//        String key = <指定七牛服务上的文件名，或 null>;
//        String token = <从服务端SDK获取>;

        // 初始化、执行上传
        // 1 构造上传策略
        JSONObject _json = new JSONObject();
        long _dataline = System.currentTimeMillis() / 1000 + 3600;
        try {
            _json.put("deadline", _dataline);// 有效时间为一个小时
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            _json.put("scope", "stateless");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String _encodedPutPolicy = UrlSafeBase64.encodeToString(_json
                .toString().getBytes());
        byte[] _sign = new byte[0];
        try {
            _sign = HmacSHA1Encrypt(_encodedPutPolicy, ConfigConstants.QINIU_SECRETKEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String _encodedSign = UrlSafeBase64.encodeToString(_sign);
        String _uploadToken = ConfigConstants.QINIU_ACCESSKEY + ':' + _encodedSign + ':'
                + _encodedPutPolicy;




        uploadManager.put(file,  key, _uploadToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if(info.isOK())
                        {
                            Log.i("qiniu", "Upload Success");
                            try {
                                richEditorStandard.getRichEditor().callUpdateUploadingImagePlaceholder(key,"http://7xpk45.com1.z0.glb.clouddn.com/"+res.getString("key"),"http://7xpk45.com1.z0.glb.clouddn.com/"+res.getString("key"),"");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else{
                            Log.i("qiniu", "Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                            richEditorStandard.getRichEditor().callMarkImageUploadFailed(key,"上传失败");
                        }
                        Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, new UploadOptions(
                        null, null, false, new UpProgressHandler(){
                    public void progress(String key, double percent){
                        Log.i("qiniu", key + ": " + percent);
                        richEditorStandard.getRichEditor().callSetUploadingProgressOfImage(key,(int)(percent*100));

                    }
                },
                        new UpCancellationSignal(){
                            public boolean isCancelled(){
                                return isCancelled;
                            }
                        })
                );
    }


    /**
     *
     使用 HMAC-SHA1 签名方法对encryptText进行签名
     *
     * @param encryptText
     *            被签名的字符串
     * @param encryptKey
     *            密钥
     * @return
     * @throws Exception
     */
    public static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey)
            throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        // 完成 Mac 操作
        return mac.doFinal(text);
    }

    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";
}
