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
import gal.caronte.caronte.custom.PercorridoCustom;
import gal.caronte.caronte.custom.sw.PercorridoParam;
import gal.caronte.caronte.custom.sw.PuntoInterese;
import gal.caronte.caronte.custom.sw.PuntoInteresePosicion;
import gal.caronte.caronte.servizo.RecuperarPercorrido;
import gal.caronte.caronte.servizo.RecuperarPuntoInteresePercorrido;
import gal.caronte.caronte.util.Constantes;
import gal.caronte.caronte.util.EModoMapa;
import gal.caronte.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 23/02/2018.
 */

public class SelectorPoiPercorrido {

    private static final String TAG = SelectorPoiPercorrido.class.getSimpleName();

    //Servizos
    private RecuperarPuntoInteresePercorrido recuperarPuntoInteresePercorrido;
    private RecuperarPercorrido recuperarPercorrido;

    private Spinner spinnerPercorrido;
    private ImageButton botonPercorrido;

    private Spinner spinnerPoi;
    private ImageButton botonPoi;

    private PercorridoCustom percorridoSeleccionado;
    private PuntoInterese poiSeleccionado;

    private MapaActivity mapaActivity;

    public SelectorPoiPercorrido(MapaActivity mapaActivity) {
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
                SelectorPoiPercorrido.this.percorridoSeleccionado = (PercorridoCustom) SelectorPoiPercorrido.this.spinnerPercorrido.getSelectedItem();
                Log.i(TAG, StringUtil.creaString("Seleccionado o percorrido: ", SelectorPoiPercorrido.this.percorridoSeleccionado));

                SelectorPoiPercorrido.this.mapaActivity.ocultarTodosPoi();
                SelectorPoiPercorrido.this.mapaActivity.ocultarPercorrido();
                if (!Constantes.ID_FICTICIO.equals(SelectorPoiPercorrido.this.percorridoSeleccionado.getIdPercorrido())) {
                    if (SelectorPoiPercorrido.this.percorridoSeleccionado.getListaPIP().isEmpty()) {
                        recuperarPuntoPercorrido(SelectorPoiPercorrido.this.percorridoSeleccionado.getIdPercorrido());
                    }
                    else {
                        SelectorPoiPercorrido.this.mapaActivity.amosarPercorrido(SelectorPoiPercorrido.this.percorridoSeleccionado.getListaPIP());
                    }

                    //Se hai POIs no spinner seleccionamos o primeiro
                    if (SelectorPoiPercorrido.this.spinnerPoi.getAdapter() != null
                            && SelectorPoiPercorrido.this.spinnerPoi.getAdapter().getCount() > 0) {
                        SelectorPoiPercorrido.this.spinnerPoi.setSelection(0);
                    }

                    //Mostramos o boton para abrir o detalle
                    SelectorPoiPercorrido.this.botonPercorrido.setVisibility(View.VISIBLE);
                }
                else {
                    //Ocultamos o boton para abrir o detalle
                    SelectorPoiPercorrido.this.botonPercorrido.setVisibility(View.INVISIBLE);
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

                //Establecemos a posicion na actividade principal
                SelectorPoiPercorrido.this.mapaActivity.setDetallePercorrido(true);

                Intent intent = new Intent(SelectorPoiPercorrido.this.mapaActivity, DetallePercorridoActivity.class);

                //Engadimos a informacion da conta ao intent
                Bundle b = new Bundle();
                b.putInt(Constantes.ID_PERCORRIDO, SelectorPoiPercorrido.this.percorridoSeleccionado.getIdPercorrido());
                b.putString(Constantes.NOME_PERCORRIDO, SelectorPoiPercorrido.this.percorridoSeleccionado.getNome());
                b.putString(Constantes.DESCRICION_PERCORRIDO, SelectorPoiPercorrido.this.percorridoSeleccionado.getDescricion());
                b.putInt(Constantes.ID_EDIFICIO, SelectorPoiPercorrido.this.percorridoSeleccionado.getIdEdificio());

                EModoMapa modo;
                if (SelectorPoiPercorrido.this.mapaActivity.comprobarPermisoEdificio(SelectorPoiPercorrido.this.percorridoSeleccionado.getIdEdificio())) {
                    modo = EModoMapa.EDICION;
                }
                else {
                    modo = EModoMapa.CONSULTA;
                }
                intent.putExtra(Constantes.MODO, modo);

                intent.putExtras(b);

                //Iniciamos a actividade do detalle do percorrido
                SelectorPoiPercorrido.this.mapaActivity.startActivityForResult(intent, Constantes.ACTIVIDADE_DETALLE_PERCORRIDO);
            }
        });
    }

    private void crearSpinnerPoi() {
        this.spinnerPoi = this.mapaActivity.findViewById(R.id.spinner_pois);
        this.spinnerPoi.setVisibility(View.INVISIBLE);
        this.spinnerPoi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SelectorPoiPercorrido.this.poiSeleccionado = (PuntoInterese) SelectorPoiPercorrido.this.spinnerPoi.getSelectedItem();
                Log.i(TAG, StringUtil.creaString("Seleccionado o poi: ", SelectorPoiPercorrido.this.poiSeleccionado));

                if (!Constantes.ID_FICTICIO.equals(SelectorPoiPercorrido.this.poiSeleccionado.getIdPuntoInterese())) {
                    SelectorPoiPercorrido.this.mapaActivity.ocultarTodosPoi();
                    SelectorPoiPercorrido.this.mapaActivity.ocultarPercorrido();
                    SelectorPoiPercorrido.this.mapaActivity.amosarPoi(SelectorPoiPercorrido.this.poiSeleccionado.getIdPuntoInterese());

                    //Se os percorridos estan visibeis seleccionamos o primeiro
                    if (SelectorPoiPercorrido.this.spinnerPercorrido.getAdapter() != null
                            && SelectorPoiPercorrido.this.spinnerPercorrido.getAdapter().getCount() > 0) {
                        SelectorPoiPercorrido.this.spinnerPercorrido.setSelection(0);
                    }

                    //Mostramos o boton para abrir o detalle
                    SelectorPoiPercorrido.this.botonPoi.setVisibility(View.VISIBLE);

                }
                else {
                    //Ocultamos o boton para abrir o detalle
                    SelectorPoiPercorrido.this.botonPoi.setVisibility(View.INVISIBLE);
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

                //Establecemos a posicion na actividade principal
                SelectorPoiPercorrido.this.mapaActivity.setDetallePoi(true);

                Intent intent = new Intent(SelectorPoiPercorrido.this.mapaActivity, DetallePoiActivity.class);

                //Engadimos a informacion da conta ao intent
                Bundle b = new Bundle();
                b.putParcelable(Constantes.PUNTO_INTERESE, SelectorPoiPercorrido.this.poiSeleccionado);

                EModoMapa modo;
                if (SelectorPoiPercorrido.this.mapaActivity.comprobarPermisoEdificio(SelectorPoiPercorrido.this.poiSeleccionado.getPosicion().getIdEdificio())) {
                    modo = EModoMapa.EDICION;
                }
                else {
                    modo = EModoMapa.CONSULTA;
                }
                intent.putExtra(Constantes.MODO, modo);

                intent.putExtras(b);

                //Iniciamos a actividade do mapa
                SelectorPoiPercorrido.this.mapaActivity.startActivityForResult(intent, Constantes.ACTIVIDADE_DETALLE_POI);
            }
        });
    }

    public void visualizarSpinnerPercorrido() {
        if (this.spinnerPercorrido.getVisibility() == View.INVISIBLE) {
            this.spinnerPercorrido.setVisibility(View.VISIBLE);
            //Se o elemento seleccionado non e o primeiro, mostramos o boton
            if (!this.spinnerPercorrido.getSelectedItem().equals(this.spinnerPercorrido.getAdapter().getItem(0))) {
                this.botonPercorrido.setVisibility(View.VISIBLE);
            }
        }
        else {
            ocultarSpinnerPercorrido();
        }
        ocultarSpinnerPoi();
    }

    public void visualizarSpinnerPoi() {
        if (this.spinnerPoi.getVisibility() == View.INVISIBLE) {
            this.spinnerPoi.setVisibility(View.VISIBLE);
            //Se o elemento seleccionado non e o primeiro, mostramos o boton
            if (!this.spinnerPoi.getSelectedItem().equals(this.spinnerPoi.getAdapter().getItem(0))) {
                this.botonPoi.setVisibility(View.VISIBLE);
            }
        }
        else {
            ocultarSpinnerPoi();
        }
        ocultarSpinnerPercorrido();
    }

    public void ocultarSpinnerPercorrido() {
        this.spinnerPercorrido.setVisibility(View.INVISIBLE);
        this.botonPercorrido.setVisibility(View.INVISIBLE);
    }

    public void ocultarSpinnerPoi() {
        this.spinnerPoi.setVisibility(View.INVISIBLE);
        this.botonPoi.setVisibility(View.INVISIBLE);
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

    public void borrarPois() {
        this.spinnerPoi.setAdapter(new ArrayAdapter<>(this.mapaActivity, R.layout.drawer_list_item, new ArrayList<PuntoInterese>()));
//        notifyDataSetChanged();
    }

    public void borrarPercorridos() {
        this.spinnerPercorrido.setAdapter(new ArrayAdapter<>(this.mapaActivity, R.layout.drawer_list_item, new ArrayList<PercorridoCustom>()));
//        this.spinnerPercorrido.getAdapter().notifyDataSetChanged();
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

            PercorridoCustom percorrido = null;
            int n = adapter.getCount();
            for (int i = 0; i < n; i++) {
                percorrido = (PercorridoCustom) adapter.getItem(i);
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
                    listaMarcadorPIP.add(new MarcadorCustom(pip.getIdPuntoInterese(), null, null));
                }

                percorrido.setListaPIP(listaMarcadorPIP);
                this.mapaActivity.ocultarPercorrido();
                this.mapaActivity.amosarPercorrido(listaMarcadorPIP);
            }
        }

    }

    public void amosarListaPoi(List<PuntoInterese> listaPoi) {

        if (listaPoi != null
                && !listaPoi.isEmpty()) {
            String seleccionar = this.mapaActivity.getString(R.string.seleccionar_poi);
            listaPoi.add(0, new PuntoInterese(Constantes.ID_FICTICIO, seleccionar, seleccionar));
            ArrayAdapter<PuntoInterese> adapter = new ArrayAdapter<>(this.mapaActivity, R.layout.drawer_list_item, listaPoi);
            this.spinnerPoi.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            this.mapaActivity.invalidateOptionsMenu();
        }

    }

    public void amosarListaPercorrido(List<PercorridoParam> listaPercorrido) {

        if (listaPercorrido != null
                && !listaPercorrido.isEmpty()) {
            List<PercorridoCustom> listaPercorridoCustom = new ArrayList<>(listaPercorrido.size());
            for (PercorridoParam percorridoParam : listaPercorrido) {
                listaPercorridoCustom.add(new PercorridoCustom(percorridoParam.getIdPercorrido(), percorridoParam.getNome(), percorridoParam.getDescricion(), percorridoParam.getIdEdificio()));
            }

            String seleccionar = this.mapaActivity.getString(R.string.seleccionar_percorrido);
            listaPercorridoCustom.add(0, new PercorridoCustom(Constantes.ID_FICTICIO, seleccionar, seleccionar, null));
            ArrayAdapter<PercorridoCustom> adapter = new ArrayAdapter<>(this.mapaActivity, R.layout.drawer_list_item, listaPercorridoCustom);
            this.spinnerPercorrido.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            this.mapaActivity.invalidateOptionsMenu();
        }

    }

    //Servizos
    private void recuperarPuntoPercorrido(Integer idPercorrido) {
        this.recuperarPuntoInteresePercorrido = new RecuperarPuntoInteresePercorrido();
        this.recuperarPuntoInteresePercorrido.setSelectorPoiPercorrido(this);
        this.recuperarPuntoInteresePercorrido.execute(idPercorrido.toString());
    }

    public void recuperarListaPercorrido(String idEdificioExterno) {
        this.recuperarPercorrido = new RecuperarPercorrido();
        this.recuperarPercorrido.setSelectorPoiPercorrido(this);
        this.recuperarPercorrido.execute(idEdificioExterno);
    }

    public MapaActivity getMapaActivity() {
        return this.mapaActivity;
    }

    public PercorridoCustom getPercorridoSeleccionado() {
        return this.percorridoSeleccionado;
    }
}
