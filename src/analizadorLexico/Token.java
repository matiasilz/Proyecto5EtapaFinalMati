package analizadorLexico;

public class Token {

    private String identificador;
    private String lexema;
    private int nroLinea;
    public Token(String id,String lex,int nroLinea){
        identificador = id;
        lexema = lex;
        this.nroLinea = nroLinea;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public int getNroLinea() {
        return nroLinea;
    }

    public void setNroLinea(int nroLinea) {
        this.nroLinea = nroLinea;
    }

    public String mostrarContenido(){
        return "("+identificador+","+lexema+","+nroLinea+")";
    }
}
