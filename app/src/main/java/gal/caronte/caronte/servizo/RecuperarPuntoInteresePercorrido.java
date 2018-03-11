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
import gal.caronte.caronte.custom.sw.PuntoInteresePosicion;
import gal.caronte.caronte.util.StringUtil;
import gal.caronte.caronte.view.SelectorPoiPercorrido;

/**
 * Created by ElessarTasardur on 10/01/2018.
 */

public class RecuperarPuntoInteresePercorrido extends AsyncTask<String, Void, List<PuntoInteresePosicion>> {

    private static final String TAG = RecuperarPuntoInteresePercorrido.class.getSimpleName();

    private SelectorPoiPercorrido selectorPoiPercorrido;

    @Override
    protected List<PuntoInteresePosicion> doInBackground(String... params) {
        String idPercorrido = params[0];

        List<PuntoInteresePosicion> listaPIP = null;
        try {
            final String url = StringUtil.creaString(this.selectorPoiPercorrido.getMapaActivity().getString(R.string.direccion_servidor), this.selectorPoiPercorrido.getMapaActivity().getString(R.string.direccion_servizo_recuperar_puntos_interese_percorrido));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, idPercorrido);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            listaPIP = mapper.convertValue(resource, new TypeReference<List<PuntoInteresePosicion>>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, StringUtil.creaString("Lista de puntos (e posicions) recuperados para o percorrido ", idPercorrido, ": ", listaPIP));

        return listaPIP;
    }

    @Override
    protected void onPostExecute(List<PuntoInteresePosicion> listaPIP) {
        this.selectorPoiPercorrido.asociarPuntosPercorrido(listaPIP);
    }

    public void setSelectorPoiPercorrido(SelectorPoiPercorrido selectorPoiPercorrido) {
        this.selectorPoiPercorrido = selectorPoiPercorrido;
    }
}
