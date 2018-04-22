package gal.caronte.caronte.servizo;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import gal.caronte.caronte.R;
import gal.caronte.caronte.activity.DetallePoiActivity;
import gal.caronte.caronte.custom.sw.ImaxeCustom;
import gal.caronte.caronte.util.StringUtil;

public class RecuperarImaxe extends AsyncTask<ImaxeCustom, Void, ImaxeCustom> {

    private static final String TAG = RecuperarImaxe.class.getSimpleName();

    private DetallePoiActivity detallePoiActivity;

    @Override
    protected ImaxeCustom doInBackground(ImaxeCustom... params) {
        ImaxeCustom imaxeCustom = params[0];
        Resource imaxe = null;
        try {
            final String url = StringUtil.creaString(this.detallePoiActivity.getString(R.string.direccion_servidor), this.detallePoiActivity.getString(R.string.direccion_servizo_recuperar_datos_imaxe));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setAuthorization(new HttpBasicAuthentication(this.detallePoiActivity.getString(R.string.usuario_sw), this.detallePoiActivity.getString(R.string.contrasinal_sw)));
            HttpEntity<List<Integer>> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, imaxeCustom.getIdEdificio(), imaxeCustom.getIdPuntoInterese(), imaxeCustom.getIdImaxe());
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            imaxe = mapper.convertValue(resource, new TypeReference<Resource>() { });
            imaxeCustom.setImaxe(imaxe);

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
