package gal.caronte.caronte.custom;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.model.cartography.Poi;

/**
 * Created by ElessarTasardur on 15/10/2017.
 */

public class Edificio {

    private Building edificio;
    private Collection<Piso> pisos;
    private Collection<Poi> listaPoi;

    public Edificio(Building edificio) {
        super();
        this.edificio = edificio;
    }

    public Building getEdificio() {
        return this.edificio;
    }

    public void setEdificio(Building edificio) {
        this.edificio = edificio;
    }

    public Collection<Piso> getPisos() {
        return this.pisos;
    }

    public void setPisos(Collection<Floor> pisos) {
        this.pisos = new ArrayList<>(pisos.size());
        for (Floor piso : pisos) {
            this.pisos.add(new Piso(piso));
        }
    }

    public Bitmap getMapa(String idPiso) {
        Bitmap retorno = null;
        for (Piso piso : this.pisos) {
            if (piso.getPiso().getIdentifier().equals(idPiso)) {
                retorno = piso.getMapa();
            }
        }
        return retorno;
    }

    public void setMapa(String idPiso, Bitmap mapa) {
        for (Piso piso : this.pisos) {
            if (piso.getPiso().getIdentifier().equals(idPiso)) {
                piso.setMapa(mapa);
                break;
            }
        }
    }

    public Floor getFloor(String idPiso) {
        Floor floor = null;
        for (Piso piso : this.pisos) {
            if (piso.getPiso().getIdentifier().equals(idPiso)) {
                floor = piso.getPiso();
                break;
            }
        }
        return floor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Edificio edificioEquals = (Edificio) o;
        return Objects.equals(edificio, edificioEquals.edificio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edificio);
    }


    public Collection<Poi> getListaPoi() {
        return listaPoi;
    }

    public void setListaPoi(Collection<Poi> listaPoi) {
        this.listaPoi = listaPoi;
    }
}
