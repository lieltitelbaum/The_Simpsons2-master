package lielTitel.thesimpsons;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.invoke.MutableCallSite;

public class ListAndMap extends FragmentActivity implements OnMapReadyCallback {

    //table fragment
    private ScoresScreen scoresScreen;
    private String json;
    private MySharedPreferences pref;

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_and_map);

        pref = new MySharedPreferences(this);
        scoresScreen = new ScoresScreen();

        // connect the fragment to the map
        scoresScreen.setCallback(myCallback);

        json = pref.getString(Constants.PREFS_PERSON_LIST, "");

        //begin Transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.tableFrag, scoresScreen);
        fragmentTransaction.commit();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a marker in telAviv and move the camera
        LatLng telAviv = new LatLng(32.065519, 34.777028);
        //need to change latlng
        this.googleMap.addMarker(new MarkerOptions().position(telAviv).title("Marker in Rothschild Blv"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(telAviv, 8f));
        this.googleMap.setMyLocationEnabled(true);//blue dot circle in the map
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);//get rid of the button that take you back to your location
    }

    CallBackList myCallback = new CallBackList() {
        @Override
        public void setMapLocation(LatLng location) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(location));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
    };
}
