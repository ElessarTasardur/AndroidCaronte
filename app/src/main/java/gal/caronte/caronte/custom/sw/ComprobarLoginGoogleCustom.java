package gal.caronte.caronte.custom.sw;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComprobarLoginGoogleCustom {

    private boolean loginCorrecto;
    private List<Short> listaIdEdificioAdministrador;

    public ComprobarLoginGoogleCustom() {
        super();
        this.listaIdEdificioAdministrador = new ArrayList<>();
    }

    public ComprobarLoginGoogleCustom(boolean loginCorrecto, List<Short> listaIdEdificioAdministrador) {
        super();
        this.loginCorrecto = loginCorrecto;
        this.listaIdEdificioAdministrador = listaIdEdificioAdministrador;
    }

    /**
     * @return the loginCorrecto
     */
    public boolean isLoginCorrecto() {
        return this.loginCorrecto;
    }

    /**
     * @param loginCorrecto the loginCorrecto to set
     */
    public void setLoginCorrecto(boolean loginCorrecto) {
        this.loginCorrecto = loginCorrecto;
    }

    /**
     * @return the listaIdEdificioAdministrador
     */
    public List<Short> getListaIdEdificioAdministrador() {
        return this.listaIdEdificioAdministrador;
    }

    /**
     * @param listaIdEdificioAdministrador the listaIdEdificioAdministrador to set
     */
    public void setListaIdEdificioAdministrador(List<Short> listaIdEdificioAdministrador) {
        this.listaIdEdificioAdministrador = listaIdEdificioAdministrador;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.loginCorrecto, this.listaIdEdificioAdministrador);
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
        return Objects.equals(this.loginCorrecto, other.loginCorrecto)
                && Objects.equals(this.listaIdEdificioAdministrador, other.listaIdEdificioAdministrador);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ComprobarLoginGoogleCustom [loginCorrecto=");
        builder.append(this.loginCorrecto);
        builder.append(", listaIdEdificioAdministrador=");
        builder.append(this.listaIdEdificioAdministrador);
        builder.append("]");
        return builder.toString();
    }


}
