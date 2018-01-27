package skot.sensormadness;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import static android.content.Context.VIBRATOR_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoopTrackFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoopTrackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoopTrackFragment
        extends android.support.v4.app.Fragment
        implements View.OnTouchListener,
        PlaybackCompleteListener, SeekBar.OnSeekBarChangeListener, View.OnLongClickListener

{

    public static final int EVENT_STOPPED_PLAYING = 1;

    Handler handler;

    RecordingThread recThread = null;
    SoundPlayer soundPlayer = null;

    private final int sampleRate = 22050;       // 44100
    private final int bufferSize = 50000;

    Button recButton, playButton;
    Switch loopSwitch;
    SeekBar speedAdjust;
    EditText startLoop, stopLoop;

    private boolean nowRecording = false;
    private boolean nowPlaying = false;
    private boolean isLoopMode = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LoopTrackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoopTrackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoopTrackFragment newInstance(String param1, String param2) {
        LoopTrackFragment fragment = new LoopTrackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                updateDisplay(msg);
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loop_track, container, false);
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        recButton = (Button) getView().findViewById(R.id.recButton);
        recButton.setOnTouchListener(this);
        enableButton(recButton);

        playButton = (Button) getView().findViewById(R.id.playButton);
        playButton.setOnTouchListener(this);
        playButton.setOnLongClickListener(this);
        disableButton(playButton);

        loopSwitch = (Switch) getView().findViewById(R.id.loopSwitch);
        loopSwitch.setOnTouchListener(this);

        speedAdjust = (SeekBar) getView().findViewById(R.id.speedAdjust);
        speedAdjust.setOnSeekBarChangeListener(this);

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (soundPlayer != null)
            soundPlayer.releaseResources();
        if (recThread != null)
            recThread.releaseResources();
        mListener = null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {

            String tag = (String) view.getTag();

            switch (tag) {
                case "recButton":
                    controlRecording();
                    break;
                case "playButton":
                    controlPlayback();
                    break;
                case "loopSwitch":
                    toggleLoop();
                    break;
                default:
                    break;
            }
        }
        return false;
    }


    private void controlRecording() {

        if ((!nowRecording) && (!nowPlaying)) {
            speedAdjust.setProgress(100);
            enableButtonActive(recButton);
            disableButton(playButton);
            nowRecording = true;
            recThread = new RecordingThread(bufferSize, sampleRate);
            recThread.start();
        } else if (!nowPlaying) {
            enableButton(recButton);
            enableButton(playButton);
            nowRecording = false;
            recThread.stopRecording();
            soundPlayer = null;
        }
    }

    private void controlPlayback() {

        System.out.println(">>> nowPlaying = " + nowPlaying);
        System.out.println(">>> nowRecording = " + nowRecording);

        if (!nowRecording && !nowPlaying) {

            if (recThread == null)
                return;

            if (soundPlayer == null) {
                soundPlayer = new SoundPlayer(this, recThread.getBuffer(), sampleRate);
                soundPlayer.init();
            }

            disableButton(recButton);
            enableButtonActive(playButton);
            nowPlaying = true;
            soundPlayer.playSound();
            vibrate();
        } else if (nowPlaying && !isLoopMode) {
            // Re-trigger the sound from the beginning
            disableButton(recButton);
            soundPlayer.playSound();
            vibrate();
        } else if (nowPlaying && isLoopMode) {
            enableButton(recButton);
            enableButton(playButton);
            nowPlaying = false;
            soundPlayer.stopPlaying();
        }
    }

    private void toggleLoop() {
        isLoopMode = !isLoopMode;

        System.out.println("isLoopMode = " + isLoopMode);

        if (isLoopMode) {

            EditText startBox = (EditText) getView().findViewById(R.id.loopStart);
            EditText endBox = (EditText) getView().findViewById(R.id.loopEnd);

            int loopStart = Integer.parseInt(startBox.getText().toString());
            int loopEnd = Integer.parseInt(endBox.getText().toString());
            soundPlayer.setToLoop(loopStart, loopEnd);
        } else {
            soundPlayer.setToOneshot();
        }
    }

    private void updateDisplay(Message msg) {

    }

    private void vibrate() {
        Vibrator steelyDan = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        steelyDan.vibrate(200);
    }

    private void disableButton(final Button button) {
        button.setBackgroundColor(Color.GRAY);
        button.setClickable(false);
    }

    private void enableButton(final Button button) {
        button.setBackgroundColor(Color.BLUE);
        button.setClickable(true);
    }

    private void enableButtonActive(final Button button) {
        button.setBackgroundColor(Color.RED);
        button.setClickable(true);
    }

    @Override
    public void onPlaybackComplete() {
        nowPlaying = false;
        enableButton(playButton);
        enableButton(recButton);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (soundPlayer != null) {
//            soundPlayer.setPlaybackRate(i);
            PlaybackParams params = soundPlayer.getPlaybackParams();
            if (params != null) {

                // TODO make this configurable via long-press
                params.setPitch((i / 100f));
//            params.setSpeed(i/100f);
                soundPlayer.setPlaybackParams(params);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onLongClick(View view) {
        System.out.println(">>> LONG CLICK!");
        return false;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
