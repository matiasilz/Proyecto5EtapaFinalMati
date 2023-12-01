package analizadorLexico;

public class LexicoExcepcion extends Exception {

    private String lexema;
    private int nroLinea;
    private String tipoError;

    LexicoExcepcion(String lex, int nroLinea, String tipoError) {
        lexema = lex;
        this.nroLinea = nroLinea;
        this.tipoError = tipoError;
    }

    public String toString() {
        return "Error Lèxico en linea " + nroLinea + ":" + lexema + " " + tipoError + "\n" + "[Error:" + lexema + "|"
                + nroLinea + "]";
    }

}
