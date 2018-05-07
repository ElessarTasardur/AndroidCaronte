package gal.caronte.servizo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import gal.caronte.R;
import gal.caronte.activity.DetallePoiActivity;
import gal.caronte.custom.sw.SubirImaxeCustom;
import gal.caronte.util.StringUtil;

public class SubirImaxe extends AsyncTask<SubirImaxeCustom, Void, Integer> {

    private static final String TAG = SubirImaxe.class.getSimpleName();

    private DetallePoiActivity detallePoiActivity;

    @Override
    protected Integer doInBackground(SubirImaxeCustom... params) {
        SubirImaxeCustom subirImaxeParam = params[0];
        Integer idImaxe = null;
        try {
            final String url = StringUtil.creaString(this.detallePoiActivity.getString(R.string.direccion_servidor_imaxe), this.detallePoiActivity.getString(R.string.direccion_servizo_subir_imaxe));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setAuthorization(new HttpBasicAuthentication(this.detallePoiActivity.getString(R.string.usuario_sw), this.detallePoiActivity.getString(R.string.contrasinal_sw)));

            MultiValueMap<String, Object> part1 = new LinkedMultiValueMap<>();
//            part1.add("file", new FileSystemResource(subirImaxeParam.getRuta()));
//            String response2 = restTemplate.postForObject(SERVER_URI+"imagepart", part3,  String.class);
            part1.add("file", new FileSystemResource(subirImaxeParam.getImaxe()));

            //TODO imaxe
//            HttpEntity<MultiPartFile> entity = new HttpEntity<>(subirImaxeParam.getImaxe(), headers);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(part1, headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class, subirImaxeParam.getIdEdificio(), subirImaxeParam.getIdPoi(), subirImaxeParam.getNomeImaxe(), subirImaxeParam.getDescricionImaxe());
            Object resource = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            idImaxe = mapper.convertValue(resource, new TypeReference<Integer>() { });




//            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
//            map.add("request", registration);
//            for (FicaDocument document : registration.documents)
//                map.add(document.documentPath, new FileSystemResource(document.documentPath));
//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
//            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);


        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, StringUtil.creaString("Imaxe subida con identificador ", idImaxe));

        return idImaxe;
    }

    @Override
    protected void onPostExecute(Integer idImaxe) {
        Intent intent = new Intent();
        this.detallePoiActivity.setResult(Activity.RESULT_OK, intent);
        this.detallePoiActivity.finish();
    }

    public void setDetallePoiActivity(DetallePoiActivity detallePoiActivity) {
        this.detallePoiActivity = detallePoiActivity;
    }
}
