package com.crab.es.study;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class MyLightActivity extends Activity {
    private GLSurfaceView mGLView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new GLSurfaceView(this);
        setContentView(mGLView);
        mGLView.setEGLContextClientVersion(2);
        mGLView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        mGLView.setRenderer(new LightRender());
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
}
