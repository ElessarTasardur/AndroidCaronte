package gal.caronte.caronte.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.Map;

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
import gal.caronte.caronte.custom.EdificioSitumCustom;
import gal.caronte.caronte.custom.ListaEdificioCustom;
import gal.caronte.caronte.custom.MarcadorCustom;
import gal.caronte.caronte.custom.Piso;
import gal.caronte.caronte.custom.UsuarioEdificioCustom;
import gal.caronte.caronte.custom.sw.EdificioCustom;
import gal.caronte.caronte.custom.sw.GardarPercorridoParam;
import gal.caronte.caronte.custom.sw.Percorrido;
import gal.caronte.caronte.custom.sw.Posicion;
import gal.caronte.caronte.custom.sw.PuntoInterese;
import gal.caronte.caronte.servizo.GardarPercorrido;
import gal.caronte.caronte.servizo.RecuperarEdificioSitum;
import gal.caronte.caronte.servizo.RecuperarMapa;
import gal.caronte.caronte.servizo.RecuperarPoi;
import gal.caronte.caronte.servizo.RecuperarRuta;
import gal.caronte.caronte.util.PermisosUtil;
import gal.caronte.caronte.util.StringUtil;
import gal.caronte.caronte.view.InfoWindowAdapterGuiado;
import gal.caronte.caronte.view.SpinnerPercorrido;

/**
 * Created by ElessarTasardur on 08/10/2017.
 */

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback,
//        GoogleMap.OnMyLocationButtonClickListener,
//        GoogleApiClient.ConnectionCallbacks,
//        com.google.android.gms.location.LocationListener,
//        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowLongClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MapaActivity.class.getSimpleName();
    private static final String NOME_CONTA = "nomeConta";
    private static final String CONTRASINAL_CONTA = "contrasinalConta";
    private static final String USUARIO_EDIFICIO = "usuarioEdificio";
    private static final String LISTA_EDIFICIO = "listaEdificio";

    private static final int CODIGO_SOLICITUDE_PERMISO_LOCALIZACION = 1;

    //GoogleMaps
    private GoogleMap map;
    private FusedLocationProviderClient mfusedLocationProviderclient;
    private boolean googleActivado = false;

    //Servizos
    private RecuperarEdificioSitum recuperarEdificioSitum;
    private RecuperarMapa recuperarMapa;
    private RecuperarPoi recuperarPoi;
    private RecuperarRuta recuperarRuta;

    private GardarPercorrido gardarPercorrido;

    //Variabeis globais
    private UsuarioEdificioCustom uec;
    private ListaEdificioCustom listaEdificio;
    private boolean modoEdicion = false;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Circle circle;

    private EdificioSitumCustom edificioSitumCustom;
    private String idEdificioExterno;
    private Integer idEdificio;
    private String idPlanta;
    private String idPlantaVisibel;
    private GroundOverlay imaxeOverlay;
    private Location posicionActual;
    //Lista cos marcadores creados na aplicacion, visibeis ou invisibeis.
    private List<MarcadorCustom> listaMarcadores = new ArrayList<>();
    private boolean todosPoisVisibeis = false;

    private Map<Integer, Float> mapaCorMarcador = new HashMap<>();
    private List<Float> listaCor = new ArrayList<>();
    private Polyline marcaPercorrido;

    //Elementos visuais
    private Toolbar toolbar;
    private SpinnerPercorrido seccionSpinner;
    private ImageButton botonEditar;
    private SelectorPiso selectorPiso;

    //Menu lateral
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        //Recuperamos a informacion do intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String nomeConta = bundle.getString(NOME_CONTA);
            String contrasinalConta = bundle.getString(CONTRASINAL_CONTA);
            this.uec = bundle.getParcelable(USUARIO_EDIFICIO);
            this.listaEdificio = bundle.getParcelable(LISTA_EDIFICIO);

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

            //Toolbar
            this.toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(this.toolbar);

            this.seccionSpinner = new SpinnerPercorrido(this);

            this.botonEditar = findViewById(R.id.image_button_editar);
            this.botonEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!MapaActivity.this.modoEdicion) {
                        activarModoEdicion();
                    }
                    else {
                        desactivarModoEdicion();
                    }
                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //Toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void activarBotonsNonEdicion(boolean activar) {

        //Se existe algun percorrido, mostramos o boton
        if (this.seccionSpinner.tenPercorrido()) {
            ActionMenuItemView selectorPercorridos = findViewById(R.id.accion_selector_percorridos);
            selectorPercorridos.setVisibility(View.VISIBLE);
        }

        //Se existe algun poi, mostramos o boton
        if (this.seccionSpinner.tenPoi()) {
            ActionMenuItemView selectorPois = findViewById(R.id.accion_selector_pois);
            selectorPois.setVisibility(View.VISIBLE);

            ActionMenuItemView amosarPois = findViewById(R.id.accion_todos_pois);
            amosarPois.setVisibility(View.VISIBLE);
        }

        invalidateOptionsMenu();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_selector_percorridos:
                this.seccionSpinner.visualizarSpinnerPercorrido();
                return true;

            case R.id.accion_selector_pois:
                this.seccionSpinner.visualizarSpinnerPoi();
                return true;

            case R.id.accion_todos_pois:
                //Deseleccionamos os spinners
                this.seccionSpinner.deseleccionarPercorrido();
                this.seccionSpinner.deseleccionarPoi();
                ocultarPercorrido();
                if (this.todosPoisVisibeis) {
                    ocultarTodosPoi();
                }
                else {
                    amosarTodosPoi();
                }
                return true;

            default:
                // En caso de que non identifiquemos a accion
                return super.onOptionsItemSelected(item);
        }
    }

    //Inicio e localizacion
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        this.map.getUiSettings().setMapToolbarEnabled(false);
        this.map.getUiSettings().setIndoorLevelPickerEnabled(false);
        this.map.setInfoWindowAdapter(new InfoWindowAdapterGuiado(LayoutInflater.from(getApplicationContext()), this));
        this.map.setOnInfoWindowClickListener(this);

        //Desactivar as opcions dos marcadores cando se fai click
        this.map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                MapaActivity.this.map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
                return true;
            }
        });

