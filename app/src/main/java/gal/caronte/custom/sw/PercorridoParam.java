package gal.caronte.custom.sw;

/**
 * Created by ElessarTasardur on 10/01/2018.
 */

public class PercorridoParam {

    private Integer idPercorrido;
    private String nome;
    private String descricion;
    private Integer idEdificio;
    private Integer tempoTotal;
    private Integer tempoCaminho;

    public PercorridoParam() {
        super();
    }

    public PercorridoParam(Integer idPercorrido, String nome, String descricion, Integer idEdificio, Integer tempoTotal, Integer tempoCaminho) {
        super();
        this.idPercorrido = idPercorrido;
        this.nome = nome;
        this.descricion = descricion;
        this.idEdificio = idEdificio;
        this.tempoTotal = tempoTotal;
        this.tempoCaminho = tempoCaminho;
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

    public Integer getTempoTotal() {
        return this.tempoTotal;
    }

    public void setTempoTotal(Integer tempoTotal) {
        this.tempoTotal = tempoTotal;
    }

    public Integer getTempoCaminho() {
        return this.tempoCaminho;
    }

    public void setTempoCaminho(Integer tempoCaminho) {
        this.tempoCaminho = tempoCaminho;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PercorridoParam{");
        sb.append("idPercorrido=").append(idPercorrido);
        sb.append(", nome='").append(nome).append('\'');
        sb.append(", descricion='").append(descricion).append('\'');
        sb.append(", idEdificio=").append(idEdificio);
        sb.append('}');
        return sb.toString();
    }
}
