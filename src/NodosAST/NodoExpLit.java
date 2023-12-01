package NodosAST;

import analizadorSemantico.*;
import analizadorLexico.Token;

public class NodoExpLit implements NodoExpInterface {
    private Token token;
    private Tipo tipo;
    private TablaDeSimbolos tablaDeSimbolos;
    private int cantidadEtiquetasString = 0;
    public NodoExpLit(Token token, TablaDeSimbolos tablaDeSimbolos) {
        this.token = token;
        this.tablaDeSimbolos = tablaDeSimbolos;
    }
    public Tipo check() throws SemanticoExcepcion {
        switch (token.getIdentificador()) {
            case "pr_true":
            case "pr_false":
                return new TipoPrimitivo(new Token("pr_boolean","boolean", token.getNroLinea()), tablaDeSimbolos);
            case "pr_null":
                return new TipoPrimitivo(new Token("pr_null","null", token.getNroLinea()), tablaDeSimbolos);
            case "pr_void":
                return new TipoPrimitivo(new Token("pr_void","void", token.getNroLinea()), tablaDeSimbolos);
            case "intLiteral":
                return new TipoPrimitivo(new Token("pr_int","int", token.getNroLinea()), tablaDeSimbolos);
            case "charLiteral":
                return new TipoPrimitivo(new Token("pr_char","char", token.getNroLinea()), tablaDeSimbolos);
            case "stringLiteral":
                return new TipoReferencia(new Token("pr_string","String", token.getNroLinea()), tablaDeSimbolos);
        }
        return null;
    }
    @Override
    public Tipo getType() {
        return tipo;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public void generarCodigo() {
        switch (token.getIdentificador()) {
            case "pr_true":
                tablaDeSimbolos.codigoGenerado.add("PUSH 1");
                break;
            case "pr_false":
                tablaDeSimbolos.codigoGenerado.add("PUSH 0");
                break;
            case "pr_null":
                tablaDeSimbolos.codigoGenerado.add("PUSH NULL");
                break;
            case "intLiteral":
                tablaDeSimbolos.codigoGenerado.add("PUSH " + token.getLexema());
                break;
            case "charLiteral":
                tablaDeSimbolos.codigoGenerado.add("PUSH " + token.getLexema());
                break;
            case "stringLiteral":
                tablaDeSimbolos.codigoGenerado.add(".DATA");
                String etiquetaString = "etiquetaString" + this.cantidadEtiquetasString++;
                tablaDeSimbolos.codigoGenerado.add(etiquetaString + " " + "DW " + token.getLexema() + ", 0");
                tablaDeSimbolos.codigoGenerado.add(".CODE");
                tablaDeSimbolos.codigoGenerado.add("PUSH " + etiquetaString);
                break;
        }
    }
}