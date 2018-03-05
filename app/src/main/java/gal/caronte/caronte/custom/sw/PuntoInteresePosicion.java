package gal.caronte.caronte.custom.sw;

/**
 * Created by ElessarTasardur on 10/01/2018.
 */

public class PuntoInteresePosicion {

    private Integer idPercorrido;
    private Integer idPuntoInterese;
    private Integer posicion;

    public PuntoInteresePosicion() {
        super();
    }

    public PuntoInteresePosicion(Integer idPercorrido, Integer idPuntoInterese, Integer posicion) {
        super();
        this.idPercorrido = idPercorrido;
        this.idPuntoInterese = idPuntoInterese;
        this.posicion = posicion;
    }

    public Integer getIdPercorrido() {
        return this.idPercorrido;
    }

    public void setIdPercorrido(Integer idPercorrido) {
        this.idPercorrido = idPercorrido;
    }

    public Integer getIdPuntoInterese() {
        return this.idPuntoInterese;
    }

    public void setIdPuntoInterese(Integer idPuntoInterese) {
        this.idPuntoInterese = idPuntoInterese;
    }

    public Integer getPosicion() {
        return this.posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

}
