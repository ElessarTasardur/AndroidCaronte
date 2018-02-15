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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import gal.caronte.caronte.activity.InicioActivity;
import gal.caronte.caronte.custom.Edificio;
import gal.caronte.caronte.custom.MarcadorCustom;
import gal.caronte.caronte.custom.Piso;
import gal.caronte.caronte.custom.sw.Conta;
import gal.caronte.caronte.custom.sw.GardarPercorridoParam;
import gal.caronte.caronte.custom.sw.Percorrido;
import gal.caronte.caronte.custom.sw.Posicion;
import gal.caronte.caronte.custom.sw.PuntoInterese;
import gal.caronte.caronte.custom.sw.PuntoInteresePosicion;
import gal.caronte.caronte.servizo.GardarPercorrido;
import gal.caronte.caronte.servizo.RecuperarEdificio;
import gal.caronte.caronte.servizo.RecuperarMapa;
import gal.caronte.caronte.servizo.RecuperarPercorrido;
import gal.caronte.caronte.servizo.RecuperarPoi;
import gal.caronte.caronte.servizo.RecuperarPuntoInteresePercorrido;
import gal.caronte.caronte.servizo.RecuperarRuta;
import gal.caronte.caronte.util.Constantes;
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
    private static final String NOME_CONTA = "nomeConta";
    private static final String CONTRASINAL_CONTA = "contrasinalConta";
    private static final String ID_TOKEN = "idToken";

    private static final int CODIGO_SOLICITUDE_PERMISO_LOCALIZACION = 1;

    //GoogleMaps
    private GoogleMap map;
    private FusedLocationProviderClient mfusedLocationProviderclient;
    private boolean googleActivado = false;

    //Servizos
    private RecuperarEdificio recuperarEdificio;
    private RecuperarMapa recuperarMapa;
    private RecuperarPoi recuperarPoi;
    private RecuperarRuta recuperarRuta;
    private RecuperarPercorrido recuperarPercorrido;
    private RecuperarPuntoInteresePercorrido recuperarPuntoInteresePercorrido;
    private GardarPercorrido gardarPercorrido;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Circle circle;

    private Edificio edificio;
    private String idEdificioExterno;
    private String idPlanta;
    private String idPlantaVisibel;
    private GroundOverlay imaxeOverlay;
    private Location posicionActual;
    //Lista cos marcadores creados na aplicacion, visibeis ou invisibeis.
    private List<MarcadorCustom> listaMarcadores = new ArrayList<>();

    private HashMap<Short, Float> mapaCorMarcador = new HashMap<>();
    private List<Float> listaCor = new ArrayList<>();
    private Polyline marcaPercorrido;

    //Menu lateral
    private DrawerLayout mDrawerLayout;
    private CheckBox checkBoxMostrarPois;

    private SelectorPiso selectorPiso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        //Recuperamos a informacion do intent
        Bundle bundle = getIntent().getExtras();
        String nomeConta = bundle.getString(NOME_CONTA);
        String contrasinalConta = bundle.getString(CONTRASINAL_CONTA);
        String idToken = bundle.getString(ID_TOKEN);

        //Inicializamos Situm
        SitumSdk.init(this);
        SitumSdk.configuration().setUserPass(nomeConta, contrasinalConta);
        this.locationManager = SitumSdk.locationManager();

        crearListaCor();

        this.selectorPiso = new SelectorPiso(this, (LinearLayout) findViewById(R.id.layout_niveis));

        ImageButton botonActualizar = findViewById(R.id.image_button_actualizar);
        botonActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refrescarLocalizacionSitum();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.mDrawerLayout = findViewById(R.id.drawer_layout);

        this.checkBoxMostrarPois = findViewById(R.id.checkbox_mostrar_pois);
        this.checkBoxMostrarPois.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (MapaActivity.this.idEdificioExterno == null) {
                    MapaActivity.this.checkBoxMostrarPois.setChecked(false);
                }
                else if (b) {
                    mostrarTodosPoiPlanta();
                }
                else {
                    ocultarTodosPoi();
                }
                MapaActivity.this.mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Set the list's click listener
