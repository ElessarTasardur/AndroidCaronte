package gal.caronte.caronte.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import gal.caronte.caronte.R;
import gal.caronte.caronte.custom.sw.GardarPercorridoParam;
import gal.caronte.caronte.custom.sw.Percorrido;
import gal.caronte.caronte.custom.sw.PuntoInterese;
import gal.caronte.caronte.servizo.GardarPercorrido;
import gal.caronte.caronte.util.Constantes;

/**
 * Created by ElessarTasardur on 04/03/2018.
 */

public class DetallePercorridoActivity extends AppCompatActivity {

    private static final String TAG = DetallePercorridoActivity.class.getSimpleName();
    private static final String ID_PERCORRIDO = "idPercorrido";
    private static final String NOME_PERCORRIDO = "nomePercorrido";
    private static final String DESCRICION_PERCORRIDO = "descricionPercorrido";
    private static final String ID_EDIFICIO = "idEdificio";
    private static final String MODO = "modo";

    private Integer idPercorrido;
    private String nomePercorrido;
    private String descricionPercorrido;
    private Integer idEdificio;
    private Short modo;

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
        this.idPercorrido = bundle.getInt(ID_PERCORRIDO);
        this.nomePercorrido = bundle.getString(NOME_PERCORRIDO);
        this.descricionPercorrido = bundle.getString(DESCRICION_PERCORRIDO);
        this.idEdificio = bundle.getInt(ID_EDIFICIO);
        this.modo = bundle.getShort(MODO);

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
        if (this.modo != null && Constantes.MODIFICACION.equals(this.modo)) {
            botonGardar.setVisibility(View.VISIBLE);
        }
        else {
            botonGardar.setVisibility(View.INVISIBLE);
        }
    }

    private void activarTexto(EditText editText) {
        boolean activar = this.modo != null && (Constantes.CREACION.equals(this.modo) || Constantes.MODIFICACION.equals(this.modo));
        editText.setEnabled(activar);
    }

    //Servizos
    private void gardarInformacionPercorrido() {
        this.gardarPercorrido = new GardarPercorrido();
        boolean cambio = false;
        Percorrido percorrido = new Percorrido(this.idPercorrido, this.nomePercorrido, this.descricionPercorrido, this.idEdificio);

        String novoNome = this.editTextNome.getText().toString();
        if (novoNome != null
                && !novoNome.isEmpty()
                && !this.nomePercorrido.equals(novoNome)) {
            percorrido.setNome(novoNome);
            cambio = true;
        }

        String novaDescricion = this.editTextDescricion.getText().toString();
        if (novaDescricion != null
                && !novaDescricion.isEmpty()
                && !this.descricionPercorrido.equals(novaDescricion)) {
            percorrido.setDescricion(novaDescricion);
            cambio = true;
        }

        if (cambio) {
            this.gardarPercorrido.setDetallePercorridoActivity(this);
            GardarPercorridoParam gpp = new GardarPercorridoParam();
            gpp.setPercorrido(percorrido);
            gpp.setListaPoi(new ArrayList<PuntoInterese>(1));
            this.gardarPercorrido.execute(gpp);
        }
    }

}
