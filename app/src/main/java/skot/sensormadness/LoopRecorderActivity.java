package skot.sensormadness;

import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class LoopRecorderActivity extends AppCompatActivity implements LoopTrackFragment.OnFragmentInteractionListener {

    LoopTrackFragment trackOne, trackTwo, trackThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop_recorder);

        trackOne = new LoopTrackFragment();
        trackTwo = new LoopTrackFragment();
        trackThree = new LoopTrackFragment();

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.add(R.id.mainLayout, trackOne);
        trans.add(R.id.mainLayout, trackTwo);
        trans.add(R.id.mainLayout, trackThree);

        trans.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
