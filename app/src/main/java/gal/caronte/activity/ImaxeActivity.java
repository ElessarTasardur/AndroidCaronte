package gal.caronte.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import gal.caronte.R;
import gal.caronte.servizo.EliminarImaxe;
import gal.caronte.util.Constantes;

public class ImaxeActivity extends AppCompatActivity {

    private static final String TAG = ImaxeActivity.class.getSimpleName();

    private EliminarImaxe eliminarImaxe;

    private Integer idImaxe;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imaxe);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarImaxe);
        setSupportActionBar(toolbar);

        //Recuperamos a informacion do intent
        String rutaImaxe = getIntent().getStringExtra(Constantes.IMAXE);
        String nomeImaxe = getIntent().getStringExtra(Constantes.NOME_IMAXE);
        this.idImaxe = getIntent().getIntExtra(Constantes.ID_IMAXE, -1);

        setTitle(nomeImaxe);

        Bitmap imaxe = null;
        try {
            Uri uriImaxe = null;
            if (rutaImaxe.startsWith("file")) {
                uriImaxe = Uri.parse(rutaImaxe);
            }
            else {
                uriImaxe = Uri.fromFile(new File(rutaImaxe));
            }

            imaxe = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriImaxe);
        }
        catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        ImageView imageViewImaxePoi = this.findViewById(R.id.imageViewImaxePoi);
        imageViewImaxePoi.setImageBitmap(imaxe);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_imaxe, menu);

        //Se esta activo o modo edicion, mostramos o boton
        MenuItem botonEliminarImaxe = menu.findItem(R.id.accion_eliminar_imaxe);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_eliminar_imaxe:
                this.eliminarImaxe = new EliminarImaxe();
                this.eliminarImaxe.setImaxeActivity(this);
                this.eliminarImaxe.execute(this.idImaxe);
                return true;

            default:
                // En caso de que non identifiquemos a accion
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public Integer getIdImaxe() {
        return this.idImaxe;
    }
}
