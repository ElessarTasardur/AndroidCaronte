package gal.caronte.caronte.mostrarmapa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.location.LocationListener;
import es.situm.sdk.location.LocationManager;
import es.situm.sdk.location.LocationRequest;
import es.situm.sdk.location.LocationStatus;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.model.location.Bounds;
import es.situm.sdk.model.location.Coordinate;
import es.situm.sdk.model.location.Location;
import gal.caronte.caronte.R;
import gal.caronte.caronte.custom.Edificio;
import gal.caronte.caronte.custom.MarcadorCustom;
import gal.caronte.caronte.custom.sw.Conta;
import gal.caronte.caronte.custom.sw.PuntoInterese;
import gal.caronte.caronte.util.PermisosUtil;
import gal.caronte.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 08/10/2017.
 */

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback,
//        GoogleMap.OnMyLocationButtonClickListener,
//        GoogleApiClient.ConnectionCallbacks,
//        com.google.android.gms.location.LocationListener,
//        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MapaActivity.class.getSimpleName();

    private static final int CODIGO_SOLICITUDE_PERMISO_LOCALIZACION = 1;

    //GoogleMaps
    private GoogleMap map;
    private FusedLocationProviderClient mfusedLocationProviderclient;
    private boolean googleActivado = false;

    //Servizos
    private RecuperarConta recuperarConta = new RecuperarConta();
    private RecuperarEdificio recuperarEdificioServizo;
    private RecuperarMapa recuperarMapaServizo;
    private RecuperarPoi recuperarPoi;
    private RecuperarRuta recuperarRuta;

    private Conta conta;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Circle circle;

    private Edificio edificio;
    private String idEdificio;
    private String idPlanta;
    private String idPlantaVisibel;
    private GroundOverlay imaxeOverlay;
    private Location posicionActual;

    //Lista cos marcadores creados na aplicacion, visibeis ou invisibeis.
    private List<MarcadorCustom> listaMarcadores = new ArrayList<>();

    //Menu lateral
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private SelectorPiso selectorPiso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        this.selectorPiso = new SelectorPiso(this, (LinearLayout) findViewById(R.id.layout_niveis));

        recuperarListaConta();

        ImageButton botonActualizar = findViewById(R.id.imageButtonActualizar);
        botonActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refrescarLocalizacionSitum();
            }
        });

        //Inicializamos Situm
        SitumSdk.init(this);
        this.locationManager = SitumSdk.locationManager();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.mDrawerLayout = findViewById(R.id.drawer_layout);
        this.mDrawerList = findViewById(R.id.left_drawer);

        // Set the list's click listener
        this.mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Conta contaSeleccionada = (Conta) mDrawerList.getItemAtPosition(position);

                if (!contaSeleccionada.equals(conta)) {
                    conta = contaSeleccionada;
                    Log.i(TAG, StringUtil.creaString("Seleccionada a conta: ", conta));
                    SitumSdk.configuration().setUserPass(conta.getNomeUsuario(), conta.getContrasinal());

                    refrescarLocalizacionSitum();
                }

                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void refrescarLocalizacionSitum() {
        deterChamadas();
        deterLocalizacionSitum();
        activarLocalizacionSitum();
    }

    private void deterChamadas() {
        if (recuperarEdificioServizo != null) {
            this.recuperarEdificioServizo.cancel();
        }
        if (recuperarMapaServizo != null) {
            this.recuperarMapaServizo.cancel();
        }
        if (recuperarPoi != null) {
            this.recuperarPoi.cancel(true);
        }
        if (recuperarRuta != null) {
            this.recuperarRuta.cancel();
        }
    }

    @Override
    protected void onStop() {
        if (recuperarConta != null) {
            this.recuperarConta.cancel(true);
        }
        deterChamadas();
        deterLocalizacionSitum();
        activarLocalizacionGoogle(false);
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        this.map.getUiSettings().setMapToolbarEnabled(false);
        this.map.getUiSettings().setIndoorLevelPickerEnabled(false);

        //Desactivar as opcions dos marcadores cando se fai click
        this.map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
                return true;
            }
        });

        //Comprobamos o permiso de localizacion para activar a localizacion
        boolean permisoConcedido = PermisosUtil.comprobarPermisos(this, Manifest.permission.ACCESS_FINE_LOCATION, CODIGO_SOLICITUDE_PERMISO_LOCALIZACION, true);
        if (permisoConcedido) {
            activarLocalizacionGoogle(true);
            activarLocalizacionSitum();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permisos, @NonNull int[] resultados) {
        if (requestCode != CODIGO_SOLICITUDE_PERMISO_LOCALIZACION) {
            return;
        }

        //Se o permiso foi entregado, activar a localizacion
        if (PermisosUtil.tenPermiso(permisos, resultados, Manifest.permission.ACCESS_FINE_LOCATION)) {
            activarLocalizacionGoogle(true);
            activarLocalizacionSitum();
        }
        //Se non foi concedido finalizamos a actividade
        else {
            Toast.makeText(this, R.string.permiso_necesario, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void activarLocalizacionGoogle(boolean activar) {
        //Localizacion permitida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && this.map != null) {
            Log.i(TAG, StringUtil.creaString("Activase a localizacion de Google: ", activar));
            this.googleActivado = activar;
            this.map.setMyLocationEnabled(activar);

            if (activar) {
                if (this.mfusedLocationProviderclient == null) {
                    this.mfusedLocationProviderclient = LocationServices.getFusedLocationProviderClient(this);
                }
                this.mfusedLocationProviderclient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MapaActivity.this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                        }
                    }
                });
            }

        }
    }

    private void activarLocalizacionSitum() {
        //Localizacion permitida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && this.map != null
                && this.conta != null) {
            Log.i(TAG, "Activase a localizacion de Situm");
            if (this.locationManager.isRunning()) {
                return;
            }

            if (this.locationListener == null) {
                iniciarLocationListener();
            }

            LocationRequest locationRequest = new LocationRequest.Builder()
                    .useWifi(true)
                    .useBle(true)
                    .useForegroundService(true)
                    .build();
            this.locationManager.requestLocationUpdates(locationRequest, locationListener);
        }
    }

    private void iniciarLocationListener() {

        this.locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(@NonNull Location location) {

                Log.i(TAG, "Localizacion: " + location);
                MapaActivity.this.posicionActual = location;

                //Atopamonos dentro dun edificio de Situm, desactivase Google
                if (!"-1".equals(location.getBuildingIdentifier())) {

                    if (MapaActivity.this.googleActivado) {
                        activarLocalizacionGoogle(false);
                    }

                    LatLng latLng = new LatLng(location.getCoordinate().getLatitude(), location.getCoordinate().getLongitude());
                    //Se o edificio e novo cargamos o seu mapa
                    if (MapaActivity.this.idEdificio == null
                            || !location.getBuildingIdentifier().equals(MapaActivity.this.idEdificio)) {
                        Log.i(TAG, "Edificio novo. Preparamos a carga do mesmo");
                        MapaActivity.this.idEdificio = location.getBuildingIdentifier();

                        MapaActivity.this.recuperarEdificioServizo = new RecuperarEdificio();
                        MapaActivity.this.recuperarEdificioServizo.get(new RecuperarEdificio.Callback() {
                            @Override
                            public void onSuccess(Edificio edificio) {

                                Log.i(TAG, StringUtil.creaString("Recuperado o edificio: ", edificio));
                                MapaActivity.this.edificio = edificio;

                                MapaActivity.this.selectorPiso.mostrarSelectorPiso(MapaActivity.this.edificio.getPisos(), MapaActivity.this.idPlanta);

                                recuperarMapa(MapaActivity.this.idPlanta);

                                recuperarListaPoi(edificio.getEdificio().getIdentifier());

                            }

                            @Override
                            public void onError(Error error) {
                                Toast.makeText(MapaActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }, idEdificio);

                        MapaActivity.this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));

                    }
                    //Se a planta visibel e a planta previa son iguais e non coinciden coa actual, debemos ocultar o mapa actual e mostrar o novo
                    else if (MapaActivity.this.idPlanta.equals(MapaActivity.this.idPlantaVisibel)
                            && !MapaActivity.this.idPlanta.equals(location.getFloorIdentifier())) {
                        Log.i(TAG, "Cambiamos o piso");
                        if (imaxeOverlay != null) {
                            imaxeOverlay.remove();
                        }
                        recuperarMapa(MapaActivity.this.idPlanta);
                        MapaActivity.this.selectorPiso.cambiarPisoSeleccionado(location.getFloorIdentifier());
                    }
                    else {
                        Log.i(TAG, "Non facemos nada");
                    }

                    //Se o piso actual coincide coa planta visibel, mostramos a localizacion do usuario
                    if (location.getFloorIdentifier().equals(MapaActivity.this.idPlantaVisibel)) {
                        if (circle == null) {
                            Log.i(TAG, "Pintamos novo circulo en " + latLng);
                            circle = MapaActivity.this.map.addCircle(new CircleOptions()
                                    .center(latLng)
                                    .radius(0.5d)
                                    .strokeWidth(0f)
                                    .fillColor(Color.BLUE)
                                    .zIndex(10));
                        }
                        else {
                            Log.i(TAG, "Pintamos circulo antigo en " + latLng);
                            circle.setCenter(latLng);
                        }
                    }

                    MapaActivity.this.idPlanta = location.getFloorIdentifier();

                }
                //Activamos a localizacion de Google se non o estaba xa
                else if (!MapaActivity.this.googleActivado) {
                    Toast.makeText(MapaActivity.this, "Saímos do edificio", Toast.LENGTH_LONG).show();
                    activarLocalizacionGoogle(true);
                    circle = null;
                }
            }

            @Override
            public void onStatusChanged(@NonNull LocationStatus locationStatus) {
                //Non facemos nada
            }

            @Override
            public void onError(@NonNull Error error) {
                Toast.makeText(MapaActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    public void recuperarMapa(final String idPlantaMapa) {

        this.idPlantaVisibel = idPlantaMapa;

        //Se ainda non temos o mapa, recuperamolo
        Bitmap mapaPiso = this.edificio.getMapa(idPlantaMapa);
        if (mapaPiso == null) {

            Floor floor = this.edificio.getFloor(idPlantaMapa);
            this.recuperarMapaServizo = new RecuperarMapa();
            this.recuperarMapaServizo.get(new RecuperarMapa.Callback() {
                @Override
                public void onSuccess(Bitmap mapa) {
                    edificio.setMapa(idPlantaMapa, mapa);
                    debuxarEdificio(mapa);
                }

                @Override
                public void onError(Error error) {
                    Toast.makeText(MapaActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }

            }, floor);
        }
        //Se xa o temos, mostramolo
        else {
            debuxarEdificio(mapaPiso);
        }
    }

    private void deterLocalizacionSitum() {
        //Reiniciamos variabeis
        if (this.map != null) {
            this.map.clear();
        }
        this.edificio = null;
        this.idEdificio = null;
        this.idPlanta = null;
        this.listaMarcadores.clear();

        if (this.locationManager != null) {
            if (!this.locationManager.isRunning()) {
                return;
            }
            this.locationManager.removeUpdates(locationListener);
        }
    }

    private void debuxarEdificio(Bitmap mapa) {

        Building building = this.edificio.getEdificio();

        Bounds drawBounds = building.getBounds();
        Coordinate coordinateNE = drawBounds.getNorthEast();
        Coordinate coordinateSW = drawBounds.getSouthWest();
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(coordinateSW.getLatitude(), coordinateSW.getLongitude()),
                new LatLng(coordinateNE.getLatitude(), coordinateNE.getLongitude()));

        GroundOverlayOptions gooMapa = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(mapa))
                .bearing((float) building.getRotation().degrees())
                .positionFromBounds(latLngBounds);

        //Agrega a superposición ao mapa e conserva un controlador para o obxecto GroundOverlay.
        this.imaxeOverlay = this.map.addGroundOverlay(gooMapa);

        this.map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));

        //TODO
        mostrarTodosPoiPlanta();
    }

