package rugvip.glass.qro.graphics;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
* Created by Rugvip on 2014-03-31.
*/
public class OverlayRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "OverlayRenderer";
    private static final int BytesPerFloat = 4;
    private static final float[] squareVertices = {
            -1, -1, -1,
            -1,  1, -1,
             1,  1, -1,

            -1, -1, -1,
             1,  1, -1,
             1, -1, -1,

             1,  1,  1,
            -1,  1,  1,
            -1, -1,  1,

             1,  1,  1,
            -1, -1,  1,
             1, -1,  1,
    };
//    private static final float[] squareVertices = {
//            -1, -1, -1,
//            -1,  1, -1,
//             1,  1, -1,
//             1, -1, -1,
//            -1, -1,  1,
//            -1,  1,  1,
//             1,  1,  1,
//             1, -1,  1,
//    };
//    private static final int[] squareIndices = {
//            0,1,2,
//            0,2,3,
//            1,5,6,
//            1,6,2,
//            3,2,6,
//            3,6,7,
//            4,5,1,
//            4,1,0,
//            0,7,4,
//            0,3,7,
//            5,7,6,
//            5,4,7,
//    };

    private FloatBuffer squareVertexBuffer;
    private IntBuffer squareIndexBuffer;

    private static FloatBuffer createFloatBuffer(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    private static FloatBuffer wrapInBuffer(float[] floats) {
        FloatBuffer buffer = createFloatBuffer(floats.length * BytesPerFloat);
        buffer.put(floats).flip();
        return buffer;
    }

    private static IntBuffer createIntBuffer(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asIntBuffer();
    }

    private static IntBuffer wrapInBuffer(int[] ints) {
        IntBuffer buffer = createIntBuffer(ints.length * BytesPerFloat);
        buffer.put(ints).flip();
        return buffer;
    }

    private static int compileShader(int type, String source) {
        int shader = glCreateShader(type);
        if (0 == shader) {
            throw new RuntimeException("error creating shader");
        }

        glShaderSource(shader, source);
        glCompileShader(shader);

        final int[] compileStatus = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            String log = glGetShaderInfoLog(shader);
            Log.e(TAG, "failed to compile shader: \n" + log);
            glDeleteShader(shader);
            shader = 0;
        }

        return shader;
    }

    private static int compileProgram(String vertexShaderSource, String fragmentShaderSource) {
        int program = glCreateProgram();
        if (0 == program) {
            throw new RuntimeException("error creating program");
        }

        glAttachShader(program, compileShader(GL_VERTEX_SHADER, vertexShaderSource));
        glAttachShader(program, compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource));

        glBindAttribLocation(program, 0, "a_position");
        glBindAttribLocation(program, 1, "a_color");

        glLinkProgram(program);

        final int[] linkStatus = new int[1];

        glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] == 0) {
            String log = glGetProgramInfoLog(program);
            Log.e(TAG, "failed to link program: \n" + log);
            glDeleteProgram(program);
            program = 0;
        }

        return program;
    }

    private float[] viewMatrix = new float[16];
    private float[] projMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 ignored, EGLConfig config) {
        glClearColor(0, 0, 0, 0);

        squareVertexBuffer = wrapInBuffer(squareVertices);

        Matrix.setLookAtM(viewMatrix, 0,
                1, 2, -5, // eye
                0, 0, 0, // center
                0, 1, 0); // up

        int program = compileProgram(Shaders.vertexShader, Shaders.fragmentShader);

        u_mvp_matrix = glGetUniformLocation(program, "u_mvp_matrix");
        a_position = glGetAttribLocation(program, "a_position");
        a_color = glGetAttribLocation(program, "a_color");

        glUseProgram(program);
    }

    private int u_mvp_matrix;
    private int a_position;
    private int a_color;

    @Override
    public void onSurfaceChanged(GL10 ignored, int width, int height) {
        glViewport(0, 0, width, height);

        final float aspectRatio = (float) width / height;
        Matrix.frustumM(projMatrix, 0,
                -aspectRatio, aspectRatio, // left, right
                -1.0f, 1.0f, // bottom, top
                1.0f, 10.0f); // near, far
    }

    private float[] modelMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 ignored) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Matrix.setIdentityM(modelMatrix, 0);

        glVertexAttribPointer(a_position, 3, GL_FLOAT, false, 3 * BytesPerFloat, squareVertexBuffer);
        glEnableVertexAttribArray(a_position);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, mvpMatrix, 0);

        glUniformMatrix4fv(u_mvp_matrix, 1, false, mvpMatrix, 0);
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }
}
