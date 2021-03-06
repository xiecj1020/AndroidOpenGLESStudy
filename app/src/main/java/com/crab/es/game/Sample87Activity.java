package com.crab.es.game;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.crab.es.game.view.Sample81SurfaceView;
import com.crab.es.game.view.Sample87SurfaceView;
import com.crab.es.study.R;


public class Sample87Activity extends AppCompatActivity {
    Sample87SurfaceView mySurfaceView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置为横屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //初始化GLSurfaceView
        mySurfaceView = new Sample87SurfaceView(this);

        //切换到主界面
        setContentView(mySurfaceView);

        mySurfaceView.requestFocus();//获取焦点
        mySurfaceView.setFocusableInTouchMode(true);//设置为可触控
    }
    @Override
    protected void onResume() {
        super.onResume();
        mySurfaceView.onResume();
        mySurfaceView.lightFlag=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mySurfaceView.onPause();
        mySurfaceView.lightFlag=false;
    }
    public boolean onKeyDown(int keyCode,KeyEvent e)
    {
        switch(keyCode)
        {
            case 4:
                System.exit(0);
                break;
        }
        return true;
    }
}
