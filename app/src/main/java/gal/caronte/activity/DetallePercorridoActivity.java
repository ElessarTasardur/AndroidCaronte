package gal.caronte.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import gal.caronte.R;
import gal.caronte.custom.sw.GardarPercorridoParam;
import gal.caronte.custom.sw.PercorridoParam;
import gal.caronte.custom.sw.PuntoInterese;
import gal.caronte.servizo.EliminarPercorrido;
import gal.caronte.servizo.GardarPercorrido;
import gal.caronte.util.Constantes;
import gal.caronte.util.EModoMapa;

/**
 * Created by ElessarTasardur on 04/03/2018.
 */

public class DetallePercorridoActivity extends AppCompatActivity {

    private static final String TAG = DetallePercorridoActivity.class.getSimpleName();

    private Integer idPercorrido;
    private String nomePercorrido;
    private String descricionPercorrido;
    private Integer idEdificio;
    private Integer tempoCaminho;
    private Integer tempoTotal;
    private List<PuntoInterese> listaPuntoInterese;
    private EModoMapa modo;

    private EditText editTextNome;
    private EditText editTextDescricion;
    private EditText editTextTempoCaminho;
    private EditText editTextTempoTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_percorrido);
        setTitle(getString(R.string.title_activity_detalle_percorrido));

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPercorrido);
        setSupportActionBar(toolbar);

        //Recuperamos a informacion do intent
        Bundle bundle = getIntent().getExtras();
        this.idPercorrido = bundle.getInt(Constantes.ID_PERCORRIDO);
        if (this.idPercorrido.equals(Constantes.ID_FICTICIO)) {
            this.idPercorrido = null;
        }
        else {
            this.nomePercorrido = bundle.getString(Constantes.NOME_PERCORRIDO);
            this.descricionPercorrido = bundle.getString(Constantes.DESCRICION_PERCORRIDO);
        }
        this.idEdificio = bundle.getInt(Constantes.ID_EDIFICIO);
        this.tempoTotal = bundle.getInt(Constantes.TEMPO_TOTAL);
        this.tempoCaminho = bundle.getInt(Constantes.TEMPO_CAMINHO);

        this.listaPuntoInterese = bundle.getParcelableArrayList(Constantes.LISTA_PUNTO_INTERESE);
        this.modo = (EModoMapa) getIntent().getSerializableExtra(Constantes.MODO);

        //EditText
        this.editTextNome = this.findViewById(R.id.editTextNome);
        this.editTextNome.setText(this.nomePercorrido);
        activarTexto(this.editTextNome);

        this.editTextDescricion = this.findViewById(R.id.editTextDescricion);
        this.editTextDescricion.setText(this.descricionPercorrido);
        activarTexto(this.editTextDescricion);

        this.editTextTempoCaminho = this.findViewById(R.id.editTextTempoCaminho);
        this.editTextTempoCaminho.setText(this.tempoCaminho.toString());
        this.editTextTempoCaminho.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                String valor = DetallePercorridoActivity.this.editTextTempoCaminho.getText().toString();
                Integer novoValor = DetallePercorridoActivity.this.tempoTotal - DetallePercorridoActivity.this.tempoCaminho;
                if (!valor.isEmpty()) {
                    novoValor = novoValor + Integer.valueOf(valor);
                }
                DetallePercorridoActivity.this.editTextTempoTotal.setText(novoValor.toString());

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        activarTexto(this.editTextTempoCaminho);

        this.editTextTempoTotal = this.findViewById(R.id.editTextTempoTotal);
        this.editTextTempoTotal.setText(this.tempoTotal.toString());

        final Button botonGardar = this.findViewById(R.id.buttonGardar);
        botonGardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botonGardar.setEnabled(false);
                gardarInformacionPercorrido();
            }
        });

        final Button botonEliminar = this.findViewById(R.id.buttonEliminar);
        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botonEliminar.setEnabled(false);
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
                && (this.modo.equals(EModoMapa.CREAR_PERCORRIDO)
                        || this.modo.equals(EModoMapa.MODIFICAR_POI_PERCORRIDO))) {
            botonGardar.setVisibility(View.VISIBLE);
            botonEliminar.setVisibility(View.INVISIBLE);
        }
        else {
            botonGardar.setVisibility(View.INVISIBLE);
            botonEliminar.setVisibility(View.INVISIBLE);
        }
    }

    private void activarTexto(EditText editText) {
        boolean activar = this.modo != null && (this.modo.equals(EModoMapa.CREAR_PERCORRIDO) || this.modo.equals(EModoMapa.EDICION) || this.modo.equals(EModoMapa.MODIFICAR_POI_PERCORRIDO));
        editText.setEnabled(activar);
    }

    //Servizos
    private void gardarInformacionPercorrido() {
        GardarPercorrido gardarPercorrido = new GardarPercorrido();
        boolean cambio = false;
        PercorridoParam percorridoParam = new PercorridoParam(this.idPercorrido, this.nomePercorrido, this.descricionPercorrido, this.idEdificio, this.tempoTotal, this.tempoCaminho);

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

        String valorTempoCaminhoStr = this.editTextTempoCaminho.getText().toString();
        Integer valorTempoCaminho = 0;
        if (!valorTempoCaminhoStr.isEmpty()) {
            valorTempoCaminho = Integer.valueOf(valorTempoCaminhoStr);
        }
        if (!valorTempoCaminho.equals(this.tempoCaminho)) {
            percorridoParam.setTempoCaminho(valorTempoCaminho);
            percorridoParam.setTempoTotal(Integer.valueOf(this.editTextTempoTotal.getText().toString()));
            cambio = true;
        }

        //Se se modificou algun POI do percorrido tamen permitimos gardar
        if (this.modo.equals(EModoMapa.MODIFICAR_POI_PERCORRIDO)) {
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

}
