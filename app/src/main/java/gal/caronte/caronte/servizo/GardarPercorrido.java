package gal.caronte.caronte.servizo;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import gal.caronte.caronte.R;
import gal.caronte.caronte.activity.DetallePercorridoActivity;
import gal.caronte.caronte.custom.sw.GardarPercorridoParam;
import gal.caronte.caronte.activity.MapaActivity;
import gal.caronte.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 12/01/2018.
 */

public class GardarPercorrido extends AsyncTask<GardarPercorridoParam, Void, Short> {

    private static final String TAG = GardarPercorrido.class.getSimpleName();

    private DetallePercorridoActivity detallePercorridoActivity;

    @Override
    protected Short doInBackground(GardarPercorridoParam... params) {
        GardarPercorridoParam percorridoParam = params[0];
        Short idPercorrido = null;
        try {
            final String url = StringUtil.creaString(this.detallePercorridoActivity.getString(R.string.direccion_servidor), this.detallePercorridoActivity.getString(R.string.direccion_servizo_gardar_percorrido));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<GardarPercorridoParam> entity = new HttpEntity<>(percorridoParam, headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            idPercorrido = mapper.convertValue(resource, new TypeReference<Short>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, StringUtil.creaString("Percorrido gardado con identificador ", idPercorrido));

        return idPercorrido;
    }

    @Override
    protected void onPostExecute(Short idPercorrido) {
        //TODO
//        Intent intent = new Intent(this.detallePercorridoActivity, MapaActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        this.detallePercorridoActivity.actualizarPercorrido(idPercorrido);
//        this.detallePercorridoActivity.startActivity(intent);
    }

    public void setDetallePercorridoActivity(DetallePercorridoActivity detallePercorridoActivity) {
        this.detallePercorridoActivity = detallePercorridoActivity;
    }
}
