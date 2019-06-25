package com.crab.es.study;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrthoMProjectionView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private  float[] mPreModeMatrix = new float[16];
    private FirstTexture mFirstTexture;
    private float[] mRotationMatrix = new float[16];

    public volatile float mAngle;
    private  int mWidth,mHeight;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }
    public OrthoMProjectionView(Context context) {
        this(context,null);
    }

    public OrthoMProjectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(this);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Resources resources = EsApplication.getGlobalResource();
        Bitmap bitmap = BitmapFactory.decodeResource(resources,R.drawable.char_patrick);
        mFirstTexture= new FirstTexture(bitmap);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mWidth = width;
        mHeight = height;
        float ratio = (float) width / height;
        Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, -1, 1);
        mPreModeMatrix =computeTheDeviceOrthoMatrix(width,height,ratio);
    }

    /**
     * 这个方法是根据正交投影矩阵设置的值来计算的
     * 计算把屏幕坐标映射到模型坐标矩阵
     */
    private float[] computeTheDeviceOrthoMatrix(float width,float height,float ratio){
        //平移x,y
        float[] translateM = new float[16];
        Matrix.setIdentityM(translateM,0);
        Matrix.translateM(translateM,0,-width/2,height/2,0.0f);
        //倒置y轴
        float[] scaleY = new float[16];
        Matrix.setIdentityM(scaleY,0);
        Matrix.scaleM(scaleY,0,1.0f,-1.0f,1.0f);
        float[] scaleM = new float[16];
        Matrix.setIdentityM(scaleM,0);
        //缩放x,y轴
        float sx = (2.0f/width)*ratio;
        float sy = 2.0f/height;
        Matrix.scaleM(scaleM,0,sx,sy,1.0f);
        float[] resultMatrix1 =new float[16];
        Matrix.multiplyMM(resultMatrix1,0,translateM,0,scaleY,0);
        float[] resultMatrix2 =new float[16];
        Matrix.multiplyMM(resultMatrix2,0,scaleM,0,resultMatrix1,0);
        return resultMatrix2;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        float[] scratch = new float[16];
        Matrix.setIdentityM(scratch,0);
        //比如想在屏幕x,y处绘制width,height
        float x = 0.0f,y=0.0f;
        float width = mWidth/4,height = mHeight/4;
        draw(gl,x,y,width,height);

    }
    void draw(GL10 gl,float x,float y,float width,float height){
        float[] scratch = new float[16];
        Matrix.setIdentityM(scratch,0);
        Matrix.setIdentityM(mViewMatrix,0);
        //Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        float[] modeMatrix = new float[16];
        Matrix.setIdentityM(modeMatrix,0);
        Matrix.translateM(modeMatrix,0,x,y,0.0f);
        Matrix.scaleM(modeMatrix,0,width,height,1.0f);
        Matrix.rotateM(modeMatrix,0,mAngle,0.0f,0.0f,1.0f);
        float[] totalModeMatrix = new float[16];

        Matrix.multiplyMM(totalModeMatrix,0,mPreModeMatrix,0,modeMatrix,0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, totalModeMatrix, 0);
        mFirstTexture.draw(scratch);
    }
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

               setAngle(
                       getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));
                //requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

}
