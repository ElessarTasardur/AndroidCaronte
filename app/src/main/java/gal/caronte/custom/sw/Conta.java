package gal.caronte.custom.sw;

import java.util.Objects;

/**
 * Created by ElessarTasardur on 25/11/2017.
 */

public class Conta {

    private Integer idContaSitum;
    private String contaUsuario;
    private String nome;
    private String contrasinal;
    private Boolean publica;

    public Conta() {
        super();
    }

    public Conta(Integer idContaSitum, String contaUsuario, String nome, String contrasinal, Boolean publica) {
        super();
        this.idContaSitum = idContaSitum;
        this.contaUsuario = contaUsuario;
        this.nome = nome;
        this.contrasinal = contrasinal;
        this.publica = publica;
    }

    public Integer getIdContaSitum() {
        return this.idContaSitum;
    }

    public void setIdContaSitum(Integer idContaSitum) {
        this.idContaSitum = idContaSitum;
    }

    public String getContaUsuario() {
        return this.contaUsuario;
    }

    public void setContaUsuario(String contaUsuario) {
        this.contaUsuario = contaUsuario;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContrasinal() {
        return this.contrasinal;
    }

    public void setContrasinal(String contrasinal) {
        this.contrasinal = contrasinal;
    }

    public Boolean getPublica() {
        return this.publica;
    }

    public void setPublica(Boolean publica) {
        this.publica = publica;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.idContaSitum);
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
        return Objects.equals(this.idContaSitum, other.idContaSitum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.nome;
    }
}