//    @Override
//    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
////        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
//        return false;
//    }

    private void recuperarListaPoi(String idEdificioExterno) {
        this.recuperarPoi = new RecuperarPoi();
        this.recuperarPoi.setMapaActivity(this);
        this.recuperarPoi.execute(idEdificioExterno);
    }

    public void crearMostrarListaPoi(List<PuntoInterese> listaPoi) {

        List<Short> listaIdPoiVisible = new ArrayList<>();

        //Comprobamos os Pois que nos pasan como parametro para ver se xa os temos creados na lista de marcadores do edificio
        if (listaPoi != null) {
            LatLng latLng;
            MarcadorCustom marcadorCustom;

            for (PuntoInterese poi : listaPoi) {
                //Se non conten o poi debemos crear o marcador
                marcadorCustom = new MarcadorCustom(poi.getIdPuntoInterese(), poi.getIdPlanta());
                if (!this.listaMarcadores.contains(marcadorCustom)) {
                    latLng = new LatLng(poi.getLatitude(), poi.getLonxitude());
                    Marker marcadorPoi = this.map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(poi.getNome()));
                    marcadorCustom.setMarcadorGoogle(marcadorPoi);
                    this.listaMarcadores.add(marcadorCustom);
                }

                if (this.idPlanta.equals(poi.getIdPlanta().toString())) {
                    listaIdPoiVisible.add(poi.getIdPuntoInterese());
                }
            }

        }

        //Facemos visibeis os pois que se pasan como parametro e invisibeis o resto
        for (MarcadorCustom marcadorCustom : this.listaMarcadores) {
            marcadorCustom.getMarcadorGoogle().setVisible(listaIdPoiVisible.contains(marcadorCustom.getIdPoi()));
        }
        recuperarRuta();
    }

    private void mostrarTodosPoiPlanta() {
        for (MarcadorCustom marcadorCustom : this.listaMarcadores) {
            marcadorCustom.getMarcadorGoogle().setVisible(marcadorCustom.getIdPlanta().toString().equals(this.idPlanta));
        }
    }

    private void recuperarListaConta() {
        this.recuperarConta.setMapaActivity(this);
        this.recuperarConta.execute();
    }

    public void mostrarListaConta(List<Conta> listaConta) {
        this.mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, listaConta));
    }

    private void recuperarRuta() {
        this.recuperarRuta = new RecuperarRuta();
        this.recuperarRuta.get(new RecuperarRuta.Callback() {
            @Override
            public void onSuccess(List<PolylineOptions> listaRutas) {
                if (listaRutas != null) {
                    Toast.makeText(MapaActivity.this, String.valueOf(listaRutas), Toast.LENGTH_LONG).show();
                    for (PolylineOptions po : listaRutas) {
                        map.addPolyline(po);
                    }
                }
//                debuxarRuta(mapa);
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));

//                hideProgress();
//                startNav(route);
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(MapaActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }

        }, this.idEdificio, this.posicionActual, this.listaMarcadores);
    }

}
