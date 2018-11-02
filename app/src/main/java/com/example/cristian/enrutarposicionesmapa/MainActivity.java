package com.example.cristian.enrutarposicionesmapa;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button btnTrazarRutaOptima;
    private ProgressBar progressRutaOptima;
    private MapView mapView;
    private GoogleMap mapa;

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        if(locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {// si gps esta activo
            Toast.makeText(this, "Por favor activar GPS", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressRutaOptima = findViewById(R.id.progress_ruta_optima);
        btnTrazarRutaOptima = findViewById(R.id.btn_trazar_ruta_optima);
        mapView = findViewById(R.id.map_view);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        validarPermisos();

        mapa = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (validarPermisos()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mapa.setMyLocationEnabled(true);
            }
        }
    }

    private boolean validarPermisos() {
        ArrayList<String> permisos = new ArrayList<>();
        boolean estanCumplidos = true;

        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            permisos.add(Manifest.permission.ACCESS_FINE_LOCATION);
            estanCumplidos = false;
        }

        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            permisos.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            estanCumplidos = false;
        }

        if( !permisos.isEmpty() ) {
            String[] permisosArray = permisos.toArray(new String[permisos.size()]);

            ActivityCompat.requestPermissions(this, permisosArray, 432);
        }

        return estanCumplidos;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
