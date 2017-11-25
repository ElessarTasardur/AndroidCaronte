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

import gal.caronte.caronte.custom.sw.Conta;

/**
 * Created by ElessarTasardur on 25/11/2017.
 */

public class RecuperarConta extends AsyncTask<String, Void, List<Conta>> {

    private static final String TAG = RecuperarConta.class.getSimpleName();

    private MapaActivity mapaActivity;

    @Override
    protected List<Conta> doInBackground(String... params) {

        List<Conta> listaContas = null;
        try {
            final String url = "http://ec2-34-241-173-6.eu-west-1.compute.amazonaws.com:8080/sw/museo/contas";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<String>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            listaContas = mapper.convertValue(resource, new TypeReference<List<Conta>>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return listaContas;
    }

    @Override
    protected void onPostExecute(List<Conta> listaContas) {
        Log.i(TAG, String.valueOf(listaContas));
        this.mapaActivity.mostrarListaConta(listaContas);
    }

    public void setMapaActivity(MapaActivity mapaActivity) {
        this.mapaActivity = mapaActivity;
    }

}
