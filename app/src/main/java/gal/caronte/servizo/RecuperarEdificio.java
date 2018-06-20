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
import gal.caronte.custom.sw.EdificioCustom;
import gal.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 19/02/2018.
 */

public class RecuperarEdificio extends AsyncTask<Void, Void, List<EdificioCustom>> {

    private static final String TAG = RecuperarEdificio.class.getSimpleName();

    private InicioActivity inicioActivity;

    @Override
    protected List<EdificioCustom> doInBackground(Void... params) {

        final String url = StringUtil.creaString(this.inicioActivity.getString(R.string.direccion_servidor),
                this.inicioActivity.getString(R.string.direccion_servizo_recuperar_edificios));
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setAuthorization(new HttpBasicAuthentication(this.inicioActivity.getString(R.string.usuario_sw),
                this.inicioActivity.getString(R.string.contrasinal_sw)));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
        Object resource = response.getBody();

        return new ObjectMapper().convertValue(resource, new TypeReference<List<EdificioCustom>>() { });
    }

    @Override
    protected void onPostExecute(List<EdificioCustom> listaEdificio) {
        this.inicioActivity.setListaEdificio(listaEdificio);
    }

    public void setInicioActivity(InicioActivity inicioActivity) {
        this.inicioActivity = inicioActivity;
    }
}

