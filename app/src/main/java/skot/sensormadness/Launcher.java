package skot.sensormadness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Launcher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
    }


    public void launchTiltTone(View v) {
        startActivity(new Intent(this, TiltTone.class));
    }

    public void launchMicRecorder(View v) {
        startActivity(new Intent(this, LoopRecorderActivity.class));
    }

}
