package gal.caronte.caronte.custom.sw;

import java.util.Objects;

/**
 * Created by ElessarTasardur on 05/11/2017.
 */

public class PuntoInterese {

    private Short idPuntoInterese;
    private String nome;
    private String descricion;
    private Short idEdificio;
    private Short idPlanta;
    private Float latitude;
    private Float lonxitude;

    public PuntoInterese() {
        super();
    }
    
    public PuntoInterese(Short idPuntoInterese, String nome, String descricion, Short idEdificio, Short idPlanta,
                         Float latitude, Float lonxitude) {
        super();
        this.idPuntoInterese = idPuntoInterese;
        this.nome = nome;
        this.descricion = descricion;
        this.idEdificio = idEdificio;
        this.idPlanta = idPlanta;
        this.latitude = latitude;
        this.lonxitude = lonxitude;
    }

    /**
     * @return the idPuntoInterese
     */
    public Short getIdPuntoInterese() {
        return this.idPuntoInterese;
    }

    /**
     * @param idPuntoInterese the idPuntoInterese to set
     */
    public void setIdPuntoInterese(Short idPuntoInterese) {
        this.idPuntoInterese = idPuntoInterese;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the descricion
     */
    public String getDescricion() {
        return this.descricion;
    }

    /**
     * @param descricion the descricion to set
     */
    public void setDescricion(String descricion) {
        this.descricion = descricion;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.nome, this.idEdificio, this.idPlanta, this.latitude, this.lonxitude);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PuntoInterese other = (PuntoInterese) obj;
        return Objects.equals(this.nome, other.nome)
                && Objects.equals(this.idEdificio, other.idEdificio)
                && Objects.equals(this.idPlanta, other.idPlanta)
                && Objects.equals(this.latitude, other.latitude)
                && Objects.equals(this.lonxitude, other.lonxitude);
    }

}
