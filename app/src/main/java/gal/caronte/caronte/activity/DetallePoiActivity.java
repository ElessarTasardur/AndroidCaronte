package gal.caronte.caronte.activity;

import android.content.Intent;
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
import gal.caronte.caronte.servizo.GardarPoi;
import gal.caronte.caronte.servizo.RecuperarPoi;
import gal.caronte.caronte.util.Constantes;
import gal.caronte.caronte.view.SpinnerPercorrido;

/**
 * Created by ElessarTasardur on 04/03/2018.
 */

public class DetallePoiActivity extends AppCompatActivity {

    private static final String TAG = DetallePoiActivity.class.getSimpleName();
    private static final String PUNTO_INTERESE = "puntoInterese";
    private static final String MODO = "modo";

    private GardarPoi gardarPoi;
    private PuntoInterese poi;
    private Short modo;

    private EditText editTextNome;
    private EditText editTextDescricion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_poi);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPoi);
        setSupportActionBar(toolbar);

        //Recuperamos a informacion do intent
        Bundle bundle = getIntent().getExtras();
        this.poi = bundle.getParcelable(PUNTO_INTERESE);
        this.modo = bundle.getShort(MODO);

        //EditText
        this.editTextNome = this.findViewById(R.id.editTextNome);
        this.editTextNome.setText(poi.getNome());
        activarTexto(this.editTextNome);

        this.editTextDescricion = this.findViewById(R.id.editTextDescricion);
        this.editTextDescricion.setText(poi.getDescricion());
        activarTexto(this.editTextDescricion);

        Button botonGardar = this.findViewById(R.id.buttonGardar);
        botonGardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gardarInformacionPoi();
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
    private void gardarInformacionPoi() {
        this.gardarPoi = new GardarPoi();

        String novoNome = this.editTextNome.getText().toString();
        if (novoNome != null
                && !novoNome.isEmpty()
                && !this.poi.getNome().equals(novoNome)) {
            this.poi.setNome(novoNome);
        }

        String novaDescricion = this.editTextDescricion.getText().toString();
        if (novaDescricion != null
                && !novaDescricion.isEmpty()
                && !this.poi.getDescricion().equals(novaDescricion)) {
            this.poi.setDescricion(novaDescricion);
        }

        this.gardarPoi.setDetallePoiActivity(this);
        this.gardarPoi.execute(this.poi);
    }

}
