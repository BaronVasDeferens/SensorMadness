package skot.sensormadness;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by skot on 1/20/18.
 */

public class RecordingThread extends Thread {
    private boolean nowRecording = false;
    byte [] buffer;
    private final int bufferSize;
    private final int sampleRate;

    public RecordingThread(final int bufferSize, final int sampleRate) {
        this.sampleRate = sampleRate;
        this.bufferSize = bufferSize;


        System.out.println(">>> sampleRate : " + sampleRate);
        System.out.println(">>> bufferSize : " + bufferSize);
    }

    public void run() {
        System.out.println(">>> (REC) Start...");
        buffer = new byte [bufferSize];
        AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                bufferSize
        );

        nowRecording = true;
        audioRecord.startRecording();
        int bytesRead = 0;
        while (nowRecording) {
            bytesRead += audioRecord.read(buffer, 0, bufferSize);
        }

        System.out.println(">>> " + bytesRead + " bytes read");

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
