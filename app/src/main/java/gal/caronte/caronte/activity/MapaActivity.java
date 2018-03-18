package gal.caronte.caronte.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.model.CameraPosition;
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
import gal.caronte.caronte.custom.sw.Posicion;
import gal.caronte.caronte.custom.sw.PuntoInterese;
import gal.caronte.caronte.servizo.situm.RecuperarMapa;
import gal.caronte.caronte.servizo.RecuperarPoi;
import gal.caronte.caronte.servizo.situm.RecuperarEdificioSitum;
import gal.caronte.caronte.servizo.situm.RecuperarListaEdificioSitum;
import gal.caronte.caronte.servizo.situm.RecuperarRuta;
import gal.caronte.caronte.util.Constantes;
import gal.caronte.caronte.util.EModoMapa;
import gal.caronte.caronte.util.PermisosUtil;
import gal.caronte.caronte.util.StringUtil;
import gal.caronte.caronte.view.InfoWindowAdapterGuiado;
import gal.caronte.caronte.view.SelectorPoiPercorrido;

/**
 * Created by ElessarTasardur on 08/10/2017.
 */

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback,
//        com.google.android.gms.location.LocationListener,
        GoogleMap.OnInfoWindowClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MapaActivity.class.getSimpleName();

    private static final int CODIGO_SOLICITUDE_PERMISO_LOCALIZACION = 1;

    //GoogleMaps
    private GoogleMap map;
    private FusedLocationProviderClient mfusedLocationProviderclient;
    private boolean googleActivado = false;

    //Servizos
    private RecuperarListaEdificioSitum recuperarListaEdificioSitum;
    private RecuperarEdificioSitum recuperarEdificioSitum;
    private RecuperarMapa recuperarMapa;
    private RecuperarPoi recuperarPoi;
    private RecuperarRuta recuperarRuta;

    //Variabeis globais
    private UsuarioEdificioCustom uec;
    private ListaEdificioCustom listaEdificio;
    private EModoMapa modoMapa = EModoMapa.CONSULTA;

    //Variabeis de situacion da aplicacion
    private boolean detallePoi = false;
    private boolean detallePercorrido = false;

    //Posicionamento
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GroundOverlay imaxePosicion;
    private GroundOverlayOptions gooPosicion;

    //Variabeis Situm+Caronte
    private Map<String, EdificioSitumCustom> mapaEdificioSitumCustom;
    private EdificioSitumCustom edificioSitumCustomLocalizado;
    private EdificioSitumCustom edificioSitumCustomActivo;
    private String idEdificioExternoLocalizado;
    private String idEdificioExternoActivo;
    private Integer idEdificioActivo;
    private String idPlantaLocalizada;
    private String idPlantaActiva;
    private String idPlantaVisibelLocalizada;
    private GroundOverlay imaxeOverlayEdificioActivo;
    private GroundOverlay imaxeOverlayEdificioLocalizado;
    private Location posicionActual;
    //Mapa cos marcadores creados na aplicacion, visibeis ou invisibeis, por edificio
    private Map<String, List<MarcadorCustom>> mapaMarcador = new HashMap<>();
    private boolean todosPoisVisibeis = false;

    private Map<Integer, Float> mapaCorMarcador = new HashMap<>();
    private List<Float> listaCor = new ArrayList<>();
    private Polyline marcaPercorrido;

    //Elementos visuais
    private SelectorPoiPercorrido seccionSpinner;
    private ImageButton botonEditar;
    private ImageButton botonCentrarPosicion;
    private SelectorPiso selectorPiso;

    //Variabeis creacion
    private Marker marcadorNovoPoi;
    private Posicion posicionNovoPoi;
    private List<MarcadorCustom> listaNovoPercorrido = new ArrayList<>();
    private List<Integer> listaIdNovoPercorrido = new ArrayList<>();

    //Menu lateral
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        //Recuperamos a informacion do intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String nomeConta = bundle.getString(Constantes.NOME_CONTA);
            String contrasinalConta = bundle.getString(Constantes.CONTRASINAL_CONTA);
            this.uec = bundle.getParcelable(Constantes.USUARIO_EDIFICIO);
            this.listaEdificio = bundle.getParcelable(Constantes.LISTA_EDIFICIO);

            //Inicializamos Situm
            SitumSdk.init(this);
            SitumSdk.configuration().setUserPass(nomeConta, contrasinalConta);
            this.locationManager = SitumSdk.locationManager();

            recuperarEdificios();

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
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            this.seccionSpinner = new SelectorPoiPercorrido(this);

            this.botonEditar = findViewById(R.id.image_button_editar);
            this.botonEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MapaActivity.this.modoMapa.equals(EModoMapa.CONSULTA)) {
                        activarModoEdicion();
                    }
                    else {
                        desactivarModoEdicion();
                    }
                }
            });

            this.botonCentrarPosicion = findViewById(R.id.image_button_centrar);
            this.botonCentrarPosicion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    centrarMapaEnPosicion();
                }
            });

            this.gooPosicion = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.ic_posicion_mapa));
        }

    }

    private void recuperarEdificios() {

        this.recuperarListaEdificioSitum = new RecuperarListaEdificioSitum();
        this.recuperarListaEdificioSitum.get(new RecuperarListaEdificioSitum.Callback() {
            @Override
            public void onSuccess(Map<String, EdificioSitumCustom> mapaEdificioSitumCustom) {
                Log.i(TAG, StringUtil.creaString("Recuperada o mapa de edificioSitumCustom: ", mapaEdificioSitumCustom));
                MapaActivity.this.mapaEdificioSitumCustom = mapaEdificioSitumCustom;
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(MapaActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();

        //Se ao volver atopamonos que estaba creando un POI ou un percorrido volvemos ao modo edicion
        if (this.modoMapa.equals(EModoMapa.CREAR_POI)
                || this.modoMapa.equals(EModoMapa.CREAR_PERCORRIDO)) {
            this.modoMapa = EModoMapa.EDICION;
            cambiarTituloActividade();
        }
        //Se estivemos no detalle de percorrido, actualizamos a lista de percorridos
        if (this.detallePercorrido) {
            ocultarTodosPoi();
            ocultarPercorrido();
            this.seccionSpinner.borrarPercorridos();
            this.seccionSpinner.recuperarListaPercorrido(this.idEdificioExternoActivo);
        }
        //Se estivemos no detalle de POI, actualizamos a lista de POIs
        if (this.detallePoi) {
            ocultarTodosPoi();
            this.seccionSpinner.borrarPois();
            recuperarListaPoi(this.idEdificioExternoActivo);
        }
        if (this.detallePercorrido
                || this.detallePoi) {
            invalidateOptionsMenu();
        }

        //Inicializamos de novo
        this.detallePercorrido = false;
        this.detallePoi = false;
    }

    //Toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        boolean modoConsulta = this.modoMapa.equals(EModoMapa.CONSULTA);
        boolean modoEdicion = this.modoMapa.equals(EModoMapa.EDICION);
        boolean modoCrearPoi = this.modoMapa.equals(EModoMapa.CREAR_POI);
        boolean modoCrearPercorrido = this.modoMapa.equals(EModoMapa.CREAR_PERCORRIDO);

        //Se esta activo o modo edicion, mostramos o boton
        MenuItem botonCrearPercorrido = menu.findItem(R.id.accion_crear_percorrido);
        botonCrearPercorrido.setVisible(modoEdicion);

        //Se esta activo o modo edicion, mostramos o boton
        MenuItem botonCrearPoi = menu.findItem(R.id.accion_crear_poi);
        botonCrearPoi.setVisible(modoEdicion);

        //Se existe algun percorrido, mostramos o boton
        MenuItem botonSeleccionPercorrido = menu.findItem(R.id.accion_selector_percorridos);
        boolean verSeleccionPercorrido = this.seccionSpinner.tenPercorrido()
                && (modoEdicion || modoConsulta);
        botonSeleccionPercorrido.setVisible(verSeleccionPercorrido);

        //Se existe algun poi, mostramos o boton
        boolean verBotonPoi = this.seccionSpinner.tenPoi()
                && (modoEdicion || modoConsulta);
        MenuItem botonSeleccionPoi = menu.findItem(R.id.accion_selector_pois);
        botonSeleccionPoi.setVisible(verBotonPoi);

        //Se existe algun poi, mostramos o boton
        MenuItem botonTodosPois = menu.findItem(R.id.accion_todos_pois);
        botonTodosPois.setVisible(verBotonPoi);

        //Boton aceptar
        MenuItem botonAceptar = menu.findItem(R.id.accion_aceptar);
        boolean mostrarBotonAceptar = (modoCrearPoi && this.posicionNovoPoi != null)
                || (modoCrearPercorrido && this.listaNovoPercorrido.size() > 2);
        botonAceptar.setVisible(mostrarBotonAceptar);

        //Boton cancelar
        MenuItem botonCancelar = menu.findItem(R.id.accion_cancelar);
        botonCancelar.setVisible(modoCrearPoi || modoCrearPercorrido);

        return true;
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

            case R.id.accion_crear_poi:
                modoCrearPoi();
                return true;

            case R.id.accion_crear_percorrido:
                modoCrearPercorrido();
                return true;

            case R.id.accion_aceptar:
                //Abrimos a activity para o detalle de poi
                if (this.modoMapa.equals(EModoMapa.CREAR_POI)) {
                    this.detallePoi = true;
                    Intent intent = new Intent(this, DetallePoiActivity.class);

                    //Engadimos a informacion do poi ao intent
                    Bundle b = new Bundle();
                    PuntoInterese poi = new PuntoInterese();
                    poi.setIdPuntoInterese(Constantes.ID_FICTICIO);
                    poi.setNome("");
                    poi.setDescricion("");
                    poi.setPosicion(this.posicionNovoPoi);
                    b.putParcelable(Constantes.PUNTO_INTERESE, poi);
                    intent.putExtras(b);

                    intent.putExtra(Constantes.MODO, this.modoMapa);

                    //Iniciamos a actividade do mapa
                    startActivityForResult(intent, Constantes.ACTIVIDADE_DETALLE_POI);
                }
                else if (this.modoMapa.equals(EModoMapa.CREAR_PERCORRIDO)) {
                    this.detallePercorrido = true;
                    Intent intent = new Intent(this, DetallePercorridoActivity.class);

                    //Engadimos a informacion do percorrido ao intent
                    Bundle b = new Bundle();
                    b.putInt(Constantes.ID_EDIFICIO, this.idEdificioActivo);

                    List<PuntoInterese> listaPoi = new ArrayList<>(this.listaNovoPercorrido.size());
                    for (MarcadorCustom mc : this.listaNovoPercorrido) {
                        listaPoi.add(mc.getPoi());
                    }
                    b.putParcelableArrayList(Constantes.LISTA_PUNTO_INTERESE, (ArrayList<? extends Parcelable>) listaPoi);

                    intent.putExtras(b);

                    intent.putExtra(Constantes.MODO, this.modoMapa);

                    //Iniciamos a actividade do mapa
                    startActivityForResult(intent, Constantes.ACTIVIDADE_DETALLE_PERCORRIDO);

                }
                return true;

            case R.id.accion_cancelar:

                //Botamos atras o novo POI
                if (this.posicionNovoPoi != null) {
                    this.posicionNovoPoi = null;
                    this.marcadorNovoPoi.remove();
                }

                //Botamos atras o novo percorrido
                if (!this.listaNovoPercorrido.isEmpty()) {
                    ocultarPercorrido();
                    this.listaNovoPercorrido.clear();
                    this.listaIdNovoPercorrido.clear();
                }

                cambiarTituloActividade();

                this.modoMapa = EModoMapa.EDICION;
                invalidateOptionsMenu();

                return true;

            default:
                // En caso de que non identifiquemos a accion
                return super.onOptionsItemSelected(item);
        }
    }

    private void modoCrearPoi() {

        //Estamos a crear un POI
        this.modoMapa = EModoMapa.CREAR_POI;

        //Mostramos os botons precisos
        invalidateOptionsMenu();

        //Cambiar o titulo da actividade
        setTitle(getString(R.string.situar_poi));

        //Ocultamos os spinner
        this.seccionSpinner.deseleccionarPercorrido();
        this.seccionSpinner.deseleccionarPoi();
        this.seccionSpinner.ocultarSpinnerPercorrido();
        this.seccionSpinner.ocultarSpinnerPoi();
        ocultarPercorrido();

        //Amosamos os POIs do piso seleccionado (ocultando o resto)
        amosarPoiPiso();

    }

    private void modoCrearPercorrido() {

        //Estamos a crear un percorrido
        this.modoMapa = EModoMapa.CREAR_PERCORRIDO;

        //Mostramos os botons precisos
        invalidateOptionsMenu();

        //Cambiar o titulo da actividade
        setTitle(getString(R.string.novo_percorrido));

        //Ocultamos os spinner
        this.seccionSpinner.deseleccionarPercorrido();
        this.seccionSpinner.deseleccionarPoi();
        this.seccionSpinner.ocultarSpinnerPercorrido();
        this.seccionSpinner.ocultarSpinnerPoi();
        ocultarPercorrido();

        //Amosamos os POIs do piso seleccionado (ocultando o resto)
        amosarPoiPiso();

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
                
                if (MapaActivity.this.modoMapa.equals(EModoMapa.CREAR_PERCORRIDO)) {
                    engadirPoiPercorrido(marker);
                }
                
                return true;
            }
        });

        this.map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                //Identificar edificio ao pinchar
                String idEdificioExterno = localizarEdificio(latLng);
                if (idEdificioExterno != null) {
                    if (MapaActivity.this.idEdificioExternoActivo != null
                            && !MapaActivity.this.idEdificioExternoActivo.equals(idEdificioExterno)) {

                        //Recuperamos os datos e activamos o edificio
                        activarEdificio(idEdificioExterno, latLng, null);
                    }
                    //Crear POI
                    else if (MapaActivity.this.modoMapa.equals(EModoMapa.CREAR_POI)) {

                        //Se hai outro previo, borramolo
                        if (MapaActivity.this.marcadorNovoPoi != null) {
                            MapaActivity.this.marcadorNovoPoi.remove();
                        }

                        int nivel = MapaActivity.this.edificioSitumCustomActivo.getFloor(MapaActivity.this.idPlantaActiva).getLevel();
                        MapaActivity.this.posicionNovoPoi = new Posicion(MapaActivity.this.idEdificioActivo, Integer.valueOf(MapaActivity.this.idPlantaActiva), nivel, latLng.latitude, latLng.longitude);
                        Float cor = MapaActivity.this.mapaCorMarcador.get(nivel);
                        MapaActivity.this.marcadorNovoPoi = MapaActivity.this.map.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(MapaActivity.this.getString(R.string.novo_poi))
                                .icon(BitmapDescriptorFactory.defaultMarker(cor)));

                        //TODO ao voltar da outra actividade: marcadorPoi.remove()

                        invalidateOptionsMenu();

                    }
                    else {
                        //TODO amosar posibilidade de guiar ao punto
                    }
                }

            }
        });

        //Comprobamos o permiso de localizacion para activar a localizacion
        boolean permisoConcedido = PermisosUtil.comprobarPermisos(this, Manifest.permission.ACCESS_FINE_LOCATION, CODIGO_SOLICITUDE_PERMISO_LOCALIZACION, true);
        if (permisoConcedido) {
            activarLocalizacionGoogle(true);
            activarLocalizacionSitum();
        }

    }

    private String localizarEdificio(LatLng latLng) {

        String idEdificioExterno = null;
        if (this.mapaEdificioSitumCustom != null) {
            for (EdificioSitumCustom esc : this.mapaEdificioSitumCustom.values()) {
                if (esc.pertenceCoordenada(latLng.latitude, latLng.longitude)) {
                    idEdificioExterno = esc.getEdificio().getIdentifier();
                    break;
                }
            }
        }
        return idEdificioExterno;
    }

    private void engadirPoiPercorrido(Marker marker) {

        MarcadorCustom mcElixido = null;
        List<MarcadorCustom> listaMarcadorEdificio = this.mapaMarcador.get(this.idEdificioExternoActivo);
        for (MarcadorCustom marcadorCustom : listaMarcadorEdificio) {
            if (marcadorCustom.getMarcadorGoogle().equals(marker)) {
                mcElixido = marcadorCustom;
                break;
            }
        }

        if (mcElixido != null) {
            int tamanhoLista = this.listaNovoPercorrido.size();

            if (tamanhoLista > 0) {
                MarcadorCustom mcPrevio = this.listaNovoPercorrido.get(this.listaNovoPercorrido.size() - 1);
                if (mcPrevio.equals(mcElixido)) {
                    //Non se pode escoller duas veces consecutivas o mesmo POI
                    Toast.makeText(this, getString(R.string.poi_consecutivo_percorrido), Toast.LENGTH_SHORT).show();
                    return;
                }

            }
            //Engadense as listas o poi e o seu identificador
            this.listaNovoPercorrido.add(mcElixido);
            this.listaIdNovoPercorrido.add(mcElixido.getIdPoi());

            //Amosar percorrido
            amosarLinhaPercorrido(this.listaNovoPercorrido);

            invalidateOptionsMenu();
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

                //Ocultar o boton de centrar posicion
                this.botonCentrarPosicion.setVisibility(View.INVISIBLE);

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
            else {
                //Mostrar o boton de centrar posicion
                this.botonCentrarPosicion.setVisibility(View.VISIBLE);
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

                Log.i(TAG, StringUtil.creaString("Localizacion: ", location));
                MapaActivity.this.posicionActual = location;

                //Atopamonos dentro dun edificioSitumCustom de Situm, desactivase Google
                if (!"-1".equals(location.getBuildingIdentifier())) {

                    if (MapaActivity.this.googleActivado) {
                        activarLocalizacionGoogle(false);
                    }

                    LatLng latLng = new LatLng(location.getCoordinate().getLatitude(), location.getCoordinate().getLongitude());
                    //Se non hai edificio seleccionado cargamos o actual
                    if (MapaActivity.this.idEdificioExternoActivo == null) {
                        Log.i(TAG, "Edificio localizado novo sen outro activo. Preparamos a carga do mesmo");
                        MapaActivity.this.idEdificioExternoLocalizado = location.getBuildingIdentifier();
                        MapaActivity.this.idPlantaVisibelLocalizada = location.getFloorIdentifier();

                        //Recuperamos os datos e activamos o edificio
                        activarEdificio(location.getBuildingIdentifier(), latLng, location.getFloorIdentifier());

                    }
                    else if (MapaActivity.this.idEdificioExternoLocalizado == null) {
                        Log.i(TAG, "Edificio localizado novo. Preparamos a carga do mesmo");
                        MapaActivity.this.idEdificioExternoLocalizado = location.getBuildingIdentifier();
                        MapaActivity.this.idPlantaVisibelLocalizada = location.getFloorIdentifier();

                        //Unicamente recuperamos os datos do edificio
                        recuperarDatosEdificio(location.getFloorIdentifier(), false);

                    }
                    //Se a planta visibel localizada, a planta localizada e a planta activa son iguais e non coinciden coa nova, debemos ocultar o mapa actual e amosar o novo
                    else if (MapaActivity.this.idPlantaLocalizada.equals(MapaActivity.this.idPlantaVisibelLocalizada)
                            && MapaActivity.this.idPlantaLocalizada.equals(MapaActivity.this.idPlantaActiva)
                            && !MapaActivity.this.idPlantaLocalizada.equals(location.getFloorIdentifier())) {
                        Log.i(TAG, "Cambiamos o piso");
                        recuperarMapa(location.getFloorIdentifier(), true);
                        MapaActivity.this.selectorPiso.cambiarPisoSeleccionado(location.getFloorIdentifier());

                        //Cambiamos a planta activa e a visibel localizada pola nova
                        MapaActivity.this.idPlantaVisibelLocalizada = location.getFloorIdentifier();
                        MapaActivity.this.idPlantaActiva = location.getFloorIdentifier();
                    }

                    //Se o piso actual coincide coa planta visibel, mostramos a localizacion do usuario
                    if (location.getFloorIdentifier().equals(MapaActivity.this.idPlantaVisibelLocalizada)) {
                        if (MapaActivity.this.imaxePosicion == null) {
                            Log.i(TAG, StringUtil.creaString("Mostramos icono en ", latLng));

                            MapaActivity.this.gooPosicion.bearing((float) location.getBearing().degrees())
                                    .position(latLng, 1);

                            MapaActivity.this.imaxePosicion = MapaActivity.this.map.addGroundOverlay(MapaActivity.this.gooPosicion);
                        }
                        else {
                            Log.i(TAG, StringUtil.creaString("Mostramos icono xa creado en ", latLng));
                            MapaActivity.this.gooPosicion.bearing((float) location.getBearing().degrees())
                                    .position(latLng, 1);
                            MapaActivity.this.imaxePosicion.remove();
                            MapaActivity.this.imaxePosicion = MapaActivity.this.map.addGroundOverlay(MapaActivity.this.gooPosicion);
                        }
                    }
                    else if (MapaActivity.this.imaxePosicion != null) {
                        MapaActivity.this.imaxePosicion.remove();
                        MapaActivity.this.imaxePosicion = null;
                    }

                    MapaActivity.this.idPlantaLocalizada = location.getFloorIdentifier();

                }
                //Se saimos do edificio debemos actualizar as variabeis de localizacion e activar a localizacion de Google Maps
                else if (MapaActivity.this.idEdificioExternoLocalizado != null) {

                    //Se o edificio activo tamen e o localizado, "reiniciamos" o mapa
                    if (MapaActivity.this.idEdificioExternoLocalizado.equals(MapaActivity.this.idEdificioExternoActivo)) {
                        //Establecemos o nome da aplicacion na barra de ferramentas
                        setTitle(getString(R.string.app_name));

                        //Ocultamos o boton de edicion
                        MapaActivity.this.botonEditar.setVisibility(View.INVISIBLE);
                    }

                    MapaActivity.this.idEdificioExternoLocalizado = null;
                    MapaActivity.this.idPlantaVisibelLocalizada = null;
                    MapaActivity.this.idPlantaLocalizada = null;

                    //Activamos a localizacion de Google se non o estaba xa
                    if (!MapaActivity.this.googleActivado) {
                        activarLocalizacionGoogle(true);
                    }
                    //Ocultamos a imaxe que indica a posicion de Situm
                    if (MapaActivity.this.imaxePosicion != null) {
                        MapaActivity.this.imaxePosicion.remove();
                        MapaActivity.this.imaxePosicion = null;
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

    private void activarEdificio(String idEdificioExternoActivar, LatLng latLng, final String idPlanta) {

        this.idEdificioExternoActivo = idEdificioExternoActivar;
        this.idEdificioActivo = localizarIdEdificio(this.idEdificioExternoActivo);
        this.idPlantaActiva = idPlanta;
        this.seccionSpinner.recuperarListaPercorrido(this.idEdificioExternoActivo);

        //Comprobamos se debemos visualizar o boton de edicion
        comprobarVisibilidadeBotonEdicion();

        //Localizamos o edificio na lista
        this.edificioSitumCustomActivo = this.mapaEdificioSitumCustom.get(this.idEdificioExternoActivo);
        setTitle(this.edificioSitumCustomActivo.getEdificio().getName());

        //Recuperamos os datos do edificio se non os temos xa
        if (this.edificioSitumCustomActivo.getPisos() == null) {
            recuperarDatosEdificio(idPlanta, true);
        }
        else {
            String idPlantaAmosar;
            if (idPlanta == null) {
                idPlantaAmosar = this.edificioSitumCustomActivo.getPisos().iterator().next().getPiso().getIdentifier();
            }
            else {
                idPlantaAmosar = idPlanta;
            }

            //Amosamos o selector de piso
            Collection<Piso> listaPisoCaronte = this.edificioSitumCustomActivo.getPisos();
            establecerCorPiso(listaPisoCaronte);
            MapaActivity.this.selectorPiso.amosarSelectorPiso(listaPisoCaronte, idPlantaAmosar, MapaActivity.this.mapaCorMarcador);

            //Mostro o mapa
            recuperarMapa(idPlantaAmosar, true);
        }

        this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
    }

    private void recuperarDatosEdificio(final String idPlanta, final boolean edificioActivo) {
        final String idEdificioConsulta;
        final EdificioSitumCustom edificio;
        if (edificioActivo) {
            idEdificioConsulta = this.idEdificioExternoActivo;
            edificio = this.edificioSitumCustomActivo;
        }
        else {
            idEdificioConsulta = this.idEdificioExternoLocalizado;
            edificio = this.edificioSitumCustomLocalizado;
        }

        //Recuperamos os POIs
        recuperarListaPoi(idEdificioConsulta);

        if (edificio.getPisos() == null) {
            //Recuperamos os pisos do edificio
            this.recuperarEdificioSitum = new RecuperarEdificioSitum();
            this.recuperarEdificioSitum.get(new RecuperarEdificioSitum.Callback() {
                @Override
                public void onSuccess(Collection<Floor> listaPiso) {
                    Log.i(TAG, String.valueOf(listaPiso));
                    MapaActivity.this.mapaEdificioSitumCustom.get(edificio.getEdificio().getIdentifier()).setPisos(listaPiso);

                    String idPlantaMostrar;
                    if (idPlanta == null) {
                        idPlantaMostrar = listaPiso.iterator().next().getIdentifier();
                    }
                    else {
                        idPlantaMostrar = idPlanta;
                    }

                    //Se e o edificio activo amosamos o selector de piso
                    if (edificioActivo) {
                        MapaActivity.this.edificioSitumCustomActivo.setPisos(listaPiso);
                        Collection<Piso> listaPisoCaronte = MapaActivity.this.mapaEdificioSitumCustom.get(edificio.getEdificio().getIdentifier()).getPisos();
                        establecerCorPiso(listaPisoCaronte);
                        MapaActivity.this.selectorPiso.amosarSelectorPiso(listaPisoCaronte, idPlantaMostrar, MapaActivity.this.mapaCorMarcador);
                    }
                    else {
                        MapaActivity.this.edificioSitumCustomLocalizado.setPisos(listaPiso);
                    }

                    //Mostro o mapa
                    recuperarMapa(idPlantaMostrar, edificioActivo);

                }

                @Override
                public void onError(Error error) {
                    Toast.makeText(MapaActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }

            }, edificio.getEdificio());
        }

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
        this.edificioSitumCustomLocalizado = null;
        this.idEdificioExternoLocalizado = null;
        this.idPlantaLocalizada = null;
        this.idPlantaVisibelLocalizada = null;

        if (this.locationManager != null) {
            if (!this.locationManager.isRunning()) {
                return;
            }
            this.locationManager.removeUpdates(locationListener);
        }
    }

    private void deterChamadas() {
        this.seccionSpinner.deterChamadas();
        if (recuperarListaEdificioSitum != null) {
            this.recuperarListaEdificioSitum.cancel();
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
    }

    @Override
    protected void onStop() {

        if (!this.detallePercorrido
                && !this.detallePoi) {
            deterLocalizacion();
            activarLocalizacionGoogle(false);
        }
        super.onStop();
    }

    //Mapa
    public void recuperarMapa(final String idPlantaMapa, final boolean edificioActivo) {

        final EdificioSitumCustom edificio;
        if (edificioActivo) {
            edificio = this.edificioSitumCustomActivo;
        }
        else {
            edificio = this.edificioSitumCustomLocalizado;
        }
        //Se ainda non temos o mapa, recuperamolo
        Bitmap mapa = edificio.getMapa(idPlantaMapa);
        if (mapa == null) {

            Floor floor = edificio.getFloor(idPlantaMapa);
            this.recuperarMapa = new RecuperarMapa();
            this.recuperarMapa.get(new RecuperarMapa.Callback() {
                @Override
                public void onSuccess(Bitmap mapa) {
                    edificio.setMapa(idPlantaMapa, mapa);
                    posicionarEdificio(mapa, edificioActivo);
                }

                @Override
                public void onError(Error error) {
                    Toast.makeText(MapaActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }

            }, floor);
        }
        //Se xa o temos, mostramolo
        else {
            amosarMapaPlanta(mapa, edificioActivo);
        }
    }

    private void posicionarEdificio(Bitmap mapa, boolean edificioActivo) {

        Building building;
        if (edificioActivo) {
            building = this.edificioSitumCustomActivo.getEdificio();
        }
        else {
            building = this.edificioSitumCustomLocalizado.getEdificio();
        }

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

        //Agrega a superposicion ao mapa e conserva un controlador para o obxecto GroundOverlay.
        if (edificioActivo) {
            this.imaxeOverlayEdificioActivo = this.map.addGroundOverlay(gooMapa);
        }
        else {
            this.imaxeOverlayEdificioLocalizado = this.map.addGroundOverlay(gooMapa);
        }
    }

    private void amosarMapaPlanta(Bitmap mapa, boolean edificioActivo) {
        if (edificioActivo) {
            this.imaxeOverlayEdificioActivo.setImage(BitmapDescriptorFactory.fromBitmap(mapa));
        }
        else {
            this.imaxeOverlayEdificioLocalizado.setImage(BitmapDescriptorFactory.fromBitmap(mapa));
        }
    }

    private void centrarMapaEnPosicion() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(this.posicionActual.getPosition().getCoordinate().getLatitude(), this.posicionActual.getPosition().getCoordinate().getLongitude()))
                .zoom(this.map.getCameraPosition().zoom)
                .build();
        this.map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

    public void crearListaPoi(String idEdificioExterno, List<PuntoInterese> listaPoi) {

        List<MarcadorCustom> listaMarcadorEdificio = this.mapaMarcador.get(idEdificioExterno);
        if (listaMarcadorEdificio == null) {
            listaMarcadorEdificio = new ArrayList<>();
        }
        else {
            listaMarcadorEdificio.clear();
        }
        if (listaPoi != null) {
            LatLng latLng;
            MarcadorCustom marcadorCustom;

            for (PuntoInterese poi : listaPoi) {
                Posicion posicion = poi.getPosicion();
                //Se non conten o poi debemos crear o marcador
                marcadorCustom = new MarcadorCustom(poi.getIdPuntoInterese(), posicion.getIdPlanta(), poi);
                latLng = new LatLng(posicion.getLatitude(), posicion.getLonxitude());
                Float cor = this.mapaCorMarcador.get(posicion.getNivel());
                Marker marcadorPoi = this.map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(poi.getNome())
                        .icon(BitmapDescriptorFactory.defaultMarker(cor)));
                marcadorCustom.setMarcadorGoogle(marcadorPoi);
                listaMarcadorEdificio.add(marcadorCustom);

            }
            ocultarTodosPoi();

            if (this.idEdificioExternoActivo != null
                    && this.idEdificioExternoActivo.equals(idEdificioExterno)) {
                this.seccionSpinner.amosarListaPoi(listaPoi);
            }
        }
        this.mapaMarcador.put(idEdificioExterno, listaMarcadorEdificio);
    }

//    private void amosarTodosPoiPlanta() {
//        if (!this.todosPoisVisibeis) {
//            for (MarcadorCustom marcadorCustom : this.listaMarcadores) {
//                marcadorCustom.getMarcadorGoogle().setVisible(marcadorCustom.getIdPlanta().toString().equals(this.idPlantaVisibel));
//            }
//        }
//    }

    public void amosarPoi(Integer idPoi) {
        List<MarcadorCustom> listaMarcadorEdificio = this.mapaMarcador.get(this.idEdificioExternoActivo);
        if (listaMarcadorEdificio != null) {
            for (MarcadorCustom marcadorCustom : listaMarcadorEdificio) {
                if (marcadorCustom.getIdPoi().equals(idPoi)) {
                    marcadorCustom.getMarcadorGoogle().setVisible(true);
                    break;
                }
            }
        }
        this.todosPoisVisibeis = false;
    }

    public void ocultarTodosPoi() {
        List<MarcadorCustom> listaMarcadorEdificio = this.mapaMarcador.get(this.idEdificioExternoActivo);
        if (listaMarcadorEdificio != null) {
            for (MarcadorCustom marcadorCustom : listaMarcadorEdificio) {
                marcadorCustom.getMarcadorGoogle().setVisible(false);
            }
        }
        this.todosPoisVisibeis = false;
    }

    private void amosarTodosPoi() {
        List<MarcadorCustom> listaMarcadorEdificio = this.mapaMarcador.get(this.idEdificioExternoActivo);
        if (listaMarcadorEdificio != null) {
            for (MarcadorCustom marcadorCustom : listaMarcadorEdificio) {
                marcadorCustom.getMarcadorGoogle().setVisible(true);
            }
            this.todosPoisVisibeis = true;
        }
    }

    public void amosarPoiPiso() {
        List<MarcadorCustom> listaMarcadorEdificio = this.mapaMarcador.get(this.idEdificioExternoActivo);
        if (listaMarcadorEdificio != null) {
            for (MarcadorCustom marcadorCustom : listaMarcadorEdificio) {
                boolean amosarPoi = false;
                //Se estamos a crear un percorrido miramos se o marcador esta entre os seleccionados para non borralo
                if (this.modoMapa.equals(EModoMapa.CREAR_PERCORRIDO)) {
                    amosarPoi = this.listaIdNovoPercorrido.contains(marcadorCustom.getIdPoi());
                }
                if (!amosarPoi) {
                    amosarPoi = marcadorCustom.getIdPlanta().toString().equals(this.idPlantaActiva);
                }
                marcadorCustom.getMarcadorGoogle().setVisible(amosarPoi);
            }
        }
        this.todosPoisVisibeis = false;
    }

    //PercorridoParam
    public void ocultarPercorrido() {
        //Ocultar as posibeis marcas
        if (MapaActivity.this.marcaPercorrido != null) {
            MapaActivity.this.marcaPercorrido.remove();
            MapaActivity.this.marcaPercorrido = null;
        }
    }

    public void amosarPercorrido(List<MarcadorCustom> listaPIP) {

        List<MarcadorCustom> listaAmosar = new ArrayList<>(listaPIP.size());
        //Amosamos os marcadores
        List<MarcadorCustom> listaMarcadorEdificio = this.mapaMarcador.get(this.idEdificioExternoActivo);
        if (listaMarcadorEdificio != null) {
            for (MarcadorCustom marcadorCustom : listaMarcadorEdificio) {
                boolean amosar = listaPIP.contains(marcadorCustom);
                marcadorCustom.getMarcadorGoogle().setVisible(amosar);
                if (amosar) {
                    listaAmosar.add(marcadorCustom);
                }
            }

            amosarLinhaPercorrido(listaAmosar);
        }

    }

    private void amosarLinhaPercorrido(List<MarcadorCustom> listaAmosar) {
        //Amosamos o percorrido
        PolylineOptions polyLineOptions = new PolylineOptions().color(Color.GREEN).width(4f);
        for (MarcadorCustom mc : listaAmosar) {
            polyLineOptions.add(new LatLng(mc.getMarcadorGoogle().getPosition().latitude, mc.getMarcadorGoogle().getPosition().longitude));
        }
        this.marcaPercorrido = this.map.addPolyline(polyLineOptions);
    }

    //Servizos
    private void recuperarListaPoi(String idEdificioExterno) {
        this.recuperarPoi = new RecuperarPoi();
        this.recuperarPoi.setMapaActivity(this);
        this.recuperarPoi.execute(idEdificioExterno);
    }

    public void guiar(Marker marcador) {

        //Se o edificio activo e o mesmo edificio no que estamos, ofrecemos o guiado
        if (this.idEdificioExternoActivo.equals(this.idEdificioExternoLocalizado)) {
            MarcadorCustom posicionGuiar = null;
            List<MarcadorCustom> listaMarcadorEdificio = this.mapaMarcador.get(this.idEdificioExternoActivo);
            for (MarcadorCustom marcadorCustom : listaMarcadorEdificio) {
                if (marcadorCustom.getMarcadorGoogle().equals(marcador)) {
                    posicionGuiar = marcadorCustom;
                    break;
                }
            }

            if (posicionGuiar != null) {
                this.recuperarRuta = new RecuperarRuta();
                this.recuperarRuta.get(this.idEdificioExternoLocalizado, this.posicionActual, posicionGuiar, new RecuperarRuta.Callback() {
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
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Pinchouse sobre a venta de informacion dun marcador. Solicitase o guiado", Toast.LENGTH_SHORT).show();
        guiar(marker);
    }

//    @Override
//    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
//        return false;
//    }

    private void comprobarVisibilidadeBotonEdicion() {

        int visibilidade;

        if (comprobarPermisoEdificio(this.idEdificioActivo)) {
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

        if (this.modoMapa.equals(EModoMapa.CONSULTA)) {
            this.modoMapa = EModoMapa.EDICION;

            cambiarTituloActividade();

            invalidateOptionsMenu();

            Toast.makeText(this, "Activouse o modo edicion", Toast.LENGTH_SHORT).show();
        }
    }

    private void desactivarModoEdicion() {

        if (!this.modoMapa.equals(EModoMapa.CONSULTA)) {
            this.modoMapa = EModoMapa.CONSULTA;

            cambiarTituloActividade();

            invalidateOptionsMenu();

            Toast.makeText(this, "Desactivouse o modo edicion", Toast.LENGTH_SHORT).show();
        }
    }

    private void cambiarTituloActividade() {
        if (this.edificioSitumCustomActivo != null) {
            setTitle(this.edificioSitumCustomActivo.getEdificio().getName());
        }
        else if (this.edificioSitumCustomLocalizado != null) {
            setTitle(this.edificioSitumCustomLocalizado.getEdificio().getName());
        }
        else {
            setTitle(getString(R.string.app_name));
        }
    }

    public EModoMapa getModoMapa() {
        return this.modoMapa;
    }

    public void setDetallePoi(boolean detallePoi) {
        this.detallePoi = detallePoi;
    }

    public void setDetallePercorrido(boolean detallePercorrido) {
        this.detallePercorrido = detallePercorrido;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == Constantes.ACTIVIDADE_DETALLE_POI) {
                this.detallePoi = true;
            }
            else if (requestCode == Constantes.ACTIVIDADE_DETALLE_PERCORRIDO) {
                this.detallePercorrido = true;
            }

            //Deseleccionamos os spinners
            this.seccionSpinner.deseleccionarPercorrido();
            this.seccionSpinner.deseleccionarPoi();
            ocultarPercorrido();
            ocultarTodosPoi();
        }
        else {
            this.detallePoi = false;
            this.detallePercorrido = false;
        }
    }
}
