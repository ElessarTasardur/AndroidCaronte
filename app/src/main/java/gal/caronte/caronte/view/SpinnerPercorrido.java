package gal.caronte.caronte.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import gal.caronte.caronte.R;
import gal.caronte.caronte.activity.DetallePercorridoActivity;
import gal.caronte.caronte.activity.DetallePoiActivity;
import gal.caronte.caronte.activity.MapaActivity;
import gal.caronte.caronte.custom.MarcadorCustom;
import gal.caronte.caronte.custom.sw.Percorrido;
import gal.caronte.caronte.custom.sw.PuntoInterese;
import gal.caronte.caronte.custom.sw.PuntoInteresePosicion;
import gal.caronte.caronte.servizo.RecuperarPercorrido;
import gal.caronte.caronte.servizo.RecuperarPuntoInteresePercorrido;
import gal.caronte.caronte.util.Constantes;
import gal.caronte.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 23/02/2018.
 */

public class SpinnerPercorrido  {

    private static final String TAG = SpinnerPercorrido.class.getSimpleName();
    private static final String PUNTO_INTERESE = "puntoInterese";
    private static final String ID_PERCORRIDO = "idPercorrido";
    private static final String NOME_PERCORRIDO = "nomePercorrido";
    private static final String DESCRICION_PERCORRIDO = "descricionPercorrido";
    private static final String ID_EDIFICIO = "idEdificio";
    private static final String MODO = "modo";

    //Servizos
    private RecuperarPuntoInteresePercorrido recuperarPuntoInteresePercorrido;
    private RecuperarPercorrido recuperarPercorrido;

    private Spinner spinnerPercorrido;
    private ImageButton botonPercorrido;

    private Spinner spinnerPoi;
    private ImageButton botonPoi;

    private Percorrido percorridoSeleccionado;
    private PuntoInterese poiSeleccionado;

    private MapaActivity mapaActivity;

    public SpinnerPercorrido(MapaActivity mapaActivity) {
        super();
        this.mapaActivity = mapaActivity;

        crearSpinnerPercorrido();
        crearBotonPercorrido();
        crearSpinnerPoi();
        crearBotonPoi();

    }

