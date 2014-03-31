package rugvip.glass.qro.graphics;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rugvip.glass.qro.MovementSensor;

import static android.opengl.GLES20.*;

/**
* Created by Rugvip on 2014-03-31.
*/
public class OverlayRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "OverlayRenderer";
    private static final int BytesPerFloat = 4;
    private static final int BytesPerShort = 2;
//    private static final float[] squareVertices = {
//            -1, -1,  1,
//            -1,  1,  1,
//             1,  1,  1,
//
//            -1, -1,  1,
//             1,  1,  1,
//             1, -1,  1,
//
//             1,  1, -1,
//            -1,  1, -1,
//            -1, -1, -1,
//
//             1,  1, -1,
//            -1, -1, -1,
//             1, -1, -1,
//    };
    private static final float[] squareVertices = {
            -1, -1,  1,
            -1,  1,  1,
             1,  1,  1,
             1, -1,  1,
            -1, -1, -1,
            -1,  1, -1,
             1,  1, -1,
             1, -1, -1,
    };
    private static final short[] squareIndices = {
            0,1,2,
            0,2,3,
            1,5,6,
            1,6,2,
            3,2,6,
            3,6,7,
            4,5,1,
            4,1,0,
            0,7,4,
            0,3,7,
            5,7,6,
            5,4,7,
    };

    private FloatBuffer squareVertexBuffer;
    private ShortBuffer squareIndexBuffer;

    private static FloatBuffer createFloatBuffer(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    private static FloatBuffer wrapInBuffer(float[] floats) {
        FloatBuffer buffer = createFloatBuffer(floats.length * BytesPerFloat);
        buffer.put(floats).flip();
        return buffer;
    }

    private static ShortBuffer createShortBuffer(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asShortBuffer();
    }

    private static ShortBuffer wrapInBuffer(short[] ints) {
        ShortBuffer buffer = createShortBuffer(ints.length * BytesPerShort);
        buffer.put(ints).flip();
        return buffer;
    }

    private float[] viewMatrix = new float[16];
    private float[] projMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 ignored, EGLConfig config) {
        glClearColor(0, 0, 0, 0);

        squareVertexBuffer = wrapInBuffer(squareVertices);
        squareIndexBuffer = wrapInBuffer(squareIndices);

        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.translateM(viewMatrix, 0, 10, 0, 10);
//        Matrix.setLookAtM(viewMatrix, 0,
//                1, 2, -5, // eye
//                0, 0, 0, // center
//                0, 1, 0); // up

        int program = Shaders.compileProgram(Shaders.vertexShader, Shaders.fragmentShader);

        u_mvp_matrix = glGetUniformLocation(program, "u_mvp_matrix");

        glUseProgram(program);
    }

    private int u_mvp_matrix;

    @Override
    public void onSurfaceChanged(GL10 ignored, int width, int height) {
        glViewport(0, 0, width, height);

        final float aspectRatio = (float) width / height;
        Matrix.frustumM(projMatrix, 0,
                -aspectRatio, aspectRatio, // left, right
                -1.0f, 1.0f, // bottom, top
                2.0f, 100.0f); // near, far
    }

    private float[] modelMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private float[] cameraMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 ignored) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Matrix.setIdentityM(modelMatrix, 0);

        glVertexAttribPointer(Attr.POSITION, 3, GL_FLOAT, false, 3 * BytesPerFloat, squareVertexBuffer);
        glEnableVertexAttribArray(Attr.POSITION);

        MovementSensor.getInstance().getRotation(cameraMatrix);
        float[] rot = new float[] {0, 0, 1, 1};
        Matrix.multiplyMV(rot, 0, cameraMatrix, 0, rot, 0);
        Log.e(TAG, "rot: " + Arrays.toString(rot));
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, cameraMatrix, 0, mvpMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, mvpMatrix, 0);
        Log.e(TAG, "err: " + glGetError());

        glUniformMatrix4fv(u_mvp_matrix, 1, false, mvpMatrix, 0);

        glDrawElements(GL_TRIANGLES, squareIndices.length, GL_UNSIGNED_SHORT, squareIndexBuffer);
    }
}
