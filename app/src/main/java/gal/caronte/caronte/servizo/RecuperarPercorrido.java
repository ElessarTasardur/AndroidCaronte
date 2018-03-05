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
import gal.caronte.caronte.custom.sw.Percorrido;
import gal.caronte.caronte.activity.MapaActivity;
import gal.caronte.caronte.util.StringUtil;
import gal.caronte.caronte.view.SpinnerPercorrido;

/**
 * Created by ElessarTasardur on 10/01/2018.
 */

public class RecuperarPercorrido extends AsyncTask<String, Void, List<Percorrido>> {

    private static final String TAG = RecuperarPercorrido.class.getSimpleName();

    private SpinnerPercorrido spinnerPercorrido;

    @Override
    protected List<Percorrido> doInBackground(String... params) {
        String idEdificioExterno = params[0];

        List<Percorrido> listaPercorrido = null;
        try {
            final String url = StringUtil.creaString( this.spinnerPercorrido.getMapaActivity().getString(R.string.direccion_servidor), this.spinnerPercorrido.getMapaActivity().getString(R.string.direccion_servizo_recuperar_percorridos));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, idEdificioExterno);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            listaPercorrido = mapper.convertValue(resource, new TypeReference<List<Percorrido>>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, StringUtil.creaString("Lista de percorridos recuperados para o edificio con id externo ", idEdificioExterno, ": ", listaPercorrido));

        return listaPercorrido;
    }

    @Override
    protected void onPostExecute(List<Percorrido> listaPercorrido) {
        this.spinnerPercorrido.amosarListaPercorrido(listaPercorrido);
    }

    public void setSpinnerPercorrido(SpinnerPercorrido spinnerPercorrido) {
        this.spinnerPercorrido = spinnerPercorrido;
    }
}