    private void crearSpinnerPercorrido() {
        //Spinners
        this.spinnerPercorrido = this.mapaActivity.findViewById(R.id.spinner_percorridos);
        this.spinnerPercorrido.setVisibility(View.INVISIBLE);
        this.spinnerPercorrido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SpinnerPercorrido.this.percorridoSeleccionado = (Percorrido) SpinnerPercorrido.this.spinnerPercorrido.getSelectedItem();
                Log.i(TAG, StringUtil.creaString("Seleccionado o percorrido: ", percorridoSeleccionado));

                SpinnerPercorrido.this.mapaActivity.ocultarTodosPoi();
                SpinnerPercorrido.this.mapaActivity.ocultarPercorrido();
                if (!Constantes.ID_FICTICIO.equals(SpinnerPercorrido.this.percorridoSeleccionado.getIdPercorrido())) {
                    if (SpinnerPercorrido.this.percorridoSeleccionado.getListaPIP().isEmpty()) {
                        recuperarPuntoPercorrido(SpinnerPercorrido.this.percorridoSeleccionado.getIdPercorrido());
                    }
                    else {
                        SpinnerPercorrido.this.mapaActivity.amosarPercorrido(SpinnerPercorrido.this.percorridoSeleccionado.getListaPIP());
                    }

                    //Se hai POIs no spinner seleccionamos o primeiro
                    if (SpinnerPercorrido.this.spinnerPoi.getAdapter() != null
                            && SpinnerPercorrido.this.spinnerPoi.getAdapter().getCount() > 0) {
                        SpinnerPercorrido.this.spinnerPoi.setSelection(0);
                    }

                    //Mostramos o boton para abrir o detalle
                    SpinnerPercorrido.this.botonPercorrido.setVisibility(View.VISIBLE);
                }
                else {
                    //Ocultamos o boton para abrir o detalle
                    SpinnerPercorrido.this.botonPercorrido.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Non se fai nada
            }
        });
    }

    private void crearBotonPercorrido() {
        //Boton percorrido
        this.botonPercorrido = this.mapaActivity.findViewById(R.id.image_button_detalle_percorrido);
        this.botonPercorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpinnerPercorrido.this.mapaActivity, DetallePercorridoActivity.class);

                //Engadimos a informacion da conta ao intent
                Bundle b = new Bundle();
                b.putInt(ID_PERCORRIDO, SpinnerPercorrido.this.percorridoSeleccionado.getIdPercorrido());
                b.putString(NOME_PERCORRIDO, SpinnerPercorrido.this.percorridoSeleccionado.getNome());
                b.putString(DESCRICION_PERCORRIDO, SpinnerPercorrido.this.percorridoSeleccionado.getDescricion());
                b.putInt(ID_EDIFICIO, SpinnerPercorrido.this.percorridoSeleccionado.getIdEdificio());

                Short modo;
                if (SpinnerPercorrido.this.mapaActivity.comprobarPermisoEdificio(SpinnerPercorrido.this.percorridoSeleccionado.getIdEdificio())) {
                    modo = Constantes.MODIFICACION;
                }
                else {
                    modo = Constantes.CONSULTA;
                }
                b.putShort(MODO, modo);

                intent.putExtras(b);

                //Iniciamos a actividade do mapa
                SpinnerPercorrido.this.mapaActivity.startActivity(intent);
            }
        });
    }

    private void crearSpinnerPoi() {
        this.spinnerPoi = this.mapaActivity.findViewById(R.id.spinner_pois);
        this.spinnerPoi.setVisibility(View.INVISIBLE);
        this.spinnerPoi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SpinnerPercorrido.this.poiSeleccionado = (PuntoInterese) SpinnerPercorrido.this.spinnerPoi.getSelectedItem();
                Log.i(TAG, StringUtil.creaString("Seleccionado o poi: ", SpinnerPercorrido.this.poiSeleccionado));

                if (!Constantes.ID_FICTICIO.equals(SpinnerPercorrido.this.poiSeleccionado.getIdPuntoInterese())) {
                    SpinnerPercorrido.this.mapaActivity.ocultarTodosPoi();
                    SpinnerPercorrido.this.mapaActivity.ocultarPercorrido();
                    SpinnerPercorrido.this.mapaActivity.amosarPoi(SpinnerPercorrido.this.poiSeleccionado.getIdPuntoInterese());

                    //Se os percorridos estan visibeis seleccionamos o primeiro
                    if (SpinnerPercorrido.this.spinnerPercorrido.getAdapter() != null
                            && SpinnerPercorrido.this.spinnerPercorrido.getAdapter().getCount() > 0) {
                        SpinnerPercorrido.this.spinnerPercorrido.setSelection(0);
                    }

                    //Mostramos o boton para abrir o detalle
                    SpinnerPercorrido.this.botonPoi.setVisibility(View.VISIBLE);

                }
                else {
                    //Ocultamos o boton para abrir o detalle
                    SpinnerPercorrido.this.botonPoi.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Non se fai nada
            }
        });
    }

    private void crearBotonPoi() {
        //Boton poi
        this.botonPoi = this.mapaActivity.findViewById(R.id.image_button_detalle_poi);
        this.botonPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpinnerPercorrido.this.mapaActivity, DetallePoiActivity.class);

                //Engadimos a informacion da conta ao intent
                Bundle b = new Bundle();
                b.putParcelable(PUNTO_INTERESE, SpinnerPercorrido.this.poiSeleccionado);

                Short modo;
                if (SpinnerPercorrido.this.mapaActivity.comprobarPermisoEdificio(SpinnerPercorrido.this.poiSeleccionado.getPosicion().getIdEdificio())) {
                    modo = Constantes.MODIFICACION;
                }
                else {
                    modo = Constantes.CONSULTA;
                }
                b.putShort(MODO, modo);
                intent.putExtras(b);

                //Iniciamos a actividade do mapa
                SpinnerPercorrido.this.mapaActivity.startActivity(intent);
            }
        });
    }

    public void visualizarSpinnerPercorrido() {
        if (this.spinnerPercorrido.getVisibility() == View.INVISIBLE) {
            this.spinnerPercorrido.setVisibility(View.VISIBLE);
        }
        else {
            this.spinnerPercorrido.setVisibility(View.INVISIBLE);
            this.botonPercorrido.setVisibility(View.INVISIBLE);
        }
        this.spinnerPoi.setVisibility(View.INVISIBLE);
        this.botonPoi.setVisibility(View.INVISIBLE);
    }


    public void visualizarSpinnerPoi() {
        if (this.spinnerPoi.getVisibility() == View.INVISIBLE) {
            this.spinnerPoi.setVisibility(View.VISIBLE);
        }
        else {
            this.spinnerPoi.setVisibility(View.INVISIBLE);
            this.botonPoi.setVisibility(View.INVISIBLE);
        }
        this.spinnerPercorrido.setVisibility(View.INVISIBLE);
        this.botonPercorrido.setVisibility(View.INVISIBLE);
    }

    public void deseleccionarPercorrido() {
        if (this.spinnerPercorrido.getAdapter() != null
                && this.spinnerPercorrido.getAdapter().getCount() > 0) {
            this.spinnerPercorrido.setSelection(0);
        }
    }

    public void deseleccionarPoi() {
        if (this.spinnerPoi.getAdapter() != null
                && this.spinnerPoi.getAdapter().getCount() > 0) {
            this.spinnerPoi.setSelection(0);
        }
    }

    public boolean tenPercorrido() {
        return this.spinnerPercorrido.getAdapter() != null
                && this.spinnerPercorrido.getAdapter().getCount() > 0;
    }

    public boolean tenPoi() {
        return this.spinnerPoi.getAdapter() != null
                && this.spinnerPoi.getAdapter().getCount() > 0;
    }

    public void deterChamadas() {
        if (this.recuperarPuntoInteresePercorrido != null) {
            this.recuperarPuntoInteresePercorrido.cancel(true);
        }
        if (this.recuperarPercorrido != null) {
            this.recuperarPercorrido.cancel(true);
        }
    }


    public void asociarPuntosPercorrido(List<PuntoInteresePosicion> listaPIP) {

        if (listaPIP != null
                && !listaPIP.isEmpty()) {
            Integer idPercorrido = listaPIP.get(0).getIdPercorrido();
            Adapter adapter = this.spinnerPercorrido.getAdapter();

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
                this.mapaActivity.amosarPercorrido(listaMarcadorPIP);
            }
        }

    }

    public void amosarListaPoi(List<PuntoInterese> listaPoi) {

        if (listaPoi != null
                && !listaPoi.isEmpty()) {
            String seleccionar = this.mapaActivity.getString(R.string.seleccionar_poi);
            listaPoi.add(0, new PuntoInterese(Constantes.ID_FICTICIO, seleccionar, seleccionar));
            this.spinnerPoi.setAdapter(new ArrayAdapter<>(this.mapaActivity, R.layout.drawer_list_item, listaPoi));

            this.mapaActivity.activarBotonsNonEdicion(true);
        }

    }

    public void amosarListaPercorrido(List<Percorrido> listaPercorrido) {

        if (listaPercorrido != null
                && !listaPercorrido.isEmpty()) {
            String seleccionar = this.mapaActivity.getString(R.string.seleccionar_percorrido);
            listaPercorrido.add(0, new Percorrido(Constantes.ID_FICTICIO, seleccionar, seleccionar, null));
            this.spinnerPercorrido.setAdapter(new ArrayAdapter<>(this.mapaActivity, R.layout.drawer_list_item, listaPercorrido));

            this.mapaActivity.activarBotonsNonEdicion(true);
        }

    }

    //Servizos
    private void recuperarPuntoPercorrido(Integer idPercorrido) {
        this.recuperarPuntoInteresePercorrido = new RecuperarPuntoInteresePercorrido();
        this.recuperarPuntoInteresePercorrido.setSpinnerPercorrido(this);
        this.recuperarPuntoInteresePercorrido.execute(idPercorrido.toString());
    }

    public void recuperarListaPercorrido(String idEdificioExterno) {
        this.recuperarPercorrido = new RecuperarPercorrido();
        this.recuperarPercorrido.setSpinnerPercorrido(this);
        this.recuperarPercorrido.execute(idEdificioExterno);
    }

    public MapaActivity getMapaActivity() {
        return this.mapaActivity;
    }
}
