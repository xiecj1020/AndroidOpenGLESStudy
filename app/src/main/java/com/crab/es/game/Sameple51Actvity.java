package com.crab.es.game;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.crab.es.game.view.Sample51SurfaceView;
import com.crab.es.game.view.Sample71SurfaceView;

public class Sameple51Actvity extends AppCompatActivity {
    private Sample71SurfaceView mGLSurfaceView;
    //纹理矩形绕X轴旋转工作标志位
    static boolean threadFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置为横屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //初始化GLSurfaceView
        mGLSurfaceView = new Sample71SurfaceView(this);

        //切换到主界面
        setContentView(mGLSurfaceView);

        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控

    }

    @Override
    protected void onResume() {
        super.onResume();
        threadFlag = true;
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        threadFlag = false;
        mGLSurfaceView.onPause();
    }
}
