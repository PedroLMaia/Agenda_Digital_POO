package classes;

import java.util.Date;

public class Pessoa {
    private final String nome;
    private final Date idade;
    private final Float altura;

    public Pessoa(String nome, Date idade, Float altura) {
        this.nome = nome;
        this.idade = idade;
        this.altura = altura;
    }

    public String getNome() {
        return nome;
    }

    public Date getIdade() {
        return idade;
    }

    public Float getAltura() {
        return altura;
    }

    @Override
    public String toString() {
        return "Pessoa { " +
                "nome='" + nome + '\'' +
                ", idade=" + idade +
                ", altura=" + altura +
                " }";
    }
}
