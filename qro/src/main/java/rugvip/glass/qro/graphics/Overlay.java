package rugvip.glass.qro.graphics;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class Overlay extends GLSurfaceView {
    private static final String TAG = "Overlay";

    private final OverlayRenderer renderer;

    public Overlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder surfaceHolder = getHolder();
        assert surfaceHolder != null;

        setEGLContextClientVersion(2);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setZOrderOnTop(true);

        renderer = new OverlayRenderer();
        setRenderer(renderer);
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

}