//        this.mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Conta contaSeleccionada = (Conta) mDrawerList.getItemAtPosition(position);
//
//                if (!contaSeleccionada.equals(conta)) {
//                    conta = contaSeleccionada;
//                    Log.i(TAG, StringUtil.creaString("Seleccionada a conta: ", conta));
//                    SitumSdk.configuration().setUserPass(conta.getNomeUsuario(), conta.getContrasinal());
//
//                    refrescarLocalizacionSitum();
//                }
//
//                mDrawerLayout.closeDrawer(GravityCompat.START);
//            }
//        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void refrescarLocalizacionSitum() {
        deterLocalizacion();
        activarLocalizacionSitum();
    }

    private void deterLocalizacion() {
        deterChamadas();
        deterLocalizacionSitum();
    }

    private void deterChamadas() {
        if (recuperarEdificio != null) {
            this.recuperarEdificio.cancel();
        }
        if (recuperarMapa != null) {
            this.recuperarMapa.cancel();
        }
        if (recuperarPoi != null) {
            this.recuperarPoi.cancel(true);
        }
        if (recuperarRuta != null) {
            this.recuperarRuta.cancel();
        }
        if (recuperarPercorrido != null) {
            this.recuperarPercorrido.cancel(true);
        }
        if (recuperarPuntoInteresePercorrido != null) {
            this.recuperarPuntoInteresePercorrido.cancel(true);
        }
        if (gardarPercorrido != null) {
            this.gardarPercorrido.cancel(true);
        }
    }

    @Override
    protected void onStop() {
        deterLocalizacion();
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
            Toast.makeText(this, getString(R.string.permiso_necesario), Toast.LENGTH_SHORT).show();
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
                && this.map != null) {
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
            public void onLocationChanged(@NonNull final Location location) {

                Log.i(TAG, "Localizacion: " + location);
                MapaActivity.this.posicionActual = location;

                //Atopamonos dentro dun edificio de Situm, desactivase Google
                if (!"-1".equals(location.getBuildingIdentifier())) {

                    if (MapaActivity.this.googleActivado) {
                        activarLocalizacionGoogle(false);
                    }

                    LatLng latLng = new LatLng(location.getCoordinate().getLatitude(), location.getCoordinate().getLongitude());
                    //Se o edificio e novo cargamos o seu mapa
                    if (MapaActivity.this.idEdificioExterno == null
                            || !location.getBuildingIdentifier().equals(MapaActivity.this.idEdificioExterno)) {
                        Log.i(TAG, "Edificio novo. Preparamos a carga do mesmo");
                        MapaActivity.this.idEdificioExterno = location.getBuildingIdentifier();
                        recuperarListaPercorrido();

                        MapaActivity.this.recuperarEdificio = new RecuperarEdificio();
                        MapaActivity.this.recuperarEdificio.get(new RecuperarEdificio.Callback() {
                            @Override
                            public void onSuccess(Edificio edificio) {

                                Log.i(TAG, StringUtil.creaString("Recuperado o edificio: ", edificio));
                                MapaActivity.this.edificio = edificio;

                                recuperarMapa(location.getFloorIdentifier());
                                establecerCorPiso(edificio.getPisos());
                                recuperarListaPoi(edificio.getEdificio().getIdentifier());

                                MapaActivity.this.selectorPiso.mostrarSelectorPiso(MapaActivity.this.edificio.getPisos(), location.getFloorIdentifier(), MapaActivity.this.mapaCorMarcador);

                            }

                            @Override
                            public void onError(Error error) {
                                Toast.makeText(MapaActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }, idEdificioExterno);

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
                    else if (circle != null) {
                        circle.remove();
                        circle = null;
                    }

                    MapaActivity.this.idPlanta = location.getFloorIdentifier();

                }
                //Activamos a localizacion de Google se non o estaba xa
                else if (!MapaActivity.this.googleActivado) {
                    Toast.makeText(MapaActivity.this, getString(R.string.mensaxe_abandonar_edificio), Toast.LENGTH_LONG).show();
                    activarLocalizacionGoogle(true);
                    if (circle != null) {
                        circle.remove();
                        circle = null;
                    }
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
        Bitmap mapa = this.edificio.getMapa(idPlantaMapa);
        if (mapa == null) {

            Floor floor = this.edificio.getFloor(idPlantaMapa);
            this.recuperarMapa = new RecuperarMapa();
            this.recuperarMapa.get(new RecuperarMapa.Callback() {
                @Override
                public void onSuccess(Bitmap mapa) {
                    MapaActivity.this.edificio.setMapa(idPlantaMapa, mapa);
                    posicionarEdificio(mapa);
                }

                @Override
                public void onError(Error error) {
                    Toast.makeText(MapaActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }

            }, floor);
        }
        //Se xa o temos, mostramolo
        else {
            mostrarMapaPlanta(mapa);
        }
    }

    private void deterLocalizacionSitum() {
        //Reiniciamos variabeis
        if (this.map != null) {
            this.map.clear();
        }
        this.edificio = null;
        this.idEdificioExterno = null;
        this.idPlanta = null;
        this.listaMarcadores.clear();

        if (this.locationManager != null) {
            if (!this.locationManager.isRunning()) {
                return;
            }
            this.locationManager.removeUpdates(locationListener);
        }
    }

    private void posicionarEdificio(Bitmap mapa) {

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

        if (this.checkBoxMostrarPois.isChecked()) {
            mostrarTodosPoiPlanta();
        }
    }

    private void mostrarMapaPlanta(Bitmap mapa) {
        this.imaxeOverlay.setImage(BitmapDescriptorFactory.fromBitmap(mapa));

        if (this.checkBoxMostrarPois.isChecked()) {
            mostrarTodosPoiPlanta();
        }
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

    private void recuperarListaPercorrido() {
        this.recuperarPercorrido = new RecuperarPercorrido();
        this.recuperarPercorrido.setMapaActivity(this);
        this.recuperarPercorrido.execute(this.idEdificioExterno);
    }

    private void recuperarPuntoPercorrido(Short idPercorrido) {
        this.recuperarPuntoInteresePercorrido = new RecuperarPuntoInteresePercorrido();
        this.recuperarPuntoInteresePercorrido.setMapaActivity(this);
        this.recuperarPuntoInteresePercorrido.execute(idPercorrido.toString());
    }

    private void gardarPercorrido() {
        this.gardarPercorrido = new GardarPercorrido();

        //TODO probas
        GardarPercorridoParam gpp = new GardarPercorridoParam();
        gpp.setPercorrido(new Percorrido(null, "proba1", "proba1", (short) 2));

        List<PuntoInterese> listaPoi = new ArrayList<>(2);
        PuntoInterese poi1 = new PuntoInterese((short) 7, null, null, (short) 2, (short) 3506, (short) 1, 43.28966F, -8.39335F);
        PuntoInterese poi2 = new PuntoInterese((short) 12, null, null, (short) 2, (short) 3506, (short) 1, 43.28962F, -8.39331F);
        listaPoi.add(poi1);
        listaPoi.add(poi2);
        gpp.setListaPoi(listaPoi);

        this.gardarPercorrido.setMapaActivity(this);
        this.gardarPercorrido.execute(gpp);
    }

    private void crearListaCor() {
        this.listaCor.add(BitmapDescriptorFactory.HUE_GREEN);
        this.listaCor.add(BitmapDescriptorFactory.HUE_YELLOW);
        this.listaCor.add(BitmapDescriptorFactory.HUE_MAGENTA);
        this.listaCor.add(BitmapDescriptorFactory.HUE_CYAN);
        this.listaCor.add(BitmapDescriptorFactory.HUE_ORANGE);
        this.listaCor.add(BitmapDescriptorFactory.HUE_ROSE);
        this.listaCor.add(BitmapDescriptorFactory.HUE_BLUE);
        this.listaCor.add(BitmapDescriptorFactory.HUE_RED);
        this.listaCor.add(BitmapDescriptorFactory.HUE_VIOLET);
        this.listaCor.add(BitmapDescriptorFactory.HUE_AZURE);
    }

    private void establecerCorPiso(Collection<Piso> listaPiso) {

        if (listaPiso != null) {
            int indice = 0;
            for (Piso piso : listaPiso) {
                this.mapaCorMarcador.put((short) piso.getPiso().getLevel(), this.listaCor.get(indice));
                indice++;
                if (indice == this.listaCor.size()) {
                    indice = 0;
                }
            }
        }

    }

    public void crearListaPoi(List<PuntoInterese> listaPoi) {

        if (listaPoi != null) {
            LatLng latLng;
            MarcadorCustom marcadorCustom;

            for (PuntoInterese poi : listaPoi) {
                Posicion posicion = poi.getPosicion();
                //Se non conten o poi debemos crear o marcador
                marcadorCustom = new MarcadorCustom(poi.getIdPuntoInterese(), posicion.getIdPlanta());
                if (!this.listaMarcadores.contains(marcadorCustom)) {
                    latLng = new LatLng(posicion.getLatitude(), posicion.getLonxitude());
                    Float cor = this.mapaCorMarcador.get(posicion.getNivel());
                    Marker marcadorPoi = this.map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(poi.getNome())
                            .icon(BitmapDescriptorFactory.defaultMarker(cor)));
                    marcadorCustom.setMarcadorGoogle(marcadorPoi);
                    this.listaMarcadores.add(marcadorCustom);
                }

            }
            ocultarTodosPoi();
        }
    }

    private void mostrarTodosPoiPlanta() {
        for (MarcadorCustom marcadorCustom : this.listaMarcadores) {
            marcadorCustom.getMarcadorGoogle().setVisible(marcadorCustom.getIdPlanta().toString().equals(this.idPlantaVisibel));
        }
    }

    private void ocultarTodosPoi() {
        for (MarcadorCustom marcadorCustom : this.listaMarcadores) {
            marcadorCustom.getMarcadorGoogle().setVisible(false);
        }
    }

    private void recuperarRuta() {
        this.recuperarRuta = new RecuperarRuta();
        this.recuperarRuta.get(new RecuperarRuta.Callback() {
            @Override
            public void onSuccess(List<PolylineOptions> listaRutas) {
                if (listaRutas != null) {
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

        }, this.idEdificioExterno, this.posicionActual, this.listaMarcadores);
    }

    public void mostrarListaPercorrido(List<Percorrido> listaPercorrido) {

        if (listaPercorrido != null
                && !listaPercorrido.isEmpty()) {

            listaPercorrido.add(0, new Percorrido(Constantes.ID_FICTICIO, getString(R.string.seleccionar_percorrido), getString(R.string.seleccionar_percorrido), null));

            final Spinner spinner = findViewById(R.id.spinner_percorridos);
            spinner.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, listaPercorrido));

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    Percorrido percorridoSeleccionado = (Percorrido) spinner.getSelectedItem();
                    Log.i(TAG, StringUtil.creaString("Seleccionado o percorrido: ", percorridoSeleccionado));

                    //Ocultar as posibeis marcas
                    if (MapaActivity.this.marcaPercorrido != null) {
                        MapaActivity.this.marcaPercorrido.remove();
                        MapaActivity.this.marcaPercorrido = null;
                        ocultarTodosPoi();
                        //gardarPercorrido();
                    }

                    if (!Constantes.ID_FICTICIO.equals(percorridoSeleccionado.getIdPercorrido())) {
                        if (percorridoSeleccionado.getListaPIP().isEmpty()) {
                            recuperarPuntoPercorrido(percorridoSeleccionado.getIdPercorrido());
                        }
                        else {
                            mostrarPercorrido(percorridoSeleccionado.getListaPIP());
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //Non se fai nada
                }

            });
        }

    }

    public void asociarPuntosPercorrido(List<PuntoInteresePosicion> listaPIP) {

        if (listaPIP != null
                && !listaPIP.isEmpty()) {
            Short idPercorrido = listaPIP.get(0).getIdPercorrido();
            final Spinner spinner = findViewById(R.id.spinner_percorridos);
            Adapter adapter = spinner.getAdapter();

            Percorrido percorrido = null;
            int n = adapter.getCount();
            for (int i = 0; i < n; i++) {
                percorrido = (Percorrido) adapter.getItem(i);
                if (idPercorrido.equals(percorrido.getIdPercorrido())) {
                    break;
                }
                else {
                    percorrido = null;
                }
            }

            if (percorrido != null) {

                List<MarcadorCustom> listaMarcadorPIP = new ArrayList<>(listaPIP.size() + 1);

                for (PuntoInteresePosicion pip : listaPIP) {
                    listaMarcadorPIP.add(new MarcadorCustom(pip.getIdPuntoInterese(), null));
                }

                percorrido.setListaPIP(listaMarcadorPIP);
                mostrarPercorrido(listaMarcadorPIP);
            }
        }

    }

    private void mostrarPercorrido(List<MarcadorCustom> listaPIP) {

        List<MarcadorCustom> listaMostrar = new ArrayList<>(listaPIP.size());
        //Mostramos os marcadores
        for (MarcadorCustom marcadorCustom : this.listaMarcadores) {
            boolean mostrar = listaPIP.contains(marcadorCustom);
            marcadorCustom.getMarcadorGoogle().setVisible(mostrar);
            if (mostrar) {
                listaMostrar.add(marcadorCustom);
            }
        }

        //Mostramos o percorrido
        PolylineOptions polyLineOptions = new PolylineOptions().color(Color.GREEN).width(4f);
        for (MarcadorCustom mc : listaMostrar) {
            polyLineOptions.add(new LatLng(mc.getMarcadorGoogle().getPosition().latitude, mc.getMarcadorGoogle().getPosition().longitude));
        }

        this.marcaPercorrido = this.map.addPolyline(polyLineOptions);

    }

    public void actualizarPercorrido(Short idPercorrido) {
        //TODO
    }
}
