package lielTitel.thesimpsons;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ScoresScreen extends Fragment  {
    private CallBackList callBackList;

//    private HighScore highScore;
    private View view = null;
    private PersonList personList;

    //SharedPreferences
    private MySharedPreferences pref;
    private String json;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_scores_screen, container, false);
        }
        //new part
        pref = new MySharedPreferences(view.getContext());
        json = pref.getString(Constants.PREFS_PERSON_LIST, "");


        if (json.compareTo("") == 0) {
            personList = new PersonList();
        } else {
            personList = new Gson().fromJson(json, PersonList.class);
        }

        RecyclerView recyclerView = view.findViewById(R.id.rvPersons);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(personList.getPersonList(), inflater.getContext());
        adapter.setClickListener(itemClickListener);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public RecyclerViewAdapter.ItemClickListener itemClickListener = new RecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            double lat = personList.getPersonList().get(position).getLatitude();
            double longi =personList.getPersonList().get(position).getLongitude();
            setLocation(new LatLng(lat, longi));
        }
    };

    public void setCallback(CallBackList callback) {
        this.callBackList = callback;
    }

    public void setLocation(LatLng location) {
        callBackList.setMapLocation(location);
    }
}
