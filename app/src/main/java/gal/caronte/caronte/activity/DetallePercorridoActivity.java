package gal.caronte.caronte.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import gal.caronte.caronte.R;

/**
 * Created by ElessarTasardur on 04/03/2018.
 */

public class DetallePercorridoActivity extends AppCompatActivity {

    private static final String TAG = DetallePercorridoActivity.class.getSimpleName();
    private static final String ID_PERCORRIDO = "idPercorrido";
    private static final String NOME_PERCORRIDO = "nomePercorrido";
    private static final String DESCRICION_PERCORRIDO = "descricionPercorrido";
    private static final String EDICION = "edicion";

    private Integer idPercorrido;
    private String nomePercorrido;
    private String descricionPercorrido;
    private boolean edicion;

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
        this.edicion = bundle.getBoolean(EDICION);

        //EditText
        EditText editTextNome = this.findViewById(R.id.editTextNome);
        editTextNome.setText(this.nomePercorrido);
        editTextNome.setEnabled(this.edicion);

        EditText editTextDescricion = this.findViewById(R.id.editTextDescricion);
        editTextDescricion.setText(this.descricionPercorrido);
        editTextDescricion.setEnabled(this.edicion);

        Button botonGardar = this.findViewById(R.id.buttonGardar);
        if (this.edicion) {
            botonGardar.setVisibility(View.VISIBLE);
        }
        else {
            botonGardar.setVisibility(View.INVISIBLE);
        }
    }

}
