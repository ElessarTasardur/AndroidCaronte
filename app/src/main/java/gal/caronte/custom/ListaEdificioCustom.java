package gal.caronte.custom;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

import gal.caronte.custom.sw.EdificioCustom;

/**
 * Created by ElessarTasardur on 19/02/2018.
 */

public class ListaEdificioCustom implements Parcelable {

    private List<EdificioCustom> listaEdificio;

    public ListaEdificioCustom() {
        super();
    }

    public ListaEdificioCustom(List<EdificioCustom> listaEdificio) {
        super();
        this.listaEdificio = listaEdificio;
    }

    protected ListaEdificioCustom(Parcel in) {
        this.listaEdificio = in.readArrayList(EdificioCustom.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList((List) this.listaEdificio);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ListaEdificioCustom> CREATOR = new Creator<ListaEdificioCustom>() {
        @Override
        public ListaEdificioCustom createFromParcel(Parcel in) {
            return new ListaEdificioCustom(in);
        }

        @Override
        public ListaEdificioCustom[] newArray(int size) {
            return new ListaEdificioCustom[size];
        }
    };

    public List<EdificioCustom> getListaEdificio() {
        return listaEdificio;
    }

    public void setListaEdificio(List<EdificioCustom> listaEdificio) {
        this.listaEdificio = listaEdificio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListaEdificioCustom that = (ListaEdificioCustom) o;
        return Objects.equals(listaEdificio, that.listaEdificio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listaEdificio);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ListaEdificioCustom{");
        sb.append("listaEdificio=").append(listaEdificio);
        sb.append('}');
        return sb.toString();
    }
}
