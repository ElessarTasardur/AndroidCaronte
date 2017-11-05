package gal.caronte.caronte.mostrarmapa;

import android.graphics.Bitmap;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.utils.Handler;
import gal.caronte.caronte.custom.Edificio;

/**
 * Created by ElessarTasardur on 04/11/2017.
 */

public class RecuperarMapa {

    private static final String TAG = RecuperarMapa.class.getSimpleName();

    private Callback callback;

    interface Callback{
        void onSuccess(Bitmap mapa);
        void onError(Error error);
    }

    void get(final RecuperarMapa.Callback callback, final Floor floor) {
        if (hasCallback()){
            return;
        }
        this.callback = callback;

        recuperarMapa(floor);
    }


    private void recuperarMapa(final Floor floor) {

        if (floor != null) {
            SitumSdk.communicationManager().fetchMapFromFloor(floor, new Handler<Bitmap>() {
                @Override
                public void onSuccess(Bitmap mapa) {

                    if (hasCallback()) {
                        callback.onSuccess(mapa);
                    }
                    clearCallback();
                }

                @Override
                public void onFailure(Error error) {
                    if (hasCallback()) {
                        callback.onError(error);
                    }
                    clearCallback();
                }
            });
        }
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
