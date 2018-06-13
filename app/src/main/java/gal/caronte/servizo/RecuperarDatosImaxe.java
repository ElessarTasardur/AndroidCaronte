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
import gal.caronte.activity.DetallePoiActivity;
import gal.caronte.custom.sw.ImaxeCustom;
import gal.caronte.util.StringUtil;

public class RecuperarDatosImaxe extends AsyncTask<String, Void, List<ImaxeCustom>> {

    private static final String TAG = RecuperarDatosImaxe.class.getSimpleName();

    private DetallePoiActivity detallePoiActivity;

    @Override
    protected List<ImaxeCustom> doInBackground(String... params) {
        String listaIdImaxeCSV = params[0];
        List<ImaxeCustom> listaImaxe = null;
        try {
            final String url = StringUtil.creaString(this.detallePoiActivity.getString(R.string.direccion_servidor), this.detallePoiActivity.getString(R.string.direccion_servizo_recuperar_datos_imaxe));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setAuthorization(new HttpBasicAuthentication(this.detallePoiActivity.getString(R.string.usuario_sw), this.detallePoiActivity.getString(R.string.contrasinal_sw)));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class, listaIdImaxeCSV);
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            listaImaxe = mapper.convertValue(resource, new TypeReference<List<ImaxeCustom>>() { });

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, StringUtil.creaString("Imaxes recuperadas: ", listaImaxe));

        return listaImaxe;
    }

    @Override
    protected void onPostExecute(List<ImaxeCustom> listaImaxe) {
        this.detallePoiActivity.setListaImaxe(listaImaxe);
    }

    public void setDetallePoiActivity(DetallePoiActivity detallePoiActivity) {
        this.detallePoiActivity = detallePoiActivity;
    }
}
