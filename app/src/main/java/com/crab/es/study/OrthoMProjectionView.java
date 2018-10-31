package com.crab.es.study;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

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
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Set the background frame color
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);
        Resources resources = EsApplication.getGlobalResource();
        Bitmap bitmap = BitmapFactory.decodeResource(resources,R.drawable.char_patrick);
        mFirstTexture= new FirstTexture(bitmap);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 15);
        mPreModeMatrix =computeTheDeviceOrthoMatrix(width,height,ratio);
    }

    /**
     * 这个方法是根据正交投影矩阵设置的值来计算的
     * 计算把屏幕坐标映射到模型坐标矩阵
     */
    private float[] computeTheDeviceOrthoMatrix(float width,float height,float ratio){
        float[] translateM = new float[16];
        Matrix.setIdentityM(translateM,0);
        Matrix.translateM(translateM,0,-width/2,height/2,0.0f);

        float[] rotateM = new float[16];
        Matrix.setIdentityM(rotateM,0);
        Matrix.rotateM(rotateM,0,180,1.0f,0.0f,0.0f);

        float[] scaleM = new float[16];
        Matrix.setIdentityM(scaleM,0);
        float sx = (2.0f/width)*ratio;
        float sy = 2.0f/height;
        Matrix.scaleM(scaleM,0,sx,sy,1.0f);

        float[] resultMatrix1 =new float[16];
        Matrix.multiplyMM(resultMatrix1,0,translateM,0,rotateM,0);

        float[] resultMatrix2 =new float[16];
        Matrix.multiplyMM(resultMatrix2,0,scaleM,0,resultMatrix1,0);
        return resultMatrix2;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        float[] scratch = new float[16];
        Matrix.setIdentityM(scratch,0);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mPreModeMatrix, 0);
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
}
