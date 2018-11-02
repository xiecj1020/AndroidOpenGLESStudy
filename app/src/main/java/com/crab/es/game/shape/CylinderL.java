package com.crab.es.game.shape;

import com.crab.es.game.utils.MatrixState;
import com.crab.es.game.view.Sample81SurfaceView;

//骨架圆柱类
public class CylinderL {
    CircleL bottomCircle;//底圆的骨架类的引用
    CircleL topCircle;//顶圆的骨架类的引用
    CylinderSideL cylinderSide;//侧面的骨架类的引用
    public float xAngle=0;//绕x轴旋转的角度
    public float yAngle=0;//绕y轴旋转的角度
    public float zAngle=0;//绕z轴旋转的角度
    float h;
    float scale;

    public CylinderL(Sample81SurfaceView mySurfaceView, float scale, float r, float h, int n)
    {
        this.scale=scale;
        this.h=h;
        topCircle=new CircleL(mySurfaceView,scale,r,n);	//创建顶面骨架圆的对象
        bottomCircle=new CircleL(mySurfaceView,scale,r,n);  //创建底面骨架圆的对象
        cylinderSide=new CylinderSideL(mySurfaceView,scale,r,h,n); //创建侧面无顶圆柱骨架的对象
    }
    public void drawSelf()
    {
        MatrixState.rotate(xAngle, 1, 0, 0);
        MatrixState.rotate(yAngle, 0, 1, 0);
        MatrixState.rotate(zAngle, 0, 0, 1);
        //顶面
        MatrixState.pushMatrix();
        MatrixState.translate(0, h/2*scale, 0);
        MatrixState.rotate(-90, 1, 0, 0);
        topCircle.drawSelf();
        MatrixState.popMatrix();

        //底面
        MatrixState.pushMatrix();
        MatrixState.translate(0, -h/2*scale, 0);
        MatrixState.rotate(90, 1, 0, 0);
        MatrixState.rotate(180, 0, 0, 1);
        bottomCircle.drawSelf();
        MatrixState.popMatrix();

        //侧面
        MatrixState.pushMatrix();
        MatrixState.translate(0, -h/2*scale, 0);
        cylinderSide.drawSelf();
        MatrixState.popMatrix();
    }
}
