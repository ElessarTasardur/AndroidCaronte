package gal.caronte.servizo.situm;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.directions.DirectionsRequest;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Point;
import es.situm.sdk.model.directions.Route;
import es.situm.sdk.model.location.Location;
import es.situm.sdk.utils.Handler;
import gal.caronte.util.Constantes;
import gal.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 10/12/2017.
 */
public class RecuperarRuta {

    private static final String TAG = RecuperarRuta.class.getSimpleName();

    private RecuperarRuta.Callback callback;

    public interface Callback {
        void onSuccess(PolylineOptions listaRutas, LatLngBounds.Builder limite);
        void onError(Error error);
    }

    public void get(final Location posicionActual, final Point posicion, final RecuperarRuta.Callback callback) {
        Log.i(TAG, "Realizase a chamada para recuperar as rutas");
        if (hasCallback()){
            Log.d(TAG, "Xa se realizou outra chamada");
            return;
        }
        this.callback = callback;

        recuperarRuta(posicionActual, posicion);
    }

    private void recuperarRuta(final Location posicionActual, final Point posicion) {

        if (posicion == null) {
            Log.e(TAG, "Non hai posicion para guiar");
        }
        else {
            Point inicio = posicionActual.getPosition();
            DirectionsRequest directionsRequest = new DirectionsRequest.Builder()
                    .from(inicio, null)
                    .to(posicion)
                    .build();
            solicitarDireccion(directionsRequest);

        }

    }

    private void solicitarDireccion(final DirectionsRequest directionsRequest) {
        Log.i(TAG, "Solicitada unha ruta");

        SitumSdk.directionsManager().requestDirections(directionsRequest, new Handler<Route>() {
            @Override
            public void onSuccess(Route route) {
                PolylineOptions polyLineOptions = new PolylineOptions().color(Constantes.COR_GUIADO).width(Constantes.GROSOR_PERCORRIDO);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                List<Point> routePoints = route.getPoints();
                Log.d(TAG, StringUtil.creaString("Exito solicitando ruta: ", routePoints));
                LatLng latLng;
                for (Point point : routePoints) {
                    latLng = new LatLng(point.getCoordinate().getLatitude(), point.getCoordinate().getLongitude());
                    builder.include(latLng);
                    polyLineOptions.add(latLng);
                }

                if (hasCallback()) {
                    Log.d(TAG, "Devolvemos os datos de rutas");
                    callback.onSuccess(polyLineOptions, builder);
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
