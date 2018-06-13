package gal.caronte.servizo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import gal.caronte.R;
import gal.caronte.activity.DetallePoiActivity;
import gal.caronte.custom.sw.ImaxeCustom;
import gal.caronte.util.StringUtil;

public class RecuperarImaxe extends AsyncTask<ImaxeCustom, Void, ImaxeCustom> {

    private static final String TAG = RecuperarImaxe.class.getSimpleName();

    private DetallePoiActivity detallePoiActivity;

    @Override
    protected ImaxeCustom doInBackground(ImaxeCustom... params) {
        ImaxeCustom imaxeCustom = params[0];
        byte[] imaxe = null;
        try {
            final String url = StringUtil.creaString(this.detallePoiActivity.getString(R.string.direccion_servidor), this.detallePoiActivity.getString(R.string.direccion_servizo_recuperar_imaxe));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setAuthorization(new HttpBasicAuthentication(this.detallePoiActivity.getString(R.string.usuario_sw), this.detallePoiActivity.getString(R.string.contrasinal_sw)));
            HttpEntity<List<Integer>> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class, imaxeCustom.getIdEdificio(), imaxeCustom.getIdPuntoInterese(), imaxeCustom.getIdImaxe());

            //Construese o Bitmap
            imaxe = response.getBody();
            Bitmap nha = BitmapFactory.decodeByteArray(imaxe,0, imaxe.length);

            //Crease o directorio e a ruta onde se garda a imaxe
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), this.detallePoiActivity.getString(R.string.app_name));
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
            }

            mediaStorageDir = new File(StringUtil.creaString(mediaStorageDir.getPath(), File.separator, imaxeCustom.getIdEdificio(), File.separator, imaxeCustom.getIdPuntoInterese()));
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
            }

            String rutaImaxe = StringUtil.creaString(mediaStorageDir.getPath(), File.separator, imaxeCustom.getIdImaxe(), ".jpg");

            OutputStream outStream = null;
            File imaxeSistema = new File(rutaImaxe);
            try {
                outStream = new FileOutputStream(imaxeSistema);
                nha.compress(Bitmap.CompressFormat.JPEG, 85, outStream);
                outStream.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Uri imageURI = Uri.fromFile(imaxeSistema);

            imaxeCustom.setRutaImaxe(imageURI.toString());

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, StringUtil.creaString("Imaxe recuperada: ", imaxe != null));

        return imaxeCustom;
    }

    @Override
    protected void onPostExecute(ImaxeCustom imaxeCustom) {
        this.detallePoiActivity.actualizarImaxe(imaxeCustom);
    }

    public void setDetallePoiActivity(DetallePoiActivity detallePoiActivity) {
        this.detallePoiActivity = detallePoiActivity;
    }
}
