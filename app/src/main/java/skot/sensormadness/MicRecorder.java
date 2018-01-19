package skot.sensormadness;

import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MicRecorder extends AppCompatActivity {


    Button playStart, playStop, recStart, recStop;


    AudioRecord audioRecord;

    final int RECORDER_SAMPLERATE = 8000;
    final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format

    byte[] buffer;
    int bufferSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic_recorder);

        recStart = (Button) findViewById(R.id.btnRecordSound);
        enableButton(recStart);

        recStop = (Button) findViewById(R.id.btnRecStop);
        disableButton(recStop);

        playStart = (Button) findViewById(R.id.btnPlaySound);
        disableButton(playStart);

        playStop = (Button) findViewById(R.id.btnPlayStop);
        disableButton(playStop);


        bufferSize = AudioRecord.getMinBufferSize(
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING);

        buffer = new byte[bufferSize];

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                BufferElements2Rec * BytesPerElement);


    }

    public void recStart(View view) {
        disableButton(recStart);
        enableButton(recStop);
        disableButton(playStart);
        disableButton(playStop);
    }

    public void recStop(View view) {
        enableButton(recStart);
        disableButton(recStop);
        enableButton(playStart);
        disableButton(playStop);

    }

    public void playStart(View view) {
        enableButton(playStop);
        disableButton(playStart);
        disableButton(recStart);
        disableButton(recStop);
    }

    public void playStop(View view) {
        enableButton(recStart);
        disableButton(recStop);
        enableButton(playStart);
        disableButton(playStop);
    }

    private void enableButton(final Button b) {
        b.setClickable(true);
        b.setBackgroundColor(Color.GREEN);
    }

    private void disableButton(final Button b) {
        b.setClickable(false);
        b.setBackgroundColor(Color.RED);
    }
}
