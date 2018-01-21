package skot.sensormadness;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class LoopRecorderActivity extends AppCompatActivity {


    Button playStart, playStop, recStart, recStop;
    RecordingThread recThread = null;
    SoundPlayer soundPlayer = null;
    private final int sampleRate = 22050;
    private final int bufferSize = 50000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop_recorder);

        recStart = (Button) findViewById(R.id.btnRecordSound);
        enableButton(recStart);

        recStop = (Button) findViewById(R.id.btnRecStop);
        disableButton(recStop);

        playStart = (Button) findViewById(R.id.btnPlaySound);
        disableButton(playStart);

        playStop = (Button) findViewById(R.id.btnPlayStop);
        disableButton(playStop);

    }

    public void recStart(View view) {

        disableButton(recStart);
        enableButton(recStop);
        disableButton(playStart);
        disableButton(playStop);

        recThread = new RecordingThread(bufferSize, sampleRate);
        recThread.start();

    }

    public void recStop(View view) {

        enableButton(recStart);
        disableButton(recStop);
        enableButton(playStart);
        disableButton(playStop);

        if (recThread != null) {
            recThread.stopRecording();
        }
    }

    public void playStart(View view) {
        enableButton(playStop);
        disableButton(playStart);
        disableButton(recStart);
        disableButton(recStop);

        soundPlayer = new SoundPlayer(recThread.getBuffer(), sampleRate);
        soundPlayer.start();
    }

    public void playStop(View view) {
        enableButton(recStart);
        disableButton(recStop);
        enableButton(playStart);
        disableButton(playStop);

        if (soundPlayer != null)
            soundPlayer.stopPlaying();

    }

    private void enableButton(final Button b) {
        b.setClickable(true);
        b.setBackgroundColor(Color.GREEN);
    }

    private void disableButton(final Button b) {
        b.setClickable(false);
        b.setBackgroundColor(Color.RED);
    }

    public void setPlaybackLoopMode(View view) {
        CheckBox checkbox = (CheckBox) view;
        if (checkbox.isChecked())
            soundPlayer.setToLoop();
        else
            soundPlayer.setToOneshot();
    }
}
