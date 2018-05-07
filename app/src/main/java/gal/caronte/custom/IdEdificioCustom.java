package gal.caronte.custom;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by ElessarTasardur on 17/02/2018.
 */

public class IdEdificioCustom implements Parcelable {

    private int idEdificio;

    public IdEdificioCustom() {
        super();
    }

    public IdEdificioCustom(int idEdificio) {
        super();
        this.idEdificio = idEdificio;
    }

    public int getIdEdificio() {
        return idEdificio;
    }

    public void setIdEdificio(int idEdificio) {
        this.idEdificio = idEdificio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdEdificioCustom that = (IdEdificioCustom) o;
        return idEdificio == that.idEdificio;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEdificio);
    }

    protected IdEdificioCustom(Parcel in) {
        this.idEdificio = in.readInt();
    }

    public static final Creator<IdEdificioCustom> CREATOR = new Creator<IdEdificioCustom>() {
        @Override
        public IdEdificioCustom createFromParcel(Parcel in) {
            return new IdEdificioCustom(in);
        }

        @Override
        public IdEdificioCustom[] newArray(int size) {
            return new IdEdificioCustom[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idEdificio);
    }

}
