package gal.caronte.servizo;

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
import java.util.List;

import gal.caronte.R;
import gal.caronte.activity.InicioActivity;
import gal.caronte.custom.sw.Conta;
import gal.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 25/11/2017.
 */

public class RecuperarConta extends AsyncTask<String, Void, List<Conta>> {

    private static final String TAG = RecuperarConta.class.getSimpleName();

    private InicioActivity inicioActivity;

    @Override
    protected List<Conta> doInBackground(String... params) {

        String idUsuario = null;
        if (params != null
                && params.length == 1) {
            idUsuario = params[0];
        }

        List<Conta> listaContas = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setAuthorization(new HttpBasicAuthentication(this.inicioActivity.getString(R.string.usuario_sw), this.inicioActivity.getString(R.string.contrasinal_sw)));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response;
            if (idUsuario != null) {
                Log.d(TAG, StringUtil.creaString("Recuperando contas para o idUsuario ", idUsuario));
                final String url = StringUtil.creaString(this.inicioActivity.getString(R.string.direccion_servidor), this.inicioActivity.getString(R.string.direccion_servizo_recuperar_contas_usuario));
                response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, idUsuario);

            }
            else {
                Log.d(TAG, "Recuperando contas publicas");
                final String url = StringUtil.creaString( this.inicioActivity.getString(R.string.direccion_servidor), this.inicioActivity.getString(R.string.direccion_servizo_recuperar_contas));
                response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
            }
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
        this.inicioActivity.amosarListaConta(listaContas);
    }

    public void setInicioActivity(InicioActivity inicioActivity) {
        this.inicioActivity = inicioActivity;
    }
}
