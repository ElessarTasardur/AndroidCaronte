package gal.caronte.caronte.custom.sw;

import java.util.Objects;

/**
 * Created by ElessarTasardur on 05/11/2017.
 */

public class PuntoInterese {

    private Short idPuntoInterese;
    private String nome;
    private String descricion;
    private Posicion posicion;

    public PuntoInterese() {
        super();
    }
    
    public PuntoInterese(Short idPuntoInterese, String nome, String descricion, Short idEdificio, Short idPlanta, Short nivel,
                         Float latitude, Float lonxitude) {
        super();
        this.idPuntoInterese = idPuntoInterese;
        this.nome = nome;
        this.descricion = descricion;
        this.posicion = new Posicion(idEdificio, idPlanta, nivel, latitude, lonxitude);
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
     * @return the posicion
     */
    public Posicion getPosicion() {
        return posicion;
    }

    /**
     * @param posicion the posicion to set
     */
    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.nome, this.posicion);
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
                && Objects.equals(this.posicion, other.posicion);
    }

}
