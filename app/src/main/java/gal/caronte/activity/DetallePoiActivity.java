package gal.caronte.activity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import gal.caronte.R;
import gal.caronte.custom.sw.ImaxeCustom;
import gal.caronte.custom.sw.PuntoInterese;
import gal.caronte.custom.sw.SubirImaxeCustom;
import gal.caronte.servizo.EliminarPoi;
import gal.caronte.servizo.GardarPoi;
import gal.caronte.servizo.RecuperarDatosImaxe;
import gal.caronte.servizo.RecuperarImaxe;
import gal.caronte.servizo.SubirImaxe;
import gal.caronte.util.Constantes;
import gal.caronte.util.EModoMapa;
import gal.caronte.util.PermisosUtil;
import gal.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 04/03/2018.
 */

public class DetallePoiActivity extends AppCompatActivity {

    private static final String TAG = DetallePoiActivity.class.getSimpleName();

    private static int RESULTADO_SELECCIONAR_IMAXE = 1;
    private static int RESULTADO_SACAR_FOTO = 2;

    private GardarPoi gardarPoi;
    private RecuperarDatosImaxe recuperarDatosImaxe;
    private EliminarPoi eliminarPoi;
    private RecuperarImaxe recuperarImaxe;

    private PuntoInterese poi;
    private EModoMapa modo;

    private EditText editTextNome;
    private EditText editTextDescricion;
    private EditText editTextTempo;

    private Button botonVerImaxe;

