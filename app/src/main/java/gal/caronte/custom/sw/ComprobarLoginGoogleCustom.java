package gal.caronte.custom.sw;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComprobarLoginGoogleCustom {

    private Integer idUsuario;
    private String contaUsuario;
    private List<Integer> listaIdEdificioAdministrador;

    public ComprobarLoginGoogleCustom() {
        super();
        this.listaIdEdificioAdministrador = new ArrayList<>();
    }

    public ComprobarLoginGoogleCustom(Integer idUsuario, String contaUsuario, List<Integer> listaIdEdificioAdministrador) {
        super();
        this.idUsuario = idUsuario;
        this.contaUsuario = contaUsuario;
        this.listaIdEdificioAdministrador = listaIdEdificioAdministrador;
    }

    public Integer getIdUsuario() {
        return this.idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getContaUsuario() {
        return contaUsuario;
    }

    public void setContaUsuario(String contaUsuario) {
        this.contaUsuario = contaUsuario;
    }

    /**
     * @return the listaIdEdificioAdministrador
     */
    public List<Integer> getListaIdEdificioAdministrador() {
        return this.listaIdEdificioAdministrador;
    }

    /**
     * @param listaIdEdificioAdministrador the listaIdEdificioAdministrador to set
     */
    public void setListaIdEdificioAdministrador(List<Integer> listaIdEdificioAdministrador) {
        this.listaIdEdificioAdministrador = listaIdEdificioAdministrador;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.idUsuario);
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
        final ComprobarLoginGoogleCustom other = (ComprobarLoginGoogleCustom) obj;
        return Objects.equals(this.idUsuario, other.idUsuario);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ComprobarLoginGoogleCustom [idUsuario=");
        builder.append(this.idUsuario);
        builder.append(", contaUsuario=");
        builder.append(this.contaUsuario);
        builder.append(", listaIdEdificioAdministrador=");
        builder.append(this.listaIdEdificioAdministrador);
        builder.append("]");
        return builder.toString();
    }

}
