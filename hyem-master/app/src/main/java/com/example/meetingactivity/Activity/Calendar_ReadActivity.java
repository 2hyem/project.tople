package com.example.meetingactivity.Activity;

import android.Manifest;
import android.animation.Animator;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.meetingactivity.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Calendar_ReadActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    Animation fb_open, fb_close;
    FloatingActionButton fab_main, fab_sub1, fab_sub2, fab_sub3;
    LinearLayout fabLayout_main, fabLayout_sub1, fabLayout_sub2, fabLayout_sub3;
    Boolean isFabOnOff = false;

    GoogleMap map;
    SupportMapFragment mapFragment;
    boolean permissionCK = false;    // 퍼미션 결과 저장
    private Marker currentMarker = null;
    Double lat, lon;    // 좌표
    String location;    // 검새 이름
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar__read);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fb_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fb_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab_main = (FloatingActionButton) findViewById(R.id.fab_main);
        fab_sub1 = (FloatingActionButton) findViewById(R.id.fab_sub1);
        fab_sub2 = (FloatingActionButton) findViewById(R.id.fab_sub2);
        fab_sub3 = (FloatingActionButton) findViewById(R.id.fab_sub3);
        fabLayout_main = (LinearLayout) findViewById(R.id.fabLayout_main);
        fabLayout_sub1 = (LinearLayout) findViewById(R.id.fabLayout_sub1);
        fabLayout_sub2 = (LinearLayout) findViewById(R.id.fabLayout_sub2);
        fabLayout_sub3 = (LinearLayout) findViewById(R.id.fabLayout_sub3);

        lat = getIntent().getDoubleExtra("lat", 0);
        lon = getIntent().getDoubleExtra("lon", 0);
        location = getIntent().getStringExtra("location");
        Log.d("[test]","여기 실행1");
        // 지도
        mapFragment.getMapAsync(this);

        fab_main.setOnClickListener(this);
        fab_sub1.setOnClickListener(this);
        fab_sub2.setOnClickListener(this);
        fab_sub3.setOnClickListener(this);

        permissionCheck();
    }

    private void permissionCheck() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
        } else {
            permissionCK = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_main:
                if (!isFabOnOff) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
                break;
        }
    }

    private void closeFABMenu() {
        isFabOnOff = false;
        fabLayout_sub1.setVisibility(View.GONE);
        fabLayout_sub2.setVisibility(View.GONE);
        fabLayout_sub3.setVisibility(View.GONE);
        fab_main.animate().rotationBy(45);
        fabLayout_sub1.animate().translationY(-55);
        fabLayout_sub2.animate().translationY(-100);
        fabLayout_sub3.animate().translationY(-145);
    }

    private void showFABMenu() {
        isFabOnOff = true;
        fab_main.animate().rotationBy(-45);
        fabLayout_sub1.animate().translationY(0);
        fabLayout_sub2.animate().translationY(0);
        fabLayout_sub3.animate().translationY(0);
        fabLayout_sub3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (isFabOnOff) {
                    fabLayout_sub1.setVisibility(View.VISIBLE);
                    fabLayout_sub2.setVisibility(View.VISIBLE);
                    fabLayout_sub3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Log.d("[test]", "read -> lat : " + lat + " / lon : " + lon);
        LatLng latLng = new LatLng(lat, lon);
        // 해당 위치로 이동
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // 마크 찍기
        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng).title(location);
        map.addMarker(marker).showInfoWindow();
        // 카메라 줌
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }
}
