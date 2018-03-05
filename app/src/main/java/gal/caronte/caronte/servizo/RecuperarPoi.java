package gal.caronte.caronte.servizo;

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
import java.util.List;

import gal.caronte.caronte.R;
import gal.caronte.caronte.custom.sw.PuntoInterese;
import gal.caronte.caronte.activity.MapaActivity;
import gal.caronte.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 05/11/2017.
 */

public class RecuperarPoi extends AsyncTask<String, Void, List<PuntoInterese>> {

    private static final String TAG = RecuperarPoi.class.getSimpleName();

    private MapaActivity mapaActivity;

    @Override
    protected List<PuntoInterese> doInBackground(String... params) {

        String idEdificioExterno = params[0];

        List<PuntoInterese> listaPoi = null;
        try {
            final String url = StringUtil.creaString(this.mapaActivity.getString(R.string.direccion_servidor), this.mapaActivity.getString(R.string.direccion_servizo_recuperar_pois));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, idEdificioExterno);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            listaPoi = mapper.convertValue(resource, new TypeReference<List<PuntoInterese>>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, StringUtil.creaString("Lista de POI recuperados para o edificio ", idEdificioExterno, ": ", listaPoi));

        return listaPoi;
    }

    @Override
    protected void onPostExecute(List<PuntoInterese> listaPoi) {
        this.mapaActivity.crearListaPoi(listaPoi);
    }

    public void setMapaActivity(MapaActivity mapaActivity) {
        this.mapaActivity = mapaActivity;
    }

}
