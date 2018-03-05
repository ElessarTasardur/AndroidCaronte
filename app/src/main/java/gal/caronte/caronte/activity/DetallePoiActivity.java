package gal.caronte.caronte.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import gal.caronte.caronte.R;
import gal.caronte.caronte.custom.sw.PuntoInterese;

/**
 * Created by ElessarTasardur on 04/03/2018.
 */

public class DetallePoiActivity extends AppCompatActivity {

    private static final String TAG = DetallePoiActivity.class.getSimpleName();
    private static final String PUNTO_INTERESE = "puntoInterese";
    private static final String EDICION = "edicion";

    private PuntoInterese poi;
    private boolean edicion;

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
        this.edicion = bundle.getBoolean(EDICION);

        //EditText
        EditText editTextNome = this.findViewById(R.id.editTextNome);
        editTextNome.setText(poi.getNome());
        editTextNome.setEnabled(this.edicion);

        EditText editTextDescricion = this.findViewById(R.id.editTextDescricion);
        editTextDescricion.setText(poi.getDescricion());
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
