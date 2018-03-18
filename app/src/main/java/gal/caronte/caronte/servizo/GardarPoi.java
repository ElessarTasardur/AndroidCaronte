package gal.caronte.caronte.servizo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

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
import gal.caronte.caronte.custom.sw.PuntoInterese;
import gal.caronte.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 05/03/2018.
 */

public class GardarPoi extends AsyncTask<PuntoInterese, Void, Short> {

    private static final String TAG = GardarPoi.class.getSimpleName();

    private DetallePoiActivity detallePoiActivity;

    @Override
    protected Short doInBackground(PuntoInterese... params) {
        PuntoInterese poiParam = params[0];
        Short idPoi = null;
        try {
            final String url = StringUtil.creaString(this.detallePoiActivity.getString(R.string.direccion_servidor), this.detallePoiActivity.getString(R.string.direccion_servizo_gardar_poi));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setAuthorization(new HttpBasicAuthentication(this.detallePoiActivity.getString(R.string.usuario_sw), this.detallePoiActivity.getString(R.string.contrasinal_sw)));
            HttpEntity<PuntoInterese> entity = new HttpEntity<>(poiParam, headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            idPoi = mapper.convertValue(resource, new TypeReference<Short>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, StringUtil.creaString("Poi gardado con identificador ", idPoi));

        return idPoi;
    }

    @Override
    protected void onPostExecute(Short idPoi) {
        Intent intent = new Intent();
        this.detallePoiActivity.setResult(Activity.RESULT_OK, intent);
        this.detallePoiActivity.finish();
    }

    public void setDetallePoiActivity(DetallePoiActivity detallePoiActivity) {
        this.detallePoiActivity = detallePoiActivity;
    }
}
