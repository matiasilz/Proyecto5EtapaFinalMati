package analizadorSemantico;

import analizadorLexico.Token;

public class Parametro extends Atributo{

    public Parametro(Token tkParametro, Tipo tipo)
    {
        super(tkParametro, tipo);
    }
    public Token getTkParametro() {
        return tkAtributo;
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
        offset = i;
    }

    public int getOffset() {
        return offset;
    }
}
