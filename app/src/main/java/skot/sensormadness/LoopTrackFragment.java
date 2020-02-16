package skot.sensormadness;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
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
        PlaybackCompleteListener,
        SeekBar.OnSeekBarChangeListener,
        View.OnLongClickListener,
        AdapterView.OnItemSelectedListener

{

    public static final int EVENT_STOPPED_PLAYING = 1;


    public enum SeekBarMode {
        VOLUME, SAMPLE_RATE, LOOP_START, LOOP_END, SAMPLE_START, SAMPLE_END
    }

    private SeekBarMode currentSeekBarMode = SeekBarMode.SAMPLE_START;

    Handler handler;

    RecordingThread recThread = null;
    SoundPlayer soundPlayer = null;

    private final int sampleRate = 22050;       // 44100
    private final int bufferSize = 50000;

    Button recButton, playButton;
    Spinner seekBarModeSelect;
    Switch loopSwitch;
    SeekBar speedAdjust;
    DataGraphView dataGraphView;

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

        seekBarModeSelect = (Spinner) getView().findViewById(R.id.seekBarModeSpinner);
        seekBarModeSelect.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.seekBarModes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seekBarModeSelect.setAdapter(adapter);

        loopSwitch = (Switch) getView().findViewById(R.id.loopSwitch);
        loopSwitch.setOnTouchListener(this);

        speedAdjust = (SeekBar) getView().findViewById(R.id.speedAdjust);
        speedAdjust.setOnSeekBarChangeListener(this);

        dataGraphView = (DataGraphView) getView().findViewById(R.id.dataGraph);
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
        System.out.println(">>> RELEASING RESOURCES...");
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
            speedAdjust.setProgress(50);
            enableButtonActive(recButton);
            disableButton(playButton);
            nowRecording = true;
            recThread = new RecordingThread(bufferSize, sampleRate, handler);
            recThread.start();
        } else if (!nowPlaying) {
            enableButton(recButton);
            enableButton(playButton);
            nowRecording = false;
            recThread.stopRecording();
            dataGraphView.setData(recThread.getBuffer());
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
                dataGraphView.setMarkerPosition(0);
            }

            disableButton(recButton);
            enableButtonActive(playButton);
            nowPlaying = true;
            soundPlayer.playSound();
            dataGraphView.invalidate();
            vibrate();
        } else if (nowPlaying && !isLoopMode) {
            // Re-trigger the sound from the beginning
            disableButton(recButton);
            soundPlayer.playSound();
            vibrate();
        } else if (nowPlaying) {
            // Looping player stops when pressed again
            enableButton(recButton);
            enableButton(playButton);
            nowPlaying = false;
            soundPlayer.stopPlaying();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        if (soundPlayer == null)
            return;

        boolean refreshDataGraph = true;

        switch (currentSeekBarMode) {
            case VOLUME:
                soundPlayer.setVolume(i / 100f);
                refreshDataGraph = false;
                break;
            case SAMPLE_RATE:
                soundPlayer.setPlaybackRate(i / 100f);
                refreshDataGraph = false;
                break;
            case SAMPLE_START:
                soundPlayer.setSampleStart(i / 100f);
                break;
            case LOOP_START:
                soundPlayer.setLoopStart(i / 100f);
                loopSwitch.setChecked(true);
                break;
            case LOOP_END:
                soundPlayer.setLoopEnd(i / 100f);

                break;
            default:
                break;
        }

        if (refreshDataGraph) {
            dataGraphView.setMarkerPosition(i);
            dataGraphView.invalidate();
        }

    }

    // Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // Determine which spinner has been selected:
        String spinnerTag = (String) parent.getTag();
        if (spinnerTag == null)
            return;

        if (spinnerTag.contentEquals("seekBarMode")) {
            final String selectedMode = (String) parent.getItemAtPosition(position);
            currentSeekBarMode = SeekBarMode.valueOf(selectedMode);
            System.out.println(">>> currentSeekBarMode = " + currentSeekBarMode);
        }

    }

    // Spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void toggleLoop() {
        isLoopMode = !isLoopMode;

        System.out.println("isLoopMode = " + isLoopMode);

        if (isLoopMode) {
            soundPlayer.setToLoop();
        } else {
            soundPlayer.setToOneshot();
        }
    }

    private void updateDisplay(Message msg) {
        RecordingThread recordingThread = (RecordingThread) msg.obj;
        dataGraphView.setData(recordingThread.getBuffer());
        dataGraphView.invalidate();
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
