package gal.caronte.servizo.situm;

import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.utils.Handler;
import gal.caronte.custom.EdificioSitumCustom;

/**
 * Created by ElessarTasardur on 08/10/2017.
 */

public class RecuperarListaEdificioSitum {

    private static final String TAG = RecuperarListaEdificioSitum.class.getSimpleName();

    private Callback callback;

    public interface Callback {
        void onSuccess(Map<String, EdificioSitumCustom> listaEdificio);
        void onError(Error error);
    }

    public void get(final Callback callback) {
        if (hasCallback()){
            Log.d(TAG, "Xa se realizou outra chamada");
            return;
        }
        this.callback = callback;

        recuperarEdificio();
    }

    private void recuperarEdificio() {
        SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {

            @Override
            public void onSuccess(Collection<Building> buildings) {

                //Convertimos os edificios aos nosos customs
                Map<String, EdificioSitumCustom> mapaEdificio = new HashMap<>(buildings.size());
                for (final Building edificio : buildings) {
                    mapaEdificio.put(edificio.getIdentifier(), new EdificioSitumCustom(edificio));
                }

                if (hasCallback()) {
                    callback.onSuccess(mapaEdificio);
                }
                clearCallback();

            }

            @Override
            public void onFailure(Error error) {
                Log.e(TAG, error.getMessage());
                if (hasCallback()){
                    callback.onError(error);
                }
                clearCallback();
            }
        });
    }

    public void cancel() {
        callback = null;
    }

    private boolean hasCallback(){
        return callback != null;
    }

    private void clearCallback(){
        callback = null;
    }
}
