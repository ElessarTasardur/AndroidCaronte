package gal.caronte.custom;

import android.graphics.Bitmap;

import es.situm.sdk.model.cartography.Floor;

/**
 * Created by ElessarTasardur on 27/10/2017.
 */

public class Piso {
    private Floor piso;
    private Bitmap mapa;

    public Piso(Floor piso) {
        super();
        this.piso = piso;
    }

    public Floor getPiso() {
        return this.piso;
    }

    public Bitmap getMapa() {
        return this.mapa;
    }

    public void setMapa(Bitmap mapa) {
        this.mapa = mapa;
    }
}
