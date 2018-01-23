package skot.sensormadness;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by skot on 1/20/18.
 */

public class RecordingThread extends Thread {
    private AudioRecord audioRecord;
    private boolean nowRecording = false;
    byte [] buffer;
    private final int bufferSize;
    private final int sampleRate;
    private long recordingStart, recordingStop, recordingDuration = 0;

    public RecordingThread(final int bufferSize, final int sampleRate) {
        this.sampleRate = sampleRate;
        this.bufferSize = bufferSize;

        buffer = new byte [bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            buffer[i] = 0;
        }
    }

    public void run() {

        System.out.println(">>> (REC) Start...");

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                bufferSize
        );

        nowRecording = true;
        recordingStart = System.currentTimeMillis();
        audioRecord.startRecording();
        int bytesRead = 0;
        while (nowRecording) {
            bytesRead = audioRecord.read(buffer, 0, bufferSize);
        }

        System.out.println(">>> " + bytesRead + " bytes read");
        recordingStop = System.currentTimeMillis();
        recordingDuration = recordingStop - recordingStart;
        System.out.println(">>> recordingDuration = " + recordingDuration);
        audioRecord.stop();
        audioRecord.release();
    }

    public synchronized void stopRecording() {
        System.out.println(">>> (REC) Stop!");
        nowRecording = false;
    }

    public byte [] getBuffer() {
        return buffer;
    }

}
