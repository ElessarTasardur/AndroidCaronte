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
import gal.caronte.activity.ImaxeActivity;
import gal.caronte.util.Constantes;
import gal.caronte.util.StringUtil;

public class EliminarImaxe extends AsyncTask<Integer, Void, Boolean> {

    private static final String TAG = EliminarImaxe.class.getSimpleName();

    private ImaxeActivity imaxeActivity;

    @Override
    protected Boolean doInBackground(Integer... params) {
        Integer idPercorrido = params[0];
        Log.i(TAG, String.valueOf(idPercorrido));
        Boolean retorno = null;
        try {
            final String url = StringUtil.creaString(this.imaxeActivity.getString(R.string.direccion_servidor), this.imaxeActivity.getString(R.string.direccion_servizo_eliminar_imaxe));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setAuthorization(new HttpBasicAuthentication(this.imaxeActivity.getString(R.string.usuario_sw), this.imaxeActivity.getString(R.string.contrasinal_sw)));
            HttpEntity<Integer> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Object.class, idPercorrido);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            retorno = mapper.convertValue(resource, new TypeReference<Boolean>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return retorno;
    }

    @Override
    protected void onPostExecute(Boolean correcto) {
        String mensaxe;
        if (correcto == null
                || !correcto) {
            mensaxe = this.imaxeActivity.getString(R.string.eliminar_imaxe_erro);
        }
        else {
            mensaxe = this.imaxeActivity.getString(R.string.eliminar_imaxe_correcto);
        }

        Log.i(TAG, StringUtil.creaString("Imaxe eliminada: ", correcto));

        Toast.makeText(this.imaxeActivity, mensaxe, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra(Constantes.ID_IMAXE, this.imaxeActivity.getIdImaxe());
        this.imaxeActivity.setResult(Activity.RESULT_OK, intent);
        this.imaxeActivity.finish();
    }

    public void setImaxeActivity(ImaxeActivity imaxeActivity) {
        this.imaxeActivity = imaxeActivity;
    }
}