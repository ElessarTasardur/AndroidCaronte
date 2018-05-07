package gal.caronte.servizo;

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

import gal.caronte.R;
import gal.caronte.activity.DetallePercorridoActivity;
import gal.caronte.custom.sw.GardarPercorridoParam;
import gal.caronte.activity.MapaActivity;
import gal.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 12/01/2018.
 */

public class GardarPercorrido extends AsyncTask<GardarPercorridoParam, Void, Short> {

    private static final String TAG = GardarPercorrido.class.getSimpleName();

    private DetallePercorridoActivity detallePercorridoActivity;
    private MapaActivity mapaActivity;

    @Override
    protected Short doInBackground(GardarPercorridoParam... params) {
        GardarPercorridoParam percorridoParam = params[0];
        Short idPercorrido = null;
        try {
            final String url = StringUtil.creaString(getActivity().getString(R.string.direccion_servidor), getActivity().getString(R.string.direccion_servizo_gardar_percorrido));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setAuthorization(new HttpBasicAuthentication(getActivity().getString(R.string.usuario_sw), getActivity().getString(R.string.contrasinal_sw)));
            HttpEntity<GardarPercorridoParam> entity = new HttpEntity<>(percorridoParam, headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            idPercorrido = mapper.convertValue(resource, new TypeReference<Short>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, StringUtil.creaString("PercorridoParam gardado con identificador ", idPercorrido));

        return idPercorrido;
    }

    @Override
    protected void onPostExecute(Short idPercorrido) {
        if (this.detallePercorridoActivity != null) {
            Intent intent = new Intent();
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
        //Se a chamada veu dende o mapa, refrescamos percorrido
        else {
            this.mapaActivity.actualizarPercorrido();
        }
    }

    public void setDetallePercorridoActivity(DetallePercorridoActivity detallePercorridoActivity) {
        this.detallePercorridoActivity = detallePercorridoActivity;
    }

    public void setMapaActivity(MapaActivity mapaActivity) {
        this.mapaActivity = mapaActivity;
    }

    private Activity getActivity() {
        Activity retorno;
        if (this.detallePercorridoActivity != null) {
            retorno = this.detallePercorridoActivity;
        }
        else {
            retorno = this.mapaActivity;
        }
        return retorno;
    }
}
