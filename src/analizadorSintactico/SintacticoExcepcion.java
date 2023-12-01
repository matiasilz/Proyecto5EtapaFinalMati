package analizadorSintactico;

import analizadorLexico.Token;

public class SintacticoExcepcion extends Exception {
    private String tokenEsperado;
    private Token tokenActual;

    SintacticoExcepcion(Token tokenActual, String tokenEsperado) {
        this.tokenActual = tokenActual;
        this.tokenEsperado = tokenEsperado;
    }

    public String toString() {
        return "Error Sintactico en linea " + tokenActual.getNroLinea() + ": se esperaba un " + tokenEsperado
                + " se encontro " + tokenActual.getLexema() + "\n" + "[Error:" + tokenActual.getLexema() + "|"
                + tokenActual.getNroLinea() + "]";
    }

}
