package NodosAST;

import analizadorLexico.Token;
import analizadorSemantico.*;

import java.util.ArrayList;
import java.util.HashMap;

public class NodoBloque implements NodoBase {
    private Token tokenInicial;
    public ArrayList<NodoBase> sentencias;
    public HashMap<String, Atributo> varLocals;

    public Clase getClaseActual() {
        return claseActual;
    }

    public void setClaseActual(Clase claseActual) {
        this.claseActual = claseActual;
    }

    public Metodo getMetodoActual() {
        return metodoActual;
    }

    public void setMetodoActual(Metodo metodoActual) {
        this.metodoActual = metodoActual;
    }

    private Clase claseActual;
    private Metodo metodoActual;
    public NodoBloque nodoPadre;
    private TablaDeSimbolos tablaDeSimbolos;
    private int ultimoOffset = 0;
    public NodoBloque(Token tokenInicial, Clase c, Metodo m, NodoBloque nodoPadre, TablaDeSimbolos tablaDeSimbolos) {
        this.tokenInicial = tokenInicial;
        this.claseActual = c;
        this.metodoActual = m;
        this.sentencias = new ArrayList<NodoBase>();
        this.varLocals = new HashMap<String, Atributo>();
        this.nodoPadre = nodoPadre;
        this.tablaDeSimbolos = tablaDeSimbolos;

    }

    public Tipo check() throws SemanticoExcepcion {
        for (NodoBase s : sentencias) {
            if (s != null){
                s.check();
                if (s instanceof NodoExpBinaria || s instanceof NodoExpUnaria){
                    throw new SemanticoExcepcion(s.getToken(), "No se puede realizar esta operación suelta en un bloque");
                }
            }
        }
        Token tknull = new Token("null", "Bloque", 0);
        return new Tipo(tknull,tablaDeSimbolos);
    }

    public void addSentencia(NodoBase s) {
        sentencias.add(s);
    }

    public Tipo getType() {
        return null;
    }

    @Override
    public Token getToken() {
        return tokenInicial;
    }


    public void addVarLocal(Atributo a) throws SemanticoExcepcion {
        if (metodoActual.getParametrosMap().containsKey(a.getTkAtributo().getLexema())) {
            throw new SemanticoExcepcion(a.getTkAtributo(), "El nombre de la variable ya existe como parametro");
        }
        if (varLocals.containsKey(a.getNombre()))
            throw new SemanticoExcepcion(a.getTkAtributo(), "La variable " + a.getNombre() + " ya existe en este bloque");
        varLocals.put(a.getNombre(), a);
    }

    public Atributo getVisibles(String nombre) {
        Atributo ret = null;
        if (varLocals.containsKey(nombre)) {
            ret = varLocals.get(nombre);
            ret.setVarLocal(true);
        } else if (metodoActual.getParametrosMap().containsKey(nombre)) {
            Atributo a = new Atributo(metodoActual.getParametrosMap().get(nombre).getTkParametro(), metodoActual.getParametrosMap().get(nombre).getTipo());
            ret = a;
            ret.setParametro(true);
        } else if (claseActual.getAtributos().containsKey(nombre)) {
            ret = claseActual.getAtributos().get(nombre);
            ret.setEsAtributo(true);
        } else if (nodoPadre != null) {
            ret = nodoPadre.getVisibles(nombre);
        }
        return ret;

    }

    public void generarCodigo() {
        for (NodoBase sentencia:sentencias) {
            sentencia.generarCodigo();
        }
        if(nodoPadre!=null){
            tablaDeSimbolos.codigoGenerado.add("FMEM "+ varLocals.size());
            ultimoOffset+= varLocals.size();
        }
        else{
            tablaDeSimbolos.codigoGenerado.add("FMEM "+ obtenerSizeVariablesLocales());
            ultimoOffset+= obtenerSizeVariablesLocales();
        }
    }

    private int obtenerSizeVariablesLocales() {
        if(nodoPadre!=null)
            return varLocals.size()+nodoPadre.obtenerSizeVariablesLocales();
        else
            return varLocals.size();
    }
}