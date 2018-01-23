package skot.sensormadness;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by skot on 1/20/18.
 */

public class SoundPlayer implements AudioTrack.OnPlaybackPositionUpdateListener {

    private byte[] buffer;
    private int sampleRate;
    private AudioTrack audioTrack = null;
    private PlaybackCompleteListener listener;

    public SoundPlayer(final PlaybackCompleteListener listener, final byte[] buffer, final int sampleRate) {
        this.listener = listener;
        this.sampleRate = sampleRate;
        this.buffer = buffer;

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                this.sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                this.buffer.length,
                AudioTrack.MODE_STATIC);

        audioTrack.setPlaybackPositionUpdateListener(this);
    }

    public void init() {
        audioTrack.write(buffer, 0, buffer.length);
        audioTrack.setPlaybackHeadPosition(0);
        audioTrack.setNotificationMarkerPosition(buffer.length);
    }

    public synchronized void setPlaybackRate(int percent) {
        sampleRate = (int)(44100 * ((float) percent / 100f));
        audioTrack.setPlaybackRate(sampleRate);
    }

    public void playSound() {
        if (audioTrack.getPlaybackHeadPosition() > 0) {
            audioTrack.stop();
            audioTrack.setPlaybackHeadPosition(0);
        }

        audioTrack.play();
    }

    public void setToLoop() {
        System.out.println(">>> SET TO LOOP");
        audioTrack.setLoopPoints(0, buffer.length, -1);
    }

    public void setToOneshot() {
        System.out.println(">>> SET TO ONE-SHOT");
        audioTrack.setLoopPoints(0, buffer.length, 0);
    }

    public synchronized void stopPlaying() {
        audioTrack.stop();
        audioTrack.release();
    }

    @Override
    public void onMarkerReached(AudioTrack audioTrack) {
        System.out.println(">>> MARKER!!!!");
        audioTrack.stop();
        listener.onPlaybackComplete();
    }

    @Override
    public void onPeriodicNotification(AudioTrack audioTrack) {

    }


}

