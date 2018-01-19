package skot.sensormadness;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by skot on 1/15/18.
 */

public class TonePlayer extends Thread implements AudioTrack.OnPlaybackPositionUpdateListener{

    private double freqOfTone = 440; // hz

    private final float duration = 1.0f; // seconds
    private final int sampleRate = 16000;
    private final int numSamples = (int) (duration * sampleRate);
    private double sample[] = new double[numSamples];

    private byte generatedSnd[] = new byte[2 * numSamples];

    private AudioTrack audioTrack;
    private boolean continuePlaying = false;

    public TonePlayer() {

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                generatedSnd.length,
                AudioTrack.MODE_STATIC);

        System.out.println(">>> AUDIO BUFFER (in frames) : " + audioTrack.getBufferSizeInFrames());
        audioTrack.setLoopPoints(0,4000, -1);
        genTone();
        continuePlaying = true;
    }

    void genTone() {

        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;

        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
    }

    public synchronized void adjustFreq(final double adjustment) {
        freqOfTone = freqOfTone + adjustment;
    }

    public synchronized void setTone(int toneInHz) {
        freqOfTone = (double) toneInHz;
    }

    public synchronized void stopPlaying() {
        continuePlaying = false;
        audioTrack.stop();
    }

    public void run() {
        audioTrack.play();

        while (continuePlaying) {
            genTone();
        }

        System.out.println(">>> ALL DONE!");

    }

    @Override
    public void onMarkerReached(AudioTrack audioTrack) {

    }

    @Override
    public void onPeriodicNotification(AudioTrack audioTrack) {

    }
}
