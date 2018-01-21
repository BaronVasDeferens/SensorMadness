package skot.sensormadness;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by skot on 1/20/18.
 */

public class SoundPlayer extends Thread {


    private byte[] buffer;
    private int sampleRate;
    private AudioTrack audioTrack = null;
    private boolean continuePlaying = false;

    public SoundPlayer(final byte [] buffer, final int sampleRate) {
        this.sampleRate = sampleRate;
        this.buffer = buffer;
    }


    public void run() {

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                buffer.length,
                AudioTrack.MODE_STREAM);

        System.out.println(">>> STATE =" + audioTrack.getState());


        audioTrack.write(buffer, 0, buffer.length);
        audioTrack.play();


        System.out.println(">>> ALL DONE!");
    }

    public synchronized void stopPlaying() {
        audioTrack.stop();
        audioTrack.release();
        continuePlaying = false;
    }

}

