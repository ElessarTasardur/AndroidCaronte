package gal.caronte.caronte.custom;

import com.google.android.gms.maps.model.Marker;

import java.util.Objects;

/**
 * Created by ElessarTasardur on 16/11/2017.
 */

public class MarcadorCustom {

    private Marker marcadorGoogle;
    private Short idPoi;
    private Short idPlanta;

    public MarcadorCustom() {
        super();
    }

    public MarcadorCustom(Short idPoi, Short idPlanta) {
        super();
        this.idPoi = idPoi;
        this.idPlanta = idPlanta;
    }

    public Marker getMarcadorGoogle() {
        return marcadorGoogle;
    }

    public void setMarcadorGoogle(Marker marcadorGoogle) {
        this.marcadorGoogle = marcadorGoogle;
    }

    public Short getIdPoi() {
        return idPoi;
    }

    public void setIdPoi(Short idPoi) {
        this.idPoi = idPoi;
    }

    public Short getIdPlanta() {
        return idPlanta;
    }

    public void setIdPlanta(Short idPlanta) {
        this.idPlanta = idPlanta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarcadorCustom that = (MarcadorCustom) o;
        return Objects.equals(idPoi, that.idPoi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPoi);
    }
}
