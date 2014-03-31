package rugvip.glass.qro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MovementSensor {
    private final GyroscopeListener gyroscopeListener;
    private final SensorManager sensorManager;
    private final Sensor gyroscope;

    public MovementSensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyroscopeListener = new GyroscopeListener();
    }

    public void start() {
        sensorManager.registerListener(gyroscopeListener, gyroscope, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(gyroscopeListener);
    }

    private class GyroscopeListener implements SensorEventListener {
        private float[] filter = new float[3];

        @Override
        public void onSensorChanged(SensorEvent event) {
            for (int i = 0; i < 3; i++) {
                filter[i] = filter[i] * 0.9f + event.values[i] * 0.1f;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
