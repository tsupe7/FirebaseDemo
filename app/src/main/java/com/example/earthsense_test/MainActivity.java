package com.example.earthsense_test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.earthsense_test.adapter.PersonAdapter;
import com.example.earthsense_test.databinding.ActivityMainBinding;
import com.example.earthsense_test.databinding.InfoWindowBinding;
import com.example.earthsense_test.model.Persons;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PersonAdapter.OnItemClick {

    private ActivityMainBinding binding;
    private DatabaseReference mDatabase;
    private String TAG = "MainActivity.class";
    private List<Persons> personsList;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        personsList = new ArrayList<Persons>();
        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("interview");
        addPostEventListener(mDatabase);
    }

    private void addPostEventListener(DatabaseReference mPostReference) {

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dsInterview : dataSnapshot.getChildren()){
                    for(DataSnapshot dssPersons : dsInterview.getChildren()) {
                        Persons person = dssPersons.getValue(Persons.class);
                        personsList.add(person);
                    }
                }

                PersonAdapter adapter = new PersonAdapter(personsList, MainActivity.this);
                binding.rvPeople.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                binding.rvPeople.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        };
        mPostReference.addValueEventListener(postListener);

    }
    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }

    @Override
    public void onPersonClick(Persons persons) {
        showLocationInfo(persons);
        this.googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
               return null;
            }

            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                InfoWindowBinding infoWindowBinding = InfoWindowBinding.inflate(getLayoutInflater());
                infoWindowBinding.tvTitle.setText(marker.getTitle());
                infoWindowBinding.tvDescription.setText(marker.getSnippet());
                return infoWindowBinding.getRoot();
            }
        });
    }

    void showLocationInfo(Persons persons){

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(persons.getLocation().getLatitude(),persons.getLocation().getLongitude())
                , 15F));
        MarkerOptions markerOption =new MarkerOptions().title(persons.getName())
                .position(new LatLng(persons.getLocation().getLatitude(),persons.getLocation().getLongitude()))
                .snippet(getString(R.string.credits)+" : "+persons.getCredits());

        Marker marker = this.googleMap.addMarker(markerOption);
        marker.showInfoWindow();
    }
}