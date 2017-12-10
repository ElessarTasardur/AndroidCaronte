package gal.caronte.caronte.mostrarmapa;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.directions.DirectionsRequest;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Point;
import es.situm.sdk.model.directions.Route;
import es.situm.sdk.model.location.Coordinate;
import es.situm.sdk.model.location.Location;
import es.situm.sdk.utils.Handler;
import gal.caronte.caronte.custom.MarcadorCustom;

/**
 * Created by ElessarTasardur on 10/12/2017.
 */

public class RecuperarRuta {

    private static final String TAG = RecuperarRuta.class.getSimpleName();

    private int numeroChamadas = 0;
    final List<PolylineOptions> listaRutas = new ArrayList<>();

    private RecuperarRuta.Callback callback;

    interface Callback {
        void onSuccess(List<PolylineOptions> listaRutas);
        void onError(Error error);
    }

    void get(final RecuperarRuta.Callback callback, final String idEdificio, final Location posicionActual, final List<MarcadorCustom> listaMarcadores) {
        Log.i(TAG, "Realizase a chamada para recuperar as rutas");
        if (hasCallback()){
            Log.d(TAG, "Xa se realizou outra chamada");
            return;
        }
        this.callback = callback;

        recuperarRuta(idEdificio, posicionActual, listaMarcadores);
    }

    private void recuperarRuta(final String idEdificio, final Location posicionActual, final List<MarcadorCustom> listaMarcadores) {

        if (listaMarcadores == null
                || listaMarcadores.isEmpty()
                || (posicionActual == null
                        && listaMarcadores.size() < 2)) {
            Log.e(TAG, "Non hai suficientes puntos para crear unha ruta");
        }
        else {
            Point inicio;
            Point fin;
            int indice = 0;
            if (posicionActual != null) {
                inicio = posicionActual.getPosition();
                MarcadorCustom mc = listaMarcadores.get(0);
                Coordinate coordenada = new Coordinate(mc.getMarcadorGoogle().getPosition().latitude, mc.getMarcadorGoogle().getPosition().longitude);
                fin = new Point(idEdificio, listaMarcadores.get(0).getIdPlanta().toString(), coordenada, null);
                indice = 1;
                DirectionsRequest directionsRequest = new DirectionsRequest.Builder()
                        .from(inicio, null)
                        .to(fin)
                        .build();
                solicitarDireccions(directionsRequest, inicio, fin);
            }
            Coordinate coordenada;
            for (; indice < listaMarcadores.size() - 1; indice++) {
                MarcadorCustom mcInicial = listaMarcadores.get(indice);
                coordenada = new Coordinate(mcInicial.getMarcadorGoogle().getPosition().latitude, mcInicial.getMarcadorGoogle().getPosition().longitude);
                inicio = new Point(idEdificio, listaMarcadores.get(0).getIdPlanta().toString(), coordenada, null);

                fin = new Point(idEdificio, listaMarcadores.get(0).getIdPlanta().toString(), coordenada, null);
                DirectionsRequest directionsRequest = new DirectionsRequest.Builder()
                        .from(inicio, null)
                        .to(fin)
                        .build();
                solicitarDireccions(directionsRequest, inicio, fin);
            }
        }

    }

    private void solicitarDireccions(final DirectionsRequest directionsRequest, final Point inicio, final Point fin) {
        Log.i(TAG, "Solicitada unha ruta");
        incrementarNumeroChamadas();

        SitumSdk.directionsManager().requestDirections(directionsRequest, new Handler<Route>() {
            @Override
            public void onSuccess(Route route) {
                PolylineOptions polyLineOptions = new PolylineOptions().color(Color.GREEN).width(4f);
//                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                List<Point> routePoints = route.getPoints();
                LatLng latLng;
                for (Point point:routePoints){
                    latLng = new LatLng(point.getCoordinate().getLatitude(), point.getCoordinate().getLongitude());
//                    builder.include(latLng);
                    polyLineOptions.add(latLng);
                }
//                builder.include(new LatLng(inicio.getCoordinate().getLatitude(), inicio.getCoordinate().getLongitude()));
//                builder.include(new LatLng(fin.getCoordinate().getLatitude(), fin.getCoordinate().getLongitude()));

                diminuirNumeroChamadas();
                if (getNumeroChamadas() == 0) {
                    if (hasCallback()) {
                        callback.onSuccess(listaRutas);
                    }
                    clearCallback();
                }

            }

            @Override
            public void onFailure(Error error) {
                Log.e(TAG, error.getMessage());
                diminuirNumeroChamadas();
                if (hasCallback()){
                    callback.onError(error);
                }
                clearCallback();
            }
        });
    }

    private void incrementarNumeroChamadas() {
        this.numeroChamadas++;
    }

    private void diminuirNumeroChamadas() {
        this.numeroChamadas--;
    }

    private int getNumeroChamadas() {
        return this.numeroChamadas;
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
