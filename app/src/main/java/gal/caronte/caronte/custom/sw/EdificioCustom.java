package gal.caronte.caronte.custom.sw;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

import gal.caronte.caronte.custom.UsuarioEdificioCustom;

/**
 * Created by ElessarTasardur on 19/02/2018.
 */

public class EdificioCustom implements Parcelable {

    private Integer idEdificio;
    private Integer idEdificioExterno;
    private String nome;
    private String descricion;

    public EdificioCustom() {
        super();
    }

    public EdificioCustom(Integer idEdificio, Integer idEdificioExterno, String nome, String descricion) {
        this.idEdificio = idEdificio;
        this.idEdificioExterno = idEdificioExterno;
        this.nome = nome;
        this.descricion = descricion;
    }

    public Integer getIdEdificio() {
        return this.idEdificio;
    }

    public void setIdEdificio(Integer idEdificio) {
        this.idEdificio = idEdificio;
    }

    public Integer getIdEdificioExterno() {
        return this.idEdificioExterno;
    }

    public void setIdEdificioExterno(Integer idEdificioExterno) {
        this.idEdificioExterno = idEdificioExterno;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricion() {
        return this.descricion;
    }

    public void setDescricion(String descricion) {
        this.descricion = descricion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdificioCustom that = (EdificioCustom) o;
        return Objects.equals(idEdificio, that.idEdificio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEdificio);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EdificioCustom{");
        sb.append("idEdificio=").append(idEdificio);
        sb.append(", idEdificioExterno=").append(idEdificioExterno);
        sb.append(", nome='").append(nome).append('\'');
        sb.append(", descricion='").append(descricion).append('\'');
        sb.append('}');
        return sb.toString();
    }

    protected EdificioCustom(Parcel in) {
        this.idEdificio = in.readInt();
        this.idEdificioExterno = in.readInt();
        this.nome = in.readString();
        this.descricion = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idEdificio);
        dest.writeInt(this.idEdificioExterno);
        dest.writeString(this.nome);
        dest.writeString(this.descricion);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EdificioCustom> CREATOR = new Creator<EdificioCustom>() {
        @Override
        public EdificioCustom createFromParcel(Parcel in) {
            return new EdificioCustom(in);
        }

        @Override
        public EdificioCustom[] newArray(int size) {
            return new EdificioCustom[size];
        }
    };
}
