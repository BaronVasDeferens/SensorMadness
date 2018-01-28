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

    private void reset() {
        audioTrack.setPlaybackHeadPosition(playbackStart);
        audioTrack.setNotificationMarkerPosition(playbackEnd);
        audioTrack.setLoopPoints(loopStart, loopEnd, loopingMode ? -1 : 0);
    }

    public synchronized void setPlaybackRate(int percent) {
        sampleRate = (int) (44100 * ((float) percent / 100f));
        audioTrack.setPlaybackRate(sampleRate);
    }

    public void playSound() {

        System.out.println(">>> SoundPlayer.playSound()...");
        audioTrack.pause();
        reset();
        audioTrack.play();
    }

    public void setToLoop(final int loopStart, final int loopEnd) {
        this.loopStart = loopStart;
        this.loopEnd = loopEnd;
        System.out.println(">>> SET TO LOOP START (" + loopStart + ") END (" + loopEnd + ")");
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

    private void analyzeSoundData() {
        byte b;


    }

    public PlaybackParams getPlaybackParams() {
        return audioTrack.getPlaybackParams();
    }

    public void setPlaybackParams(PlaybackParams params) {
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

