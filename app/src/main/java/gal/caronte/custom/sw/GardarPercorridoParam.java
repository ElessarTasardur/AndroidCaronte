package gal.caronte.custom.sw;

import java.util.List;

/**
 * Created by ElessarTasardur on 12/01/2018.
 */

public class GardarPercorridoParam {

    private PercorridoParam percorrido;
    private List<PuntoInterese> listaPoi;

    public GardarPercorridoParam() {
        super();
    }

    public GardarPercorridoParam(PercorridoParam percorrido, List<PuntoInterese> poi) {
        super();
        this.percorrido = percorrido;
        this.listaPoi = poi;
    }

    /**
     * @return the percorrido
     */
    public PercorridoParam getPercorrido() {
        return this.percorrido;
    }

    /**
     * @param percorrido the percorrido to set
     */
    public void setPercorrido(PercorridoParam percorrido) {
        this.percorrido = percorrido;
    }

    /**
     * @return the lista poi
     */
    public List<PuntoInterese> getListaPoi() {
        return this.listaPoi;
    }

    /**
     * @param listaPoi the lista poi to set
     */
    public void setListaPoi(List<PuntoInterese> listaPoi) {
        this.listaPoi = listaPoi;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GardarPercorridoParam{");
        sb.append("percorrido=").append(percorrido);
        sb.append(", listaPoi=").append(listaPoi);
        sb.append('}');
        return sb.toString();
    }
}
