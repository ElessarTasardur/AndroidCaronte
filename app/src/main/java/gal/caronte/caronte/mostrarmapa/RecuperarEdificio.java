package gal.caronte.caronte.mostrarmapa;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Collection;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.utils.Handler;
import gal.caronte.caronte.custom.Edificio;

/**
 * Created by ElessarTasardur on 08/10/2017.
 */

public class RecuperarEdificio {

    private static final String TAG = RecuperarEdificio.class.getSimpleName();



    private Edificio edificio;

    interface Callback{
        void onSuccess(Edificio edficio);
        void onError(Error error);
    }

    private Callback callback;

    void get(final Callback callback, final String idEdificio, final String idPiso) {
        if (hasCallback()){
            Log.d(TAG, "Xa se realizou outra chamada");
            return;
        }
        this.callback = callback;

        recuperarEdificio(idEdificio, idPiso);
    }

    private void recuperarEdificio(final String idEdificio, final String idPiso) {
        SitumSdk.communicationManager().fetchBuildings(new Handler<Collection<Building>>() {

            @Override
            public void onSuccess(Collection<Building> buildings) {

                //Convertimos os edificios aos nosos customs
                Edificio novoEdificio = null;
                for (final Building edificio : buildings) {
                    if (idEdificio.equals(edificio.getIdentifier())) {
                        novoEdificio = new Edificio(edificio);
                        break;
                    }
                }

                //Recuperar pisos a partir de edificioActual
                recuperarPiso(novoEdificio, idPiso);
            }

            @Override
            public void onFailure(Error error) {
                if (hasCallback()){
                    callback.onError(error);
                }
                clearCallback();
            }
        });
    }

    private void recuperarPiso(final Edificio edificio, final String idPiso) {

        SitumSdk.communicationManager().fetchFloorsFromBuilding(edificio.getEdificio(), new Handler<Collection<Floor>>() {
            @Override
            public void onSuccess(Collection<Floor> pisos) {
                if (!pisos.isEmpty()) {
                    edificio.setPisos(pisos);

                    if (hasCallback()) {
                        callback.onSuccess(edificio);
                    }
                    clearCallback();
                }
            }

            @Override
            public void onFailure(Error error) {
                if (hasCallback()){
                    callback.onError(error);
                }
                clearCallback();
            }
        });
    }

    void cancel() {
        callback = null;
    }

    private boolean hasCallback(){
        return callback != null;
    }

    private void clearCallback(){
        callback = null;
    }
}
