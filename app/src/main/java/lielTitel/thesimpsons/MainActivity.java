package lielTitel.thesimpsons;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer openSong;
    private CheckBox checkSensors;
    private int isSensorsChecked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = findViewById(R.id.startBtn);
        Button startFiveLanes = findViewById(R.id.fiveLanesBtn);
        Button topScores = findViewById(R.id.highScores);
        checkSensors = findViewById(R.id.sensorOrBtn);

        openSong = MediaPlayer.create(MainActivity.this, R.raw.start_music);
        openSong.start();
        checkSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSensors.isChecked())
                    isSensorsChecked = 1;
                else
                    isSensorsChecked = 0;
            }
        });


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSong.release();
                Intent intent = new Intent(v.getContext(), MainGame.class);
                v.getContext().startActivity(intent);
            }
        });

        topScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSong.release();
                Intent myInent = new Intent(MainActivity.this, ListAndMap.class);
                startActivity(myInent);
            }
        });
        startFiveLanes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openSong.release();
                Intent intent = new Intent(v.getContext(), FiveLanes.class);
                intent.putExtra(Constants.KEY_IS_SENSOR,isSensorsChecked);
                v.getContext().startActivity(intent);
            }
        });
    }
    //if user press back in the welcome screen
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event){
//        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
//            finish();
//            System.exit(0);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}

