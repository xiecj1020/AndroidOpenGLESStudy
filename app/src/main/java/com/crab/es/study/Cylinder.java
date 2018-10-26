package com.crab.es.study;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * 圆柱与圆锥类似，可以把圆柱拆解成上下两个圆面，加上一个圆筒。
 * 圆筒之前也没画过，它怎么拆解成三角形呢？可以如同拆圆的思路来理解圆柱，
 * 想想正三菱柱、正八菱柱、正一百菱柱……菱越多，就越圆滑与圆柱越接近了，然后再把每个菱面（矩形）拆解成两个三角形就OK了
 */

public class Cylinder {
    private int mProgram;

    private Circle mCircleBottom, mCircleTop;
    private FloatBuffer vertexBuffer;


    private int n=360;  //切割份数
    private float height=2.0f;  //圆锥高度
    private float radius=1.0f;  //圆锥底面半径

    private int vSize;

    public Cylinder(){
        mCircleBottom =new Circle();
        mCircleTop =new Circle(height);
        ArrayList<Float> pos=new ArrayList<>();
        float angDegSpan=360f/n;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            pos.add((float) (radius*Math.sin(i*Math.PI/180f)));
            pos.add((float)(radius*Math.cos(i*Math.PI/180f)));
            pos.add(height);
            pos.add((float) (radius*Math.sin(i*Math.PI/180f)));
            pos.add((float)(radius*Math.cos(i*Math.PI/180f)));
            pos.add(0.0f);
        }
        float[] d=new float[pos.size()];
        for (int i=0;i<d.length;i++){
            d[i]=pos.get(i);
        }
        vSize=d.length/3;
        ByteBuffer buffer=ByteBuffer.allocateDirect(d.length*4);
        buffer.order(ByteOrder.nativeOrder());
        vertexBuffer=buffer.asFloatBuffer();
        vertexBuffer.put(d);
        vertexBuffer.position(0);

        mProgram=ShaderUtils.createProgram(EsApplication.getGlobalResource(),"vshader/Cone.sh","fshader/Cone.sh");
    }
    public void draw(float[] mvpMatrix) { // pass in the calculated transformation matrix
        GLES20.glUseProgram(mProgram);
        int mMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
        GLES20.glUniformMatrix4fv(mMatrix,1,false,mvpMatrix,0);
        int mPositionHandle=GLES20.glGetAttribLocation(mProgram,"vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle,3,GLES20.GL_FLOAT,false,0,vertexBuffer);
//        int mColorHandle=GLES20.glGetUniformLocation(mProgram,"vColor");
//        GLES20.glEnableVertexAttribArray(mColorHandle);
//        GLES20.glUniform4fv(mColorHandle,1,colors,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,vSize);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        mCircleBottom.draw(mvpMatrix);
        mCircleTop.draw(mvpMatrix);
    }
}
