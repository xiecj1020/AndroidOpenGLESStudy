package com.crab.es.study;

import android.opengl.GLES20;

import com.crab.es.study.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * 球体的拆解就复杂了许多，比较常见的拆解方法是将按照经纬度拆解和按照正多面体拆解
 *
 * 使用经纬度的方法拆解（每一个小块看做一个矩形，再拆成三角形)
 * 参见图ball1.png
 * 左右两条经线，上下两条纬线构成一个正方形（近似）。正方形可以看做是两个三角形构成。
 * 途中土黄色的箭头，代表使用GL_TRIANGLE_STRIP模式画图时采用的顶点顺序。
 * 这种切割方法，看起来清晰很多，纵横经纬两层循环遍历所有顶点。
 *
 * 关键是：怎么计算球面的顶点坐标？(x, y, z)
 * 参见图ball2.png
 * 如上图，任意球面上的点，三维坐标 (x0, y0, z0) 计算：(R为球半径)
 * x0 = R * cos(a) * sin(b);
 * y0 = R * sin(a);
 * z0 = R * cos(a) * cos(b);
 * a为圆心到点的线段与xz平面的夹角，b为圆心到点的线段在xz平面的投影与z轴的夹角
 */

public class Ball {
    private float step=5f;
    private FloatBuffer vertexBuffer;
    private int vSize;

    private int mProgram;

    private float radius=1.0f;

    public Ball() {
        float[] dataPos=createBallPos();
        ByteBuffer buffer=ByteBuffer.allocateDirect(dataPos.length*4);
        buffer.order(ByteOrder.nativeOrder());
        vertexBuffer=buffer.asFloatBuffer();
        vertexBuffer.put(dataPos);
        vertexBuffer.position(0);
        vSize=dataPos.length/3;

        mProgram= ShaderUtils.createProgram(EsApplication.getGlobalResource(),"vshader/Ball.sh","fshader/Cone.sh");
    }

    private float[] createBallPos(){
        //球以(0,0,0)为中心，以R为半径，则球上任意一点的坐标为
        // ( R * cos(a) * sin(b),y0 = R * sin(a),R * cos(a) * cos(b))
        // 其中，a为圆心到点的线段与xz平面的夹角，b为圆心到点的线段在xz平面的投影与z轴的夹角
        ArrayList<Float> data=new ArrayList<>();
        float r1,r2;
        float h1,h2;
        float sin,cos;
        for(float i=-90;i<90+step;i+=step){
            r1 = (float)Math.cos(i * Math.PI / 180.0) * radius;   //当前纬度圆的半径 (R * cos(a))
            r2 = (float)Math.cos((i + step) * Math.PI / 180.0) * radius; //下一个纬度圆的半径 (R * cos(a))
            h1 = (float)Math.sin(i * Math.PI / 180.0) * radius; // 当前纬度圆的y坐标
            h2 = (float)Math.sin((i + step) * Math.PI / 180.0) * radius;//下一个纬度圆的y坐标
            // 固定纬度, 360 度旋转遍历一条经线
            float step2=step*2;
            for (float j = 0.0f; j <360.0f+step; j +=step2 ) {
                cos = (float) Math.cos(j * Math.PI / 180.0);
                sin = -(float) Math.sin(j * Math.PI / 180.0);

                data.add(r2 * cos);  // 当前纬度圆x坐标
                data.add(h2);        // 当前纬度圆y坐标
                data.add(r2 * sin);  // 当前纬度圆z坐标
                data.add(r1 * cos);  //下一个纬度圆x坐标
                data.add(h1);        //下一个纬度圆y坐标
                data.add(r1 * sin);  //下一个纬度圆z坐标
            }
        }
        float[] f=new float[data.size()];
        for(int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }
        return f;
    }
    public void draw(float[] mvpMatrix) { // pass in the calculated transformation matrix
        GLES20.glUseProgram(mProgram);
        int mMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
        GLES20.glUniformMatrix4fv(mMatrix,1,false,mvpMatrix,0);
        int mPositionHandle=GLES20.glGetAttribLocation(mProgram,"vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle,3,GLES20.GL_FLOAT,false,0,vertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,vSize);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
