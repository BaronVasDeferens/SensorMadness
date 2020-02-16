package skot.sensormadness;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.PlaybackParams;

/**
 * Created by skot on 1/20/18.
 */

public class SoundPlayer implements AudioTrack.OnPlaybackPositionUpdateListener {

    private byte[] buffer;
    private int sampleRate;
    private AudioTrack audioTrack = null;
    private boolean loopingMode = false;
    private int playbackStart;
    private int playbackEnd;
    private int loopStart;
    private int loopEnd;
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

        playbackStart = 0;
        playbackEnd = buffer.length - 1;
        loopStart = 0;
        loopEnd = buffer.length - 1;

        audioTrack.setPlaybackPositionUpdateListener(this);
    }

    public void init() {
        audioTrack.write(buffer, 0, buffer.length);
        reset();
    }

    private synchronized void reset() {
        audioTrack.pause();
        audioTrack.setPlaybackHeadPosition(playbackStart);
        audioTrack.setNotificationMarkerPosition(playbackEnd);
        audioTrack.setLoopPoints(loopStart, loopEnd, loopingMode ? -1 : 0);
    }

    public synchronized void setPlaybackRate(final float percent) {
        sampleRate = (int) (44100 * percent);
        System.out.println("sampleRate = " + sampleRate);
        audioTrack.setPlaybackRate(sampleRate);
    }

    public synchronized void playSound() {

        System.out.println(">>> SoundPlayer.playSound()...");
        audioTrack.pause();
        reset();
        audioTrack.play();
    }


    public void setVolume(float v) {
        audioTrack.setVolume(v);
    }

    public void setSampleStart(final float percentPosition) {
        playbackStart = (int)(percentPosition * buffer.length);
        System.out.println("percentPosition = " + percentPosition);
        System.out.println("playbackStart = " + playbackStart);
        reset();
    }

    public void setLoopStart(final float percentPosition) {
        loopingMode = true;
        loopStart = (int)(percentPosition * buffer.length);
        setSampleStart(loopStart);
        //audioTrack.setPlaybackHeadPosition((int)(percentPosition * buffer.length));
        reset();
    }

    public void setLoopEnd(final float percentPosition) {
        loopingMode = true;
        this.loopEnd = (int)(percentPosition * buffer.length);
        reset();
    }


    public void setToLoop() {
        loopingMode = true;
        reset();
    }

    public void setToOneshot() {
        System.out.println(">>> SET TO ONE-SHOT");
        loopStart = 0;
        loopEnd = buffer.length - 1;
        reset();
        loopingMode = false;
    }


    public synchronized void stopPlaying() {
        audioTrack.pause();
        reset();
    }

    public synchronized PlaybackParams getPlaybackParams() {
        return audioTrack.getPlaybackParams();
    }

    public synchronized void setPlaybackParams(PlaybackParams params) {
        if (audioTrack != null) {
            audioTrack.setPlaybackParams(params);
        }
    }

    public void releaseResources() {

        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }

        buffer = null;
    }


    @Override
    public void onMarkerReached(AudioTrack audioTrack) {
        if (!loopingMode) {
            System.out.println(">>> MARKER REACHED (" + playbackEnd + ")");
            listener.onPlaybackComplete();
        }
    }

    @Override
    public void onPeriodicNotification(AudioTrack audioTrack) {

    }


}

