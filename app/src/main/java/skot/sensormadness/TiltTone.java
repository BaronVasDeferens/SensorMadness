package skot.sensormadness;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class TiltTone extends AppCompatActivity {

    private SensorManager sensorManager;
    Sensor accelerometer;
    Handler handler;
    SensorReader sensorReader;
    TonePlayer tonePlayer;

    float priorData = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        for (Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)) {
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
        Thread t = new Thread();
        tonePlayer = new TonePlayer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorReader.startUp();
        tonePlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorReader.shutDown();
        tonePlayer.stopPlaying();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void updateDisplay(final Message msg) {
        SensorEvent e = (SensorEvent) msg.obj;
        float[] data = e.values;

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

        switch ((int)data[0]) {
            case 0:
                tonePlayer.setTone(440);
                break;
            case 1:
                tonePlayer.setTone(466);
                break;
            case 2:
                tonePlayer.setTone(493);
                break;
            case 3:
                tonePlayer.setTone(523);
                break;
            case 4:
                tonePlayer.setTone(554);
                break;
            case 5:
                tonePlayer.setTone(587);
                break;
            case 6:
                tonePlayer.setTone(622);
                break;
            case 7:
                tonePlayer.setTone(659);
                break;
            case 8:
                tonePlayer.setTone(698);
                break;
            case 9:
                tonePlayer.setTone(880);
                break;
            default:
                break;
        }

//        if ((int)(data[0]) > (int)(priorData))
//            tonePlayer.adjustFreq((int)(data[0]) - (int)(priorData));
//        else if ((int)(data[0]) < (int)(priorData))
//            tonePlayer.adjustFreq((int)(priorData) - (int)(data[0]));
//
//        priorData = data[0];
    }

}
