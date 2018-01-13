package skot.sensormadness;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    Sensor accelerometer;
    Handler handler;
    SensorReader sensorReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        for (Sensor sensor: sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)) {
            System.out.println("sensor = " + sensor.toString());
            accelerometer = sensor;
        }

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                updateDisplay(msg);
            }
        };

        sensorReader = new SensorReader(sensorManager, accelerometer, handler);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorReader.shutDown();
    }


    private void updateDisplay(final Message msg) {
        SensorEvent e = (SensorEvent) msg.obj;
        float [] data = e.values;

        TextView sensorDisplay = (TextView) findViewById(R.id.sensorDisplay);
        sensorDisplay.setText(Float.toString(data[0]) + " : " + Float.toString(data[1]) + " : " + Float.toString(data[2]));

        GraphView graphView0 = (GraphView) findViewById(R.id.graphView0);
        graphView0.addPoint(data[0]);
        graphView0.invalidate();

        GraphView graphView1 = (GraphView) findViewById(R.id.graphView1);
        graphView1.addPoint(data[1]);
        graphView1.invalidate();

        GraphView graphView2 = (GraphView) findViewById(R.id.graphView2);
        graphView2.addPoint(data[2]);
        graphView2.invalidate();
    }

}
