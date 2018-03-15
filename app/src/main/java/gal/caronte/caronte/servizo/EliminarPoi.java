package gal.caronte.caronte.servizo;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import gal.caronte.caronte.R;
import gal.caronte.caronte.activity.DetallePoiActivity;
import gal.caronte.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 10/03/2018.
 */

public class EliminarPoi extends AsyncTask<Integer, Void, Boolean> {

    private static final String TAG = EliminarPoi.class.getSimpleName();

    private DetallePoiActivity detallePoiActivity;

    @Override
    protected Boolean doInBackground(Integer... params) {
        Integer idPoi = params[0];
        Log.i(TAG, String.valueOf(idPoi));
        Boolean retorno = null;
        try {
            final String url = StringUtil.creaString(this.detallePoiActivity.getString(R.string.direccion_servidor), this.detallePoiActivity.getString(R.string.direccion_servizo_eliminar_poi));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setAuthorization(new HttpBasicAuthentication(this.detallePoiActivity.getString(R.string.usuario_sw), this.detallePoiActivity.getString(R.string.contrasinal_sw)));
            HttpEntity<Integer> entity = new HttpEntity<>(idPoi, headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            retorno = mapper.convertValue(resource, new TypeReference<Boolean>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, StringUtil.creaString("Eliminado POI con identificador ", idPoi));

        return retorno;
    }

    @Override
    protected void onPostExecute(Boolean correcto) {
        String mensaxe;
        if (correcto == null) {
            mensaxe = this.detallePoiActivity.getString(R.string.eliminar_poi_erro);
        }
        else if (correcto) {
            mensaxe = this.detallePoiActivity.getString(R.string.eliminar_poi_correcto);
        }
        else {
            mensaxe = this.detallePoiActivity.getString(R.string.eliminar_poi_en_percorrido);
        }
        Toast.makeText(this.detallePoiActivity, mensaxe, Toast.LENGTH_SHORT).show();
        this.detallePoiActivity.onBackPressed();
    }

    public void setDetallePoiActivity(DetallePoiActivity detallePoiActivity) {
        this.detallePoiActivity = detallePoiActivity;
    }
}
