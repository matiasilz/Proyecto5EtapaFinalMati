package analizadorSemantico;

import analizadorLexico.Token;

public class Parametro {
    private Token tkParametro;
    private String nombre;
    private Tipo tipo;

    public Parametro(Token tkParametro, Tipo tipo)
    {
        this.tkParametro = tkParametro;
        nombre = tkParametro.getLexema();
        this.tipo = tipo;
    }
    public Token getTkParametro() {
        return tkParametro;
    }
    public String getNombre() {
        return nombre;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String toString() {
        return "Parametro: " + nombre + " Tipo: " + tipo.getToken().getLexema();
    }


    public void setOffset(int i) {
    }
}
