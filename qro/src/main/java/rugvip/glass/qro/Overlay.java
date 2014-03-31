package rugvip.glass.qro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

public class Overlay extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "Overlay";
    private RenderThread thread;

    public Overlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder surfaceHolder = getHolder();
        assert surfaceHolder != null;

        surfaceHolder.addCallback(this);
        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new RenderThread(holder);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.interrupt();
    }

    public class RenderThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private boolean running = true;

        public RenderThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        @Override
        public void interrupt() {
            running = false;
            super.interrupt();
        }

        @Override
        public void run() {
            while (running) {
                Canvas canvas = surfaceHolder.lockCanvas();

                if (canvas == null) {
                    Log.e(TAG, "failed to lock canvas");
                } else {
                    Log.e(TAG, "draw: " + canvas.getWidth() + ", " + canvas.getHeight());

                    canvas.drawColor(0);

                    Paint paint = new Paint();
                    paint.setColor(0xFFFF0000);
                    canvas.drawRect(redx - 5, redy - 5, redx + 5, redy + 5, paint);
                    paint.setColor(0xFF00FF00);
                    canvas.drawRect(greenx - 5, greeny - 5, greenx + 5, greeny + 5, paint);
                    paint.setColor(0xFF0000FF);
                    canvas.drawRect(bluex - 5, bluey - 5, bluex + 5, bluey + 5, paint);

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

                try {
                    sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
