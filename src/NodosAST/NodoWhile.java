package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.SemanticoExcepcion;
import analizadorSemantico.TablaDeSimbolos;
import analizadorSemantico.Tipo;

public class NodoWhile implements NodoSentencia{
    private NodoBase condicion;
    private NodoBase sentencia;
    private Token tkwhile;
    private TablaDeSimbolos tablaDeSimbolos;
    private int cantidadEtiquetasInicioWhile = 0;
    private int cantidadEtiquetasFinWhile = 0;
    public NodoWhile(Token tkwhile, NodoBase condicion, NodoBase sentencia, TablaDeSimbolos tablaDeSimbolos) {
        this.condicion = condicion;
        this.sentencia = sentencia;
        this.tkwhile = tkwhile;
        this.tablaDeSimbolos = tablaDeSimbolos;
    }
    public Tipo check() throws SemanticoExcepcion {
        Tipo tipoCondicion = condicion.check();
        System.out.println(tipoCondicion.getToken().getIdentificador());
        if (tipoCondicion.getToken().getIdentificador() != "pr_boolean") {
            throw new SemanticoExcepcion(tkwhile, "La condicion del while debe ser de tipo boolean");
        }
        sentencia.check();
        Token tknull = new Token("null", "Bloque", 0);
        return new Tipo(tknull,tablaDeSimbolos);
    }

    @Override
    public Tipo getType() {
        return null;
    }

    public Token getToken() {
        return tkwhile;
    }
    @Override
    public void generarCodigo() {
        String etiquetaFin = "etiquetaWhileFin" + this.cantidadEtiquetasFinWhile++;
        String etiquetaInicio = "etiquetaWhileInicio" + this.cantidadEtiquetasInicioWhile++;
        tablaDeSimbolos.codigoGenerado.add(etiquetaInicio + ":");
        condicion.generarCodigo();
        tablaDeSimbolos.codigoGenerado.add("BF " + etiquetaFin + " #Si la condicion es falsa, salta a la etiqueta " + etiquetaFin);
        sentencia.generarCodigo();
        tablaDeSimbolos.codigoGenerado.add("JUMP " + "Se salta al principio para volver a evaluar la condicion" + etiquetaInicio);
        tablaDeSimbolos.codigoGenerado.add(etiquetaFin + ":");
    }

}