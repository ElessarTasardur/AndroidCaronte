package gal.caronte.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import gal.caronte.R;
import gal.caronte.activity.DetallePercorridoActivity;
import gal.caronte.activity.DetallePoiActivity;
import gal.caronte.activity.MapaActivity;
import gal.caronte.custom.MarcadorCustom;
import gal.caronte.custom.PercorridoCustom;
import gal.caronte.custom.sw.PercorridoParam;
import gal.caronte.custom.sw.PuntoInterese;
import gal.caronte.custom.sw.PuntoInteresePosicion;
import gal.caronte.servizo.RecuperarPercorrido;
import gal.caronte.servizo.RecuperarPuntoInteresePercorrido;
import gal.caronte.util.Constantes;
import gal.caronte.util.EModoMapa;
import gal.caronte.util.StringUtil;

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
    private ImageButton botonPercorridoEngadirPoi;

    private Spinner spinnerPoi;
    private ImageButton botonPoi;

    private PercorridoCustom percorridoSeleccionado;
    private PuntoInterese poiSeleccionado;

    private MapaActivity mapaActivity;

    //Para evitar parte da loxica cando se selecciona un elemento directamente
    private boolean seleccionarElemento;

    //Para evitar o listener cando seleccionamos automaticamente un elemento
    private boolean seleccionAutomatica = false;

    public SelectorPoiPercorrido(MapaActivity mapaActivity) {
        super();
        this.mapaActivity = mapaActivity;

        crearSpinnerPercorrido();
        crearBotonPercorrido();
        crearBotonPercorridoEngadirPoi();
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

                //Se a seleccion non e automatica, executamos o listener
                if (!SelectorPoiPercorrido.this.seleccionAutomatica) {

                    SelectorPoiPercorrido.this.percorridoSeleccionado = (PercorridoCustom) SelectorPoiPercorrido.this.spinnerPercorrido.getSelectedItem();
                    Log.i(TAG, StringUtil.creaString("Seleccionado o percorrido: ", SelectorPoiPercorrido.this.percorridoSeleccionado));

                    if (!Constantes.ID_FICTICIO.equals(SelectorPoiPercorrido.this.percorridoSeleccionado.getIdPercorrido())) {

                        SelectorPoiPercorrido.this.mapaActivity.ocultarTodosPoi();
                        SelectorPoiPercorrido.this.mapaActivity.ocultarPercorrido();

                        //Se hai POIs no spinner seleccionamos o primeiro
                        if (SelectorPoiPercorrido.this.spinnerPoi.getAdapter() != null
                                && SelectorPoiPercorrido.this.spinnerPoi.getAdapter().getCount() > 0
                                && SelectorPoiPercorrido.this.spinnerPoi.getSelectedItemPosition() != 0) {
                            SelectorPoiPercorrido.this.seleccionAutomatica = true;
                            SelectorPoiPercorrido.this.spinnerPoi.setSelection(0, false);
                        }

                        if (SelectorPoiPercorrido.this.percorridoSeleccionado.getListaPIP().isEmpty()) {
                            recuperarPuntoPercorrido(SelectorPoiPercorrido.this.percorridoSeleccionado.getIdPercorrido());
                        } else {
                            SelectorPoiPercorrido.this.mapaActivity.amosarPercorrido(SelectorPoiPercorrido.this.percorridoSeleccionado.getListaPIP());
                        }

                        //Mostramos o boton para abrir o detalle
                        SelectorPoiPercorrido.this.botonPercorrido.setVisibility(View.VISIBLE);

                        //Se temos o modo edicion activado, mostramos o boton
                        if (EModoMapa.EDICION.equals(SelectorPoiPercorrido.this.mapaActivity.getModoMapa())) {
                            SelectorPoiPercorrido.this.botonPercorridoEngadirPoi.setVisibility(View.VISIBLE);
                        } else {
                            SelectorPoiPercorrido.this.botonPercorridoEngadirPoi.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        //Ocultamos o boton para abrir o detalle e o boton para engadir POIs
                        SelectorPoiPercorrido.this.botonPercorrido.setVisibility(View.INVISIBLE);
                        SelectorPoiPercorrido.this.botonPercorridoEngadirPoi.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    SelectorPoiPercorrido.this.seleccionAutomatica = false;
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
                b.putInt(Constantes.TEMPO_TOTAL, SelectorPoiPercorrido.this.percorridoSeleccionado.getTempoTotal());
                b.putInt(Constantes.TEMPO_CAMINHO, SelectorPoiPercorrido.this.percorridoSeleccionado.getTempoCaminho());

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

    private void crearBotonPercorridoEngadirPoi() {
        //Boton engadir POI a percorrido
        this.botonPercorridoEngadirPoi = this.mapaActivity.findViewById(R.id.image_button_percorrido_engadir_poi);
        this.botonPercorridoEngadirPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectorPoiPercorrido.this.mapaActivity.setModoMapa(EModoMapa.ENGADIR_POI_PERCORRIDO);
                SelectorPoiPercorrido.this.mapaActivity.invalidateOptionsMenu();
                SelectorPoiPercorrido.this.mapaActivity.amosarPoiPiso();
                //Cambiar o titulo da actividade
                SelectorPoiPercorrido.this.mapaActivity.setTitle(SelectorPoiPercorrido.this.mapaActivity.getString(R.string.seleccionar_poi));
            }
        });

    }

    private void crearSpinnerPoi() {
        this.spinnerPoi = this.mapaActivity.findViewById(R.id.spinner_pois);
        this.spinnerPoi.setVisibility(View.INVISIBLE);
        this.spinnerPoi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (!SelectorPoiPercorrido.this.seleccionAutomatica) {
                    SelectorPoiPercorrido.this.poiSeleccionado = (PuntoInterese) SelectorPoiPercorrido.this.spinnerPoi.getSelectedItem();
                    Log.i(TAG, StringUtil.creaString("Seleccionado o poi: ", SelectorPoiPercorrido.this.poiSeleccionado));

                    if (!Constantes.ID_FICTICIO.equals(SelectorPoiPercorrido.this.poiSeleccionado.getIdPuntoInterese())) {

                        //Se se selecciona o POI no spinner, realizamos as accions. Se se escolle a traves dun marcador, non
                        if (!SelectorPoiPercorrido.this.seleccionarElemento) {
                            SelectorPoiPercorrido.this.mapaActivity.ocultarTodosPoi();
                            SelectorPoiPercorrido.this.mapaActivity.ocultarPercorrido();
                            SelectorPoiPercorrido.this.mapaActivity.amosarPoi(SelectorPoiPercorrido.this.poiSeleccionado.getIdPuntoInterese());

                            //Seleccionamos o primeiro percorrido
                            if (SelectorPoiPercorrido.this.spinnerPercorrido.getAdapter() != null
                                    && SelectorPoiPercorrido.this.spinnerPercorrido.getAdapter().getCount() > 0
                                    && SelectorPoiPercorrido.this.spinnerPercorrido.getSelectedItemPosition() != 0) {
                                SelectorPoiPercorrido.this.seleccionAutomatica = true;
                                SelectorPoiPercorrido.this.spinnerPercorrido.setSelection(0, false);
                            }

                        }

                        //Mostramos o boton para abrir o detalle
                        SelectorPoiPercorrido.this.botonPoi.setVisibility(View.VISIBLE);

                    }
                    else {
                        SelectorPoiPercorrido.this.mapaActivity.ocultarTodosPoi();
//                    SelectorPoiPercorrido.this.mapaActivity.ocultarPercorrido();
                        //Ocultamos o boton para abrir o detalle
                        SelectorPoiPercorrido.this.botonPoi.setVisibility(View.INVISIBLE);
                    }

                    SelectorPoiPercorrido.this.seleccionarElemento = false;
                }
                else {
                    SelectorPoiPercorrido.this.seleccionAutomatica = false;
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

                intent.putExtra(Constantes.MODO, SelectorPoiPercorrido.this.mapaActivity.getModoMapa());

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
                //Se estamos no modo edicion, mostramos o boton de engadir POIs
                if (EModoMapa.EDICION.equals(this.mapaActivity.getModoMapa())) {
                    this.botonPercorridoEngadirPoi.setVisibility(View.VISIBLE);
                }
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
        this.botonPercorridoEngadirPoi.setVisibility(View.INVISIBLE);
    }

    public void ocultarSpinnerPoi() {
        this.spinnerPoi.setVisibility(View.INVISIBLE);
        this.botonPoi.setVisibility(View.INVISIBLE);
    }

    public boolean isSpinnerPoiVisible() {
        return this.spinnerPoi.getVisibility() == View.VISIBLE;
    }

    public boolean isSpinnerPercorridoVisible() {
        return this.spinnerPercorrido.getVisibility() == View.VISIBLE;
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

    public void seleccionarPoi(Integer idPoi) {
        if (this.spinnerPoi.getAdapter() != null
                && this.spinnerPoi.getAdapter().getCount() > 0) {
            SpinnerAdapter nha = this.spinnerPoi.getAdapter();
            int indice;
            for (indice = 0; indice < nha.getCount(); indice++) {
                if (((PuntoInterese) nha.getItem(indice)).getIdPuntoInterese().equals(idPoi)) {
                    break;
                }
            }
            this.seleccionarElemento = true;
            this.spinnerPoi.setSelection(indice);
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
            listaPoi.add(0, new PuntoInterese(Constantes.ID_FICTICIO, seleccionar, seleccionar, null));
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
                listaPercorridoCustom.add(new PercorridoCustom(percorridoParam.getIdPercorrido(), percorridoParam.getNome(), percorridoParam.getDescricion(), percorridoParam.getIdEdificio(), percorridoParam.getTempoTotal(), percorridoParam.getTempoCaminho()));
            }

            String seleccionar = this.mapaActivity.getString(R.string.seleccionar_percorrido);
            listaPercorridoCustom.add(0, new PercorridoCustom(Constantes.ID_FICTICIO, seleccionar, seleccionar, null, 0, 0));
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
