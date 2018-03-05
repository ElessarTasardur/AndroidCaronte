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
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import gal.caronte.caronte.R;
import gal.caronte.caronte.activity.InicioActivity;
import gal.caronte.caronte.custom.sw.ComprobarLoginGoogleCustom;
import gal.caronte.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 11/02/2018.
 */

public class ComprobarUsuarioGoogle extends AsyncTask<String, Void, ComprobarLoginGoogleCustom> {

    private static final String TAG = GardarPercorrido.class.getSimpleName();

    private InicioActivity inicioActivity;

    @Override
    protected ComprobarLoginGoogleCustom doInBackground(String... strings) {
        String idToken = strings[0];
        ComprobarLoginGoogleCustom usuarioCorrecto = null;
        try {
            final String url = StringUtil.creaString( this.inicioActivity.getString(R.string.direccion_servidor), this.inicioActivity.getString(R.string.direccion_servizo_comprobar_usuario_google));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new TestErrorHandler());
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(idToken, headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            usuarioCorrecto = mapper.convertValue(resource, new TypeReference<ComprobarLoginGoogleCustom>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, StringUtil.creaString("Usuario de Google correctamente logueado: ", usuarioCorrecto));

        return usuarioCorrecto;
    }

    @Override
    protected void onPostExecute(ComprobarLoginGoogleCustom usuarioCorrecto) {
        this.inicioActivity.setUsuarioCorrecto(usuarioCorrecto);
    }

    public void setInicioActivity(InicioActivity inicioActivity) {
        this.inicioActivity = inicioActivity;
    }


    private class TestErrorHandler extends DefaultResponseErrorHandler {

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            //conversion logic for decoding conversion
            ByteArrayInputStream arrayInputStream = (ByteArrayInputStream) response.getBody();
            Scanner scanner = new Scanner(arrayInputStream);
            scanner.useDelimiter("\\Z");
            String data = "";
            if (scanner.hasNext())
                data = scanner.next();
            System.out.println(data);
        }
    }

}


