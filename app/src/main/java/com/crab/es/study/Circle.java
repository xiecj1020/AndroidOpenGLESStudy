package com.crab.es.study;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Circle {
    private FloatBuffer vertexBuffer;
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;"+
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private int mProgram;

    static final int COORDS_PER_VERTEX = 3;


    private int mPositionHandle;
    private int mColorHandle;

    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    private int mMatrixHandler;

    private float radius=1.0f;
    private int n=360;  //切割份数

    private float[] shapePos;

    //设置颜色，依次为红绿蓝和透明通道
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };

    public Circle(){
        shapePos= createPositions();
        ByteBuffer bb = ByteBuffer.allocateDirect(
                shapePos.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(shapePos);
        vertexBuffer.position(0);
        int vertexShader = MyGLRender.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRender.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }
    private float[]  createPositions(){
        ArrayList<Float> data=new ArrayList<>();
        data.add(0.0f);             //设置圆心坐标
        data.add(0.0f);
        data.add(0.0f);
        float angDegSpan=360f/n;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            data.add((float) (radius*Math.sin(i*Math.PI/180f)));
            data.add((float)(radius*Math.cos(i*Math.PI/180f)));
            data.add(0.0f);
        }
        float[] f=new float[data.size()];
        for (int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }
        return f;
    }

    public void draw(float[] mvpMatrix) { // pass in the calculated transformation matrix
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);
        // get handle to shape's transformation matrix
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mvpMatrix, 0);
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // Draw the triangle
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        //绘制三角形,
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, shapePos.length/3);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
