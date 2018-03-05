package gal.caronte.caronte.servizo;

import android.util.Log;

import java.util.Collection;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.utils.Handler;
import gal.caronte.caronte.custom.EdificioSitumCustom;

/**
 * Created by ElessarTasardur on 08/10/2017.
 */

public class RecuperarEdificioSitum {

    private static final String TAG = RecuperarEdificioSitum.class.getSimpleName();

    private EdificioSitumCustom edificioSitumCustom;
    private Callback callback;

    public interface Callback {
        void onSuccess(EdificioSitumCustom edficio);
        void onError(Error error);
    }

    public void get(final Callback callback, final String idEdificio) {
        if (hasCallback()){
            Log.d(TAG, "Xa se realizou outra chamada");
            return;
        }
        this.callback = callback;

        recuperarEdificio(idEdificio);
    }

    private void recuperarEdificio(final String idEdificio) {
        SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {

            @Override
            public void onSuccess(Collection<Building> buildings) {

                //Convertimos os edificios aos nosos customs
                EdificioSitumCustom novoEdificio = null;
                for (final Building edificio : buildings) {

                    if (idEdificio.equals(edificio.getIdentifier())) {
                        novoEdificio = new EdificioSitumCustom(edificio);
                        break;
                    }
                }

                //Recuperar pisos a partir de edificioActual
                if (novoEdificio != null) {
                    recuperarPiso(novoEdificio);
                }
                else {
                    clearCallback();
                }
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

    private void recuperarPiso(final EdificioSitumCustom edificioSitumCustom) {

        SitumSdk.communicationManager().fetchFloorsFromBuilding(edificioSitumCustom.getEdificio(), new Handler<Collection<Floor>>() {
            @Override
            public void onSuccess(Collection<Floor> pisos) {
                if (!pisos.isEmpty()) {
                    edificioSitumCustom.setPisos(pisos);

                    if (hasCallback()) {
                        callback.onSuccess(edificioSitumCustom);
                    }
                    clearCallback();
                }
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
