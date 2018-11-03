package com.example.cristian.enrutarposicionesmapa;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnMarkerClickListener{

    private static final String TAG = "ActividadPrincipal";

    private boolean mostarTurorial;

    private Button btnTrazarRutaOptima;
    private FloatingActionButton btnAddPos, btnTrazarRutaUnica;
    private ProgressBar progressRutaOptima;
    private MapView mapView;
    private GoogleMap mapa;
    private TextView txtPosNoDisponible;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient locationClient;
    private Location posicion;
    private ArrayList<Marker> marcadores = new ArrayList<>();
    private boolean modoAddPos;// Modo esperando a que toquen mapa para agregar un marcador
    private ServicioWeb servicioWeb;
    private Gson gson;

    private String destinoRutaUnica;

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {// si gps esta activo
            Toast.makeText(this, "Por favor activar GPS", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mostarTurorial = true;
        modoAddPos = false;

        progressRutaOptima = findViewById(R.id.progress_ruta_optima);
        btnTrazarRutaOptima = findViewById(R.id.btn_trazar_ruta_optima);
        btnTrazarRutaUnica = findViewById(R.id.btn_trazar_ruta_unica);
        btnAddPos = findViewById(R.id.btn_add_pos);
        mapView = findViewById(R.id.map_view);
        txtPosNoDisponible = findViewById(R.id.txt_pos_no_disponible);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationClient.requestLocationUpdates(locationRequest, new PosicionCallback(), null);
        }

        btnTrazarRutaOptima.setOnClickListener(this);
        btnAddPos.setOnClickListener(this);
        btnTrazarRutaUnica.setOnClickListener(this);

        // Inicializamos Retrofit y el servicio web para consultar las rutas al API de Google
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( Utils.Constantes.BASE_URL_RUTAS )// URL de google maps
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        servicioWeb = retrofit.create(ServicioWeb.class);

        gson = new Gson();// Será utilizado para los logs de objetos
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        validarPermisos();

        mapa = googleMap;
        mapa.setOnMapClickListener(this);
        mapa.setOnInfoWindowLongClickListener(this);
        mapa.setOnMarkerClickListener(this);

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
                locationClient.requestLocationUpdates(locationRequest, new PosicionCallback(), null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_trazar_ruta_optima:

                break;
            case R.id.btn_add_pos:
                modoAddPos = true;
                btnAddPos.hide();

                // Mostramos solo la primera vez que se le de click
                if ( mostarTurorial) {
                    Toast.makeText(this, "Toque el mapa para agregar marcador", Toast.LENGTH_LONG).show();
                    mostarTurorial = false;
                }

                break;
            case R.id.btn_trazar_ruta_unica:

                pedirRuta(destinoRutaUnica);

                btnTrazarRutaUnica.hide();
                break;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if( modoAddPos ) {
            String index = String.valueOf( (marcadores.size() + 1) );

            Marker marcador = mapa.addMarker(
                    new MarkerOptions()
                            .title( index )
                            .position(latLng)
            );

            marcador.setTag(index);

            marcadores.add(marcador);

            // Termina el modo para agregar marcador
            modoAddPos = false;
            btnAddPos.show();
        }
    }

    @Override
    public void onInfoWindowLongClick(final Marker marker) {

        Dialog dialog = new AlertDialog.Builder(this)
                .setMessage("¿Seguro que quiere eliminar el marcker "+marker.getTag()+"?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marcadores.remove(marker);
                        marker.remove();// lo quitamos del mapa

                        if( !MainActivity.this.isFinishing() )
                            dialog.dismiss();
                    }
                })
                .setNegativeButton("Ahora no joven", null)
                .create();

        dialog.show();

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        btnTrazarRutaUnica.show();

        // El destino que buscaremos al dar click en btnTrazarRutaUnica
        destinoRutaUnica = marker.getPosition().latitude + "," + marker.getPosition().longitude;

        return false;
    }

    private class PosicionCallback extends LocationCallback{

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);

            if( locationAvailability.isLocationAvailable() ) {
                txtPosNoDisponible.setVisibility(View.GONE);
            } else {
                txtPosNoDisponible.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if( txtPosNoDisponible.getVisibility() == View.VISIBLE )
                txtPosNoDisponible.setVisibility(View.GONE);

            posicion =  locationResult.getLastLocation();
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

    private void pedirRuta(String destino){
        // El origen es la posición actual del dispositivo
        String origen = posicion.getLatitude() + "," + posicion.getLongitude();

        Call<ResRutas> resCall = servicioWeb.pedirRuta(origen, destino, getString(R.string.llave_api_google_maps));

        resCall.enqueue(new Callback<ResRutas>() {
            @Override
            public void onResponse(Call<ResRutas> call, Response<ResRutas> response) {

                if( response.isSuccessful() ) {
                    ResRutas resRutas = response.body();
                    Log.v(TAG, "ResRutas: "+gson.toJson(resRutas));

                    if( resRutas != null && resRutas.getStatus().equals("OK") ) {

                        if( !resRutas.getRoutes().isEmpty() ){
                            // Tomamos la polilinea de la primera ruta
                            ArrayList<LatLng> rutaLatLng = Utils.decodeOverviewPolyLinePonts(
                                    resRutas.getRoutes().get(0).getOverviewPolyline().getPoints()
                            );

                            if( rutaLatLng != null ) {

                                PolylineOptions polyOpt = new PolylineOptions();
                                polyOpt.color( Color.parseColor("#9c27b0") );
                                polyOpt.width(5);
                                polyOpt.addAll(rutaLatLng);

                                Polyline polyRutaUnica = mapa.addPolyline(polyOpt);

                            }else
                                Toast.makeText(MainActivity.this, "Error al decodificar la ruta", Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(MainActivity.this, "No se encontraron rutas", Toast.LENGTH_SHORT).show();
                    } else {
                        if( resRutas != null && resRutas.getStatus().equals("OVER_QUERY_LIMIT") ) {
                            Toast.makeText(MainActivity.this, "Limite de peticiones gratuitas diarias superado para el API de Google", Toast.LENGTH_LONG).show();
                        } else if( resRutas != null && resRutas.getStatus().equals("REQUEST_DENIED") ) {
                            Toast.makeText(MainActivity.this, "Petición denegada, revise la llave de la API", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Ruta nula o estado no OK", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Pedir ruta no fue exitosa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResRutas> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "Ocurrió un error al pedir la ruta", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
