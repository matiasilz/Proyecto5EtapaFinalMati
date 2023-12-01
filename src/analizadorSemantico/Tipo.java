package analizadorSemantico;

import analizadorLexico.Token;


public class Tipo {
    public Token getToken() {
        return tokenTipo;
    }
    protected Token tokenTipo;
    protected TablaDeSimbolos tabladeSimbolos;
    public Tipo(Token actualToken, TablaDeSimbolos tabladeSimbolos) {
        tokenTipo = actualToken;
        this.tabladeSimbolos = tabladeSimbolos;

    }

    public boolean conformaCon(Tipo otroTipo) {
        return false;
    }

    public boolean esCompatible(Tipo tipoArgumento) {
        if (this instanceof TipoPrimitivo){
            if (tipoArgumento instanceof TipoPrimitivo){
                return this.tokenTipo.getLexema().equals(tipoArgumento.tokenTipo.getLexema());
            } else {
                return this.getToken().getLexema().equals("null") && tipoArgumento instanceof TipoReferencia;
            }
        } else {
            if (tipoArgumento instanceof TipoPrimitivo){
                return tipoArgumento.getToken().getLexema().equals("null");
            } else {
                return this.conformaCon(tipoArgumento);
            }
        }
    }
}