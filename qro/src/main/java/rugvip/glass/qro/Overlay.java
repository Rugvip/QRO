package rugvip.glass.qro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class Overlay extends GLSurfaceView {
    private static final String TAG = "Overlay";

    public Overlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder surfaceHolder = getHolder();
        assert surfaceHolder != null;

        setEGLContextClientVersion(2);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setZOrderOnTop(true);

        setRenderer(new Renderer());
    }

    private int redx, redy, greenx, greeny, bluex, bluey;

    public void setRed(int x, int y) {
        redx = x;
        redy = y;
    }
    public void setGreen(int x, int y) {
        greenx = x;
        greeny = y;
    }
    public void setBlue(int x, int y) {
        bluex = x;
        bluey = y;
    }

    public class Renderer implements GLSurfaceView.Renderer {
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(1, 0, 0, 0.5f);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            Log.e(TAG, "asd");
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        }
    }
}
