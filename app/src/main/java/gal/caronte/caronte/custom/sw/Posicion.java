package gal.caronte.caronte.custom.sw;

import java.util.Objects;

/**
 * Created by ElessarTasardur on 09/01/2018.
 */

public class Posicion {

    private Short idEdificio;
    private Short idPlanta;
    private Short nivel;
    private Float latitude;
    private Float lonxitude;

    public Posicion() {
        super();
    }

    public Posicion(Short idEdificio, Short idPlanta, Short nivel, Float latitude, Float lonxitude) {
        super();
        this.idEdificio = idEdificio;
        this.idPlanta = idPlanta;
        this.nivel = nivel;
        this.latitude = latitude;
        this.lonxitude = lonxitude;
    }

    /**
     * @return the idEdificio
     */
    public Short getIdEdificio() {
        return this.idEdificio;
    }

    /**
     * @param idEdificio the idEdificio to set
     */
    public void setIdEdificio(Short idEdificio) {
        this.idEdificio = idEdificio;
    }

    /**
     * @return the idPlanta
     */
    public Short getIdPlanta() {
        return this.idPlanta;
    }

    /**
     * @param idPlanta the idPlanta to set
     */
    public void setIdPlanta(Short idPlanta) {
        this.idPlanta = idPlanta;
    }

    /**
     * @return the nivel
     */
    public Short getNivel() {
        return this.nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(Short nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the latitude
     */
    public Float getLatitude() {
        return this.latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the lonxitude
     */
    public Float getLonxitude() {
        return this.lonxitude;
    }

    /**
     * @param lonxitude the lonxitude to set
     */
    public void setLonxitude(Float lonxitude) {
        this.lonxitude = lonxitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Posicion posicion = (Posicion) o;
        return Objects.equals(idEdificio, posicion.idEdificio) &&
                Objects.equals(idPlanta, posicion.idPlanta) &&
                Objects.equals(nivel, posicion.nivel) &&
                Objects.equals(latitude, posicion.latitude) &&
                Objects.equals(lonxitude, posicion.lonxitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEdificio, idPlanta, nivel, latitude, lonxitude);
    }
}
