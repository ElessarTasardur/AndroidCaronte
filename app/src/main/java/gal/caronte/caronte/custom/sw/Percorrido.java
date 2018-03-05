package gal.caronte.caronte.custom.sw;

import java.util.ArrayList;
import java.util.List;

import gal.caronte.caronte.custom.MarcadorCustom;

/**
 * Created by ElessarTasardur on 10/01/2018.
 */

public class Percorrido {

    private Integer idPercorrido;
    private String nome;
    private String descricion;
    private Integer idEdificio;
    private List<MarcadorCustom> listaPIP = new ArrayList<>();

    public Percorrido() {
        super();
    }

    public Percorrido(Integer idPercorrido, String nome, String descricion, Integer idEdificio) {
        super();
        this.idPercorrido = idPercorrido;
        this.nome = nome;
        this.descricion = descricion;
        this.idEdificio = idEdificio;
    }

    /**
     * @return the idPercorrido
     */
    public Integer getIdPercorrido() {
        return this.idPercorrido;
    }

    /**
     * @param idPercorrido the idPercorrido to set
     */
    public void setIdPercorrido(Integer idPercorrido) {
        this.idPercorrido = idPercorrido;
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
     * @return the idEdificio
     */
    public Integer getIdEdificio() {
        return this.idEdificio;
    }

    /**
     * @param idEdificio the idEdificio to set
     */
    public void setIdEdificio(Integer idEdificio) {
        this.idEdificio = idEdificio;
    }

    /**
     * @return the listaPIP
     */
    public List<MarcadorCustom> getListaPIP() {
        return listaPIP;
    }

    /**
     * @param listaPIP the listaPIP to set
     */
    public void setListaPIP(List<MarcadorCustom> listaPIP) {
        this.listaPIP = listaPIP;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
