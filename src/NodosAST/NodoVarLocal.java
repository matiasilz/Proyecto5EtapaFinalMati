package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.*;

public class NodoVarLocal implements NodoSentencia {
    private Token id;
    private NodoBase expresion;
    private NodoBloque bloqueActual;
    private Clase claseActual;
    private Metodo metodoActual;
    private TablaDeSimbolos tablaDeSimbolos;
    public NodoVarLocal(Token id, NodoBase expresion, NodoBloque bloqueActual, Clase claseActual, Metodo metodoActual, TablaDeSimbolos tablaDeSimbolos) {
        this.id = id;
        this.expresion = expresion;
        this.bloqueActual = bloqueActual;
        this.claseActual = claseActual;
        this.metodoActual = metodoActual;
        this.tablaDeSimbolos = tablaDeSimbolos;
    }
    public String getName() {
        return id.getLexema();
    }
    public Token getId() {
        return id;
    }
    @Override
    public Tipo check() throws SemanticoExcepcion {
        Tipo ret = null;
        if (expresion != null) {
            System.out.println(expresion.getToken().getLexema());
            if(expresion.getToken().getLexema().equals("null"))
                throw new SemanticoExcepcion(expresion.getToken(),"No se puede inferir el tipo");
            ret = expresion.check();
            if(ret==null)
                throw new SemanticoExcepcion(expresion.getToken(), "No se puede inferir el tipo");
            Atributo atributo = bloqueActual.getVisibles(id.getLexema());
            if(atributo==null)
                bloqueActual.addVarLocal(new Atributo(id, ret));
            else {
                throw new SemanticoExcepcion(id, "La variable " + id.getLexema() + " ya esta declarada");
            }
        }
        return ret;
    }

    @Override
    public Tipo getType() {

        return null;
    }

    public Token getToken() {
        return id;
    }

    @Override
    public void generarCodigo() {
        tablaDeSimbolos.codigoGenerado.add("RMEM 1");
        if (expresion != null) {
            expresion.generarCodigo();
            tablaDeSimbolos.codigoGenerado.add("STORE " + bloqueActual.getVisibles(id.getLexema()).getOffset());
        }
    }
}