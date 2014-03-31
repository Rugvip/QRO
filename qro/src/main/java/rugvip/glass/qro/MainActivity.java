package rugvip.glass.qro;

import android.app.Activity;

import com.google.zxing.ResultPoint;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import rugvip.glass.qro.graphics.Overlay;
import rugvip.glass.qro.qr.ImageQrFactory;
import rugvip.glass.qro.qr.Qr;
import rugvip.glass.qro.qr.QrDetectionListener;
import rugvip.glass.qro.qr.QrMotionListener;
import rugvip.glass.qro.qr.QrDetector;
import rugvip.glass.qro.qr.TextQrFactory;


public class MainActivity extends Activity implements CameraPreview.FrameConsumer, QrDetectionListener, QrMotionListener {
    private static final String TAG = "ASDASD";

    private CameraPreview cameraPreview;
    private Overlay overlay;
    private QrDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        cameraPreview.setFrameConsumer(this);

        overlay = (Overlay) findViewById(R.id.overlay);

        detector = new QrDetector(new ImageQrFactory(), new TextQrFactory());
        detector.addDetectionListener(this);
        detector.addMotionListener(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new MovementSensor(this).start();
        overlay.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MovementSensor.getInstance().stop();
        overlay.onPause();
    }

    private void fitView(ImageView view, ResultPoint[] points) {
        int width = (int) (points[2].getX() - points[1].getX());
        int height = (int) (points[0].getY() - points[1].getY());
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        assert layoutParams != null;
        layoutParams.width = width * 3;
        layoutParams.height = height * 3;

        float x12 = points[2].getX() - points[1].getX();
        float y12 = points[2].getY() - points[1].getY();
        double angle = Math.atan2(y12, x12);
        view.setRotation((float) Math.toDegrees(angle));

        view.setX(points[1].getX() - width);
        view.setY(points[1].getY() - height);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.removeDetectionListener(this);
        detector.removeMotionListener(this);
    }

    @Override
    public void onPreviewFrame(byte[] data, int width, int height) {
        detector.consume(data, width, height);
    }

    @Override
    public void onQrDetected(Qr code) {
        Log.e(TAG, "detected code: " + code);
        overlay.setRed(((int) code.bottom().getX()), ((int) code.bottom().getY()));
        overlay.setGreen(((int) code.middle().getX()), ((int) code.middle().getY()));
        overlay.setBlue(((int) code.right().getX()), ((int) code.right().getY()));
    }

    @Override
    public void onQrMoved(Qr code) {
        Log.e(TAG, "code moved: " + code);
        overlay.setRed(((int) code.bottom().getX()), ((int) code.bottom().getY()));
        overlay.setGreen(((int) code.middle().getX()), ((int) code.middle().getY()));
        overlay.setBlue(((int) code.right().getX()), ((int) code.right().getY()));
    }
}
