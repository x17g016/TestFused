package com.example.testfused;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSION = 1000;
    private FusedLocationProviderClient fusedLocationClient;
    private Location location;
    private Marker mm = null;
    Toast toast; //デバック用

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Android 6, API 23以上でパーミッションの確認
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            };
            checkPermission(permissions, REQUEST_CODE);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = new LocationRequest();

        //どれか一つを選択
        locationRequest.setPriority(
//              LocationRequest.PRIORITY_HIGH_ACCURACY);  //高精度の位置情報を取得
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);  //バッテリー消費を抑えたい場合、精度は100m程度
//              LocationRequest.PRIORITY_LOW_POWER);              //バッテリー消費を抑えたい場合、精度は10km
//              LocationRequest.PRIORITY_NO_POWER);               //位置情報取得をアプリが自ら測位しない

        getLastLocation();
    }

    //最新の位置情報の取得(nullが返ってくる可能性)
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                //アクセスが成功したら
                                if (task.isSuccessful() && task.getResult() != null) {
                                    location = task.getResult();
                                    toast = Toast.makeText(MapsActivity.this, "緯度" + location.getLatitude() + "\n" + "経度" + location.getLongitude(), Toast.LENGTH_LONG);
                                    toast.show();
                                    Log.d("debug", "緯度" + location.getLatitude() + "\n" + "経度" + location.getLongitude());
                                    Log.d("debug", "計測成功");
                                } else {
                                    toast = Toast.makeText(MapsActivity.this, "計測不能", Toast.LENGTH_LONG);
                                    toast.show();
                                    Log.d("debug", "計測失敗");
                                }
                            }
                        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("debug", "計測開始");
        LatLng spot = new LatLng(35.7044997, 139.9843911);
        mm = mMap.addMarker(
                new MarkerOptions()
                        .position(spot)
                        .title("Marker in FJB")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.smile1)
                        ));
        mMap.clear();
        LatLng spot2 = new LatLng(35.7044800, 139.9843800);
        mm = mMap.addMarker(
                new MarkerOptions()
                        .position(spot2)
                        .title("Marker in TIGA")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.smile1)
                        ));
        LatLng spot3 = new LatLng(35.7044400, 139.9843400);
        mm = mMap.addMarker(
                new MarkerOptions()
                        .position(spot3)
                        .title("Marker in TIT")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.smile1)
                        ));
        // マーカークリック時のイベントハンドラ登録
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO Auto-generated method stub
                String id = marker.getId();
                String msg = "IDは" + id;
                /*if (id.equals("m0")) {
                    msg = "関西国際空港(" + id + ")";
                } else if (id.equals("m1")) {
                    msg = "伊丹空港(" + id + ")";
                } else if (id.equals("m2")) {
                    msg = "神戸空港(" + id + ")";
                }*/
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                return false;
            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLng(spot));
        //マップのズーム絶対値指定　1: 世界 5: 大陸 10:都市 15:街路 20:建物 ぐらいのサイズ
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15F));
    }

    //許可されていないパーミッションリクエスト
    public void checkPermission(final String[] permissions, final int request_code) {
        ActivityCompat.requestPermissions(this, permissions, request_code);
    }

    //パーミッション結果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    Log.d("Permission", "Added Permission: " + permissions[i]);
                } else {
                    // パーミッションが拒否された
                    Log.d("Permission", "Rejected Permission: " + permissions[i]);
                }
            }
        }
    }


}
