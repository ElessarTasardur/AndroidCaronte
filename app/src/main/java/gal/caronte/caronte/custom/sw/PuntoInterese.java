package gal.caronte.caronte.custom.sw;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

/**
 * Created by ElessarTasardur on 05/11/2017.
 */

public class PuntoInterese implements Parcelable {

    private Integer idPuntoInterese;
    private String nome;
    private String descricion;
    private Posicion posicion;
    private Integer tempo;
    private List<Integer> listaIdImaxe;
    private List<ImaxeCustom> listaImaxe;

    public PuntoInterese() {
        super();
    }

    public PuntoInterese(Integer idPuntoInterese, String nome, String descricion, Integer tempo) {
        super();
        this.idPuntoInterese = idPuntoInterese;
        this.nome = nome;
        this.descricion = descricion;
        this.tempo = tempo;
    }

    public PuntoInterese(Integer idPuntoInterese, String nome, String descricion, Integer idEdificio, Integer idPlanta, Integer nivel,
                         Double latitude, Double lonxitude, Integer tempo) {
        this(idPuntoInterese, nome, descricion, tempo);
        this.posicion = new Posicion(idEdificio, idPlanta, nivel, latitude, lonxitude);
    }

    /**
     * @return the idPuntoInterese
     */
    public Integer getIdPuntoInterese() {
        return this.idPuntoInterese;
    }

    /**
     * @param idPuntoInterese the idPuntoInterese to set
     */
    public void setIdPuntoInterese(Integer idPuntoInterese) {
        this.idPuntoInterese = idPuntoInterese;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the descricion
     */
    public String getDescricion() {
        return this.descricion;
    }

    /**
     * @param descricion the descricion to set
     */
    public void setDescricion(String descricion) {
        this.descricion = descricion;
    }

    /**
     * @return the posicion
     */
    public Posicion getPosicion() {
        return posicion;
    }

    /**
     * @param posicion the posicion to set
     */
    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    public Integer getTempo() {
        return this.tempo;
    }

    public void setTempo(Integer tempo) {
        this.tempo = tempo;
    }

    public List<Integer> getListaIdImaxe() {
        return this.listaIdImaxe;
    }

    public void setListaIdImaxe(List<Integer> listaIdImaxe) {
        this.listaIdImaxe = listaIdImaxe;
    }

    public List<ImaxeCustom> getListaImaxe() {
        return this.listaImaxe;
    }

    public void setListaImaxe(List<ImaxeCustom> listaImaxe) {
        this.listaImaxe = listaImaxe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.nome, this.posicion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PuntoInterese other = (PuntoInterese) obj;
        return Objects.equals(this.nome, other.nome)
                && Objects.equals(this.posicion, other.posicion);
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public String toString() {
//        return this.nome;
//    }


    @Override
    public String toString() {
        return this.nome;
    }

    protected PuntoInterese(Parcel in) {
        this.idPuntoInterese = in.readInt();
        this.nome = in.readString();
        this.descricion = in.readString();
        this.posicion = in.readParcelable(Posicion.class.getClassLoader());
        this.tempo = in.readInt();
        this.listaIdImaxe = in.readArrayList(Integer.class.getClassLoader());
        this.listaImaxe = in.readArrayList(ImaxeCustom.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idPuntoInterese);
        dest.writeString(this.nome);
        dest.writeString(this.descricion);
        dest.writeParcelable(this.posicion, 0);
        dest.writeInt(this.tempo);
        dest.writeList(this.listaIdImaxe);
        dest.writeList(this.listaImaxe);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PuntoInterese> CREATOR = new Creator<PuntoInterese>() {
        @Override
        public PuntoInterese createFromParcel(Parcel in) {
            return new PuntoInterese(in);
        }

        @Override
        public PuntoInterese[] newArray(int size) {
            return new PuntoInterese[size];
        }
    };
}
