uniform mat4 uMVPMatrix;

attribute vec4 vPosition;
attribute vec2 aCoord;
varying vec2 vCoord;
void main(){
    gl_Position = uMVPMatrix * vPosition;;
    vCoord = aCoord;
}