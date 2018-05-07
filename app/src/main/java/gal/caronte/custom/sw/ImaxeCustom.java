package gal.caronte.custom.sw;

import android.os.Parcel;
import android.os.Parcelable;

public class ImaxeCustom implements Parcelable {

    private Integer idImaxe;
    private String nome;
    private String descricion;
    private Integer idPuntoInterese;
    private Object imaxe;
    //De inicio, null. Hai que establecerlle o valor
    private Integer idEdificio;

    public ImaxeCustom() {
        super();
    }

    public ImaxeCustom(Integer idImaxe, String nome, String descricion, Integer idPuntoInterese, Object imaxe) {
        super();
        this.idImaxe = idImaxe;
        this.nome = nome;
        this.descricion = descricion;
        this.idPuntoInterese = idPuntoInterese;

        this.imaxe = imaxe;
    }

    public Integer getIdImaxe() {
        return this.idImaxe;
    }

    public void setIdImaxe(Integer idImaxe) {
        this.idImaxe = idImaxe;
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

    public Integer getIdPuntoInterese() {
        return idPuntoInterese;
    }

    public void setIdPuntoInterese(Integer idPuntoInterese) {
        this.idPuntoInterese = idPuntoInterese;
    }

    public Object getImaxe() {
        return this.imaxe;
    }

    public void setImaxe(Object imaxe) {
        this.imaxe = imaxe;
    }

    public Integer getIdEdificio() {
        return idEdificio;
    }

    public void setIdEdificio(Integer idEdificio) {
        this.idEdificio = idEdificio;
    }

    protected ImaxeCustom(Parcel in) {
        this.idImaxe = in.readInt();
        this.nome = in.readString();
        this.descricion = in.readString();
        this.idPuntoInterese = in.readInt();
        this.idEdificio = in.readInt();
//        this.imaxe = in.read(Integer.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idImaxe);
        dest.writeString(this.nome);
        dest.writeString(this.descricion);
        dest.writeInt(this.idPuntoInterese);
        dest.writeInt(this.idEdificio);
//        dest.writeList(this.imaxe);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImaxeCustom> CREATOR = new Creator<ImaxeCustom>() {
        @Override
        public ImaxeCustom createFromParcel(Parcel in) {
            return new ImaxeCustom(in);
        }

        @Override
        public ImaxeCustom[] newArray(int size) {
            return new ImaxeCustom[size];
        }
    };


}
