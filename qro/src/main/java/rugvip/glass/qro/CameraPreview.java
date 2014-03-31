package rugvip.glass.qro;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements Camera.PreviewCallback {
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);

        surfaceHolder = getHolder();
        assert surfaceHolder != null;
        surfaceHolder.addCallback(new Renderer());
    }

    public void stop() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    private FrameConsumer consumer;

    public void setFrameConsumer(FrameConsumer consumer) {
        this.consumer = consumer;
    }

    private byte[][] buffers;
    private int currentBuffer;
    private static final int NUM_BUFFERS = 3;
    private int width = 0;
    private int height = 0;

    public interface FrameConsumer {
        public void onPreviewFrame(byte[] data, int width, int height);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (consumer != null) {
            consumer.onPreviewFrame(data, width, height);
        }
        camera.addCallbackBuffer(buffers[currentBuffer]);
        currentBuffer = (currentBuffer + 1) % NUM_BUFFERS;
    }

    private class Renderer implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            camera = Camera.open(0);
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (camera != null) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewFpsRange(10000, 10000);
                camera.setParameters(parameters);

                try {
                    camera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                    stop();
                    return;
                }

                camera.startPreview();

                parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();
                assert size != null;

                CameraPreview.this.width = size.width;
                CameraPreview.this.height = size.height;

                int bpp = ImageFormat.getBitsPerPixel(parameters.getPreviewFormat());

                buffers = new byte[NUM_BUFFERS][];
                for (int i = 0; i < NUM_BUFFERS; i++) {
                    buffers[i] = new byte[(size.width * size.height * bpp) / 8 + 1];
                }
                camera.addCallbackBuffer(buffers[NUM_BUFFERS - 1]);
                camera.setPreviewCallbackWithBuffer(CameraPreview.this);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stop();
        }
    }
}
