package skot.sensormadness;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    Sensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        for (Sensor sensor: sensorManager.getSensorList(Sensor.TYPE_LIGHT)) {
            System.out.println("sensor = " + sensor.toString());
            lightSensor = sensor;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {

        System.out.println("SENSOR : " + sensorEvent.values[0]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void derp (final SensorEvent sensorEvent) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                TextView sensorView = (TextView) findViewById(R.id.sensorDisplay);
                sensorView.setText((int) sensorEvent.values[0]);

            }
        });
    }
}