//        this.map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//
//            @Override
//            public void onMapClick(LatLng latLng) {
//                boolean modoEdicion = true;
//                if (modoEdicion) {
//                    //TODO comprobar que estamos na creacion dun POI ou percorrido
////                    if () {
////
////                    }
//                }
//                else {
//                    //TODO mostrar posibilidade de guiar ao punto
//                }
//
//            }
//        });

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

    //Localizacion
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

                //Atopamonos dentro dun edificioSitumCustom de Situm, desactivase Google
                if (!"-1".equals(location.getBuildingIdentifier())) {

                    if (MapaActivity.this.googleActivado) {
                        activarLocalizacionGoogle(false);
                    }

                    LatLng latLng = new LatLng(location.getCoordinate().getLatitude(), location.getCoordinate().getLongitude());
                    //Se o edificioSitumCustom e novo cargamos o seu mapa
                    if (MapaActivity.this.idEdificioExterno == null
                            || !location.getBuildingIdentifier().equals(MapaActivity.this.idEdificioExterno)) {
                        Log.i(TAG, "EdificioSitumCustom novo. Preparamos a carga do mesmo");
                        MapaActivity.this.idEdificioExterno = location.getBuildingIdentifier();
                        MapaActivity.this.idEdificio = localizarIdEdificio(MapaActivity.this.idEdificioExterno);
                        MapaActivity.this.seccionSpinner.recuperarListaPercorrido(MapaActivity.this.idEdificioExterno);

                        //Comprobamos se debemos visualizar o boton de edicion
                        comprobarVisibilidadeBotonEdicion();

                        MapaActivity.this.recuperarEdificioSitum = new RecuperarEdificioSitum();
                        MapaActivity.this.recuperarEdificioSitum.get(new RecuperarEdificioSitum.Callback() {
                            @Override
                            public void onSuccess(EdificioSitumCustom edificioSitumCustom) {

                                Log.i(TAG, StringUtil.creaString("Recuperado o edificioSitumCustom: ", edificioSitumCustom));
                                MapaActivity.this.edificioSitumCustom = edificioSitumCustom;

                                setTitle(edificioSitumCustom.getEdificio().getName());

                                recuperarMapa(location.getFloorIdentifier());
                                establecerCorPiso(edificioSitumCustom.getPisos());
                                recuperarListaPoi(edificioSitumCustom.getEdificio().getIdentifier());

                                MapaActivity.this.selectorPiso.amosarSelectorPiso(MapaActivity.this.edificioSitumCustom.getPisos(), location.getFloorIdentifier(), MapaActivity.this.mapaCorMarcador);

                            }

                            @Override
                            public void onError(Error error) {
                                Toast.makeText(MapaActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }, idEdificioExterno);

                        MapaActivity.this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));

                    }
                    //Se a planta visibel e a planta previa son iguais e non coinciden coa actual, debemos ocultar o mapa actual e amosar o novo
                    else if (MapaActivity.this.idPlanta.equals(MapaActivity.this.idPlantaVisibel)
                            && !MapaActivity.this.idPlanta.equals(location.getFloorIdentifier())) {
                        Log.i(TAG, "Cambiamos o piso");
                        if (imaxeOverlay != null) {
                            imaxeOverlay.remove();
                        }
                        recuperarMapa(MapaActivity.this.idPlanta);
                        MapaActivity.this.selectorPiso.cambiarPisoSeleccionado(location.getFloorIdentifier());
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
                            Log.i(TAG, "Pintamos circulo xa creado en " + latLng);
                            circle.setCenter(latLng);
                        }
                    }
                    else if (circle != null) {
                        circle.remove();
                        circle = null;
                    }

                    MapaActivity.this.idPlanta = location.getFloorIdentifier();

                }
                //Se estamos fora dun edificioSitumCustom de Situm
                else {
                    //Establecemos o nome da aplicacion na barra de ferramentas
                    setTitle(getString(R.string.app_name));

                    //Ocultamos o boton de edicion
                    MapaActivity.this.botonEditar.setVisibility(View.INVISIBLE);

                    //Activamos a localizacion de Google se non o estaba xa
                    if (!MapaActivity.this.googleActivado) {
                        Toast.makeText(MapaActivity.this, getString(R.string.mensaxe_abandonar_edificio), Toast.LENGTH_LONG).show();
                        activarLocalizacionGoogle(true);
                        if (circle != null) {
                            circle.remove();
                            circle = null;
                        }
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

    private Integer localizarIdEdificio(String idEdificioExterno) {
        Integer idEdificioInt = null;
        if (idEdificioExterno != null) {
            for (EdificioCustom ec : MapaActivity.this.listaEdificio.getListaEdificio()) {
                //Se coindice o id externo recuperamos o noso identificador
                if (ec.getIdEdificioExterno().equals(Integer.valueOf(idEdificioExterno))) {
                    idEdificioInt = ec.getIdEdificio();
                    break;
                }
            }
        }
        return idEdificioInt;
    }

    //Refresco e detencion
    private void refrescarLocalizacionSitum() {
        deterLocalizacion();
        activarLocalizacionSitum();
    }

    private void deterLocalizacion() {
        deterChamadas();
        deterLocalizacionSitum();
    }

    private void deterLocalizacionSitum() {
        //Reiniciamos variabeis
        if (this.map != null) {
            this.map.clear();
        }
        this.edificioSitumCustom = null;
        this.idEdificio = null;
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

    private void deterChamadas() {
        this.seccionSpinner.deterChamadas();
        if (recuperarEdificioSitum != null) {
            this.recuperarEdificioSitum.cancel();
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
        if (gardarPercorrido != null) {
            this.gardarPercorrido.cancel(true);
        }
    }

    @Override
    protected void onStop() {
//        deterLocalizacion();
//        activarLocalizacionGoogle(false);
        super.onStop();
    }

    //Mapa
    public void recuperarMapa(final String idPlantaMapa) {

        this.idPlantaVisibel = idPlantaMapa;

        //Se ainda non temos o mapa, recuperamolo
        Bitmap mapa = this.edificioSitumCustom.getMapa(idPlantaMapa);
        if (mapa == null) {

            Floor floor = this.edificioSitumCustom.getFloor(idPlantaMapa);
            this.recuperarMapa = new RecuperarMapa();
            this.recuperarMapa.get(new RecuperarMapa.Callback() {
                @Override
                public void onSuccess(Bitmap mapa) {
                    MapaActivity.this.edificioSitumCustom.setMapa(idPlantaMapa, mapa);
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
            amosarMapaPlanta(mapa);
        }
    }

    private void posicionarEdificio(Bitmap mapa) {

        Building building = this.edificioSitumCustom.getEdificio();

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

//        amosarTodosPoiPlanta();
    }

    private void amosarMapaPlanta(Bitmap mapa) {
        this.imaxeOverlay.setImage(BitmapDescriptorFactory.fromBitmap(mapa));
//        amosarTodosPoiPlanta();
    }

    //POI
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
                this.mapaCorMarcador.put(piso.getPiso().getLevel(), this.listaCor.get(indice));
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
            this.seccionSpinner.amosarListaPoi(listaPoi);
        }
    }

//    private void amosarTodosPoiPlanta() {
//        if (!this.todosPoisVisibeis) {
//            for (MarcadorCustom marcadorCustom : this.listaMarcadores) {
//                marcadorCustom.getMarcadorGoogle().setVisible(marcadorCustom.getIdPlanta().toString().equals(this.idPlantaVisibel));
//            }
//        }
//    }

    public void amosarPoi(Integer idPoi) {
        for (MarcadorCustom marcadorCustom : this.listaMarcadores) {
            if (marcadorCustom.getIdPoi().equals(idPoi)) {
                marcadorCustom.getMarcadorGoogle().setVisible(true);
                break;
            }
        }
        this.todosPoisVisibeis = false;
    }

    public void ocultarTodosPoi() {
        for (MarcadorCustom marcadorCustom : this.listaMarcadores) {
            marcadorCustom.getMarcadorGoogle().setVisible(false);
        }
        this.todosPoisVisibeis = false;
    }

    private void amosarTodosPoi() {
        for (MarcadorCustom marcadorCustom : this.listaMarcadores) {
            marcadorCustom.getMarcadorGoogle().setVisible(true);
        }
        this.todosPoisVisibeis = true;
    }



    //Percorrido
    public void ocultarPercorrido() {
        //Ocultar as posibeis marcas
        if (MapaActivity.this.marcaPercorrido != null) {
            MapaActivity.this.marcaPercorrido.remove();
            MapaActivity.this.marcaPercorrido = null;
        }
    }

    public void amosarPercorrido(List<MarcadorCustom> listaPIP) {

        List<MarcadorCustom> listaAmosar = new ArrayList<>(listaPIP.size());
        //Mostramos os marcadores
        for (MarcadorCustom marcadorCustom : this.listaMarcadores) {
            boolean amosar = listaPIP.contains(marcadorCustom);
            marcadorCustom.getMarcadorGoogle().setVisible(amosar);
            if (amosar) {
                listaAmosar.add(marcadorCustom);
            }
        }

        //Mostramos o percorrido
        PolylineOptions polyLineOptions = new PolylineOptions().color(Color.GREEN).width(4f);
        for (MarcadorCustom mc : listaAmosar) {
            polyLineOptions.add(new LatLng(mc.getMarcadorGoogle().getPosition().latitude, mc.getMarcadorGoogle().getPosition().longitude));
        }

        this.marcaPercorrido = this.map.addPolyline(polyLineOptions);

    }

    public void actualizarPercorrido(Short idPercorrido) {
        //TODO
    }

    public void actualizarPoi(Short idPoi) {
        //TODO
    }

    //Servizos
    private void recuperarListaPoi(String idEdificioExterno) {
        this.recuperarPoi = new RecuperarPoi();
        this.recuperarPoi.setMapaActivity(this);
        this.recuperarPoi.execute(idEdificioExterno);
    }

    private void gardarPercorrido() {
//        this.gardarPercorrido = new GardarPercorrido();
//
//        //TODO probas
//        GardarPercorridoParam gpp = new GardarPercorridoParam();
//        gpp.setPercorrido(new Percorrido(null, "proba1", "proba1",  2));
//
//        List<PuntoInterese> listaPoi = new ArrayList<>(2);
//        PuntoInterese poi1 = new PuntoInterese(7, null, null, 2, 3506,1, 43.28966F, -8.39335F);
//        PuntoInterese poi2 = new PuntoInterese(12, null, null, 2, 3506,1, 43.28962F, -8.39331F);
//        listaPoi.add(poi1);
//        listaPoi.add(poi2);
//        gpp.setListaPoi(listaPoi);
//
//        this.gardarPercorrido.setDetallePercorridoActivity(this);
//        this.gardarPercorrido.execute(gpp);
    }

    public void guiar(Marker marcador) {

        MarcadorCustom posicionGuiar = null;
        for (MarcadorCustom mc : this.listaMarcadores) {
            if (mc.getMarcadorGoogle().equals(marcador)) {
                posicionGuiar = mc;
                break;
            }
        }

        if (posicionGuiar != null) {
            this.recuperarRuta = new RecuperarRuta();
            this.recuperarRuta.get(this.idEdificioExterno, this.posicionActual, posicionGuiar, new RecuperarRuta.Callback() {
                @Override
                public void onSuccess(PolylineOptions listaLinhas, LatLngBounds.Builder limite) {
                    if (listaLinhas != null) {
                        ocultarPercorrido();
                        MapaActivity.this.marcaPercorrido = map.addPolyline(listaLinhas);
                    }
//                debuxarRuta(mapa);
                    MapaActivity.this.map.animateCamera(CameraUpdateFactory.newLatLngBounds(limite.build(), 100));

//                hideProgress();
//                startNav(route);
                }

                @Override
                public void onError(Error error) {
                    Toast.makeText(MapaActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }

            });
        }
        else {
            Log.e(TAG, "Non se localizou o marcador seleccionado");
            Toast.makeText(this, R.string.marcador_non_localizado, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Pinchouse sobre a venta de informacion dun marcador. Solicitase o guiado", Toast.LENGTH_SHORT).show();
        guiar(marker);
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Toast.makeText(this, "Pinchouse sobre a venta de informacion dun marcador durante dous segundos. Solicitase a informacion do marcador", Toast.LENGTH_SHORT).show();
//        guiar(marker);
    }

//    @Override
//    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
//        return false;
//    }

    private void comprobarVisibilidadeBotonEdicion() {

        int visibilidade;

        if (comprobarPermisoEdificio(this.idEdificio)) {
            visibilidade = View.VISIBLE;
        }
        else {
            visibilidade = View.INVISIBLE;
        }
        this.botonEditar.setVisibility(visibilidade);

        if (visibilidade == View.INVISIBLE) {
            desactivarModoEdicion();
        }
    }

    public boolean comprobarPermisoEdificio(Integer idEdificioComprobar) {
        boolean permiso = false;
        if (this.uec != null) {
            for (EdificioCustom ec : MapaActivity.this.listaEdificio.getListaEdificio()) {
                //Cando atopemos o edificio comprobamos na lista do usuario se ten permisos de edicion
                if (ec.getIdEdificio().equals(idEdificioComprobar)
                        && this.uec.getListaIdEdificioAdministrador().contains(ec.getIdEdificio())) {
                    permiso = true;
                    break;
                }
            }
        }
        return permiso;
    }

    //Modo edicion
    private void activarModoEdicion() {
        this.modoEdicion = true;

        ActionMenuItemView crearPercorrido = findViewById(R.id.accion_crear_percorrido);
        crearPercorrido.setVisibility(View.VISIBLE);

        ActionMenuItemView crearPoi = findViewById(R.id.accion_crear_poi);
        crearPoi.setVisibility(View.VISIBLE);

        invalidateOptionsMenu();

        //TODO
        Toast.makeText(this, "Activouse o modo edicion", Toast.LENGTH_SHORT).show();
    }

    private void desactivarModoEdicion() {
        this.modoEdicion = false;
        //TODO
        ActionMenuItemView crearPercorrido = findViewById(R.id.accion_crear_percorrido);
        crearPercorrido.setVisibility(View.INVISIBLE);

        ActionMenuItemView crearPoi = findViewById(R.id.accion_crear_poi);
        crearPoi.setVisibility(View.INVISIBLE);

        invalidateOptionsMenu();

        Toast.makeText(this, "Desactivouse o modo edicion", Toast.LENGTH_SHORT).show();
    }

}
