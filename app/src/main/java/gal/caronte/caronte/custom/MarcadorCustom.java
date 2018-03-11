package gal.caronte.caronte.custom;

import com.google.android.gms.maps.model.Marker;

import java.util.Objects;

import gal.caronte.caronte.custom.sw.PuntoInterese;

/**
 * Created by ElessarTasardur on 16/11/2017.
 */

public class MarcadorCustom {

    private Marker marcadorGoogle;
    private PuntoInterese poi;
    private Integer idPoi;
    private Integer idPlanta;

    public MarcadorCustom() {
        super();
    }

    public MarcadorCustom(Integer idPoi, Integer idPlanta, PuntoInterese poi) {
        super();
        this.idPoi = idPoi;
        this.idPlanta = idPlanta;
        this.poi = poi;
    }

    public Marker getMarcadorGoogle() {
        return this.marcadorGoogle;
    }

    public void setMarcadorGoogle(Marker marcadorGoogle) {
        this.marcadorGoogle = marcadorGoogle;
    }

    public PuntoInterese getPoi() {
        return this.poi;
    }

    public void setPoi(PuntoInterese poi) {
        this.poi = poi;
    }

    public Integer getIdPoi() {
        return this.idPoi;
    }

    public void setIdPoi(Integer idPoi) {
        this.idPoi = idPoi;
    }

    public Integer getIdPlanta() {
        return this.idPlanta;
    }

    public void setIdPlanta(Integer idPlanta) {
        this.idPlanta = idPlanta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarcadorCustom that = (MarcadorCustom) o;
        return Objects.equals(this.idPoi, that.idPoi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idPoi);
    }
}
