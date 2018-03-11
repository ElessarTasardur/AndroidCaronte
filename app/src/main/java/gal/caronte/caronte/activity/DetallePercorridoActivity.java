package gal.caronte.caronte.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import gal.caronte.caronte.R;
import gal.caronte.caronte.custom.sw.GardarPercorridoParam;
import gal.caronte.caronte.custom.sw.PercorridoParam;
import gal.caronte.caronte.custom.sw.PuntoInterese;
import gal.caronte.caronte.servizo.EliminarPercorrido;
import gal.caronte.caronte.servizo.GardarPercorrido;
import gal.caronte.caronte.util.Constantes;
import gal.caronte.caronte.util.EModoMapa;

/**
 * Created by ElessarTasardur on 04/03/2018.
 */

public class DetallePercorridoActivity extends AppCompatActivity {

    private static final String TAG = DetallePercorridoActivity.class.getSimpleName();

    private Integer idPercorrido;
    private String nomePercorrido;
    private String descricionPercorrido;
    private Integer idEdificio;
    private List<PuntoInterese> listaPuntoInterese;
    private EModoMapa modo;

    private EditText editTextNome;
    private EditText editTextDescricion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_percorrido);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPercorrido);
        setSupportActionBar(toolbar);

        //Recuperamos a informacion do intent
        Bundle bundle = getIntent().getExtras();
        this.idPercorrido = bundle.getInt(Constantes.ID_PERCORRIDO);
        if (this.idPercorrido.intValue() == 0) {
            this.idPercorrido = null;
        }
        this.nomePercorrido = bundle.getString(Constantes.NOME_PERCORRIDO);
        this.descricionPercorrido = bundle.getString(Constantes.DESCRICION_PERCORRIDO);
        this.idEdificio = bundle.getInt(Constantes.ID_EDIFICIO);
        this.listaPuntoInterese = bundle.getParcelableArrayList(Constantes.LISTA_PUNTO_INTERESE);
        this.modo = (EModoMapa) getIntent().getSerializableExtra(Constantes.MODO);

        //EditText
        this.editTextNome = this.findViewById(R.id.editTextNome);
        this.editTextNome.setText(this.nomePercorrido);
        activarTexto(this.editTextNome);

        this.editTextDescricion = this.findViewById(R.id.editTextDescricion);
        this.editTextDescricion.setText(this.descricionPercorrido);
        activarTexto(this.editTextDescricion);

        Button botonGardar = this.findViewById(R.id.buttonGardar);
        botonGardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gardarInformacionPercorrido();
            }
        });

        Button botonEliminar = this.findViewById(R.id.buttonEliminar);
        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarPercorrido();
            }
        });

        if (this.modo != null
                && this.modo.equals(EModoMapa.EDICION)) {
            botonGardar.setVisibility(View.VISIBLE);
            if (this.idPercorrido != null) {
                botonEliminar.setVisibility(View.VISIBLE);
            }
        }
        else if (this.modo != null
                && this.modo.equals(EModoMapa.CREAR_PERCORRIDO)) {
            botonGardar.setVisibility(View.VISIBLE);
            botonEliminar.setVisibility(View.INVISIBLE);
        }
        else {
            botonGardar.setVisibility(View.INVISIBLE);
            botonEliminar.setVisibility(View.INVISIBLE);
        }
    }

    private void activarTexto(EditText editText) {
        boolean activar = this.modo != null && (this.modo.equals(EModoMapa.CREAR_PERCORRIDO) || this.modo.equals(EModoMapa.EDICION));
        editText.setEnabled(activar);
    }

    //Servizos
    private void gardarInformacionPercorrido() {
        GardarPercorrido gardarPercorrido = new GardarPercorrido();
        boolean cambio = false;
        PercorridoParam percorridoParam = new PercorridoParam(this.idPercorrido, this.nomePercorrido, this.descricionPercorrido, this.idEdificio);

        String novoNome = this.editTextNome.getText().toString();
        if (!novoNome.isEmpty()
                && !novoNome.equals(this.nomePercorrido)) {
            percorridoParam.setNome(novoNome);
            cambio = true;
        }

        String novaDescricion = this.editTextDescricion.getText().toString();
        if (!novaDescricion.isEmpty()
                && !novaDescricion.equals(this.descricionPercorrido)) {
            percorridoParam.setDescricion(novaDescricion);
            cambio = true;
        }

        if (cambio
                && percorridoParam.getNome() != null
                && !percorridoParam.getNome().isEmpty()) {
            gardarPercorrido.setDetallePercorridoActivity(this);
            GardarPercorridoParam gpp = new GardarPercorridoParam();
            gpp.setPercorrido(percorridoParam);
            gpp.setListaPoi(this.listaPuntoInterese);
            gardarPercorrido.execute(gpp);
        }
    }

    private void eliminarPercorrido() {
        EliminarPercorrido eliminarPercorrido = new EliminarPercorrido();
        eliminarPercorrido.setDetallePercorridoActivity(this);
        eliminarPercorrido.execute(this.idPercorrido);
    }

}
