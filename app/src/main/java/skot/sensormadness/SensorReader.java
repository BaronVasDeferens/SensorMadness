package skot.sensormadness;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

/**
 * Created by skot on 1/7/18.
 */

public class SensorReader implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor mySensor;
    private Handler handler;


    public SensorReader(SensorManager sensorManager, Sensor mySensor, Handler handler) {
        this.sensorManager = sensorManager;
        this.mySensor = mySensor;
        this.handler = handler;

        startUp();
    }

    public void startUp() {
        sensorManager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void shutDown() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Message m = Message.obtain();
        m.obj = sensorEvent;
        handler.sendMessage(m);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}
