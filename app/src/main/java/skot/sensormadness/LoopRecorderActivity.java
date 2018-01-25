package skot.sensormadness;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class LoopRecorderActivity extends AppCompatActivity implements LoopTrackFragment.OnFragmentInteractionListener {

    List<LoopTrackFragment> loopTracks;
    final int totalLoopTracks = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop_recorder);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction trans = fragmentManager.beginTransaction();

//        if (fragmentManager.getFragments() != null) {
//            for (Fragment frag : fragmentManager.getFragments()) {
//                trans.remove(frag);
//            }
//            trans.commit();
//        }

        loopTracks = new ArrayList<>(totalLoopTracks);
        for (int i = 0; i < totalLoopTracks; i++) {
            LoopTrackFragment frag = new LoopTrackFragment();
            loopTracks.add(frag);
            trans.add(R.id.mainLayout, frag, Integer.toString(i));
        }

        trans.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
