package gal.caronte.caronte.custom.sw;

import java.util.Objects;

import gal.caronte.caronte.util.StringUtil;

/**
 * Created by ElessarTasardur on 25/11/2017.
 */

public class Conta {

    private String contaUsuario;
    private String nome;
    private String contrasinal;

    public Conta() {
        super();
    }

    public Conta(String contaUsuario, String nome, String contrasinal) {
        super();
        this.contaUsuario = contaUsuario;
        this.nome = nome;
        this.contrasinal = contrasinal;
    }

    public String getContaUsuario() {
        return contaUsuario;
    }

    public void setContaUsuario(String contaUsuario) {
        this.contaUsuario = contaUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
        return Objects.hash(this.contaUsuario, this.nome);
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
        return Objects.equals(this.contaUsuario, other.contaUsuario)
                && Objects.equals(this.nome, other.nome);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.nome;
    }
}
