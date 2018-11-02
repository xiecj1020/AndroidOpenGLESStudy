package com.crab.es.game.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.crab.es.game.shape.SixPointedStar;
import com.crab.es.game.utils.MatrixState;

import javax.microedition.khronos.opengles.GL10;

public class Sample51SurfaceView extends GLSurfaceView {
    //角度缩放比例
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    //场景渲染器
    private SceneRenderer mRenderer;
    //上次的触控位置Y坐标
    private float mPreviousY;
    //上次的触控位置X坐标
    private float mPreviousX;

    public Sample51SurfaceView(Context context) {
        this(context, null);
    }

    public Sample51SurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置使用OPENGL ES2.0
        setEGLContextClientVersion(2);
        //创建场景渲染器
        mRenderer = new SceneRenderer();
        //设置渲染器
        setRenderer(mRenderer);
        //设置渲染模式为主动渲染
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //计算触控笔Y位移
                float dy = y - mPreviousY;
                //计算触控笔X位移
                float dx = x - mPreviousX;
                for (SixPointedStar h : mRenderer.ha) {
                    h.yAngle += dx * TOUCH_SCALE_FACTOR;//设置六角星数组中的各个六角星绕y轴旋转角度
                    h.xAngle += dy * TOUCH_SCALE_FACTOR;//设置六角星数组中的各个六角星绕x轴旋转角度
                }
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {
        SixPointedStar[] ha = new SixPointedStar[6];//六角星数组

        public void onDrawFrame(GL10 gl) {
            //清除深度缓冲与颜色缓冲
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //绘制六角星数组中的各个六角星
            for (SixPointedStar h : ha) {
                h.drawSelf();
            }
        }

        @Override
        public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            //创建六角星数组中的各个对象
            for (int i = 0; i < ha.length; i++) {
                ha[i] = new SixPointedStar(Sample51SurfaceView.this, 0.2f, 0.5f, -0.3f * i);
            }
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置
            GLES20.glViewport(0, 0, width, height);
            //计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //设置平行投影
            MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 10);

            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(
                    0, 0, 3f,
                    0, 0, 0f,
                    0f, 1.0f, 0.0f
            );
        }
    }
}
