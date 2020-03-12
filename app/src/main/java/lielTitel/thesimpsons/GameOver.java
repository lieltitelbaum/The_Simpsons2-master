package lielTitel.thesimpsons;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

public class GameOver extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private TextView scoreLabel;
    private EditText personName,personId;
    private MediaPlayer gameOverSong;
    private Button replay, scores, submit;


    private MySharedPreferences pref;
    private String json;

    private String name;
    private int score;
    private PersonList personList;
    private Person person;
    private double latitude,longitude;

    //Location
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        requestPermissionn();
        gameOverSong = MediaPlayer.create(GameOver.this, R.raw.game_over);
        gameOverSong.start();

        scoreLabel = findViewById(R.id.scoreGameOver);
        personName = findViewById(R.id.personName);
        replay = findViewById(R.id.replay);
        scores = findViewById(R.id.highScores);
        submit = findViewById(R.id.send);

        //location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation();

        pref = new MySharedPreferences(this);
        json = pref.getString(Constants.PREFS_PERSON_LIST, "");
        Log.d("vvvGameOver", "json: " + json);
        if (json.compareTo("") == 0)
        {
            personList = new PersonList();
            Log.d("vvvGameOver", "created new PersonList");
        }
        else {
            personList = new Gson().fromJson(json, PersonList.class);
            Log.d("vvvGameOver", "Got List!");
            Log.d("vvvGameOver", "name: " + personList.getPersonList().get(0).getName());
        }
//        loadData();
        person = new Person("",0,0,0);
        //user won't see this buttons until he'll submit his info
        replay.setVisibility(View.INVISIBLE);
        scores.setVisibility(View.INVISIBLE);

        score = getIntent().getIntExtra(Constants.KEY_SCORE, 0);
        person.setScore(score);
        scoreLabel.setText("score : " + score);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = personName.getText().toString();

                person.setName(name);
                personList.addPerson(person);

                replay.setVisibility(View.VISIBLE);
                scores.setVisibility(View.VISIBLE);
                personName.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.INVISIBLE);

                saveData();
            }
        });

        scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameOverSong.release();
                Intent myInent = new Intent(GameOver.this, ListAndMap.class);
                myInent.putExtra(Constants.KEY_JSON, json);
                startActivity(myInent);
            }
        });
    }

    private void getLastKnownLocation() {
        //permissions check:
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();

                        person.setLatitude(latitude);
                        person.setLongitude(longitude);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void saveData() {
        json = new Gson().toJson(personList);
        pref.putString(Constants.PREFS_PERSON_LIST, json);
    }

    public void startGame(View view)
    {
        gameOverSong.release();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void requestPermissionn() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

}