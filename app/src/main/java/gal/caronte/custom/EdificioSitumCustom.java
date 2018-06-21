package gal.caronte.custom;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import es.situm.sdk.location.util.CoordinateConverter;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.model.cartography.Poi;
import es.situm.sdk.model.location.Bounds;
import es.situm.sdk.model.location.CartesianCoordinate;
import es.situm.sdk.model.location.Coordinate;

/**
 * Created by ElessarTasardur on 15/10/2017.
 */

public class EdificioSitumCustom {

    private Building edificio;
    private Collection<Piso> pisos;
    private Collection<Poi> listaPoi;
    private CoordinateConverter convertidor;

    public EdificioSitumCustom(Building edificio) {
        super();
        this.edificio = edificio;
        this.convertidor = new CoordinateConverter(edificio.getDimensions(), edificio.getCenter(), edificio.getRotation());
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
        if (this.pisos != null) {
            for (Piso piso : this.pisos) {
                if (piso.getPiso().getIdentifier().equals(idPiso)) {
                    retorno = piso.getMapa();
                }
            }
        }
        return retorno;
    }

    public void setMapa(String idPiso, Bitmap mapa) {
        if (this.pisos != null) {
            for (Piso piso : this.pisos) {
                if (piso.getPiso().getIdentifier().equals(idPiso)) {
                    piso.setMapa(mapa);
                    break;
                }
            }
        }
    }

    public Floor getFloor(String idPiso) {
        Floor floor = null;
        if (this.pisos != null) {
            for (Piso piso : this.pisos) {
                if (piso.getPiso().getIdentifier().equals(idPiso)) {
                    floor = piso.getPiso();
                    break;
                }
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
        EdificioSitumCustom edificioEquals = (EdificioSitumCustom) o;
        return Objects.equals(this.edificio, edificioEquals.edificio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.edificio);
    }

    public Collection<Poi> getListaPoi() {
        return this.listaPoi;
    }

    public void setListaPoi(Collection<Poi> listaPoi) {
        this.listaPoi = listaPoi;
    }

    public boolean pertenceCoordenada(double latitude, double lonxitude) {

        Bounds bounds = this.edificio.getBounds();

        double maxLatitude = Math.max(bounds.getNorthEast().getLatitude(), bounds.getNorthWest().getLatitude());
        double minLatitude = Math.max(bounds.getSouthEast().getLatitude(), bounds.getSouthWest().getLatitude());
        double maxLonxitude = Math.max(bounds.getNorthEast().getLongitude(), bounds.getSouthEast().getLongitude());
        double minLonxitude = Math.max(bounds.getNorthWest().getLongitude(), bounds.getSouthWest().getLongitude());

        boolean retorno = false;
        if (maxLatitude > latitude
                && minLatitude < latitude
                && maxLonxitude > lonxitude
                && minLonxitude < lonxitude) {
            retorno = true;
        }

        return retorno;
    }

    public CartesianCoordinate convertirACoordenadaCartesiana(Coordinate coordenada) {
        return this.convertidor.toCartesianCoordinate(coordenada);
    }
}
