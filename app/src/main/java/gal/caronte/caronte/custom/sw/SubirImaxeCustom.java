package gal.caronte.caronte.custom.sw;

import java.io.File;

import gal.caronte.caronte.servizo.SubirImaxe;

public class SubirImaxeCustom {

    private Integer idEdificio;
    private Integer idPoi;
    private String nomeImaxe;
    private String descricionImaxe;
    private File imaxe;
    private String ruta;

    public SubirImaxeCustom() {
        super();
    }

    public SubirImaxeCustom(Integer idEdificio, Integer idPoi, String nomeImaxe, String descricionImaxe, String ruta) {
        super();
        this.idEdificio = idEdificio;
        this.idPoi = idPoi;
        this.nomeImaxe = nomeImaxe;
        this.descricionImaxe = descricionImaxe;
        this.ruta = ruta;
    }

    public SubirImaxeCustom(Integer idEdificio, Integer idPoi, String nomeImaxe, String descricionImaxe, File imaxe) {
        super();
        this.idEdificio = idEdificio;
        this.idPoi = idPoi;
        this.nomeImaxe = nomeImaxe;
        this.descricionImaxe = descricionImaxe;
        this.imaxe = imaxe;
    }

    public Integer getIdEdificio() {
        return this.idEdificio;
    }

    public void setIdEdificio(Integer idEdificio) {
        this.idEdificio = idEdificio;
    }

    public Integer getIdPoi() {
        return this.idPoi;
    }

    public void setIdPoi(Integer idPoi) {
        this.idPoi = idPoi;
    }

    public String getNomeImaxe() {
        return this.nomeImaxe;
    }

    public void setNomeImaxe(String nomeImaxe) {
        this.nomeImaxe = nomeImaxe;
    }

    public String getDescricionImaxe() {
        return this.descricionImaxe;
    }

    public void setDescricionImaxe(String descricionImaxe) {
        this.descricionImaxe = descricionImaxe;
    }

    public File getImaxe() {
        return imaxe;
    }

    public void setImaxe(File imaxe) {
        this.imaxe = imaxe;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

}
