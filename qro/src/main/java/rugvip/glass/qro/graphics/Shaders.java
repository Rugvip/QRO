package rugvip.glass.qro.graphics;


import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindAttribLocation;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;

public class Shaders {
    private static final String TAG = "Shaders";
    public static final String vertexShader =
            "uniform mat4 u_mvp_matrix;" +
            "attribute vec4 a_position;" +
            "attribute vec4 a_color;" +
            "varying vec4 v_color;" +

            "void main()" +
            "{" +
            "    v_color = a_position;" +
            "    gl_Position = u_mvp_matrix * a_position;" +
            "}";

    public static final String fragmentShader =
            "precision mediump float;" +
            "varying vec4 v_color;" +
            "void main()" +
            "{" +
            "    gl_FragColor = abs(v_color);" +
            "}";

    public static final int ATTR_POSITION = 0;
    public static final int ATTR_COLOR = 1;


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

    public static int compileProgram(String vertexShaderSource, String fragmentShaderSource) {
        int program = glCreateProgram();
        if (0 == program) {
            throw new RuntimeException("error creating program");
        }

        glAttachShader(program, compileShader(GL_VERTEX_SHADER, vertexShaderSource));
        glAttachShader(program, compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource));

        glBindAttribLocation(program, Attr.POSITION, "a_position");
        glBindAttribLocation(program, Attr.COLOR, "a_color");

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
}
