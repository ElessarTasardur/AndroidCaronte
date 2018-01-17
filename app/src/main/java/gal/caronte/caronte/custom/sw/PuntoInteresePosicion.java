package gal.caronte.caronte.custom.sw;

/**
 * Created by ElessarTasardur on 10/01/2018.
 */

public class PuntoInteresePosicion {

    private Short idPercorrido;
    private Short idPuntoInterese;
    private Short posicion;

    public PuntoInteresePosicion() {
        super();
    }

    public PuntoInteresePosicion(Short idPercorrido, Short idPuntoInterese, Short posicion) {
        super();
        this.idPercorrido = idPercorrido;
        this.idPuntoInterese = idPuntoInterese;
        this.posicion = posicion;
    }

    public Short getIdPercorrido() {
        return this.idPercorrido;
    }

    public void setIdPercorrido(Short idPercorrido) {
        this.idPercorrido = idPercorrido;
    }

    public Short getIdPuntoInterese() {
        return this.idPuntoInterese;
    }

    public void setIdPuntoInterese(Short idPuntoInterese) {
        this.idPuntoInterese = idPuntoInterese;
    }

    public Short getPosicion() {
        return this.posicion;
    }

    public void setPosicion(Short posicion) {
        this.posicion = posicion;
    }

}
