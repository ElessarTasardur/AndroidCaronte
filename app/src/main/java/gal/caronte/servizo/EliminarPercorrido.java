package gal.caronte.servizo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
import gal.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 10/03/2018.
 */

public class EliminarPercorrido extends AsyncTask<Integer, Void, Boolean> {

    private static final String TAG = EliminarPercorrido.class.getSimpleName();

    private DetallePercorridoActivity detallePercorridoActivity;

    @Override
    protected Boolean doInBackground(Integer... params) {
        Integer idPercorrido = params[0];
        Log.i(TAG, String.valueOf(idPercorrido));
        Boolean retorno = null;
        try {
            final String url = StringUtil.creaString(this.detallePercorridoActivity.getString(R.string.direccion_servidor), this.detallePercorridoActivity.getString(R.string.direccion_servizo_eliminar_percorrido));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setAuthorization(new HttpBasicAuthentication(this.detallePercorridoActivity.getString(R.string.usuario_sw), this.detallePercorridoActivity.getString(R.string.contrasinal_sw)));
            HttpEntity<Integer> entity = new HttpEntity<>(idPercorrido, headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            retorno = mapper.convertValue(resource, new TypeReference<Boolean>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, StringUtil.creaString("Percorrido eliminado ", retorno));

        return retorno;
    }

    @Override
    protected void onPostExecute(Boolean correcto) {
        String mensaxe;
        if (correcto == null
                || !correcto) {
            mensaxe = this.detallePercorridoActivity.getString(R.string.eliminar_percorrido_erro);
        }
        else {
            mensaxe = this.detallePercorridoActivity.getString(R.string.eliminar_percorrido_correcto);
        }
        Toast.makeText(this.detallePercorridoActivity, mensaxe, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        this.detallePercorridoActivity.setResult(Activity.RESULT_OK, intent);
        this.detallePercorridoActivity.finish();
    }

    public void setDetallePercorridoActivity(DetallePercorridoActivity detallePercorridoActivity) {
        this.detallePercorridoActivity = detallePercorridoActivity;
    }
}
