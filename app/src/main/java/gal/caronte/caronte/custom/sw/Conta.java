package gal.caronte.caronte.custom.sw;

import java.util.Objects;

/**
 * Created by ElessarTasardur on 25/11/2017.
 */

public class Conta {

    private String nomeUsuario;
    private String contrasinal;

    public Conta() {
        super();
    }

    public Conta(String nomeUsuario, String contrasinal) {
        super();
        this.nomeUsuario = nomeUsuario;
        this.contrasinal = contrasinal;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getContrasinal() {
        return contrasinal;
    }

    public void setContrasinal(String contrasinal) {
        this.contrasinal = contrasinal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.nomeUsuario, this.contrasinal);
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
        final Conta other = (Conta) obj;
        return Objects.equals(this.nomeUsuario, other.nomeUsuario)
                && Objects.equals(this.contrasinal, other.contrasinal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return nomeUsuario.toString();
    }
}
