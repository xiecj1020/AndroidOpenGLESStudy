package com.crab.es.study;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

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
        // Set the background frame color
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        float[] scratch = new float[16];
        // Create a rotation transformation for the triangle
        long time = SystemClock.uptimeMillis() % 4000L;
        //float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);

        // Set the camera position (View matrix)
        //eyeZ的值google传递的是-3,我把它修改为6
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        // Draw shape
        mTriangle.draw(scratch);

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
