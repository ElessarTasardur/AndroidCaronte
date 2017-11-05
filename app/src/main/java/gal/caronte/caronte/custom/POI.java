package gal.caronte.caronte.custom;

import java.util.Objects;

/**
 * Created by ElessarTasardur on 27/10/2017.
 */

public class POI {

    private Short idPoi;
    private String nome;
    private String descricion;
    private Short idEdificio;
    private Short idPlanta;
    private Float latitude;
    private Float lonxitude;

    public POI() {
        super();
    }

    public Short getIdPoi() {
        return idPoi;
    }

    public void setIdPoi(Short idPoi) {
        this.idPoi = idPoi;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricion() {
        return descricion;
    }

    public void setDescricion(String descricion) {
        this.descricion = descricion;
    }

    public Short getIdEdificio() {
        return idEdificio;
    }

    public void setIdEdificio(Short idEdificio) {
        this.idEdificio = idEdificio;
    }

    public Short getIdPlanta() {
        return idPlanta;
    }

    public void setIdPlanta(Short idPlanta) {
        this.idPlanta = idPlanta;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLonxitude() {
        return lonxitude;
    }

    public void setLonxitude(Float lonxitude) {
        this.lonxitude = lonxitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        POI poi = (POI) o;
        return Objects.equals(idPoi, poi.idPoi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPoi);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("POI{");
        sb.append("idPoi=").append(idPoi);
        sb.append(", nome='").append(nome).append('\'');
        sb.append(", descricion='").append(descricion).append('\'');
        sb.append(", idEdificio=").append(idEdificio);
        sb.append(", idPlanta=").append(idPlanta);
        sb.append(", latitude=").append(latitude);
        sb.append(", lonxitude=").append(lonxitude);
        sb.append('}');
        return sb.toString();
    }
}
