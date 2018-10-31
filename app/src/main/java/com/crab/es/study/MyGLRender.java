package com.crab.es.study;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRender implements GLSurfaceView.Renderer {
    /**
     * In the OpenGL ES environment, projection and camera views allow you to
     * drawn objects in a way that more closely resembles how you see physical
     * objects with your eyes. This simulation of physical viewing is done with
     * mathematical transformations of drawn object coordinates:
     * (1)Projection - This transformation adjusts the coordinates of drawn objects
     * based on the width and height of the GLSurfaceView where they are displayed.
     * Without this calculation, objects drawn by OpenGL ES are skewed by the unequal
     * proportions of the view window. A projection transformation typically only has
     * to be calculated when the proportions of the OpenGL view are established or
     * changed in the onSurfaceChanged() method of your renderer.
     * For more information about OpenGL ES projections and coordinate mapping, see Mapping coordinates for drawn objects.
     * (2)Camera View - This transformation adjusts the coordinates of drawn objects
     * based on a virtual camera position. It’s important to note that OpenGL ES does
     * not define an actual camera object, but instead provides utility methods that
     * simulate a camera by transforming the display of drawn objects. A camera view
     * transformation might be calculated only once when you establish your GLSurfaceView,
     * or might change dynamically based on user actions or your application’s function.
     */
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private Triangle mTriangle;
    private Square mSquare;
    private Circle mCirCle;
    private Cube mCube;
    private Cone mCone;
    private Cylinder mCylinder;
    private Ball mBall;
    private float[] mRotationMatrix = new float[16];

    public volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // initialize a triangle
        mTriangle = new Triangle();
        // initialize a Square
        mSquare = new Square();
        // initialize a Circle
        mCirCle = new Circle();
        // initialize a Cube
        mCube = new Cube();
        //initialize a Cone
        mCone = new Cone();
        //initialize a Cylinder
        mCylinder = new Cylinder();
        //initialize a Ball
        mBall = new Ball();
        //开启深度测试
        /**
        *（1）什么是深度？
        * 深度其实就是该象素点在3d世界中距离摄象机的距离（绘制坐标），深度缓存中存储着每个象素点（绘制在屏幕上的）的深度值！
        * 深度值（Z值）越大，则离摄像机越远。深度值是存贮在深度缓存里面的，我们用深度缓存的位数来衡量深度缓存的精度。
        * 深度缓存位数越高，则精确度越高，目前的显卡一般都可支持16位的Z Buffer，一些高级的显卡已经可以支持32位的Z Buffer，但一般用24位Z Buffer就已经足够了。
        *（2）为什么需要深度？
        * 在不使用深度测试的时候，如果我们先绘制一个距离较近的物体，再绘制距离较远的物体，则距离远的物体因为后绘制，会把距离近的物体覆盖掉，这样的效果并不是我们所希望的。
        * 而有了深度缓冲以后，绘制物体的顺序就不那么重要了，都能按照远近（Z值）正常显示，这很关键。
        * 实际上，只要存在深度缓冲区，无论是否启用深度测试，OpenGL在像素被绘制时都会尝试将深度数据写入到缓冲区内，除非调用了glDepthMask(GL_FALSE)来禁止写入。
        * 这些深度数据除了用于常规的测试外，还可以有一些有趣的用途，比如绘制阴影等等。
        * (3)启用深度测试
        *  使用 glEnable(GL_DEPTH_TEST);
        *  在默认情况是将需要绘制的新像素的z值与深度缓冲区中对应位置的z值进行比较，如果比深度缓存中的值小，那么用新像素的颜色值更新帧缓存中对应像素的颜色值。
        *  但是可以使用glDepthFunc(func)来对这种默认测试方式进行修改。
        *  其中参数func的值可以为GL_NEVER（没有处理）、GL_ALWAYS（处理所有）、GL_LESS（小于）、GL_LEQUAL（小于等于）、GL_EQUAL（等于）、GL_GEQUAL（大于等于）、GL_GREATER（大于）或GL_NOTEQUAL（不等于），其中默认值是GL_LESS。
        *  一般来，使用glDepthFunc(GL_LEQUAL);来表达一般物体之间的遮挡关系。
        *  启用了深度测试，那么这就不适用于同时绘制不透明物体。
         */
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Set the background frame color
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 15);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        //scratch = ProjectMatrix X ViewMatrix X ModeMatrix
        float[] scratch = new float[16];
        // Create a rotation transformation for the triangle
        long time = SystemClock.uptimeMillis() % 4000L;
        //float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);

        // Set the camera position (View matrix)
        //eyeZ的值google传递的是-3,我把它修改为6
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        /**
         * 打印视图矩矩阵，结果如下
         * 1.0000    	-0.0000    	0.0000    	0.0000
         * 0.0000    	1.0000    	0.0000    	0.0000
         * -0.0000    	-0.0000    	1.0000    	-6.0000
         * 0.0000    	0.0000    	0.0000    	1.0000
         * 这个矩阵是设置的眼睛位置的逆矩阵，眼睛位置的矩阵如下:
         * 1.0000    	0.0000    	0.0000    	0.0000
         * 0.0000    	1.0000    	0.0000    	0.0000
         * 0.0000    	0.0000    	1.0000    	6.0000   (眼睛位置)
         * 0.0000    	0.0000    	0.0000    	1.0000
         *
         * 所以模型坐标的z值应该取值范围:[eye-near,eye-far]
         *
         */
        //Log.e("mytag","mViewMatrix="+Arrays.toString(mViewMatrix));
        //绘制立方体的时候需要修改眼睛位置，否则我们只看得到一个面，会以为只绘制的一个正方形出来
        //Matrix.setLookAtM(mViewMatrix, 0, 5, 5, 6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        //this is ProjectMatrix X ViewMatrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        //this is ProjectMatrix X ViewMatrix X ModeMatrix
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        // Draw shape
        mTriangle.draw(scratch);
        //Draw Square
        //mSquare.draw(scratch);
        //Draw Circle
        //mCirCle.draw(scratch);
        //Draw Cube
        //mCube.draw(scratch);
        //Draw Cone
        //mCone.draw(scratch);
        //Draw Cylinder
        //mCylinder.draw(scratch);
        //Draw Ball
        //mBall.draw(scratch);
        //Draw texture
        //mFirstTexture.draw(scratch);

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
