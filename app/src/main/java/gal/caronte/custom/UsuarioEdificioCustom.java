package gal.caronte.custom;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ElessarTasardur on 17/02/2018.
 */

public class UsuarioEdificioCustom implements Parcelable {

    private int idUsuario;
    private String nomeMostrar;
    private List<Integer> listaIdEdificioAdministrador;

    public UsuarioEdificioCustom() {
        super();
        this.listaIdEdificioAdministrador = new ArrayList<>(1);
    }

    public UsuarioEdificioCustom(int idUsuario, String nomeMostrar, List<Integer> listaIdEdificioAdministrador) {
        this.idUsuario = idUsuario;
        this.nomeMostrar = nomeMostrar;
        this.listaIdEdificioAdministrador = listaIdEdificioAdministrador;
    }

    protected UsuarioEdificioCustom(Parcel in) {
        this.idUsuario = in.readInt();
        this.nomeMostrar = in.readString();
        this.listaIdEdificioAdministrador = in.readArrayList(Integer.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idUsuario);
        dest.writeString(this.nomeMostrar);
        dest.writeList((List) this.listaIdEdificioAdministrador);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UsuarioEdificioCustom> CREATOR = new Creator<UsuarioEdificioCustom>() {
        @Override
        public UsuarioEdificioCustom createFromParcel(Parcel in) {
            return new UsuarioEdificioCustom(in);
        }

        @Override
        public UsuarioEdificioCustom[] newArray(int size) {
            return new UsuarioEdificioCustom[size];
        }
    };

    public int getIdUsuario() {
        return this.idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeMostrar() {
        return this.nomeMostrar;
    }

    public void setNomeMostrar(String nomeMostrar) {
        this.nomeMostrar = nomeMostrar;
    }

    public List<Integer> getListaIdEdificioAdministrador() {
        return this.listaIdEdificioAdministrador;
    }

    public void setListaIdEdificioAdministrador(List<Integer> listaIdEdificioAdministrador) {
        this.listaIdEdificioAdministrador = listaIdEdificioAdministrador;
    }

}
