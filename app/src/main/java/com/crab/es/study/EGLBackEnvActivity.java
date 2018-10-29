package com.crab.es.study;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crab.es.study.egl.GLES20BackEnv;
import com.crab.es.study.filter.GrayFilter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by user on 18-10-29.
 */

public class EGLBackEnvActivity extends AppCompatActivity {
    private ImageView mImage;

    private int mBmpWidth,mBmpHeight;
    private String mImgPath;
    private GLES20BackEnv mBackEnv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl_back_env);
        mImage= (ImageView)findViewById(R.id.mImage);
        mImage= (ImageView)findViewById(R.id.mImage);
        boolean hasRead = checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED;
        boolean hasWrite = checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED;
        if(!hasRead || !hasWrite){
            if(!hasRead && !hasWrite){
                requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"},1000);
            }else if(!hasRead){
                requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"},1000);
            }else if(!hasWrite){
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},1000);
            }
        }
    }

    public void onClick(View view){
        //调用相册
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            mImgPath = c.getString(columnIndex);
            Log.e("wuwang","img->"+mImgPath);
            Bitmap bmp= BitmapFactory.decodeFile(mImgPath);
            mBmpWidth=bmp.getWidth();
            mBmpHeight=bmp.getHeight();
            mBackEnv=new GLES20BackEnv(mBmpWidth,mBmpHeight);
            mBackEnv.setThreadOwner(getMainLooper().getThread().getName());
            mBackEnv.setFilter(new GrayFilter(getResources()));
            mBackEnv.setInput(bmp);
            saveBitmap(mBackEnv.getBitmap());
            c.close();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allGranted = true;
        for(int i=0;i<grantResults.length;i++){
            if(grantResults[i]==PackageManager.PERMISSION_DENIED){
                allGranted = false;
            }
        }
        if(!allGranted){
            finish();
        }
    }

    //图片保存
    public void saveBitmap(final Bitmap b){
        String path = mImgPath.substring(0,mImgPath.lastIndexOf("/")+1);
        File folder=new File(path);
        if(!folder.exists()&&!folder.mkdirs()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EGLBackEnvActivity.this, "无法保存照片", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        long dataTake = System.currentTimeMillis();
        final String jpegName=path+ dataTake +".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EGLBackEnvActivity.this, "保存成功->"+jpegName, Toast.LENGTH_SHORT).show();
                mImage.setImageBitmap(b);
            }
        });

    }
}
