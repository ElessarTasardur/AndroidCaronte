package gal.caronte.caronte.mostrarmapa;

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

import gal.caronte.caronte.custom.sw.PuntoInterese;

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
            final String url = "http://ec2-34-241-173-6.eu-west-1.compute.amazonaws.com:8080/sw/museo/pois/{idEdificioExterno}";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<String>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, idEdificioExterno);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            listaPoi = mapper.convertValue(resource, new TypeReference<List<PuntoInterese>>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return listaPoi;
    }

    @Override
    protected void onPostExecute(List<PuntoInterese> listaPoi) {
        Log.i(TAG, String.valueOf(listaPoi));
        this.mapaActivity.mostrarListaPoi(listaPoi);
    }

    public void setMapaActivity(MapaActivity mapaActivity) {
        this.mapaActivity = mapaActivity;
    }

}
