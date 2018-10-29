package com.crab.es.study;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.crab.es.study.camera.CameraView;


public class CameraActivity extends AppCompatActivity {
    private CameraView mCameraView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(checkSelfPermission("android.permission.CAMERA")== PackageManager.PERMISSION_GRANTED){
            initViewRunnable.run();
        }else{
            requestPermissions(new String[]{"android.permission.CAMERA"},1000);
        }
    }

    private Runnable initViewRunnable=new Runnable() {
        @Override
        public void run() {
            setContentView(R.layout.activity_camera);
            mCameraView= (CameraView)findViewById(R.id.mCameraView);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(String permission:permissions){
            if("android.permission.CAMERA".equals(permission)){
                if(checkSelfPermission("android.permission.CAMERA")== PackageManager.PERMISSION_GRANTED){
                    initViewRunnable.run();
                }else{
                    finish();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCameraView!=null){
            mCameraView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mCameraView!=null){
            mCameraView.onPause();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("切换摄像头").setTitle("切换摄像头").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mCameraView!=null){
            String name=item.getTitle().toString();
            if(name.equals("切换摄像头")){
                mCameraView.switchCamera();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
