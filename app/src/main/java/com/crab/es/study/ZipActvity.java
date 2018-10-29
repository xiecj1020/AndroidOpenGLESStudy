package com.crab.es.study;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.crab.es.study.etc.StateChangeListener;
import com.crab.es.study.etc.ZipAniView;
import com.crab.es.study.utils.Gl2Utils;

/**
 * 显示动画，从zip文件中读取动画资源
 */

public class ZipActvity extends AppCompatActivity {
    private ZipAniView mAniView;
    private String nowMenu = "assets/etczip/cc.zip";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip);
        mAniView = findViewById(R.id.mAni);
        mAniView.setScaleType(Gl2Utils.TYPE_CENTERINSIDE);
        mAniView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAniView.isPlay()) {
                    mAniView.setAnimation(nowMenu, 50);
                    mAniView.start();
                }
            }
        });
        mAniView.setStateChangeListener(new StateChangeListener() {
            @Override
            public void onStateChanged(int lastState, int nowState) {
                if (nowState == STOP) {
                    if (!mAniView.isPlay()) {
                        mAniView.setAnimation(nowMenu, 50);
                        mAniView.start();
                    }
                }
            }
        });
    }
}
