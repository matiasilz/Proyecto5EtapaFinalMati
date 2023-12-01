package NodosAST;
import analizadorLexico.Token;
import analizadorSemantico.SemanticoExcepcion;
import analizadorSemantico.TablaDeSimbolos;
import analizadorSemantico.Tipo;
public class NodoIf implements NodoSentencia {
    private NodoBase condicion;
    private NodoBase sentencia;
    private NodoBase sentenciaElse;
    private Token tkif;
    private TablaDeSimbolos tablaDeSimbolos;
    private int cantidadEtiquetasIf = 0;
    private int cantidadEtiquetasElse = 0;
    public NodoIf(Token tkif, NodoBase condicion, NodoBase sentencia, TablaDeSimbolos tablaDeSimbolos) {
        this.condicion = condicion;
        this.sentencia = sentencia;
        this.tkif = tkif;
        this.tablaDeSimbolos = tablaDeSimbolos;
    }
    public Tipo check() throws SemanticoExcepcion {
        Tipo tipoCondicion = condicion.check();
        if (tipoCondicion.getToken().getIdentificador() != "pr_boolean") {
            throw new SemanticoExcepcion(tkif, "La condicion del if debe ser de tipo boolean");
        }
        sentencia.check();
        if (sentenciaElse != null) {
            sentenciaElse.check();
        }
        Token tknull = new Token("null", "Bloque", 0);
        return new Tipo(tknull, tablaDeSimbolos);
    }
    public Tipo getType() {
        return null;
    }

    public void setSentenciaElse(NodoBase se) {
        sentenciaElse = se;
    }

    public Token getToken() {
        return tkif;
    }

    @Override
    public void generarCodigo() {
        String etiquetaElse = "etiquetaElse" + this.cantidadEtiquetasElse++;
        String etiquetaIf = "etiquetaIf" + this.cantidadEtiquetasIf++;
        condicion.generarCodigo();
        if(sentenciaElse!=null){
            tablaDeSimbolos.codigoGenerado.add("BF " + etiquetaElse + " #Si la condicion es falsa, salta a la etiqueta " + etiquetaElse);
            sentencia.generarCodigo();
            tablaDeSimbolos.codigoGenerado.add("JUMP " + "Se salta al final del if" + etiquetaIf);
            tablaDeSimbolos.codigoGenerado.add(etiquetaElse + ":");
            sentenciaElse.generarCodigo();
        }
        else{
            tablaDeSimbolos.codigoGenerado.add("BF " + etiquetaIf + " #Si la condicion es falsa, salta a la etiqueta " + etiquetaIf);
            sentencia.generarCodigo();
        }
        tablaDeSimbolos.codigoGenerado.add(etiquetaIf + ":");
        tablaDeSimbolos.codigoGenerado.add("NOP");
    }
}