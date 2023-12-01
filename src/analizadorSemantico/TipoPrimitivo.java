package analizadorSemantico;

import analizadorLexico.Token;

public class TipoPrimitivo extends Tipo {
    public TipoPrimitivo(Token tokenActual, TablaDeSimbolos tablaDeSimbolos) {
        super(tokenActual, tablaDeSimbolos);
    }

    public boolean conformaCon(Tipo otroTipo) {
        if (tokenTipo.getLexema().equals("int") && otroTipo.getToken().getLexema().equals("int")) {
            return true;
        } else if (tokenTipo.getLexema().equals("boolean") && otroTipo.getToken().getLexema().equals("boolean")) {
            return true;
        } else if (tokenTipo.getLexema().equals("char") && otroTipo.getToken().getLexema().equals("char")) {
            return true;
        } else if (tokenTipo.getLexema().equals("null") && (otroTipo.getToken().getLexema().equals("null") || otroTipo instanceof TipoReferencia)) {
            return true;
        } else {
            return false;
        }
    }
}
