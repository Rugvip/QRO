package rugvip.glass.qro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

public class Overlay extends TextureView implements TextureView.SurfaceTextureListener {
    private static final String TAG = "Overlay";
    private RenderThread thread;

    public Overlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        thread = new RenderThread(this);
        thread.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        thread.interrupt();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

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

    public class RenderThread extends Thread {
        private TextureView view;
        private boolean running = true;

        public RenderThread(TextureView view) {
            this.view = view;
        }

        @Override
        public void interrupt() {
            running = false;
            super.interrupt();
        }

        @Override
        public void run() {
            while (running) {
                Canvas canvas = view.lockCanvas();

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

                    view.unlockCanvasAndPost(canvas);
                }

                try {
                    sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
