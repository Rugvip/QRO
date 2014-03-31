package rugvip.glass.qro;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.TextureView;

import java.io.IOException;

public class CameraPreview extends TextureView implements Camera.PreviewCallback {
    private Camera camera;

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);

        setSurfaceTextureListener(new Renderer());
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

    private class Renderer implements SurfaceTextureListener {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            camera = Camera.open();
            assert camera != null;

            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewFpsRange(10000, 10000);
            camera.setParameters(parameters);

            try {
                camera.setPreviewTexture(surface);
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
            } catch (IOException e) {
                e.printStackTrace();
                stop();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            stop();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }
}
