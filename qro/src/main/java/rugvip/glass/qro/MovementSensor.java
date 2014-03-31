package rugvip.glass.qro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.opengl.Matrix;
import android.util.Log;

import java.util.Arrays;

public class MovementSensor {
    private final RotationListener rotationListener;
    private final SensorManager sensorManager;
    private final Sensor rotationVector;

    private static MovementSensor instance;

    public MovementSensor(Context context) {
        if (instance != null) {
            throw new IllegalStateException("only one mevement sensor allowed");
        }
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        rotationListener = new RotationListener();

        instance = this;
    }

    public static MovementSensor getInstance() {
        return instance;
    }

    public void start() {
        sensorManager.registerListener(rotationListener, rotationVector, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(rotationListener);
    }

    public void getRotation(float[] out) {
        if (out.length != 9 && out.length != 16) {
            throw new IllegalArgumentException("out matrix should be 3x3 or 4x4");
        }

        if (rotationListener.rotation != null) {
            SensorManager.getRotationMatrixFromVector(out, rotationListener.rotation);
        } else {
            Matrix.setIdentityM(out, 0);
        }
    }

    private class RotationListener implements SensorEventListener {
        private float[] rotation = null;
        private static final String TAG = "RotationSensor";

        @Override
        public void onSensorChanged(SensorEvent event) {
            rotation = event.values;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
