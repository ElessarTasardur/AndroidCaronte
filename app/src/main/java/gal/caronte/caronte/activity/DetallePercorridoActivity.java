package gal.caronte.caronte.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import gal.caronte.caronte.R;
import gal.caronte.caronte.custom.sw.GardarPercorridoParam;
import gal.caronte.caronte.custom.sw.Percorrido;
import gal.caronte.caronte.custom.sw.PuntoInterese;
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

    private GardarPercorrido gardarPercorrido;

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
        this.nomePercorrido = bundle.getString(Constantes.NOME_PERCORRIDO);
        this.descricionPercorrido = bundle.getString(Constantes.DESCRICION_PERCORRIDO);
        this.idEdificio = bundle.getInt(Constantes.ID_EDIFICIO);
        this.idEdificio = bundle.getInt(Constantes.ID_EDIFICIO);
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
        if (this.modo != null
                && (this.modo.equals(EModoMapa.EDICION)
                        || this.modo.equals(EModoMapa.CREAR_PERCORRIDO))) {
            botonGardar.setVisibility(View.VISIBLE);
        }
        else {
            botonGardar.setVisibility(View.INVISIBLE);
        }
    }

    private void activarTexto(EditText editText) {
        boolean activar = this.modo != null && (this.modo.equals(EModoMapa.CREAR_PERCORRIDO) || this.modo.equals(EModoMapa.EDICION));
        editText.setEnabled(activar);
    }

    //Servizos
    private void gardarInformacionPercorrido() {
        this.gardarPercorrido = new GardarPercorrido();
        boolean cambio = false;
        Percorrido percorrido = new Percorrido(this.idPercorrido, this.nomePercorrido, this.descricionPercorrido, this.idEdificio);

        String novoNome = this.editTextNome.getText().toString();
        if (!novoNome.isEmpty()
                && !this.nomePercorrido.equals(novoNome)) {
            percorrido.setNome(novoNome);
            cambio = true;
        }

        String novaDescricion = this.editTextDescricion.getText().toString();
        if (!novaDescricion.isEmpty()
                && !this.descricionPercorrido.equals(novaDescricion)) {
            percorrido.setDescricion(novaDescricion);
            cambio = true;
        }

        if (cambio) {
            this.gardarPercorrido.setDetallePercorridoActivity(this);
            GardarPercorridoParam gpp = new GardarPercorridoParam();
            gpp.setPercorrido(percorrido);
            gpp.setListaPoi(this.listaPuntoInterese);
            this.gardarPercorrido.execute(gpp);
        }
    }

}
