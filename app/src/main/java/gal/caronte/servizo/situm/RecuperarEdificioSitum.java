package gal.caronte.servizo.situm;

import android.util.Log;

import java.util.Collection;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.utils.Handler;

/**
 * Created by ElessarTasardur on 16/03/2018.
 */

public class RecuperarEdificioSitum {

    private static final String TAG = RecuperarEdificioSitum.class.getSimpleName();

    private RecuperarEdificioSitum.Callback callback;

    public interface Callback {
        void onSuccess(Collection<Floor> listaPiso);
        void onError(Error error);
    }

    public void get(final RecuperarEdificioSitum.Callback callback, final Building edificioSitum) {
        if (hasCallback()){
            Log.d(TAG, "Xa se realizou outra chamada");
            return;
        }
        this.callback = callback;

        recuperarPiso(edificioSitum);
    }

    private void recuperarPiso(final Building edificioSitum) {

        SitumSdk.communicationManager().fetchFloorsFromBuilding(edificioSitum, new Handler<Collection<Floor>>() {
            @Override
            public void onSuccess(Collection<Floor> listaPiso) {
                if (hasCallback()) {
                    callback.onSuccess(listaPiso);
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
