package analizadorSemantico;

import analizadorLexico.Token;

public class SemanticoExcepcion extends Exception {
    private String mensaje;
    private Token tokenActual;

    public SemanticoExcepcion(Token tokenActual, String mensaje) {
        this.tokenActual = tokenActual;
        this.mensaje= mensaje;
    }

    public String toString() {
        return "Error Semantico en linea " + tokenActual.getNroLinea() + ": " + mensaje
                + "\n" + "[Error:" + tokenActual.getLexema() + "|"
                + tokenActual.getNroLinea() + "]";
    }

}