package rugvip.glass.qro.graphics;


public class Shaders {
    public static final String vertexShader =
            "uniform mat4 u_mvp_matrix;" +
            "attribute vec4 a_position;" +
            "attribute vec4 a_color;" +
            "varying vec4 v_color;" +

            "void main()" +
            "{" +
            "    v_color = vec4(1, 0, 0, 1);" +
            "    gl_Position = u_mvp_matrix * a_position;" +
            "}";

    public static final String fragmentShader =
            "precision mediump float;" +
            "varying vec4 v_color;" +
            "void main()" +
            "{" +
            "    gl_FragColor = v_color;" +
            "}";
}