    private Integer MENU_ID_IMAXE = 1654984681;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_poi);
        setTitle(getString(R.string.title_activity_detalle_poi));

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPoi);
        setSupportActionBar(toolbar);

        //Recuperamos a informacion do intent
        Bundle bundle = getIntent().getExtras();
        this.poi = bundle.getParcelable(Constantes.PUNTO_INTERESE);
        this.modo = (EModoMapa) getIntent().getSerializableExtra(Constantes.MODO);

        if (Constantes.ID_FICTICIO.equals(this.poi.getIdPuntoInterese())) {
            this.poi.setIdPuntoInterese(null);
        }
        else if (this.poi.getListaIdImaxe() != null
                && !this.poi.getListaIdImaxe().isEmpty()
                && (this.poi.getListaImaxe() == null || this.poi.getListaImaxe().isEmpty())) {
            recuperarDatosImaxe();
        }

        //EditText
        this.editTextNome = this.findViewById(R.id.editTextNome);
        this.editTextNome.setText(this.poi.getNome());
        activarTexto(this.editTextNome);

        this.editTextDescricion = this.findViewById(R.id.editTextDescricion);
        this.editTextDescricion.setText(this.poi.getDescricion());
        activarTexto(this.editTextDescricion);

        this.editTextTempo = this.findViewById(R.id.editTextTempoAprox);
        this.editTextTempo.setText(this.poi.getTempo().toString());
        activarTexto(this.editTextTempo);

        this.botonVerImaxe = this.findViewById(R.id.buttonVerImaxe);
        registerForContextMenu(this.botonVerImaxe);
        this.botonVerImaxe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContextMenu(DetallePoiActivity.this.botonVerImaxe);
            }
        });
        if (this.poi.getListaIdImaxe() != null
                && !this.poi.getListaIdImaxe().isEmpty()) {
            this.botonVerImaxe.setVisibility(View.VISIBLE);
        }
        else {
            this.botonVerImaxe.setVisibility(View.INVISIBLE);
        }

        final Button botonSubirImaxe = this.findViewById(R.id.buttonSubirImaxe);
        registerForContextMenu(botonSubirImaxe);
        botonSubirImaxe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContextMenu(botonSubirImaxe);
            }
        });

        final Button botonGardar = this.findViewById(R.id.buttonGardar);
        botonGardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botonGardar.setEnabled(false);
                gardarInformacionPoi();
            }
        });

        final Button botonEliminar = this.findViewById(R.id.buttonEliminar);
        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botonEliminar.setEnabled(false);
                eliminarPoi();
            }
        });

        if (this.modo != null
                && this.modo.equals(EModoMapa.EDICION)) {
            botonGardar.setVisibility(View.VISIBLE);
            if (this.poi.getIdPuntoInterese() != null) {
                botonEliminar.setVisibility(View.VISIBLE);
                botonSubirImaxe.setVisibility(View.VISIBLE);
            }
        }
        else if (this.modo != null
                && this.modo.equals(EModoMapa.CREAR_POI)) {
            botonGardar.setVisibility(View.VISIBLE);
            botonEliminar.setVisibility(View.INVISIBLE);
            botonSubirImaxe.setVisibility(View.INVISIBLE);
        }
        else {
            botonGardar.setVisibility(View.INVISIBLE);
            botonEliminar.setVisibility(View.INVISIBLE);
            botonSubirImaxe.setVisibility(View.INVISIBLE);
        }

    }

    private void recuperarDatosImaxe() {
        this.recuperarDatosImaxe = new RecuperarDatosImaxe();
        this.recuperarDatosImaxe.setDetallePoiActivity(this);
        this.recuperarDatosImaxe.execute(StringUtil.convertirListaIntegerCSV(this.poi.getListaIdImaxe()));
    }

    private void eliminarPoi() {
        this.eliminarPoi = new EliminarPoi();
        this.eliminarPoi.setDetallePoiActivity(this);
        this.eliminarPoi.execute(this.poi.getIdPuntoInterese());
    }

    private void recuperarImaxe(ImaxeCustom imaxeCustom) {
        this.recuperarImaxe = new RecuperarImaxe();
        this.recuperarImaxe.setDetallePoiActivity(this);
        this.recuperarImaxe.execute(imaxeCustom);
    }

    private void activarTexto(EditText editText) {
        boolean activar = this.modo != null && (this.modo.equals(EModoMapa.CREAR_POI) || this.modo.equals(EModoMapa.EDICION));
        editText.setEnabled(activar);
    }

    //Servizos
    private void gardarInformacionPoi() {
        this.gardarPoi = new GardarPoi();

        String novoNome = this.editTextNome.getText().toString();
        if (!novoNome.isEmpty()
                && !this.poi.getNome().equals(novoNome)) {
            this.poi.setNome(novoNome);
        }

        String novaDescricion = this.editTextDescricion.getText().toString();
        if (!novaDescricion.isEmpty()
                && !this.poi.getDescricion().equals(novaDescricion)) {
            this.poi.setDescricion(novaDescricion);
        }

        Integer novoTempo = Integer.valueOf(this.editTextTempo.getText().toString());
        if (!novoTempo.equals(this.poi.getTempo())) {
            this.poi.setTempo(novoTempo);
        }

        this.gardarPoi.setDetallePoiActivity(this);
        this.gardarPoi.execute(this.poi);
    }

    @Override
    public void onBackPressed() {
        //TODO actualizarPOI na actividade principal aqui?
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    protected void onStop() {

        if (this.gardarPoi != null) {
            this.gardarPoi.cancel(true);
        }
        if (this.recuperarDatosImaxe != null) {
            this.recuperarDatosImaxe.cancel(true);
        }
        if (this.eliminarPoi != null) {
            this.eliminarPoi.cancel(true);
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK
                && data != null
                && (requestCode == RESULTADO_SELECCIONAR_IMAXE
                        || requestCode == RESULTADO_SACAR_FOTO)) {

            File imaxe = null;
            if (requestCode == RESULTADO_SELECCIONAR_IMAXE) {
                if (data.getData() != null) {
                    imaxe = new File(getPath(data.getData()));
                }
            }
            else if (requestCode == RESULTADO_SACAR_FOTO) {

                Bitmap foto = (Bitmap) data.getExtras().get("data");

                imaxe = new File(StringUtil.creaString(Environment.getExternalStorageDirectory(), File.separator, "temporary_file.png"));
                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(imaxe))) {
                    foto.compress(Bitmap.CompressFormat.PNG, 100, os);
                }
                catch (FileNotFoundException e) {
                    Log.e(TAG, "Erro ao convertir a foto", e);
                }
                catch (IOException e2) {
                    Log.e(TAG, "Erro ao convertir a foto", e2);
                }
            }

            if (imaxe != null) {
                Intent intent = new Intent(this, DatosImaxeActivity.class);

                //Engadimos a informacion do poi ao intent
                Bundle b = new Bundle();
                b.putParcelable(Constantes.PUNTO_INTERESE, this.poi);
                intent.putExtras(b);

                intent.putExtra(Constantes.FILE, imaxe);

                //Iniciamos a actividade do mapa
                startActivityForResult(intent, Constantes.ACTIVIDADE_DATOS_IMAXE);
            }

        }
        else if (resultCode == RESULT_OK
                && data != null
                && requestCode == Constantes.ACTIVIDADE_DATOS_IMAXE) {

            int idImaxe = data.getIntExtra(Constantes.ID_IMAXE, -1);
            if (idImaxe != -1) {
                this.poi.getListaIdImaxe().add(idImaxe);
                recuperarDatosImaxe();
                if (this.botonVerImaxe.getVisibility() == View.INVISIBLE) {
                    this.botonVerImaxe.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    public String getPath(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String res = cursor.getString(column_index);
        cursor.close();
        return res;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();

        if(v.getId() == R.id.buttonVerImaxe) {
            inflater.inflate(R.menu.menu_seleccionar_imaxe, menu);
            menu.setHeaderTitle(getString(R.string.poi_seleccionar_imaxe));

            int i = 0;
            for (Integer idImaxe : this.poi.getListaIdImaxe()) {
                menu.add(0, idImaxe, i, "Ver " + idImaxe);
                i++;
            }
        }
        else if(v.getId() == R.id.buttonSubirImaxe) {
            inflater.inflate(R.menu.menu_escoller_opcion_subida, menu);
            menu.setHeaderTitle(getString(R.string.poi_seleccionar_opcion));
        }


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.my_menu, menu);
//
//        getLayoutInflater().setFactory(new Factory() {
//            @Override
//            public View onCreateView(String name, Context context,
//                                     AttributeSet attrs) {
//
//                if (name .equalsIgnoreCase(“com.android.internal.view.menu.IconMenuItemView”)) {
//                    try {
//                        LayoutInflater f = getLayoutInflater();
//                        final View view = f.createView(name, null, attrs);
//
//                        new Handler().post(new Runnable() {
//                            public void run() {
//
//// set the background drawable
//                                view .setBackgroundResource(R.drawable.my_ac_menu_background);
//
//// set the text color
//                                ((TextView) view).setTextColor(Color.WHITE);
//                            }
//                        });
//                        return view;
//                    } catch (InflateException e) {
//                    } catch (ClassNotFoundException e) {
//                    }
//                }
//                return null;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.accion_camara) {
            //Comprobamos o permiso de localizacion para activar a localizacion
            boolean permisoEscritura = PermisosUtil.comprobarPermisos(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermisosUtil.CODIGO_SOLICITUDE_PERMISO_ESCRITURA_ALMACENAMENTO, true);
            boolean permisoCamara = PermisosUtil.comprobarPermisos(this, Manifest.permission.CAMERA, PermisosUtil.CODIGO_SOLICITUDE_PERMISO_CAMARA, true);
            if (permisoEscritura
                    && permisoCamara) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, RESULTADO_SACAR_FOTO);


//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_PICTURES), "IMG_FOLDER");
//
//                if (!mediaStorageDir.exists()) {
//                    if (!mediaStorageDir.mkdirs()) {
//                        return false;
//                    }
//                }
//
//                Uri imageURI = Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + "profile_img.jpg"));
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
//
//                startActivityForResult(intent, RESULTADO_SACAR_FOTO);
            }
        }
        else if (item.getItemId() == R.id.accion_galeria) {
            //Comprobamos o permiso de localizacion para activar a localizacion
            boolean permisoConcedido = PermisosUtil.comprobarPermisos(this, Manifest.permission.READ_EXTERNAL_STORAGE, PermisosUtil.CODIGO_SOLICITUDE_PERMISO_LECTURA_ALMACENAMENTO, true);
            if (permisoConcedido) {
                Intent seleccionarFoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(seleccionarFoto, RESULTADO_SELECCIONAR_IMAXE);
            }
        }
        else if (this.poi.getListaImaxe() != null) {
            for (ImaxeCustom imaxe : this.poi.getListaImaxe()) {
                if (imaxe.getIdImaxe().equals(item.getItemId())) {
                    //TODO Visualizar imaxe
                    imaxe.setIdEdificio(this.poi.getPosicion().getIdEdificio());
                    Toast.makeText(this, "Visualizar imaxe " + imaxe.getIdImaxe(), Toast.LENGTH_SHORT).show();
                    recuperarImaxe(imaxe);
                    return true;
                }
            }
        }

        // En caso de que non identifiquemos a accion
        return super.onContextItemSelected(item);
    }

    public void setListaImaxe(List<ImaxeCustom> listaImaxe) {
        this.poi.setListaImaxe(listaImaxe);
    }

    public void actualizarImaxe(ImaxeCustom novaImaxeCustom) {
        for (ImaxeCustom imaxeCustom : this.poi.getListaImaxe()) {
            if (imaxeCustom.getIdImaxe().equals(novaImaxeCustom.getIdImaxe())) {
                imaxeCustom.setImaxe(novaImaxeCustom.getImaxe());
                break;
            }
        }
    }
}