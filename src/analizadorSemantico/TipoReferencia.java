package analizadorSemantico;

import analizadorLexico.Token;

public class TipoReferencia extends Tipo {
    public TipoReferencia(Token tokenActual, TablaDeSimbolos tablaDeSimbolos) {
        super(tokenActual, tablaDeSimbolos);
    }
    public boolean conformaCon(Tipo otroTipo) {
        String nombreClaseActual = this.tokenTipo.getLexema();
        String nombreClaseParametro = otroTipo.getToken().getLexema();
        Clase claseActual = tabladeSimbolos.getClaseOInterface(nombreClaseActual);
        Clase claseParametro = tabladeSimbolos.getClaseOInterface(nombreClaseParametro);
        if (claseActual == null || claseParametro == null) {
            return false;
        }
        if (claseActual.getNombre().equals(claseParametro.getNombre())) {
            return true;
        }
        if(claseActual.esSubTipo(claseParametro)){
            return true;
        }
        else{
            return false;
        }
    }
}
