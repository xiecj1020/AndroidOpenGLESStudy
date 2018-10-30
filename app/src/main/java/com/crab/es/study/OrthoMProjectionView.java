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
    private  float[] mModeMatrix = new float[16];
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
        mModeMatrix=computeTheDeviceOrthoMatrix(width,height,ratio);
    }

    /**
     * 这个方法是根据正交投影矩阵设置的值来计算的
     * 计算把屏幕坐标映射到模型坐标矩阵
     */
    private float[] computeTheDeviceOrthoMatrix(float width,float height,float ratio){
        //先计算出屏幕坐标对应的模型坐标是多少,矩阵格式如下(竖着看):
        //1.0f,0.0f,0.0f,-width/2
        //0.0f,-1.0f,0.0f,height/2
        //0.0f,0.0f,1.0f,0.0f
        //0.0f,0.0f,0.0f,1.0f
        float[] moveMatrix=new float[16];
        moveMatrix[0] = 1.0f;
        moveMatrix[1] = 0.0f;
        moveMatrix[2] = 0.0f;
        moveMatrix[3] = 0.0f;
        moveMatrix[4] = 0.0f;
        moveMatrix[5] = -1.0f;
        moveMatrix[6] = 0.0f;
        moveMatrix[7] = 0.0f;
        moveMatrix[8] = 0.0f;
        moveMatrix[9] = 0.0f;
        moveMatrix[10] = 1.0f;
        moveMatrix[11] = 0.0f;
        moveMatrix[12] = (-1.0f*width/2);
        moveMatrix[13] = (1.0f*height/2);
        moveMatrix[14] = 0.0f;
        moveMatrix[15] = 1.0f;
        //把计算出的坐标缩放到指定的范围,x轴[-ratio,ratio],y轴[-1,1]
        //矩阵格式如下(竖着看):
        //(2.0f/width)*ratio,0.0f,0.0f,0.0f
        //0.0f,(2.0f/height),0.0f,0.0f
        //0.0f,0.0f,1.0f,0.0f
        //0.0f,0.0f,0.0f,1.0f
       float[] scaleMatrix = new float[16];
        scaleMatrix[0] = (2.0f/width)*ratio;
        scaleMatrix[1] = 0.0f;
        scaleMatrix[2] = 0.0f;
        scaleMatrix[3] = 0.0f;
        scaleMatrix[4] = 0.0f;
        scaleMatrix[5] = (2.0f/height);
        scaleMatrix[6] = 0.0f;
        scaleMatrix[7] = 0.0f;
        scaleMatrix[8] = 0.0f;
        scaleMatrix[9] = 0.0f;
        scaleMatrix[10] = 1.0f;
        scaleMatrix[11] = 0.0f;
        scaleMatrix[12] = 0.0f;
        scaleMatrix[13] = 0.0f;
        scaleMatrix[14] = 0.0f;
        scaleMatrix[15] = 1.0f;
        float[] resultMatrix = new float[16];
        Matrix.multiplyMM(resultMatrix,0,scaleMatrix,0,moveMatrix,0);
        return resultMatrix;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        float[] scratch = new float[16];
        Matrix.setIdentityM(scratch,0);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mModeMatrix, 0);
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
