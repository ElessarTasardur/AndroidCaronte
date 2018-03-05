package gal.caronte.caronte.custom.sw;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by ElessarTasardur on 09/01/2018.
 */

public class Posicion implements Parcelable {

    private Integer idEdificio;
    private Integer idPlanta;
    private Integer nivel;
    private Float latitude;
    private Float lonxitude;

    public Posicion() {
        super();
    }

    public Posicion(Integer idEdificio, Integer idPlanta, Integer nivel, Float latitude, Float lonxitude) {
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
    public Integer getIdEdificio() {
        return this.idEdificio;
    }

    /**
     * @param idEdificio the idEdificio to set
     */
    public void setIdEdificio(Integer idEdificio) {
        this.idEdificio = idEdificio;
    }

    /**
     * @return the idPlanta
     */
    public Integer getIdPlanta() {
        return this.idPlanta;
    }

    /**
     * @param idPlanta the idPlanta to set
     */
    public void setIdPlanta(Integer idPlanta) {
        this.idPlanta = idPlanta;
    }

    /**
     * @return the nivel
     */
    public Integer getNivel() {
        return this.nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(Integer nivel) {
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

    protected Posicion(Parcel in) {
        this.idEdificio = in.readInt();
        this.idPlanta = in.readInt();
        this.nivel = in.readInt();
        this.latitude = in.readFloat();
        this.lonxitude = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idEdificio);
        dest.writeInt(this.idPlanta);
        dest.writeInt(this.nivel);
        dest.writeFloat(this.latitude);
        dest.writeFloat(this.lonxitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Posicion> CREATOR = new Parcelable.Creator<Posicion>() {
        @Override
        public Posicion createFromParcel(Parcel in) {
            return new Posicion(in);
        }

        @Override
        public Posicion[] newArray(int size) {
            return new Posicion[size];
        }
    };
}
