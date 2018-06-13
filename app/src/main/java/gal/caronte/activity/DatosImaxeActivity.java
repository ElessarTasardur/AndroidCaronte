package gal.caronte.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

import gal.caronte.R;
import gal.caronte.custom.sw.PuntoInterese;
import gal.caronte.custom.sw.SubirImaxeCustom;
import gal.caronte.servizo.SubirImaxe;
import gal.caronte.util.Constantes;

public class DatosImaxeActivity extends AppCompatActivity {

    private static final String TAG = DetallePoiActivity.class.getSimpleName();

    private PuntoInterese poi;
    private File imaxe;

    private SubirImaxe subirImaxe;

    private EditText editTextNomeImaxe;
    private EditText editTextDescricionImaxe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_imaxe);
        setTitle(getString(R.string.title_activity_datos_imaxe));

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarDatosImaxe);
        setSupportActionBar(toolbar);

        //Recuperamos a informacion do intent
        this.imaxe = (File) getIntent().getSerializableExtra(Constantes.FILE);
        this.poi = getIntent().getExtras().getParcelable(Constantes.PUNTO_INTERESE);

        //EditText
        this.editTextNomeImaxe = this.findViewById(R.id.editTextNomeImaxe);
        this.editTextDescricionImaxe = this.findViewById(R.id.editTextDescricionImaxe);

        final Button botonGardar = this.findViewById(R.id.buttonGardarImaxe);
        botonGardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DatosImaxeActivity.this.editTextNomeImaxe.getText().toString().isEmpty()
                        && !DatosImaxeActivity.this.editTextDescricionImaxe.getText().toString().isEmpty()) {
                    botonGardar.setEnabled(false);
                    subirImaxe(new SubirImaxeCustom(DatosImaxeActivity.this.poi.getPosicion().getIdEdificio(), DatosImaxeActivity.this.poi.getIdPuntoInterese(),
                            DatosImaxeActivity.this.editTextNomeImaxe.getText().toString(), DatosImaxeActivity.this.editTextDescricionImaxe.getText().toString(),
                            DatosImaxeActivity.this.imaxe));
                }
            }
        });

        final Button botonCancelar = this.findViewById(R.id.buttonCancelarImaxe);
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelar();
            }
        });

    }

    @Override
    protected void onStop() {

        if (this.subirImaxe != null) {
            this.subirImaxe.cancel(true);
        }
        super.onStop();
    }

    private void subirImaxe(SubirImaxeCustom subirImaxeCustom) {
        this.subirImaxe = new SubirImaxe();
        this.subirImaxe.setDatosImaxeActivity(this);
        this.subirImaxe.execute(subirImaxeCustom);
    }

    @Override
    public void onBackPressed() {
        cancelar();
    }

    private void cancelar() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
